/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search;

import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.psyco.exceptions.RenamingException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mmuesly
 */
public class Transition {
  private List<Variable<?>> oldNames;
  private List<Variable<?>> primeNames;
  private Expression expression;
  private Valuation transitionResult;
  private boolean success;
  private StringBuilder reachedErrors;
  public Transition(){
    oldNames = new ArrayList<Variable<?>>();
    primeNames = new ArrayList<Variable<?>>();
    expression = null;
    transitionResult = null;
    success = false;
    reachedErrors = new StringBuilder();
  }

  public Valuation getTransitionResult() {
    return transitionResult;
  }

  public void setTransitionResult(Valuation transitionResult) {
    this.transitionResult = transitionResult;
  }

  public Expression getExpression() {
    return expression;
  }

  public void setExpresion(Expression transition) {
    this.expression = transition;
  }
  
  public void addRenamingPair(Variable oldVar, Variable primeVar) 
          throws RenamingException{
    if(!oldNames.contains(oldVar)){
      oldNames.add(oldVar);
      primeNames.add(primeVar);
    }else{
      int index = oldNames.indexOf(oldVar);
      if(!primeNames.get(index).equals(primeVar)){
        throw new RenamingException("Cannot rename the same variables "
                + "to two names in one Iteration");
      }
    }
  }

  public List<Variable<?>> getOldNames() {
    return oldNames;
  }

  public List<Variable<?>> getPrimeNames() {
    return primeNames;
  }
  
  public void setSuccess(boolean success){
    this.success = success;
  }
  public boolean isSuccess(){
    return success;
  }
  
  public void addError(String errorName, int depth){
    reachedErrors.append("error in depth: ");
    reachedErrors.append(depth);
    reachedErrors.append(" error name: ");
    reachedErrors.append(errorName);
    reachedErrors.append("\n");
  }
  public String getErrors(){
    return reachedErrors.toString();
  }
}
