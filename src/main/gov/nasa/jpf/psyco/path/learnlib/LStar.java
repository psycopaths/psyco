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
package gov.nasa.jpf.psyco.path.learnlib;

import de.learnlib.algorithms.lstargeneric.ce.ObservationTableCEXHandlers;
import de.learnlib.algorithms.lstargeneric.closing.ClosingStrategies;
import de.learnlib.algorithms.lstargeneric.mealy.ClassicLStarMealy;
import java.util.ArrayList;
import java.util.List;
import net.automatalib.words.Alphabet;
import net.automatalib.words.Word;

/**
 *
 * @author falkhowar
 */
public class LStar extends ClassicLStarMealy<PathSymbol, PathQueryOutput> {
  
  public LStar(Alphabet<PathSymbol> alphabet, PathQueryOracle oracle) {        
    super(alphabet, oracle, asSuffixes(alphabet), 
            ObservationTableCEXHandlers.RIVEST_SCHAPIRE,
            ClosingStrategies.CLOSE_FIRST);
  }
  
  private static List<Word<PathSymbol>> asSuffixes(Alphabet<PathSymbol> sigma) {
    List<Word<PathSymbol>> suffixes = new ArrayList<>();
    Word<PathSymbol> eps = Word.epsilon();
    suffixes.add(eps);
//    for (SymbolicMethodSymbol s : sigma) {
//      suffixes.add(Word.fromLetter(s));
//    }
    return suffixes;
  }
  
}
