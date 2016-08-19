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
package gov.nasa.jpf.psyco.filter;

import de.learnlib.api.Query;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.psyco.alphabet.SymbolicMethodAlphabet;
import gov.nasa.jpf.psyco.alphabet.SymbolicMethodSymbol;
import gov.nasa.jpf.util.JPFLogger;
import gov.nasa.jpf.util.Pair;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.automatalib.words.Word;

public class PORFilter extends MethodExecutionFilter {

  private static final JPFLogger logger = JPF.getLogger("psyco");
 
  private final Map<String, Pair<BitSet,BitSet>> po = new HashMap<>(); 

  public PORFilter(Collection<String> methods, SymbolicMethodAlphabet inputs) {
    this(methods, inputs, null);
  }

  public PORFilter(Collection<String> methods, SymbolicMethodAlphabet inputs, 
          MethodExecutionFilter next) {
    super(next);
    initialize(methods, inputs);
  }
  

  @Override
  public boolean evaluateQuery(Query<SymbolicMethodSymbol, Boolean> query) {
    Word<SymbolicMethodSymbol> input = query.getInput();
    // constructor + 1 will always be executed
    if (input.size() < 3) {
      return false;
    }

    String lastName = input.getSymbol(1).getConcolicMethodConfig().getId();
    Pair<BitSet,BitSet> lastMethod = po.get(lastName);
    for (int i=2; i<input.size(); i++) {      
      String curName = input.getSymbol(i).getConcolicMethodConfig().getId();
      Pair<BitSet,BitSet> cur = po.get(curName); 

      if (cur != null && lastMethod != null) {
        if (curName.compareTo(lastName) < 0
                && !lastMethod._1.intersects(cur._2) // read-write
                && !lastMethod._2.intersects(cur._1) // write-read
                && !lastMethod._2.intersects(cur._2) // write-write
              ) {
          return false;
        }
      }

      lastName = curName;
      lastMethod = cur;
    }
    return true;
  }

  private void initialize(Collection<String> mconfig, 
          SymbolicMethodAlphabet inputs) {
    
    logger.info("POR CONFIG: " + Arrays.toString(mconfig.toArray()));
    
    Map<String,Pair<BitSet,BitSet>> config = new HashMap<>();

    for (String line : mconfig) {    
      String[] conf = line.trim().split(",");      
      logger.info("line: " + Arrays.toString(conf));
      config.put( conf[0].trim(), new Pair<>( 
              makeSet(conf[1].trim()), makeSet(conf[2].trim()) ));
    }

    for (SymbolicMethodSymbol m : inputs) {
      
      String mName = m.getConcolicMethodConfig().getId();
      
      Pair<BitSet,BitSet> sets = config.get(mName);
      if (sets == null) {
        logger.warning("POR: Symbol " + m + " has no POR configuration!");
        continue;
      }
      
      logger.fine("bit-sets for " + m + "  :  " 
              + sets._1.toString() + " : " + sets._2.toString());
      this.po.put(mName, sets);
    }   
  }

  private BitSet makeSet(String bits) {    
    BitSet set = new BitSet(bits.length());
    for (int i=0;i<bits.length();i++) {
      set.set(i,  bits.charAt(i) == '1');
    }
    return set;
  }
}