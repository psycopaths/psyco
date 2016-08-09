/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.datastructures;

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
}
