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
