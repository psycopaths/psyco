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
package gov.nasa.jpf.psyco;

import gov.nasa.jpf.psyco.alphabet.AlphabetRefiner;
import de.learnlib.oracles.DefaultQuery;
import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.constraints.util.MixedParamsException;
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

public class InterfaceGenerator {

  private final ConstraintSolver solver;

  private final SymbolicMethodAlphabet inputs;

  private final SymbolicExecutionOracle seOracle;

  private ThreeValuedOracle mqOrcale;

  private final SymbolicEquivalenceTest eqTest;

  private final AlphabetRefiner refiner;

  private LStar lstar = null;

  private MealyMachine<?, SymbolicMethodSymbol, ?, SymbolicQueryOutput> model = null;

  public InterfaceGenerator(DefaultOracleProvider provider, PsycoConfig pconf) {
    this.inputs = provider.getInputs();
    this.seOracle = provider.getSymbolicExecutionOracle();
    this.mqOrcale = provider.getThreeValuedOracle();
    this.eqTest = provider.getEqTest();
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
      DefaultQuery<SymbolicMethodSymbol, SymbolicQueryOutput> ce
              = this.eqTest.findCounterExample(this.model, this.inputs);

      if (ce == null) {
        break;
      }

      this.lstar.refineHypothesis(ce);
    }
  }
}
