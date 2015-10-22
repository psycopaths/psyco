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

import de.learnlib.api.MembershipOracle;
import de.learnlib.api.Query;
import gov.nasa.jpf.jdart.termination.TerminationStrategy;
import gov.nasa.jpf.psyco.alphabet.SymbolicMethodSymbol;
import gov.nasa.jpf.psyco.exceptions.Terminate;
import gov.nasa.jpf.psyco.filter.ThreeValuedFilter;
import gov.nasa.jpf.psyco.learnlib.SymbolicQueryOutput;
import gov.nasa.jpf.psyco.learnlib.ThreeValuedOracle;
import java.util.Collection;
import java.util.Collections;

public class TerminationCheckOracle implements ThreeValuedFilter, ThreeValuedOracle {
  
  private final TerminationStrategy termination;
  
  private MembershipOracle<SymbolicMethodSymbol, SymbolicQueryOutput> oracle;

  public TerminationCheckOracle(TerminationStrategy termination, 
          MembershipOracle<SymbolicMethodSymbol, SymbolicQueryOutput> oracle) {
    this.termination = termination;
    this.oracle = oracle;
  }

  @Override
  public void setNext(MembershipOracle<SymbolicMethodSymbol, SymbolicQueryOutput> mo) {
    this.oracle = mo;
  }

  @Override
  public void processQueries(Collection<? extends Query<SymbolicMethodSymbol, SymbolicQueryOutput>> clctn) {
    for (Query<SymbolicMethodSymbol, SymbolicQueryOutput> query : clctn) {
      if (termination.isDone()) {
        throw new Terminate(termination);
      }
      this.oracle.processQueries(Collections.singleton(query));
    }
  }
}
