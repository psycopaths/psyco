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
package gov.nasa.jpf.psyco.path.equivalence;

import de.learnlib.oracles.DefaultQuery;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.psyco.PsycoConfig;
import gov.nasa.jpf.psyco.exceptions.Terminate;
import gov.nasa.jpf.psyco.filter.ValidQueryFilter;
import gov.nasa.jpf.psyco.learnlib.SymbolicQueryOutput;
import gov.nasa.jpf.psyco.path.learnlib.PathEquivalenceTest;
import gov.nasa.jpf.psyco.path.learnlib.PathQueryOracle;
import gov.nasa.jpf.psyco.path.learnlib.PathQueryOutput;
import gov.nasa.jpf.psyco.path.learnlib.PathSymbol;
import gov.nasa.jpf.util.JPFLogger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import net.automatalib.automata.transout.MealyMachine;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;

/**
 *
 * @author falkhowar
 */
public class IncreasingDepthExhaustiveTest implements PathEquivalenceTest {
  
  private static final JPFLogger logger = JPF.getLogger("psyco");
  
  private int k = 2;
  
  private int kMax = -1;
  
  private MealyMachine<Object, PathSymbol, ?, PathQueryOutput> model;

  private final PathQueryOracle oracle;

  private final Alphabet<PathSymbol> inputs;
    
  public IncreasingDepthExhaustiveTest(PathQueryOracle oracle, 
          Alphabet<PathSymbol> inputs, PsycoConfig pconf) {
    this.oracle = oracle;
    this.inputs = inputs;
    this.kMax = pconf.getMaxDepth();
  }

  @Override
  public DefaultQuery<PathSymbol, PathQueryOutput> findCounterExample(
          MealyMachine<?, PathSymbol, ?, PathQueryOutput> a, 
          Collection<? extends PathSymbol> clctn) {

    this.model = (MealyMachine<Object, PathSymbol, ?, PathQueryOutput>)a;
    DefaultQuery<PathSymbol, PathQueryOutput> ce = null;
    
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
  
  private DefaultQuery<PathSymbol, PathQueryOutput> findCounterExampleAtDepthK() {
    Collection<DefaultQuery<PathSymbol, PathQueryOutput>> queries = unroll();
    for (DefaultQuery<PathSymbol, PathQueryOutput> q : queries) {      
      this.oracle.processQueries(Collections.singleton(q));
      PathQueryOutput refOut = 
              this.model.computeOutput(q.getInput()).lastSymbol();
      
      if (!refOut.equals(q.getOutput())) {
        logger.info("================ CE: " + q.getInput() + 
                " : " + refOut +" <> " + q.getOutput());
        return q;
      }
    } 
    return null;
  }

  private Collection<DefaultQuery<PathSymbol, PathQueryOutput>> unroll() {
    ArrayList<DefaultQuery<PathSymbol, PathQueryOutput>> ret = 
            new ArrayList<>();
    
    Object init = this.model.getInitialState();
    Word<PathSymbol> eps = Word.epsilon();
    unroll(eps, init, ret);
    return ret;
  }
  
  private void unroll(Word<PathSymbol> prefix, Object state,
          Collection<DefaultQuery<PathSymbol, PathQueryOutput>> words) {
    
    if (prefix.length() == k) {
      add(new DefaultQuery<PathSymbol, PathQueryOutput>(prefix), words);
      return;
    }
    
    for (PathSymbol a : this.inputs) {
      Word<PathSymbol> next = prefix.append(a);
      PathQueryOutput out = this.model.getOutput(state, a);
      if (out.equals(SymbolicQueryOutput.OK)) {
        unroll(next, this.model.getSuccessor(state, a), words);
      } else {
        add(new DefaultQuery<PathSymbol, PathQueryOutput>(next), words);
      }      
    }
  }

  private void add(DefaultQuery<PathSymbol, PathQueryOutput> q,
          Collection<DefaultQuery<PathSymbol, PathQueryOutput>> words) {

    if (PathQueryOracle.isValidWord(q.getInput())) {
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
