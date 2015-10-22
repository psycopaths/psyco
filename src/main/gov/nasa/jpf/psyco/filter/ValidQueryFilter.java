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
import gov.nasa.jpf.psyco.alphabet.SymbolicMethodSymbol;
import gov.nasa.jpf.psyco.learnlib.SymbolicQueryOutput;
import gov.nasa.jpf.psyco.learnlib.ThreeValuedOracle;
import java.util.ArrayList;
import java.util.Collection;
import net.automatalib.words.Word;

public class ValidQueryFilter implements ThreeValuedFilter, ThreeValuedOracle {

  private MembershipOracle<SymbolicMethodSymbol, SymbolicQueryOutput> oracle;

  public ValidQueryFilter(ThreeValuedOracle oracle) {
    this.oracle = oracle;
  }
  
  @Override
  public void processQueries(Collection<? extends Query<SymbolicMethodSymbol, SymbolicQueryOutput>> clctn) {
    ArrayList<Query<SymbolicMethodSymbol, SymbolicQueryOutput>> queries
            = new ArrayList<>();
    
    for (Query<SymbolicMethodSymbol, SymbolicQueryOutput> q : clctn) {
      if (isValid(q.getInput())) {
        queries.add(q);
      }
      else {
        q.answer(SymbolicQueryOutput.ERROR);
      }
    }
    this.oracle.processQueries(queries);
  }

  private boolean isValid(Word<SymbolicMethodSymbol> input) {
    int pos = 0;
    for (SymbolicMethodSymbol s : input) {
      if (pos == 0 ^ s.isConstructor()) {
        return false;
      }
      pos++;
    }
    return true;
  }

  @Override
  public void setNext(MembershipOracle<SymbolicMethodSymbol, SymbolicQueryOutput> mo) {
    this.oracle = mo;
  }
  
}
