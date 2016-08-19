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
package gov.nasa.jpf.psyco.search.util.region;

import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.constraints.api.ValuationEntry;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.psyco.search.datastructures.region.EnumerativeRegion;
import gov.nasa.jpf.psyco.search.datastructures.state.EnumerativeState;
import gov.nasa.jpf.psyco.search.datastructures.region.Region;
import java.util.HashMap;
import java.util.List;

public class EnumerativeRegionUtil
        extends RegionUtil<EnumerativeState, EnumerativeRegion> {

  public EnumerativeRegionUtil(ConstraintSolver solver) {
    super(solver);
  }

  @Override
  public EnumerativeRegion rename(
          Region<?, EnumerativeState> region,
          List<Variable<?>> primeNames, List<Variable<?>> variableNames) {
    EnumerativeRegion toBeRenamed = (EnumerativeRegion) region;
    EnumerativeRegion renamedRegion = toBeRenamed.createNewRegion();
    HashMap<Variable, Variable> names =
            createHashMap(primeNames, variableNames);
    for (String key : region.keySet()) {
      EnumerativeState state = region.get(key);
      state = renameState(state, names);
      renamedRegion.put(key, state);
    }
    return renamedRegion;
  }

  private EnumerativeState renameState(EnumerativeState state,
          HashMap<Variable, Variable> nameReplacements) {
    EnumerativeState renamedState = new EnumerativeState();
    for (ValuationEntry entry : state) {
      Variable newVar
              = nameReplacements.getOrDefault(entry.getVariable(),
                      entry.getVariable());
      renamedState.add(new ValuationEntry(newVar, entry.getValue()));
    }
    return renamedState;
  }

  private HashMap<Variable, Variable> createHashMap(
          List<Variable<?>> primeNames, List<Variable<?>> variableNames) {
    HashMap<Variable, Variable> resultMap = new HashMap<>();
    for (int i = 0; i < primeNames.size(); i++) {
      resultMap.put(primeNames.get(i), variableNames.get(i));
    }
    return resultMap;
  }

}