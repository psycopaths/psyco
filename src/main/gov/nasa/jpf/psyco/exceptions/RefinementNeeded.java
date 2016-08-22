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
package gov.nasa.jpf.psyco.exceptions;

import de.learnlib.oracles.DefaultQuery;
import gov.nasa.jpf.psyco.alphabet.SymbolicMethodSymbol;
import gov.nasa.jpf.psyco.learnlib.SymbolicQueryOutput;

public class RefinementNeeded extends RuntimeException {
  
  private final DefaultQuery<SymbolicMethodSymbol, SymbolicQueryOutput> query;

  public RefinementNeeded(DefaultQuery<SymbolicMethodSymbol,
          SymbolicQueryOutput> query) {
    this.query = query;
  }

  public DefaultQuery<SymbolicMethodSymbol, SymbolicQueryOutput> getQuery() {
    return query;
  }
   
}
