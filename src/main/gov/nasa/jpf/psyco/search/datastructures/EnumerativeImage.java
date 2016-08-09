/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.datastructures;

import gov.nasa.jpf.psyco.search.region.EnumerativeRegion;
import gov.nasa.jpf.psyco.search.region.ValuationRegion;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mmuesly
 */
public class EnumerativeImage extends StateImage{
  private EnumerativeRegion reachableStates, previousNewStates,newStates;
  
  public EnumerativeImage(EnumerativeRegion region){
    super();
    this.reachableStates = region;
  }

  public EnumerativeImage(EnumerativeRegion region, StringBuilder errors, int depth){
    this.reachableStates = region;
  }
  
  public EnumerativeRegion getReachableStates(){
    return reachableStates;
  }
  
  public void setReachableStates(EnumerativeRegion reachableRegion){
          this.reachableStates = reachableRegion;
  }

  public EnumerativeRegion getNewStates(){
    return newStates;
  }

  public void setNewStates(EnumerativeRegion newStates){
    this.newStates = newStates;
  }

  public void addNewStates(EnumerativeRegion newStates){
    if(this.newStates == null){
      this.newStates = this.newStates;
    } else{
      this.newStates.putAll(newStates);
    }
  }

   public EnumerativeRegion getPreviousNewStates() {
    if(this.previousNewStates == null){
      return new EnumerativeRegion();
    }
    return this.previousNewStates;
  }

  public void setPreviousNewStates(EnumerativeRegion previousNewStates) {
    this.previousNewStates = previousNewStates;
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
    searchResultString.append("There are: " + reachableStates.size() 
            + " states reachable\n");
    searchResultString.append("The reachable states are:\n");
    try {
      reachableStates.print(searchResultString);
    } catch (IOException ex) {
      Logger.getLogger("psyco")
              .log(Level.SEVERE, null, ex);
    }
    searchResultString.append("Further the following errors are reached:\n");
    searchResultString.append(errors);
    searchResultString.append("\niteration image end\n");
  }
}
