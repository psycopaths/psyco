/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.util;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.constraints.api.ConstraintSolver.Result;
import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.api.ValuationEntry;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.expressions.NumericBooleanExpression;
import gov.nasa.jpf.constraints.expressions.NumericComparator;
import gov.nasa.jpf.constraints.util.ExpressionUtil;
import gov.nasa.jpf.jdart.constraints.Path;
import gov.nasa.jpf.jdart.constraints.PathResult;
import gov.nasa.jpf.jdart.constraints.PathResult.OkResult;
import gov.nasa.jpf.psyco.exceptions.RenamingException;
import gov.nasa.jpf.psyco.search.Transition;
import gov.nasa.jpf.psyco.search.collections.VariableRenamingMap;
import gov.nasa.jpf.util.JPFLogger;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mmuesly
 */
public class SymbolicSearchUtil {

  public static Valuation post(Valuation newRegion,
          List<Path> transitionSystem, ConstraintSolver solver) {
    Set<Variable<?>> variablesInPreviousState = 
        ValuationUtil.convertToVariableSet(newRegion);
    List<Transition> newStates = applyIterationOfTheTransitionSystem(
            newRegion, transitionSystem, solver);
    Valuation resultingStates = mergeTransitionsToNewState(newStates);
    
    Valuation existingRegion = ValuationUtil.exists(resultingStates,
              variablesInPreviousState);
    VariableRenamingMap renamings = 
            convertTransitionsToVariableRenmaingMap(newStates);
    resultingStates = ValuationUtil.rename(existingRegion, 
            renamings.getPrimeNames(), renamings.getOldNames());
    return resultingStates;
  }

  private static List<Transition> applyIterationOfTheTransitionSystem(
          Valuation inputRegion, List<Path> transitionSystem,
          ConstraintSolver solver){
    List<Transition> transitions = new ArrayList<>();
    Expression inputRegionExpression = ExpressionUtil.
            valuationToExpression(inputRegion);
    for(Path possibleTransition: transitionSystem){
      Transition transitionResult =
            applySingleTransition(inputRegionExpression, possibleTransition,
              solver);
      if(transitionResult != null && transitionResult.isSuccess()){
        transitions.add(transitionResult);
      }
    }
    return transitions;
  }
  
  private static Transition applySingleTransition(
          Expression inputRegionExpression, Path possibleTransition,
          ConstraintSolver solver) {
    JPFLogger logger = JPF.getLogger("psyco");
    Transition transition = ResultPathUtil
            .convertPathToTransition(possibleTransition);
    if (transition == null){
      return null;
    }
    Expression transitionExpression = transition.getExpression();
    Expression nextStepForIteration = ExpressionUtil.and(inputRegionExpression,
            transitionExpression);
    Valuation nextStepResult = new Valuation();
    Result result = solver.solve(nextStepForIteration, nextStepResult);
    if(result == Result.SAT){
      transition.setTransitionResult(nextStepResult);
      logger.fine("apply transformation: ");
      logger.fine(possibleTransition);
      logger.fine("reachableStates in this Iteration");
      logger.fine(nextStepResult);
      transition.setTransitionResult(nextStepResult);
      transition.setSuccess(true);
    }
    else{
      logger.fine("cannot use the following tranformation: ");
      logger.fine(transition);
      transition.setSuccess(false);
    }
    return transition;
  }
  
  private static Expression orWithNullExpression(Expression one, Expression two){
    if(one == null)
      return two;
    else
      return ExpressionUtil.or(one, two);
  }
  
  private static Valuation mergeTransitionsToNewState(List<Transition> transitions){
    Valuation resultState = new Valuation();
    if(transitions.isEmpty()){
      return resultState;
    }
    for(Transition transition: transitions){
      Valuation resultingValuation = transition.getTransitionResult();
      for(ValuationEntry entry: resultingValuation.entries()){
        resultState.addEntry(entry);
      }
    }
    return resultState;
  }
  private static VariableRenamingMap convertTransitionsToVariableRenmaingMap(
          List<Transition> transitions){
    VariableRenamingMap renamings = new VariableRenamingMap();
    for(Transition transition: transitions){
      renamings.addRenamingsOfTransition(transition);
    }
    return renamings;
  }
}
