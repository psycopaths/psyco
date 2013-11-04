/*******************************************************************************
 * Copyright (C) 2008 United States Government as represented by the
 * Administrator of the National Aeronautics and Space Administration
 * (NASA).  All Rights Reserved.
 * 
 * This software is distributed under the NASA Open Source Agreement
 * (NOSA), version 1.3.  The NOSA has been approved by the Open Source
 * Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
 * directory tree for the complete NOSA document.
 * 
 * THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
 * KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
 * LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
 * SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
 * A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
 * THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
 * DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
 ******************************************************************************/
package gov.nasa.jpf.psyco.equivalence;

import de.learnlib.oracles.DefaultQuery;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.psyco.DefaultOracleProvider;
import gov.nasa.jpf.psyco.PsycoConfig;
import gov.nasa.jpf.psyco.alphabet.SymbolicMethodAlphabet;
import gov.nasa.jpf.psyco.alphabet.SymbolicMethodSymbol;
import gov.nasa.jpf.psyco.exceptions.Terminate;
import gov.nasa.jpf.psyco.filter.MethodExecutionFilter;
import gov.nasa.jpf.psyco.learnlib.SymbolicEquivalenceTest;
import gov.nasa.jpf.psyco.learnlib.SymbolicQueryOutput;
import gov.nasa.jpf.psyco.learnlib.ThreeValuedOracle;
import gov.nasa.jpf.util.JPFLogger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import net.automatalib.automata.transout.MealyMachine;
import net.automatalib.words.Word;

/**
 *
 * @author falkhowar
 */
public class IncreasingDepthExhaustiveTest implements SymbolicEquivalenceTest {
  
  private static final JPFLogger logger = JPF.getLogger("psyco");
  
  private int k = 2;
  
  private int kMax = -1;
  
  private MealyMachine<Object, SymbolicMethodSymbol, ?, SymbolicQueryOutput> model;

  private final ThreeValuedOracle oracle;

  private final SymbolicMethodAlphabet inputs;
  
  private final MethodExecutionFilter filter;
  
  public IncreasingDepthExhaustiveTest(DefaultOracleProvider provider, PsycoConfig pconf) {
    this.oracle = provider.getThreeValuedOracle();
    this.inputs = provider.getInputs();
    this.filter = provider.getExecutionFilter();
    this.kMax = pconf.getMaxDepth();
  }

  @Override
  public DefaultQuery<SymbolicMethodSymbol, SymbolicQueryOutput> findCounterExample(
          MealyMachine<?, SymbolicMethodSymbol, ?, SymbolicQueryOutput> a, 
          Collection<? extends SymbolicMethodSymbol> clctn) {

    this.model = (MealyMachine<Object, SymbolicMethodSymbol, ?, SymbolicQueryOutput>)a;
    DefaultQuery<SymbolicMethodSymbol, SymbolicQueryOutput> ce = null;
    
    try {
      while (true) {
        ce = findCounterExampleAtDepthK();
        if (ce != null) {
          return ce;
        }
        logger.info("==== completed depth " + k);
        k++;
        if (deepEnough()) {
          return null;
        }
      }
    } catch (Terminate t) {
      return null;      
    }
  }
  
  private DefaultQuery<SymbolicMethodSymbol, SymbolicQueryOutput> findCounterExampleAtDepthK() {
    Collection<DefaultQuery<SymbolicMethodSymbol, SymbolicQueryOutput>> queries = unroll();
    for (DefaultQuery<SymbolicMethodSymbol, SymbolicQueryOutput> q : queries) {      
      this.oracle.processQueries(Collections.singleton(q));
      SymbolicQueryOutput refOut = 
              this.model.computeOutput(q.getInput()).lastSymbol();
      
      if (!refOut.equals(q.getOutput())) {
        logger.info("================ CE: " + q.getInput() + 
                " : " + refOut +" <> " + q.getOutput());
        return q;
      }
    } 
    return null;
  }

  private Collection<DefaultQuery<SymbolicMethodSymbol, SymbolicQueryOutput>> unroll() {
    ArrayList<DefaultQuery<SymbolicMethodSymbol, SymbolicQueryOutput>> ret = 
            new ArrayList<>();
    
    Object init = this.model.getInitialState();
    Word<SymbolicMethodSymbol> eps = Word.epsilon();
    unroll(eps, init, ret);
    return ret;
  }
  
  private void unroll(Word<SymbolicMethodSymbol> prefix, Object state,
          Collection<DefaultQuery<SymbolicMethodSymbol, SymbolicQueryOutput>> words) {
    
    if (prefix.length() == k) {
      add(new DefaultQuery<SymbolicMethodSymbol, SymbolicQueryOutput>(prefix), words);
      return;
    }
    
    for (SymbolicMethodSymbol a : this.inputs) {
      Word<SymbolicMethodSymbol> next = prefix.append(a);
      SymbolicQueryOutput out = this.model.getOutput(state, a);
      if (out.equals(SymbolicQueryOutput.OK)) {
        unroll(next, this.model.getSuccessor(state, a), words);
      } else {
        add(new DefaultQuery<SymbolicMethodSymbol, SymbolicQueryOutput>(next), words);
      }      
    }
  }

  private void add(DefaultQuery<SymbolicMethodSymbol, SymbolicQueryOutput> q,
          Collection<DefaultQuery<SymbolicMethodSymbol, SymbolicQueryOutput>> words) {

    if (this.filter == null) {
      words.add(q);
      return;
    }
    
    DefaultQuery<SymbolicMethodSymbol, Boolean> test = 
            new DefaultQuery<>(q.getInput());
    
    this.filter.processQueries(Collections.singleton(test));
    
    if (test.getOutput()) {
      words.add(q);      
    }
  }
  
  /**
   * @return the k
   */
  public int getCurrentK() {
    return k;
  }
  
  private boolean deepEnough() {
    return (kMax > 0 ) && (k > kMax);
  }

  @Override
  public void logStatistics() {
    logger.info("EQ Test depth completed: " + (k-1));
    logger.info("EQ Test max depth: " + kMax);
  }
  
}
