/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.datastructures.region;

import gov.nasa.jpf.psyco.search.datastructures.state.SymbolicState;
import gov.nasa.jpf.psyco.search.datastructures.state.SymbolicEntry;
import gov.nasa.jpf.constraints.api.Valuation;

/**
 *
 * @author mmuesly
 */
public class SymbolicRegion extends Region<SymbolicEntry, SymbolicState>{

  public SymbolicRegion(){
    super();
  }
  
  public SymbolicRegion(Valuation initValuation){
    super(initValuation);
  }

  @Override
  public void addInitialValuation(Valuation initValuation) {
    SymbolicState initState = new SymbolicState(initValuation);
    put("initState", initState);
  }

  @Override
  public SymbolicRegion createNewRegion(){
    return new SymbolicRegion();
  }

}