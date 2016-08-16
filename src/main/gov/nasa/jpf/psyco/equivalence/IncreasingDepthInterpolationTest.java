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
import gov.nasa.jpf.constraints.api.InterpolationSolver;
import gov.nasa.jpf.jdart.constraints.Path;
import gov.nasa.jpf.jdart.termination.TerminationStrategy;
import gov.nasa.jpf.psyco.alphabet.SummaryAlphabet;
import gov.nasa.jpf.psyco.alphabet.SymbolicMethodSymbol;
import gov.nasa.jpf.psyco.exceptions.CounterexampleFound;
import gov.nasa.jpf.psyco.exceptions.Terminate;
import gov.nasa.jpf.psyco.interpolation.InterpolationCache;
import gov.nasa.jpf.psyco.interpolation.InterpolationUtil;
import gov.nasa.jpf.psyco.learnlib.SymbolicEquivalenceTest;
import gov.nasa.jpf.psyco.learnlib.SymbolicQueryOutput;
import gov.nasa.jpf.psyco.learnlib.ThreeValuedOracle;
import gov.nasa.jpf.util.JPFLogger;
import gov.nasa.jpf.util.SimpleProfiler;
import java.util.Collection;
import java.util.Collections;
import net.automatalib.automata.transout.MealyMachine;
import net.automatalib.words.Word;

public class IncreasingDepthInterpolationTest implements SymbolicEquivalenceTest {
  
  private static final JPFLogger logger = JPF.getLogger("psyco");

  private InterpolationUtil util;
  
  private final int kMax;
  
  private final SummaryAlphabet inputs;

  private final ThreeValuedOracle oracle;
    
  private final ConstraintSolver cSolver;
  
  private final InterpolationSolver iSolver;

  private final TerminationStrategy termination;
  
  private int k = 1;
    
  private MealyMachine<Object, SymbolicMethodSymbol, ?, SymbolicQueryOutput> model;

  public IncreasingDepthInterpolationTest(int kMax, SummaryAlphabet inputs, 
          ThreeValuedOracle oracle, ConstraintSolver cSolver, InterpolationSolver iSolver, 
          TerminationStrategy termination) {
    this.kMax = kMax;
    this.inputs = inputs;
    this.oracle = oracle;
    this.cSolver = cSolver;
    this.iSolver = iSolver;
    this.termination = termination;
    this.util = new InterpolationUtil(iSolver, cSolver, inputs, model);
  }

  public IncreasingDepthInterpolationTest(SummaryAlphabet inputs, 
          ThreeValuedOracle oracle, ConstraintSolver cSolver, InterpolationSolver iSolver, 
          TerminationStrategy termination) {
    this(-1, inputs, oracle, cSolver, iSolver, termination);
  }  
    
  @Override
  public void logStatistics() {
    logger.info("EQ Test depth completed: " + (k-1));
    logger.info("EQ Test max depth: " + kMax);
  }  

  @Override
  public DefaultQuery<SymbolicMethodSymbol, SymbolicQueryOutput> findCounterExample(
          MealyMachine<?, SymbolicMethodSymbol, ?, SymbolicQueryOutput> a, Collection<? extends SymbolicMethodSymbol> clctn) {

    this.model = (MealyMachine<Object, SymbolicMethodSymbol, ?, SymbolicQueryOutput>)a;
    DefaultQuery<SymbolicMethodSymbol, SymbolicQueryOutput> ce = null;
    this.util = new InterpolationUtil(iSolver, cSolver, inputs, model);
    k = 2;
    try {      
      while (true) {
        ce = check(k);
        if (ce != null) {
          return ce;
        }
        logger.finest(SimpleProfiler.getResults());
        logger.info("==== completed depth " + k);
        logger.fine(this.util.getCache());
        k++;
        //this.util = new InterpolationUtil(iSolver, cSolver, inputs, model);    
        if (deepEnough()) {          
          return null;
        }
      }
      
    } catch (Terminate t) {
      return null;
    }
  }

  private DefaultQuery<SymbolicMethodSymbol, SymbolicQueryOutput> check(int k) {
    logger.fine("Checking conformance for depth " + k);
    try {
      Word<SymbolicMethodSymbol> eps = Word.epsilon();
      Word<Path> empty = Word.epsilon();
      util.expand(eps, empty, k);
    } catch (CounterexampleFound ce) {
      DefaultQuery<SymbolicMethodSymbol, SymbolicQueryOutput> ret = 
              new DefaultQuery<>(ce.getCounterexample());
      oracle.processQueries(Collections.singletonList(ret));
      return ret;
    }
    return null;
  }
  
  public int getCurrentK() {
    return k;
  }
  
  private boolean deepEnough() {
    return (kMax > 0 ) && (k > kMax);
  }

}