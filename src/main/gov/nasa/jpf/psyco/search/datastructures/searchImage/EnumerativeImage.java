/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.datastructures.searchImage;

import gov.nasa.jpf.psyco.search.datastructures.region.EnumerativeRegion;

/**
 *
 * @author mmuesly
 */
public class EnumerativeImage extends StateImage<EnumerativeRegion>{

  public EnumerativeImage(EnumerativeRegion region){
    super(region);
  }

  public EnumerativeImage(EnumerativeRegion region, StringBuilder errors, int depth){
    super(region, errors, depth);
  }

  @Override
   public EnumerativeRegion getPreviousNewStates() {
    if(this.previousNewStates == null){
      return new EnumerativeRegion();
    }
    return this.previousNewStates;
  }
}
