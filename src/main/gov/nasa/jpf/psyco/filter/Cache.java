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

import de.learnlib.api.MembershipOracle;
import de.learnlib.api.Query;
import de.learnlib.oracles.DefaultQuery;
import gov.nasa.jpf.psyco.alphabet.SymbolicMethodSymbol;
import gov.nasa.jpf.psyco.learnlib.SymbolicQueryOutput;
import gov.nasa.jpf.psyco.learnlib.ThreeValuedOracle;
import java.util.Collection;
import java.util.Collections;
import net.automatalib.words.Word;

public class Cache implements ThreeValuedFilter, ThreeValuedOracle {
  
  private final MemoizeTable table = new MemoizeTable();
  
  private MembershipOracle<SymbolicMethodSymbol, SymbolicQueryOutput> oracle;

  public Cache(ThreeValuedOracle oracle) {
    this.oracle = oracle;
  }
  
  
  @Override
  public void processQueries(
          Collection<? extends 
                  Query<SymbolicMethodSymbol, SymbolicQueryOutput>> clctn) {

    for (Query<SymbolicMethodSymbol, SymbolicQueryOutput> q : clctn) {
      processQuery(q);
    }
  }

  private void processQuery(Query<SymbolicMethodSymbol,
          SymbolicQueryOutput> q) {
   String[] test = queryToString(q.getInput());
   SymbolicQueryOutput result = table.getSimulatedResult(test);
   if (result == null) {
     DefaultQuery<SymbolicMethodSymbol, SymbolicQueryOutput> defq =
            new DefaultQuery<>(q.getInput());
     
     this.oracle.processQueries(Collections.singleton(defq));
     
     result = defq.getOutput();
     table.setResult(test, result);
   } 
   q.answer(result);   
  }

  @Override
  public void setNext(MembershipOracle<SymbolicMethodSymbol,
          SymbolicQueryOutput> mo) {
    this.oracle = mo;
  }

  private String[] queryToString(Word<SymbolicMethodSymbol> query) {
    String[] ret = new String[query.length()];
    int i = 0;
    for (SymbolicMethodSymbol s : query) {
      ret[i++] = s.getId() + "_" + i;
    }
    return ret;
  }
}