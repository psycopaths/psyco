/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.util;

import gov.nasa.jpf.psyco.search.region.util.RegionUtil;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.constraints.api.ConstraintSolver.Result;
import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.api.ValuationEntry;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.util.ExpressionUtil;
import gov.nasa.jpf.jdart.constraints.Path;
import gov.nasa.jpf.psyco.search.transitionSystem.TransformationRepresentation;
import gov.nasa.jpf.psyco.search.collections.IterationImage;
import gov.nasa.jpf.psyco.search.collections.VariableRenamingMap;
import gov.nasa.jpf.psyco.search.region.Region;
import gov.nasa.jpf.util.JPFLogger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
/**
 *
 * @author mmuesly
 */
public class EnumerativSearchUtil<S extends Region<ValuationEntry>,
        T extends RegionUtil<S>> implements SearchUtil<S>{
  private T regionUtil;
  int depth = 0;
  public EnumerativSearchUtil(T passedRegionUtil){
    regionUtil = passedRegionUtil;
  }
  public IterationImage<S> post(S newRegion, List<Path> transitionSystem,
          ConstraintSolver solver) {
    Set<Variable<?>> variablesInPreviousState = 
        regionUtil.convertToVariableSet(newRegion);
    List<TransformationRepresentation> newStates = applyIterationOfTheTransitionSystem(
            newRegion, transitionSystem, solver);
    S resultingStates = mergeTransitionsToNewState(newStates);
    StringBuilder errors = putErrorsTogether(newStates);
    S existingRegion = regionUtil.exists(resultingStates,
              variablesInPreviousState);
    VariableRenamingMap renamings = 
            convertTransitionsToVariableRenmaingMap(newStates);
    resultingStates = regionUtil.rename(existingRegion, 
            renamings.getPrimeNames(), renamings.getOldNames());
    IterationImage<S> result = new IterationImage<>(resultingStates);
    result.setDepth(depth);
    result.setErrors(errors);
    depth++;
    return result;
  }

  private List<TransformationRepresentation> applyIterationOfTheTransitionSystem(
          Region inputRegion, List<Path> transitionSystem,
          ConstraintSolver solver){
    List<TransformationRepresentation> transitions = new ArrayList<>();
    Expression inputRegionExpression = inputRegion.toExpression();
    for(Path possibleTransition: transitionSystem){
      TransformationRepresentation transitionResult =
            applySingleTransition(inputRegionExpression, possibleTransition,
              solver);
      if(transitionResult != null && transitionResult.isApplied()){
        transitions.add(transitionResult);
      }
    }
    return transitions;
  }
  
  private TransformationRepresentation applySingleTransition(
          Expression inputRegionExpression, Path possibleTransition,
          ConstraintSolver solver) {
    JPFLogger logger = JPF.getLogger("psyco");
    TransformationRepresentation transition = ResultPathUtil
            .convertPathToTransition(possibleTransition, depth);
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
      transition.setApplied(true);
    }
    else{
      logger.fine("cannot use the following tranformation: ");
      logger.fine(transition);
      transition.setApplied(false);
    }
    return transition;
  }
  
//  private Expression orWithNullExpression(Expression one, Expression two){
//    if(one == null)
//      return two;
//    else
//      return ExpressionUtil.or(one, two);
//  }
  
  private S mergeTransitionsToNewState(List<TransformationRepresentation> transitions){
    S resultState = regionUtil.createRegion();
    if(transitions.isEmpty()){
      return resultState;
    }
    for(TransformationRepresentation transition: transitions){
      Valuation resultingValuation = transition.getTransitionResult();
      for(ValuationEntry entry: resultingValuation.entries()){
        resultState.add(entry);
      }
    }
    return resultState;
  }
  private VariableRenamingMap convertTransitionsToVariableRenmaingMap(
          List<TransformationRepresentation> transitions){
    VariableRenamingMap renamings = new VariableRenamingMap();
    for(TransformationRepresentation transition: transitions){
      renamings.addRenamingsOfTransition(transition);
    }
    return renamings;
  }

  private StringBuilder putErrorsTogether(List<TransformationRepresentation> transitions) {
    StringBuilder reachedErrors = new StringBuilder();
    for(TransformationRepresentation transition: transitions){
      reachedErrors.append(transition.getErrors());
    }
    return reachedErrors;
  }
}
