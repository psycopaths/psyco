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
import gov.nasa.jpf.jdart.termination.TerminationStrategy;
import gov.nasa.jpf.psyco.alphabet.SymbolicMethodAlphabet;
import gov.nasa.jpf.psyco.alphabet.SymbolicMethodSymbol;
import gov.nasa.jpf.psyco.exceptions.Terminate;
import gov.nasa.jpf.psyco.learnlib.SymbolicEquivalenceTest;
import gov.nasa.jpf.psyco.learnlib.SymbolicQueryOutput;
import gov.nasa.jpf.psyco.learnlib.ThreeValuedOracle;
import gov.nasa.jpf.psyco.oracles.TerminationCheckOracle;
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
  
  private int k = 2;
  
  private MealyMachine<Object, SymbolicMethodSymbol, ?, SymbolicQueryOutput> model;

  private final ThreeValuedOracle oracle;

  private final SymbolicMethodAlphabet inputs;
  
  public IncreasingDepthExhaustiveTest(ThreeValuedOracle oracle, 
          SymbolicMethodAlphabet inputs) {
    this.oracle = oracle;
    this.inputs = inputs;
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
        System.out.println("========================== Completed depth " + k);
        k++;
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
        System.out.println("================ CE: " + q.getInput() + 
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
      words.add(new DefaultQuery<SymbolicMethodSymbol, SymbolicQueryOutput>(prefix));
      return;
    }
    
    for (SymbolicMethodSymbol a : this.inputs) {
      Word<SymbolicMethodSymbol> next = prefix.append(a);
      SymbolicQueryOutput out = this.model.getOutput(state, a);
      if (out.equals(SymbolicQueryOutput.OK)) {
        unroll(next, this.model.getSuccessor(state, a), words);
      } else {
        words.add(new DefaultQuery<SymbolicMethodSymbol, SymbolicQueryOutput>(next));
      }      
    }
  }
  
}
