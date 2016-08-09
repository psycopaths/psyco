/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.datastructures;

import gov.nasa.jpf.psyco.search.region.Region_old;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mmuesly
 */
public class IterationImage<R extends Region_old> extends StateImage{
  private R reachableStates;
  private StringBuilder errors;
  private int depth;

  public IterationImage(){
    errors = new StringBuilder();
    depth = 0;
  }
  public IterationImage(R reachableStates){
    this();
    this.reachableStates = reachableStates;
  }
  
  public IterationImage(R reachableStates, StringBuilder errors, int depth){
    this.errors = errors;
    this.reachableStates = reachableStates;
    this.depth = depth;
  }
  
  public R getReachableStates() {
    return reachableStates;
  }

  public void setReachableStates(R reachableStates) {
    this.reachableStates = reachableStates;
  }

  public int getDepth() {
    return depth;
  }

  public void setDepth(int depth) {
    this.depth = depth;
  }
  

  public void print(Appendable searchResultString) throws IOException {
    searchResultString.append("This is the iteration image of depth: ");
    searchResultString.append(Integer.toString(depth));
    searchResultString.append("\n");
    searchResultString.append(
            "In case this iteration image is the search result:\n");
    searchResultString.append("A good choice for k in Psyco would be k = ");
    searchResultString.append(Integer.toString(depth));
    searchResultString.append("\n");
    searchResultString.append("The reachable states are:\n");
    try {
      reachableStates.print(searchResultString);
    } catch (IOException ex) {
      Logger.getLogger("psyco")
              .log(Level.SEVERE, null, ex);
    }
    searchResultString.append("Further the following errors are reached:\n");
    searchResultString.append(errors);
    searchResultString.append("iteration image end\n");
  }
}
