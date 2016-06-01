/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.jConstraintsExtension;

import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.api.ValuationEntry;
import gov.nasa.jpf.constraints.api.Variable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author mmuesly
 */
public class ValuationRegion extends Region<ValuationEntry>{
  private Set<ValuationEntry> region;

  public ValuationRegion(){
    region = new HashSet<>();
  }

  @Override
  public void add(ValuationEntry toAdd){
    region.add(toAdd);
  }

  @Override
  public List<ValuationEntry> getRegionEntries() {
    return new ArrayList<>(region);
  }

  @Override
  public Set<ValuationEntry> getValuesForEntry(Variable entry) {
    Set<ValuationEntry> result = new HashSet<>();
    for(ValuationEntry currentEntry: region){
      if(currentEntry.getVariable().equals(entry)){
        result.add(currentEntry);
      }
    }
    return result;
  }

  @Override
  public boolean isEmtpy() {
    return region.isEmpty();
  }
}
