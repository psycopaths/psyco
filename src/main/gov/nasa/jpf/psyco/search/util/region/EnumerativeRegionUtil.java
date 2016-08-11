/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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

/**
 *
 * @author mmuesly
 */
public class EnumerativeRegionUtil 
        extends RegionUtil<EnumerativeState, EnumerativeRegion>{

  public EnumerativeRegionUtil(ConstraintSolver solver) {
    super(solver);
  }

  @Override
  public EnumerativeRegion rename(Region<?, EnumerativeState> region, List<Variable<?>> primeNames, List<Variable<?>> variableNames) {
    EnumerativeRegion toBeRenamed = (EnumerativeRegion) region;
    EnumerativeRegion renamedRegion = toBeRenamed.createNewRegion();
    HashMap<Variable, Variable> names = createHashMap(primeNames, variableNames);
    for(String key: region.keySet()){
      EnumerativeState state = region.get(key);
      state = renameState(state, names);
      renamedRegion.put(key, state);
    }
    return renamedRegion;
  }
  private EnumerativeState renameState(EnumerativeState state, 
          HashMap<Variable, Variable> nameReplacements){
    EnumerativeState renamedState = new EnumerativeState();
    for(ValuationEntry entry: state){
      Variable newVar = 
              nameReplacements.getOrDefault(entry.getVariable(),
                      entry.getVariable());
      renamedState.add(new ValuationEntry(newVar, entry.getValue()));
    }
    return renamedState;
  }

  private HashMap<Variable, Variable> createHashMap(List<Variable<?>> primeNames, List<Variable<?>> variableNames) {
    HashMap<Variable, Variable> resultMap = new HashMap<>();
    for(int i = 0; i < primeNames.size(); i++){
      resultMap.put(primeNames.get(i), variableNames.get(i));
    }
    return resultMap;
  }
  
}
