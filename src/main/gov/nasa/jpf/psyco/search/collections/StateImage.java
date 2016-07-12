/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.collections;

import gov.nasa.jpf.psyco.search.region.Region;
import gov.nasa.jpf.psyco.search.region.SymbolicRegion;

/**
 *
 * @author mmuesly
 */
public abstract class StateImage {
  protected int depth = 0;
  protected StringBuilder errors = new StringBuilder();

  public int getDepth() {
    return depth;
  }

  public void setDepth(int depth) {
    this.depth = depth;
  }

  public void increaseDepth(int amount){
    this.depth += amount;
    errors.append("\n");
  }
  
}
