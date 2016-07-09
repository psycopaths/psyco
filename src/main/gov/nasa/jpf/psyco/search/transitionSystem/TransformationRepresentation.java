/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.transitionSystem;

import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.api.ValuationEntry;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.psyco.exceptions.RenamingException;
import gov.nasa.jpf.psyco.search.region.SymbolicEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author mmuesly
 */
public class TransformationRepresentation {
  private List<Variable<?>> oldNames;
  private List<Variable<?>> primeNames;
  private Expression expression;
  private SymbolicEntry newState;

  private Valuation transitionResult;
  private boolean applied;
  private boolean errorTransition;

  private StringBuilder reachedErrors;
  
  public TransformationRepresentation(){
    oldNames = new ArrayList<Variable<?>>();
    primeNames = new ArrayList<Variable<?>>();
    expression = null;
    transitionResult = null;
    applied = false;
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
  
  public void setApplied(boolean applied){
    this.applied = applied;
  }
  public boolean isApplied(){
    return applied;
  }
  
  public void addError(String errorName, int depth){
    String errorString = "In :" + depth 
            + " reached the error: " +errorName +"\n";
    reachedErrors.append(errorString);

  }
  public String getErrorsAsString(){
    return reachedErrors.toString();
  }

  public StringBuilder getErrors(){
    return reachedErrors;
  }

  public SymbolicEntry getNewState() {
    return newState;
  }

  public void setNewState(SymbolicEntry newState) {
    this.newState = newState;
  }
  public boolean isErrorTransition() {
    return errorTransition;
  }

  public void setErrorTransition(boolean errorTransition) {
    this.errorTransition = errorTransition;
  }
}
