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
package gov.nasa.jpf.psyco.learnlib;

import gov.nasa.jpf.psyco.alphabet.SymbolicMethodAlphabet;
import gov.nasa.jpf.psyco.alphabet.SymbolicMethodSymbol;
import de.learnlib.algorithms.lstargeneric.ce.ObservationTableCEXHandlers;
import de.learnlib.algorithms.lstargeneric.closing.ClosingStrategies;
import de.learnlib.algorithms.lstargeneric.mealy.ClassicLStarMealy;
import java.util.ArrayList;
import java.util.List;
import net.automatalib.words.Word;

public class LStar extends 
        ClassicLStarMealy<SymbolicMethodSymbol, SymbolicQueryOutput> {
  
  public LStar(SymbolicMethodAlphabet alphabet, 
          ThreeValuedOracle oracle) {        
    super(alphabet, oracle, asSuffixes(alphabet), 
            ObservationTableCEXHandlers.RIVEST_SCHAPIRE,
            ClosingStrategies.CLOSE_FIRST);
  }
  
  private static List<Word<SymbolicMethodSymbol>> asSuffixes(
          SymbolicMethodAlphabet sigma) {
    List<Word<SymbolicMethodSymbol>> suffixes = new ArrayList<>();
    Word<SymbolicMethodSymbol> eps = Word.epsilon();
    suffixes.add(eps);
//    for (SymbolicMethodSymbol s : sigma) {
//      suffixes.add(Word.fromLetter(s));
//    }
    return suffixes;
  }
  
}
