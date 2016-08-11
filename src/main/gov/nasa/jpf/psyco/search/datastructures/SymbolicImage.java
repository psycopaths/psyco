/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.datastructures;

import gov.nasa.jpf.psyco.search.region.SymbolicRegion;

/**
 *
 * @author mmuesly
 */
public class SymbolicImage extends StateImage<SymbolicRegion>{

  public SymbolicImage(SymbolicRegion reachableStates){
    super(reachableStates);
  }

  public SymbolicImage(SymbolicRegion reachableStates, StringBuilder errors, int depth){
    super(reachableStates, errors, depth);
  }

  @Override
  public SymbolicRegion getPreviousNewStates() {
    if(this.previousNewStates == null){
      return new SymbolicRegion();
     
    }
    return this.previousNewStates;
  }
}
