/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.datastructures.searchImage;

import gov.nasa.jpf.psyco.search.datastructures.region.Region;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mmuesly
 */
public abstract class StateImage<T extends Region> {
  protected int depth = 0;
  protected StringBuilder errors = new StringBuilder();
  protected T 
          reachableStates, newStates, previousNewStates;

  public StateImage(T reachableStates){
    this.reachableStates = reachableStates;
  }
  public StateImage(T reachableStates, StringBuilder errors, int depth){
    this.reachableStates = reachableStates;
    this.errors = errors;
    this.depth = depth;
  }
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
  public String reachableErrorsAsString(){
    return errors.toString();
  }

  public StringBuilder getErrors(){
    return errors;
  }

  public void setErrors(StringBuilder errors){
    this.errors = errors;
  }

  public void addError(String error, int depth){
    String errorString = "In: " + depth + " reached the error: " +error +"\n";
    errors.append(errorString);
  }

  public void addErrorInCurrentDepth(String error){
    addError(error, depth);
  }

  public T getReachableStates() {
    return (T) reachableStates;
  }

  public void setReachableStates(T reachableStates) {
    this.reachableStates = reachableStates;
  }

  public void setNewStates(T newStates) {
    this.newStates = newStates;
  }

  public T getNewStates(){
    return newStates;
  }

  public void addNewStates(T newStates){
    if(this.newStates == null){
      this.newStates = newStates;
    }else{
      this.newStates.putAll(newStates);
    }
  }

  public abstract T getPreviousNewStates();

  public void setPreviousNewStates(T previousNewStates) {
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
