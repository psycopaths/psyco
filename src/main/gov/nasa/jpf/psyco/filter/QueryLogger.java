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
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.psyco.alphabet.SymbolicMethodSymbol;
import gov.nasa.jpf.psyco.learnlib.SymbolicQueryOutput;
import gov.nasa.jpf.psyco.learnlib.ThreeValuedOracle;
import gov.nasa.jpf.util.JPFLogger;
import java.util.ArrayList;
import java.util.Collection;
import net.automatalib.words.Word;

public class QueryLogger implements ThreeValuedFilter, ThreeValuedOracle {

  private static class LoggingQuery extends Query<SymbolicMethodSymbol, SymbolicQueryOutput> {

    private static final JPFLogger logger = JPF.getLogger("psyco");

    private final Query<SymbolicMethodSymbol, SymbolicQueryOutput> query;

    public LoggingQuery(Query<SymbolicMethodSymbol, SymbolicQueryOutput> query) {
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
    public void answer(SymbolicQueryOutput o) {
      logger.finer("MQ: " + query.getInput() + " : " + o);
      query.answer(o);
    }

  }

  private MembershipOracle<SymbolicMethodSymbol, SymbolicQueryOutput> oracle;

  public QueryLogger(MembershipOracle<SymbolicMethodSymbol, SymbolicQueryOutput> oracle) {
    this.oracle = oracle;
  }

  @Override
  public void setNext(MembershipOracle<SymbolicMethodSymbol, SymbolicQueryOutput> mo) {
    this.oracle = mo;
  }

  @Override
  public void processQueries(Collection<? extends Query<SymbolicMethodSymbol, SymbolicQueryOutput>> clctn) {
    Collection<Query<SymbolicMethodSymbol, SymbolicQueryOutput>> lCol = new ArrayList<>();
    for (Query<SymbolicMethodSymbol, SymbolicQueryOutput> query : clctn) {
      lCol.add(new LoggingQuery(query));
    }
    this.oracle.processQueries(lCol);
  }

}
