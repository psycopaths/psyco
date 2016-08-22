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
package gov.nasa.jpf.psyco.search.datastructures.region;

import gov.nasa.jpf.psyco.search.datastructures.state.SymbolicState;
import gov.nasa.jpf.psyco.search.datastructures.state.SymbolicEntry;
import gov.nasa.jpf.constraints.api.Valuation;

public class SymbolicRegion extends Region<SymbolicEntry, SymbolicState> {

  public SymbolicRegion() {
    super();
  }

  public SymbolicRegion(Valuation initValuation) {
    super(initValuation);
  }

  @Override
  public void addInitialValuation(Valuation initValuation) {
    SymbolicState initState = new SymbolicState(initValuation);
    put("initState", initState);
  }

  @Override
  public SymbolicRegion createNewRegion() {
    return new SymbolicRegion();
  }
}