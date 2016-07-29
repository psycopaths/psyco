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
package gov.nasa.jpf.psyco;

import gov.nasa.jpf.psyco.alphabet.AlphabetRefiner;
import de.learnlib.oracles.DefaultQuery;
import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.constraints.util.MixedParamsException;
import gov.nasa.jpf.jdart.termination.TerminationStrategy;
import gov.nasa.jpf.psyco.learnlib.LStar;
import gov.nasa.jpf.psyco.learnlib.SymbolicEquivalenceTest;
import gov.nasa.jpf.psyco.learnlib.SymbolicExecutionOracle;
import gov.nasa.jpf.psyco.alphabet.SymbolicMethodAlphabet;
import gov.nasa.jpf.psyco.alphabet.SymbolicMethodSymbol;
import gov.nasa.jpf.psyco.exceptions.RefinementNeeded;
import gov.nasa.jpf.psyco.exceptions.Terminate;
import gov.nasa.jpf.psyco.learnlib.SymbolicQueryOutput;
import gov.nasa.jpf.psyco.learnlib.ThreeValuedOracle;
import net.automatalib.automata.transout.MealyMachine;

/**
 *
 * @author falkhowar
 */
public class InterfaceGenerator {
  
  private final ConstraintSolver solver;
  
  private final SymbolicMethodAlphabet inputs;
  
  private final SymbolicExecutionOracle seOracle;
  
  private ThreeValuedOracle mqOrcale;
  
  private final SymbolicEquivalenceTest eqTest;
    
  private final AlphabetRefiner refiner;
  
  private LStar lstar = null;

  private MealyMachine<?, SymbolicMethodSymbol, ?, SymbolicQueryOutput> model = null;

  public InterfaceGenerator(DefaultOracleProvider provider, PsycoConfig pconf,
          SymbolicEquivalenceTest eqTest) {
    this.inputs = provider.getInputs();
    this.seOracle = provider.getSymbolicExecutionOracle();
    this.mqOrcale = provider.getThreeValuedOracle();
    this.eqTest = eqTest;
    this.solver = pconf.getConstraintSolver();
    this.refiner = new AlphabetRefiner(seOracle, inputs, solver);
    
  }
  
  public MealyMachine<?, SymbolicMethodSymbol, ?, SymbolicQueryOutput> generateInterface() {    
    while (true) {
      try {
        learnModel();
        break;
      } catch (Terminate t) {
        break;
      } catch (MixedParamsException mp) {
        System.err.println("Mixed parameter: " + mp);
        break;
      } catch (RefinementNeeded r) {
        boolean refined = this.refiner.refine(r.getQuery().getInput());
        if (!refined) {
          throw new IllegalStateException("Expected a refinement but did not happen");
        }
        continue;
      }
    }
    return this.model;
  }
      
  private void learnModel() {
    this.lstar = new LStar(inputs, mqOrcale);    
    this.lstar.startLearning();    
    while (true) {    
      this.model = this.lstar.getHypothesisModel();
      DefaultQuery<SymbolicMethodSymbol, SymbolicQueryOutput> ce = 
              this.eqTest.findCounterExample(this.model, this.inputs);
      
      if (ce == null) {
        break;
      }
      
      this.lstar.refineHypothesis(ce);
    }    
  }
    
}
