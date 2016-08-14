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
package gov.nasa.jpf.psyco.oracles;

import de.learnlib.api.Query;
import gov.nasa.jpf.psyco.alphabet.SymbolicMethodSymbol;
import gov.nasa.jpf.psyco.learnlib.SymbolicExecutionOracle;
import gov.nasa.jpf.psyco.learnlib.SymbolicExecutionResult;
import gov.nasa.jpf.psyco.learnlib.SymbolicQueryOutput;
import gov.nasa.jpf.psyco.learnlib.ThreeValuedOracle;
import java.util.ArrayList;
import java.util.Collection;
import net.automatalib.words.Word;

public class SymbolicExecutionOracleWrapper implements ThreeValuedOracle {

  private static class WrapperQuery extends Query<SymbolicMethodSymbol, SymbolicExecutionResult> {
    
    private final Query<SymbolicMethodSymbol, SymbolicQueryOutput> query;

    public WrapperQuery(Query<SymbolicMethodSymbol, SymbolicQueryOutput> query) {
      this.query = query;
    }

    @Override
    public Word<SymbolicMethodSymbol> getPrefix() {
      return query.getPrefix();
    }

    @Override
    public Word<SymbolicMethodSymbol> getSuffix() {
      return query.getSuffix();
    }

    @Override
    public void answer(SymbolicExecutionResult o) {
      query.answer(new SymbolicQueryOutput(o));
    }
   
  }
  
  private final SymbolicExecutionOracle oracle;

  public SymbolicExecutionOracleWrapper(SymbolicExecutionOracle oracle) {
    this.oracle = oracle;
  }
 
  @Override
  public void processQueries(Collection<? extends Query<SymbolicMethodSymbol, SymbolicQueryOutput>> clctn) {
    ArrayList<WrapperQuery> queries = new ArrayList<>();
    for (Query<SymbolicMethodSymbol, SymbolicQueryOutput> q : clctn) {
      queries.add(new WrapperQuery(q));
    }
    this.oracle.processQueries(queries);
  }
}
