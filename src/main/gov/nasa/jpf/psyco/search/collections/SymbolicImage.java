/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.collections;

import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.expressions.Constant;
import gov.nasa.jpf.constraints.expressions.NumericBooleanExpression;
import gov.nasa.jpf.constraints.expressions.PropositionalCompound;
import gov.nasa.jpf.psyco.search.region.SymbolicEntry;
import gov.nasa.jpf.psyco.search.region.SymbolicRegion;
import gov.nasa.jpf.psyco.search.region.SymbolicState;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mmuesly
 */
public class SymbolicImage extends StateImage{
  private SymbolicRegion reachableStates;
  private SymbolicRegion newStates;
  private SymbolicRegion previousNewStates;

  public SymbolicImage(SymbolicRegion reachableStates){
    super();
    this.reachableStates = reachableStates;
  }

  public SymbolicImage(SymbolicRegion reachableStates, StringBuilder errors, int depth){
    this.errors = errors;
    this.reachableStates = reachableStates;
    this.depth = depth;
  }

  public SymbolicRegion getReachableStates() {
    return reachableStates;
  }

  public void setReachableStates(SymbolicRegion reachableStates) {
    this.reachableStates = reachableStates;
  }

  public SymbolicRegion getNewStates() {
    if(this.newStates == null){
      return new SymbolicRegion();
    }
    return this.newStates;
  }

  public void setNewStates(SymbolicRegion newStates) {
    this.newStates = newStates;
  }

  public void addNewStates(SymbolicRegion newStates){
    if(this.newStates == null){
      this.newStates = newStates;
    }else{
      this.newStates.putAll(newStates);
    }
  }

  public SymbolicRegion getPreviousNewStates() {
    if(this.previousNewStates == null){
      return new SymbolicRegion();
    }
    return this.previousNewStates;
  }

  public void setPreviousNewStates(SymbolicRegion previousNewStates) {
    this.previousNewStates = previousNewStates;
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

//  public String toCSV(){
//    SymbolicState state = reachableStates.get("initState");
//    
//    String csv = "cLAS, cEDS, cLSAM_DESCENT, cStage2, cCM, cStage1, cLSAM_ASCENT, cSM\n";
//    for(SymbolicState state: reachableStates.values()){
//      String[] values = new String[8];
//      for(SymbolicEntry entry: state){
//        if(entry.getVariable().getName().endsWith("cLAS")){
//          values[0] = getValue(entry.getValue());
//        }
//        if(entry.getVariable().getName().endsWith("cEDS")){
//          values[1] = getValue(entry.getValue());
//        }
//        if(entry.getVariable().getName().endsWith("cLSAM_DESCENT")){
//          values[2] = getValue(entry.getValue());
//        }
//        if(entry.getVariable().getName().endsWith("cStage2")){
//          values[3] = getValue(entry.getValue());
//        }
//        if(entry.getVariable().getName().endsWith("cCM")){
//          values[4] = getValue(entry.getValue());
//        }
//        if(entry.getVariable().getName().endsWith("cStage1")){
//          values[5] = getValue(entry.getValue());
//        }
//        if(entry.getVariable().getName().endsWith("cLSAM_ASCENT")){
//          values[6] = getValue(entry.getValue());
//        }
//        if(entry.getVariable().getName().endsWith("cSM")){
//          values[7] = getValue(entry.getValue());
//        }
//      }
//      for(String value: values){
//        csv+=value +",";
//      }
//      csv = csv.substring(0, csv.length() - 1) + "\n";
//    }
//    return csv;
//  }

  private String getValue(Expression expr){
    if(expr instanceof PropositionalCompound){
      Expression right = ((PropositionalCompound) expr).getRight();
      return getValue(right);
    }
    if(expr instanceof NumericBooleanExpression){
      Expression right = ((NumericBooleanExpression) expr).getRight();
      return getValue(right);
    }
    if(expr instanceof Constant){
      return expr.toString();
    }
    System.out.println("gov.nasa.jpf.psyco.search.collections.SymbolicImage.getValue()");
    System.out.println(expr.getClass() + " value: " + expr.toString());
    return null;
  }
}
