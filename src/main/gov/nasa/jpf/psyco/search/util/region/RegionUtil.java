/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import gov.nasa.jpf.psyco.search.region.Region;
import gov.nasa.jpf.psyco.search.region.State;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 *
 * @author mmuesly
 */
public abstract class RegionUtil<V extends State<?>, T extends Region<?,V>> {
  private long unique;
  ConstraintSolver solver;
  protected final Logger logger;

  public RegionUtil(ConstraintSolver solver){
    this.solver = solver;
    this.unique = 1L;
    logger = Logger.getLogger("psyco");
  }

  public T disjunction(T regionA, T regionB) {
    T disjunctedRegion = (T) regionA.createNewRegion();
    disjunctedRegion.putAll(regionA);
    for(String key: regionB.keySet()){
      //This assumes, that state names are unique to work!
      if(disjunctedRegion.containsKey(key)){
        continue;
      }
      disjunctedRegion.put(key, regionB.get(key));
    }
    return disjunctedRegion;
  }

  public T conjunction(T regionA,
          T regionB) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public T difference(T outterRegion, T excludedRegion) {
    return difference(outterRegion, excludedRegion, this.solver);
  }

  public T difference(T outterRegion, T excludedRegion,
          ConstraintSolver solver) {
    T resultRegion = (T) outterRegion.createNewRegion();
    Expression notRegion = null;
    if(null == excludedRegion || excludedRegion.isEmpty()){
      return outterRegion;
    }
    Set<State> toExclude = new HashSet();
    for(State<?> state: excludedRegion.values()){
      toExclude.add(state);
    }
    logger.finer("gov.nasa.jpf.psyco.search.region."
            + "util.SymbolicRegionUtil.difference()");
    for(String key : outterRegion.keySet()){
        Expression excludedRegionExpr = convertSetToExpression(toExclude);
        Set<Variable<?>> stateVariables = convertToVariableSet(excludedRegion);
        notRegion = new Negation(excludedRegionExpr);
        notRegion = 
                bindParameters(notRegion, stateVariables, Quantifier.FORALL);
        V state = outterRegion.get(key);
        Expression stateRegion = state.toExpression();
        Set<Variable<?>> newStateVariables = convertToVariableSet(state);
        newStateVariables.addAll(stateVariables);
        stateRegion =
                bindParameters(stateRegion,
                        newStateVariables, Quantifier.EXISTS);
        Expression testDiffState = ExpressionUtil.and(stateRegion, notRegion);
        long start = System.currentTimeMillis();
        Valuation val = new Valuation();
        ConstraintSolver.Result rs = solver.solve(testDiffState, val);
        long stop = System.currentTimeMillis();
        logger.finer("Time needed for difference: " 
                + Long.toString(stop - start) + " in Millis");
        if(rs == ConstraintSolver.Result.SAT){
          resultRegion.put(key, state);
          toExclude.add(state);
          logger.finer("excludedSize: " + toExclude.size());
        }
        else{
          logger.finer("result: " + rs);
        }
    }
    return resultRegion;
  }

  public T exists(T aRegion, Set<Variable<?>> subsetOfVariables) {
    T existingRegion = (T) aRegion.createNewRegion();
    if(aRegion.isEmpty()){
      return existingRegion;
    }
    for(String key: aRegion.keySet()){
      State<?> state = aRegion.get(key);
      for(ValuationEntry entry: state){
        if(!subsetOfVariables.contains(entry.getVariable())){
          existingRegion.put(key, (V) state);
          break;
        }
      }
    }
    return existingRegion;
  }

  public Set<Variable<?>> convertToVariableSet(T region) {
    Set<Variable<?>> resultingSet = new HashSet<>();
    for(State <?> state: region.values()){
      for(ValuationEntry entry: state){
        resultingSet.add(entry.getVariable());
      }
    }
    return resultingSet;
  }

  public Set<Variable<?>> convertToVariableSet(State<?> state) {
    Set<Variable<?>> resultingSet = new HashSet<>();
    for(ValuationEntry entry: state){
      resultingSet.add(entry.getVariable());
    }
    return resultingSet;
  }

  protected String getUniqueName(){
    String uniqueName = "uVarReplacement_" + unique;
    ++unique;
    return uniqueName;
  }
  
  protected String getUniqueParameterName(Variable parameter){
    String uniqueName = "p" + parameter.getName() + "_" + unique;
    ++unique;
    return uniqueName;
  }

  private Expression bindParameters(Expression region,
          Set<Variable<?>> stateVariables, Quantifier quantifier){
    Set<Variable<?>> freeVars = ExpressionUtil.freeVariables(region);
    ArrayList<Variable<?>> bound = new ArrayList<>();
    for(Variable var: freeVars){
      if(!stateVariables.contains(var) 
              && !var.getName().startsWith("uVarReplacement")){
        logger.finest("gov.nasa.jpf.psyco.search.region"
                + ".util.SymbolicRegionUtil.bindParameters()");
        logger.finest(var.getName());
        bound.add(var);
      }
    }
    if(!bound.isEmpty()){
      region = new QuantifierExpression(quantifier, bound, region);
    }
    return region;
  }

  private Expression convertSetToExpression(Set<State> states){
    Expression expr = null;
    for(State state: states){
      Expression stateExpr = state.toExpression();
      expr = expr==null? stateExpr : ExpressionUtil.or(expr, stateExpr);
    }
    return expr;
  }

  public abstract T rename(Region<?,V> region,
          List<Variable<?>> primeNames, List<Variable<?>> variableNames);
}
