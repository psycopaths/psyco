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
import java.util.ArrayList;
import java.util.Collection;

public abstract class MethodExecutionFilter implements 
        MembershipOracle.DFAMembershipOracle<SymbolicMethodSymbol> {

  private final MethodExecutionFilter next;

  public MethodExecutionFilter() {
    this.next = null;
  }

  public MethodExecutionFilter(MethodExecutionFilter next) {
    this.next = next;
  }
  
  @Override
  public final void processQueries(
          Collection<? extends Query<SymbolicMethodSymbol, Boolean>> clctn) {
    
    ArrayList<Query<SymbolicMethodSymbol, Boolean>> queries = new ArrayList<>();
    for (Query<SymbolicMethodSymbol, Boolean> q : clctn) {
      boolean test = evaluateQuery(q);
      if (!test) {
        q.answer(false);
        continue;
      }
      
      if (next == null) {
        q.answer(true);
        continue;
      }
      
      queries.add(q);
    }
    
    if (!queries.isEmpty()) {
      next.processQueries(queries);
    }
  }
  
  public abstract boolean evaluateQuery(Query<SymbolicMethodSymbol, Boolean> query); 
}