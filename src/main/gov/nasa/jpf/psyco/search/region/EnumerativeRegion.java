/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.region;

import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.api.ValuationEntry;

/**
 *
 * @author mmuesly
 */
public class EnumerativeRegion extends Region<ValuationEntry, EnumerativeState>{

  public EnumerativeRegion(){
    super();
  }

  public EnumerativeRegion(Valuation initValuation){
    super(initValuation);
  }

  @Override
  public void addInitialValuation(Valuation initValuation) {
    EnumerativeState initState = new EnumerativeState (initValuation);
    this.put("init", initState);
  }

  @Override
  public EnumerativeRegion createNewRegion() {
    return new EnumerativeRegion();
  }

}
