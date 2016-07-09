/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.transitionSystem;

import gov.nasa.jpf.constraints.api.ConstraintSolver.Result;
import static gov.nasa.jpf.constraints.api.ConstraintSolver.Result.SAT;
import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.expressions.Constant;
import gov.nasa.jpf.constraints.expressions.NumericBooleanExpression;
import gov.nasa.jpf.constraints.expressions.NumericComparator;
import gov.nasa.jpf.constraints.util.ExpressionUtil;
import gov.nasa.jpf.psyco.search.SolverInstance;
import gov.nasa.jpf.psyco.search.collections.NameMap;
import gov.nasa.jpf.psyco.search.collections.StateImage;
import gov.nasa.jpf.psyco.search.collections.SymbolicImage;
import gov.nasa.jpf.psyco.search.collections.VariableReplacementMap;
import gov.nasa.jpf.psyco.search.region.SymbolicEntry;
import gov.nasa.jpf.psyco.search.region.SymbolicRegion;
import gov.nasa.jpf.psyco.search.region.SymbolicState;
import gov.nasa.jpf.psyco.search.util.HelperMethods;
import java.util.logging.Logger;

/**
 *
 * @author mmuesly
 */
public class SymbolicTransitionHelper implements TransitionHelper{
  SolverInstance solver = SolverInstance.getInstance();
  Logger logger = Logger.getLogger("psyco");
  @Override
  public StateImage applyTransition(StateImage image, Transition transition) {
    if(image instanceof SymbolicImage){
      SymbolicRegion newRegion = new SymbolicRegion();
      SymbolicImage currentState = (SymbolicImage) image;
      for(SymbolicState state: currentState.getPreviousNewStates().values()){
        if(satisfiesGuardCondition(state, transition)){
          SymbolicState newState = executeTransition(state, transition);
          newRegion.put(HelperMethods.getUniqueStateName(), newState);
        }
      }
      currentState.addNewStates(newRegion);
      return currentState;
    }
    return null;
  }

  private SymbolicState executeTransition(SymbolicState state,
          Transition transition) {
    Expression newValue = null;
    SymbolicState resultingState = new SymbolicState();
    if(!transition.isGuardSymbolicConstant()){
      newValue = transition.getGuardCondition();
    }
    for(SymbolicEntry entry: state){
      SymbolicEntry primeEntry =
              executeTransitionOnEntry(entry, transition, newValue);
      resultingState.add(primeEntry);
    }
    return resultingState;
  }

  private SymbolicEntry executeTransitionOnEntry(SymbolicEntry entry,
          Transition transition, Expression prefix){
    Variable oldVariable = entry.getVariable(),
            primeVariable = createPrimeVariable(oldVariable);
    Expression transitionEffekt = transition.getTransitionEffect(oldVariable);
    Expression newValue = prefix;
    if(isStutterEffektForVariable(oldVariable, transitionEffekt)){
      newValue = createStutterTransition(oldVariable,
                          primeVariable, entry.getValue());
    }else if(isConstantAssignment(transitionEffekt)){
      Expression assignment = 
              createConstantAssignment(primeVariable, transitionEffekt);
      newValue = appendNewValue(newValue, assignment);
    }else if(transitionEffekt != null){
      Expression resultingValue = createResultValue(oldVariable, primeVariable,
              transitionEffekt, entry.getValue());
      newValue = appendNewValue(newValue, resultingValue);
    }
    return new SymbolicEntry(primeVariable, newValue);
  }

  private boolean isStutterEffektForVariable(Variable oldVariable,
          Expression transitionEffekt){
    return transitionEffekt instanceof Variable 
            && transitionEffekt.equals(oldVariable);
  }
  private Expression createStutterTransition(Variable oldVariable,
          Variable primeVariable, Expression<Boolean> value) {
    NameMap rename = new NameMap();
    rename.mapNames(oldVariable.getName(), primeVariable.getName());
    return ExpressionUtil.renameVars(value, rename);
  }

  private boolean isConstantAssignment(Expression transitionEffekt) {
    return transitionEffekt instanceof Constant;
  }

  private Expression createConstantAssignment(Variable primeVariable,
          Expression transitionEffekt) {
    return NumericBooleanExpression.create(primeVariable,
                    NumericComparator.EQ, transitionEffekt);
  }

  private Expression createResultValue(Variable oldVariable, 
          Variable primeVariable,
          Expression transitionEffekt, Expression oldValue){
    //TODO: Here is some replacement needed.
//    VariableReplacementMap replacements = new VariableReplacementMap();
//    replacements.put(oldVariable, transitionEffekt);
//    System.out.println("gov.nasa.jpf.psyco.search.transitionSystem.SymbolicTransitionHelper.createResultValue()");
//    System.out.println("transitionEffekt: " + transitionEffekt);
//    transitionEffekt = ExpressionUtil.transformVars(oldVariable, replacements);
//    System.out.println("transitionEffektAfterReplacement: " + transitionEffekt);
    Expression newValuePart = NumericBooleanExpression.create(primeVariable,
                    NumericComparator.EQ, transitionEffekt);
    return ExpressionUtil.and(oldValue, newValuePart);
  }
  private Expression appendNewValue(Expression newValue, Expression toAppend){
    newValue = (newValue == null? toAppend:
              ExpressionUtil.and(newValue, toAppend));
    return newValue;
  }
  private Variable createPrimeVariable(Variable var){
    String newName = var.getName() + "'";
    return Variable.create(var.getType(), newName);
  }
  @Override
  public StateImage applyError(StateImage alreadyReachedStates,
          Transition transition) {
    if(alreadyReachedStates instanceof SymbolicImage){
      SymbolicImage currentState = (SymbolicImage) alreadyReachedStates;
      String error = transition.getError();
      for(SymbolicState state: currentState.getReachableStates().values()){
        if(satisfiesGuardCondition(state, transition)){
          ((SymbolicImage) alreadyReachedStates).addErrorInCurrentDepth(error);
        }
      }
      return currentState;
    }
    return null;
  }

  private boolean satisfiesGuardCondition(SymbolicState state,
          Transition transition) {
    Expression guard = transition.getGuardCondition();
    Expression stateExpression = state.toExpression();
    Expression guardTest = 
            stateExpression != null?
            ExpressionUtil.and(guard, stateExpression) : guard;
    Result res = solver.isSatisfiable(guardTest);
    if(res == SAT){
      return true;
    }else if(res == Result.DONT_KNOW){
      logger.severe("Cannot decide transition.");
      System.exit(42);
    }
    return false;
  }
}
