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
import gov.nasa.jpf.jdart.termination.TerminationStrategy;
import gov.nasa.jpf.psyco.learnlib.LStar;
import gov.nasa.jpf.psyco.learnlib.SymbolicEquivalenceTest;
import gov.nasa.jpf.psyco.learnlib.SymbolicExecutionOracle;
import gov.nasa.jpf.psyco.alphabet.SymbolicMethodAlphabet;
import gov.nasa.jpf.psyco.alphabet.SymbolicMethodSymbol;
import gov.nasa.jpf.psyco.exceptions.RefinementNeeded;
import gov.nasa.jpf.psyco.exceptions.Terminate;
import gov.nasa.jpf.psyco.filter.Cache;
import gov.nasa.jpf.psyco.filter.QueryLogger;
import gov.nasa.jpf.psyco.filter.ValidQueryFilter;
import gov.nasa.jpf.psyco.learnlib.SymbolicQueryOutput;
import gov.nasa.jpf.psyco.learnlib.ThreeValuedOracle;
import gov.nasa.jpf.psyco.oracles.RefinementCheckOracle;
import gov.nasa.jpf.psyco.oracles.SymbolicExecutionOracleWrapper;
import gov.nasa.jpf.psyco.oracles.TerminationCheckOracle;
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
    
  private final TerminationStrategy termination;
  
  private final AlphabetRefiner refiner;
  
  private LStar lstar = null;

  private MealyMachine<?, SymbolicMethodSymbol, ?, SymbolicQueryOutput> model = null;

  public InterfaceGenerator(SymbolicMethodAlphabet inputs, SymbolicExecutionOracle seOracle,  
          SymbolicEquivalenceTest eqTest, TerminationStrategy termination,
          ConstraintSolver solver) {
    this.inputs = inputs;
    this.seOracle = seOracle;
    this.eqTest = eqTest;
    this.termination = termination;
    this.solver = solver;
    this.refiner = new AlphabetRefiner(seOracle, inputs, solver);
    
    this.mqOrcale = buildOracle(this.seOracle);
  }
  
  public MealyMachine<?, SymbolicMethodSymbol, ?, SymbolicQueryOutput> generateInterface() {    
    while (true) {
      try {
        learnModel();
        break;
      } catch (Terminate t) {
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
  
  private String getTerminationReason() {
    return this.termination.getReason();
  }
  
  private ThreeValuedOracle buildOracle(SymbolicExecutionOracle back) {
    // wrap back
    SymbolicExecutionOracleWrapper wrapper = new
            SymbolicExecutionOracleWrapper(back);
    
    // add logger
    QueryLogger logger = new QueryLogger(wrapper);
    
    // termination and refinement filters
    TerminationCheckOracle term = 
            new TerminationCheckOracle(this.termination, logger);
    
    RefinementCheckOracle refine = new RefinementCheckOracle(term);
    
    // cache + prefixclosure
    Cache cache = new Cache(refine);
    
    ValidQueryFilter valid = new ValidQueryFilter(cache);
    
    return valid;
  }
  
}
