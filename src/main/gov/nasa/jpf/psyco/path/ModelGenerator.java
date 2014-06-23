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
package gov.nasa.jpf.psyco.path;

import gov.nasa.jpf.psyco.path.learnlib.PathSymbol;
import gov.nasa.jpf.psyco.path.learnlib.PathQueryOracle;
import de.learnlib.oracles.DefaultQuery;
import gov.nasa.jpf.psyco.path.learnlib.LStar;
import gov.nasa.jpf.psyco.exceptions.Terminate;
import gov.nasa.jpf.psyco.path.learnlib.PathEquivalenceTest;
import gov.nasa.jpf.psyco.path.learnlib.PathQueryOutput;
import net.automatalib.automata.transout.MealyMachine;
import net.automatalib.words.Alphabet;

/**
 *
 * @author falkhowar
 */
public class ModelGenerator {
  
  private final Alphabet<PathSymbol> inputs;
  
  private final PathQueryOracle oracle;
  
  private final PathEquivalenceTest eqTest;
      
  private LStar lstar = null;

  private MealyMachine<?, PathSymbol, ?, PathQueryOutput> model = null;

  public ModelGenerator(PathQueryOracle oracle, 
          PathEquivalenceTest eqTest, Alphabet<PathSymbol> inputs) {
    this.inputs = inputs;
    this.oracle = oracle;
    this.eqTest = eqTest;
  }
  
  public MealyMachine<?, PathSymbol, ?, PathQueryOutput> generateModel() {    
    while (true) {
      try {
        learnModel();
        break;
      } catch (Terminate t) {
        break;
      }
    }
    return this.model;
  }
      
  private void learnModel() {
    this.lstar = new LStar(inputs, oracle);    
    this.lstar.startLearning();    
    while (true) {    
      this.model = this.lstar.getHypothesisModel();
      DefaultQuery<PathSymbol, PathQueryOutput> ce = 
              this.eqTest.findCounterExample(this.model, this.inputs);
      
      if (ce == null) {
        break;
      }
      
      this.lstar.refineHypothesis(ce);
    }    
  }
    
}
