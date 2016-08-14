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
package gov.nasa.jpf.psyco.equivalence;

import de.learnlib.oracles.DefaultQuery;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.InterpolationSolver;
import gov.nasa.jpf.constraints.util.ExpressionUtil;
import gov.nasa.jpf.jdart.constraints.Path;
import gov.nasa.jpf.jdart.constraints.PathState;
import gov.nasa.jpf.psyco.alphabet.SummaryAlphabet;
import gov.nasa.jpf.psyco.alphabet.SymbolicMethodSymbol;
import gov.nasa.jpf.psyco.exceptions.CounterexampleFound;
import gov.nasa.jpf.psyco.interpolation.InterpolationUtil;
import gov.nasa.jpf.psyco.learnlib.SymbolicEquivalenceTest;
import gov.nasa.jpf.psyco.learnlib.SymbolicQueryOutput;
import gov.nasa.jpf.psyco.learnlib.ThreeValuedOracle;
import gov.nasa.jpf.psyco.util.PathUtil;
import gov.nasa.jpf.psyco.util.PathUtil.PathQuery;
import gov.nasa.jpf.util.JPFLogger;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import net.automatalib.automata.transout.MealyMachine;
import net.automatalib.words.Word;

public class InvarianceTest implements SymbolicEquivalenceTest {

  private static final JPFLogger logger = JPF.getLogger("psyco");
  
  private MealyMachine<Object, SymbolicMethodSymbol, ?, SymbolicQueryOutput> model;
  private Map<Object, Word<SymbolicMethodSymbol>> accessors;
  private InterpolationUtil util;
  
  private final SummaryAlphabet inputs;  
  private final InterpolationSolver iSolver;
  private final ConstraintSolver cSolver;
  private final ThreeValuedOracle oracle;

  private int k;  
  private int kDone = 0;
  private boolean found = false;
  
  private final int kMax;
    
  public InvarianceTest(SummaryAlphabet inputs, InterpolationSolver iSolver, 
          ConstraintSolver cSolver, ThreeValuedOracle oracle, int kMax) {
    this.inputs = inputs;
    this.iSolver = iSolver;
    this.cSolver = cSolver;
    this.oracle = oracle;
    this.kMax = kMax;
  }

  @Override
  public void logStatistics() {    
    logger.info("EQTEST: " + (found ? "Found safe invariant." : "No safe invariant could be found."));
    logger.info("k (last, highest, max): " + k + ", " + kDone + ", " + kMax);
    logger.info(this.util.getCache());    
  }

  @Override
  public DefaultQuery<SymbolicMethodSymbol, SymbolicQueryOutput> findCounterExample(
          MealyMachine<?, SymbolicMethodSymbol, ?, SymbolicQueryOutput> a, Collection<? extends SymbolicMethodSymbol> clctn) {
    
    this.model = (MealyMachine<Object, SymbolicMethodSymbol, ?, SymbolicQueryOutput>) a;
    this.accessors = getAccessSequences();
    this.util = new InterpolationUtil(iSolver, cSolver, inputs, model);
    this.k=0;
    
    while (true) {
      this.k++;
      if (this.k > this.kMax) {
        logger.info("Exceeded max. depth of " + kMax);
        return null;
      }
      kDone = java.lang.Math.max(k, kDone);
      try {
        logger.info("Checking futures of length " + k);
        Map<Object, Expression<Boolean>> invars = computeInvariants(k);
        InvarianceProof proof = new InvarianceProof(inputs, cSolver, model, invars);
        if (proof.prove()) {
          found = true;
          logger.info("Found proof at depth " + k);
          return null;          
        }        
      } catch (CounterexampleFound ex) {
      DefaultQuery<SymbolicMethodSymbol, SymbolicQueryOutput> ret = 
              new DefaultQuery<>(ex.getCounterexample());
      oracle.processQueries(Collections.singletonList(ret));
      return ret;
      }
    }
  }

  
  
  
  private Map<Object, Expression<Boolean>> computeInvariants(int k) 
          throws CounterexampleFound {
    
    Map<Object, Expression<Boolean>> invars = new HashMap<>();
    for (Entry<Object, Word<SymbolicMethodSymbol>> e : this.accessors.entrySet()) {    
      Expression<Boolean> itp = expand(e.getValue(), k);
      invars.put(e.getKey(), itp);
    }    
    return invars;
  }
  
  
  private Expression<Boolean> expand(Word<SymbolicMethodSymbol> prefix, int depth) 
          throws CounterexampleFound {

    Expression<Boolean> approx = ExpressionUtil.TRUE;    
    for (PathQuery q : PathUtil.explode(prefix, inputs)) {
      
      if (prefix.length() > 0) {
        Path p = PathUtil.executeSymbolically(
                q.getMethods(), q.getPaths(), inputs.getInitialValuation());

        if (!p.getState().equals(PathState.OK) || !sat(p.getPathCondition())) {
          continue;
        }
      }
      
      Expression<Boolean> itp = util.expand(q.getMethods(), q.getPaths(), depth);
      
      System.out.println("----------------------------------------------------");
      System.out.println("M: " + q.getMethods());
      System.out.println("P: " + q.getPaths());
      System.out.println("D: " + depth);
      System.out.println("I: " + itp);
            
      approx = ExpressionUtil.and(approx, itp);      
    }
    return approx;    
  }
          
  private Map<Object, Word<SymbolicMethodSymbol>> getAccessSequences() {
    Map<Object, Word<SymbolicMethodSymbol>> map = new HashMap<>();
    
    Word<SymbolicMethodSymbol> word = Word.epsilon();
    Object state = this.model.getInitialState();
    map.put(state, word);
    
    Queue<Object> queue = new LinkedList<>();
    queue.add(state);
    
    while (!queue.isEmpty()) {
      state = queue.poll();
      word = map.get(state);
      
      for (SymbolicMethodSymbol a : this.inputs) {
        SymbolicQueryOutput out = this.model.getOutput(state, a);
        Object succ = this.model.getSuccessor(state, a);
        if (!out.equals(SymbolicQueryOutput.OK) ||
                map.containsKey(succ)) {
          continue;
        }        
        map.put(succ, word.append(a));
        queue.add(succ);
      }
    }
    
    return map;
  }
  
  private boolean sat(Expression<Boolean> expr) {
    return cSolver.isSatisfiable(expr) == ConstraintSolver.Result.SAT;
  }    
}
