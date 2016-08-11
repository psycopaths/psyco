/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.transitionSystem;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.expressions.Constant;
import gov.nasa.jpf.constraints.expressions.NumericBooleanExpression;
import gov.nasa.jpf.constraints.expressions.NumericComparator;
import gov.nasa.jpf.constraints.util.ExpressionUtil;
import gov.nasa.jpf.psyco.search.SymbolicSearchEngine;
import gov.nasa.jpf.psyco.search.datastructures.NameMap;
import gov.nasa.jpf.psyco.search.datastructures.StateImage;
import gov.nasa.jpf.psyco.search.datastructures.SymbolicImage;
import gov.nasa.jpf.psyco.search.datastructures.VariableReplacementMap;
import gov.nasa.jpf.psyco.search.region.SymbolicEntry;
import gov.nasa.jpf.psyco.search.region.SymbolicRegion;
import gov.nasa.jpf.psyco.search.region.SymbolicState;
import gov.nasa.jpf.util.JPFLogger;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mmuesly
 */
public class SymbolicTransitionHelper extends TransitionHelper{
  private final JPFLogger logger;
  private final VariableReplacementVisitor replacementVisitor;
  private final VariableAssignmentVisitor assignmentVisitor;

  public SymbolicTransitionHelper() {
    this.assignmentVisitor = new VariableAssignmentVisitor();
    this.replacementVisitor = new VariableReplacementVisitor();
    this.logger = JPF.getLogger(SymbolicSearchEngine.getSearchLoggerName());
  }
  @Override
  public StateImage applyTransition(StateImage image, Transition transition) {
    if(image instanceof SymbolicImage){
      SymbolicRegion newRegion = new SymbolicRegion();
      SymbolicImage currentState = (SymbolicImage) image;
      int depth = currentState.getDepth();
      for(SymbolicState state: currentState.getPreviousNewStates().values()){
        if(satisfiesGuardCondition(state, transition, depth)){
          try {
            SymbolicState newState = executeTransition(state, transition);
            newRegion.put(HelperMethods.getUniqueStateName(), newState);
            newRegion.print(System.out);
          } catch (IOException ex) {
            Logger.getLogger(SymbolicTransitionHelper.class.getName()).log(Level.SEVERE, null, ex);
          }
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
    VariableReplacementMap replacements = 
            extractValueReplacements(state);
    for(SymbolicEntry entry: state){
      SymbolicEntry primeEntry =
              executeTransitionOnEntry(
                      entry, transition, newValue, replacements);
      resultingState.add(primeEntry);
    }
    transition.setIsReached(true);
    return resultingState;
  }

  private VariableReplacementMap extractValueReplacements(SymbolicState state){
    VariableReplacementMap replacements = new VariableReplacementMap();
    for(SymbolicEntry entry: state){
      Expression replacement = 
            extractValueForReplacement(entry.getVariable(), entry.getValue());
      if(!replacements.containsKey(entry.getVariable())){
        replacements.put(entry.getVariable(), replacement);
      }else{
        throw new IllegalStateException("IT IS NOT POSSIBLE TO "
                + "REPLACE A VARIABLE WITH TWO VALUES");
      }
    }
    return replacements;
  }
  private SymbolicEntry executeTransitionOnEntry(SymbolicEntry entry,
          Transition transition, Expression prefix,
          VariableReplacementMap replacements){
    Variable oldVariable = entry.getVariable(),
            primeVariable = createPrimeVariable(oldVariable);
    Expression transitionEffekt = transition.getTransitionEffect(oldVariable);
    Expression newValue = 
            prefix != null ?
            (Expression) prefix.accept(replacementVisitor, replacements)
            : prefix;
    if(isStutterEffektForVariable(oldVariable, transitionEffekt)
            || transitionEffekt == null){
      newValue = createStutterTransition(oldVariable,
                          primeVariable, entry.getValue());
    }else if(isConstantAssignment(transitionEffekt)){
        newValue = 
              createConstantAssignment(primeVariable, transitionEffekt);
    }else{
      newValue = createResultValue(oldVariable, primeVariable,
              transitionEffekt, replacements, newValue);
    }
    logger.finest("gov.nasa.jpf.psyco.search.transitionSystem."
            + "SymbolicTransitionHelper.executeTransitionOnEntry()");
    logger.finest("primeVariable: " + primeVariable + " newValue: " 
            + newValue + " oldValue: " + entry.getValue());
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
          Expression transitionEffekt,
          VariableReplacementMap replacements,
          Expression prefix){
    transitionEffekt = (Expression) 
            transitionEffekt.accept(replacementVisitor, replacements);
    Expression newValuePart = NumericBooleanExpression.create(primeVariable,
                    NumericComparator.EQ, transitionEffekt);
    return appendNewValue(prefix, newValuePart);
  }

  private Expression extractValueForReplacement(Variable oldVariable,
          Expression oldValue){
    HashMap<Variable, List<Expression>> data = new HashMap<>();
    data.put(oldVariable, new ArrayList<Expression>());
    oldValue.accept(assignmentVisitor, data);
    List list = data.get(oldVariable);
    if(list.size() > 1){
      throw new IllegalStateException(
              "Cannot handle undefined value assignment");
    }else if(list.size() == 1){
      return (Expression) list.get(0);
    }
    return oldVariable;
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
}