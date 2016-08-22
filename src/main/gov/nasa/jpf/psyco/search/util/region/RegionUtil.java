/*
 * Copyright (C) 2015, United States Government, as represented by the 
 * Administrator of the National Aeronautics and Space Administration.
 * All rights reserved.
 *
 * The PSYCO: A Predicate-based Symbolic Compositional Reasoning environment 
 * platform is licensed under the Apache License, Version 2.0 (the "License"); you 
 * may not use this file except in compliance with the License. You may obtain a 
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0. 
 *
 * Unless required by applicable law or agreed to in writing, software distributed 
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 * specific language governing permissions and limitations under the License.
 */
package gov.nasa.jpf.psyco.search.util.region;

import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.api.ValuationEntry;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.expressions.Negation;
import gov.nasa.jpf.constraints.expressions.Quantifier;
import gov.nasa.jpf.constraints.expressions.QuantifierExpression;
import gov.nasa.jpf.constraints.util.ExpressionUtil;
import gov.nasa.jpf.psyco.search.datastructures.region.Region;
import gov.nasa.jpf.psyco.search.datastructures.state.State;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
/**
 * Supporting class to performe a operations on Regions.
 * Supported operations are renamin, exists, conjunction, difference.
 * It is used in the search algorithms. Renaming must be implemented in a 
 * subclass so that it fits the state entries.
 */
public abstract class RegionUtil<V extends State<?>, T extends Region<?, V>> {

  private long unique;
  ConstraintSolver solver;
  protected final Logger logger;

  public RegionUtil(ConstraintSolver solver) {
    this.solver = solver;
    this.unique = 1L;
    logger = Logger.getLogger("psyco");
  }

  public T union(T regionA, T regionB) {
    T union = (T) regionA.createNewRegion();
    union.putAll(regionA);
    for (String key : regionB.keySet()) {
      //This assumes, that state names are unique to work!
      if (union.containsKey(key)) {
        continue;
      }
      union.put(key, regionB.get(key));
    }
    return union;
  }

  public T difference(T outterRegion, T excludedRegion) {
    return difference(outterRegion, excludedRegion, this.solver);
  }

  public T difference(T outterRegion, T excludedRegion,
          ConstraintSolver solver) {
    T resultRegion = (T) outterRegion.createNewRegion();
    Expression notRegion = null;
    if (null == excludedRegion || excludedRegion.isEmpty()) {
      return outterRegion;
    }
    Set<State> toExclude = new HashSet();
    for (State<?> state : excludedRegion.values()) {
      toExclude.add(state);
    }
    logger.finer("gov.nasa.jpf.psyco.search.region."
            + "util.SymbolicRegionUtil.difference()");
    for (String key : outterRegion.keySet()) {
      Expression excludedRegionExpr = convertSetToExpression(toExclude);
      Set<Variable<?>> stateVariables = convertToVariableSet(excludedRegion);
      notRegion = new Negation(excludedRegionExpr);
      notRegion
              = bindParameters(notRegion, stateVariables, Quantifier.FORALL);
      V state = outterRegion.get(key);
      Expression stateRegion = state.toExpression();
      Set<Variable<?>> newStateVariables = convertToVariableSet(state);
      newStateVariables.addAll(stateVariables);
      stateRegion
              = bindParameters(stateRegion,
                      newStateVariables, Quantifier.EXISTS);
      Expression testDiffState = ExpressionUtil.and(stateRegion, notRegion);
      long start = System.currentTimeMillis();
      Valuation val = new Valuation();
      logger.finest("Diff state for test: " + testDiffState.toString());
      ConstraintSolver.Result rs = solver.solve(testDiffState, val);
      logger.finest("result" + ExpressionUtil.valuationToExpression(val));
      long stop = System.currentTimeMillis();
      logger.finer("Time needed for difference: "
              + Long.toString(stop - start) + " in Millis");
      if (rs == ConstraintSolver.Result.SAT) {
        resultRegion.put(key, state);
        toExclude.add(state);
        logger.finer("excludedSize: " + toExclude.size());
      } else if (rs == ConstraintSolver.Result.DONT_KNOW) {
        throw new IllegalStateException("Cannot compute difference test");
      } else {
        logger.finer("result: " + rs);
      }
    }
    return resultRegion;
  }

  public T exists(T aRegion, Set<Variable<?>> subsetOfVariables) {
    T existingRegion = (T) aRegion.createNewRegion();
    if (aRegion.isEmpty()) {
      return existingRegion;
    }
    for (String key : aRegion.keySet()) {
      State<?> state = aRegion.get(key);
      for (ValuationEntry entry : state) {
        if (!subsetOfVariables.contains(entry.getVariable())) {
          existingRegion.put(key, (V) state);
          break;
        }
      }
    }
    return existingRegion;
  }

  public Set<Variable<?>> convertToVariableSet(T region) {
    Set<Variable<?>> resultingSet = new HashSet<>();
    for (State<?> state : region.values()) {
      for (ValuationEntry entry : state) {
        resultingSet.add(entry.getVariable());
      }
    }
    return resultingSet;
  }

  public Set<Variable<?>> convertToVariableSet(State<?> state) {
    Set<Variable<?>> resultingSet = new HashSet<>();
    for (ValuationEntry entry : state) {
      resultingSet.add(entry.getVariable());
    }
    return resultingSet;
  }

  protected String getUniqueName() {
    String uniqueName = "uVarReplacement_" + unique;
    ++unique;
    return uniqueName;
  }

  protected String getUniqueParameterName(Variable parameter) {
    String uniqueName = "p" + parameter.getName() + "_" + unique;
    ++unique;
    return uniqueName;
  }

  private Expression bindParameters(Expression region,
          Set<Variable<?>> stateVariables, Quantifier quantifier) {
    Set<Variable<?>> freeVars = ExpressionUtil.freeVariables(region);
    ArrayList<Variable<?>> bound = new ArrayList<>();
    for (Variable var : freeVars) {
      if (!stateVariables.contains(var)
              && !var.getName().startsWith("uVarReplacement")) {
        logger.finest("gov.nasa.jpf.psyco.search.region"
                + ".util.SymbolicRegionUtil.bindParameters()");
        logger.finest(var.getName());
        bound.add(var);
      }
    }
    if (!bound.isEmpty()) {
      region = new QuantifierExpression(quantifier, bound, region);
    }
    return region;
  }

  private Expression convertSetToExpression(Set<State> states) {
    Expression expr = null;
    for (State state : states) {
      Expression stateExpr = state.toExpression();
      expr = expr == null ? stateExpr : ExpressionUtil.or(expr, stateExpr);
    }
    return expr;
  }

  public abstract T rename(Region<?, V> region,
          Map<Variable, Variable> renamings);
}