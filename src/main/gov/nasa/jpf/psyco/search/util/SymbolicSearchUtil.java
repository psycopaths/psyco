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
import gov.nasa.jpf.constraints.api.ValuationEntry;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.util.ExpressionUtil;
import gov.nasa.jpf.jdart.constraints.Path;
import gov.nasa.jpf.psyco.search.transitionSystem.TransformationRepresentation;
import gov.nasa.jpf.psyco.search.collections.IterationImage;
import gov.nasa.jpf.psyco.search.collections.NameMap;
import gov.nasa.jpf.psyco.search.collections.VariableRenamingMap;
import gov.nasa.jpf.psyco.search.region.Region;
import gov.nasa.jpf.psyco.search.region.SymbolicEntry;
import gov.nasa.jpf.psyco.search.region.util.RegionUtil;
import gov.nasa.jpf.psyco.util.PsycoProfiler;
import gov.nasa.jpf.util.JPFLogger;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mmuesly
 */
public class SymbolicSearchUtil<T extends Region<SymbolicEntry>,
        U extends RegionUtil<T>> implements SearchUtil<T> {
  private int depth;
  private U regionUtil;
  private long uniqueCount;
  public SymbolicSearchUtil(U util){
    this.regionUtil = util;
    uniqueCount = 0L;
  }
  
  @Override
  public IterationImage<T> post(T baseRegion, List<Path> transitionSystem,
          ConstraintSolver solver) {
    Set<Variable<?>> variablesInPreviousState = 
        regionUtil.convertToVariableSet(baseRegion);
    List<TransformationRepresentation> newStates = applyIterationOfTheTransitionSystem(
            baseRegion, transitionSystem, solver);

    T resultingStates = 
            collectStateDescriptionsFromTransition(newStates);
    
    StringBuilder errors = mergeErrorsOfTransitions(newStates);
    T existingRegion = regionUtil.exists(resultingStates,
              variablesInPreviousState);
    
    PsycoProfiler.startRenamingProfiler(depth);
    VariableRenamingMap renamings = 
            convertTransitionsToVariableRenmaingMap(newStates);
    resultingStates = rename(existingRegion, 
            renamings.getPrimeNames(), renamings.getOldNames());
    PsycoProfiler.stopRenamingProfiler(depth);
    
    IterationImage<T> result = new IterationImage<>(resultingStates);
    result.setErrors(errors);
    result.setDepth(depth);
    depth++;
    
    return result;
//    return null;
  }
  

  private List<TransformationRepresentation> applyIterationOfTheTransitionSystem(T inputRegion,
          List<Path> transitionSystem, ConstraintSolver solver) {
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

//  private T mergeTransitionsToNewStateDescription(List<Transition> transitions,
//          T resultState) {
//    if(transitions.isEmpty()){
//      return resultState;
//    }
//    HashMap<Variable, ArrayList<Expression<Boolean>>> newStates = 
//            gatherStateDescriptions(transitions);
//    resultState = combineStateDescriptions(newStates, resultState);
//    return resultState;
//  }

//  private HashMap<Variable, ArrayList<Expression<Boolean>>> gatherStateDescriptions(
//          List<Transition> transitions){
//    HashMap<Variable, ArrayList<Expression<Boolean>>> newStates = new HashMap<>();
//    for(Transition transition: transitions){
//      Set<ValuationEntry> resultingValuation = transition.getNewState();
//      for(ValuationEntry entry: resultingValuation){
//        if(entry instanceof SymbolicEntry){
//          SymbolicEntry symEntry = (SymbolicEntry) entry;
//          ArrayList<Expression<Boolean>> statesForVariable = 
//                newStates.getOrDefault(symEntry.getVariable(),
//                        new ArrayList<Expression<Boolean>>());
//          statesForVariable.add(symEntry.getValue());
//        }else{
//          throw new IllegalStateException("The symbolic search should only" 
//                  + "operate with symbolic state definitions.");
//        }
//      }
//    }
//    return newStates;
//  }

  private T combineStateDescriptions(HashMap<Variable,
          ArrayList<Expression<Boolean>>> newStates, T resultStates) {
    for(Variable var: newStates.keySet()){
      ArrayList<Expression<Boolean>> valueDescriptions = newStates.get(var);
      Expression value = ExpressionUtil.or(valueDescriptions);
      value = ExpressionUtil.simplify(value);
      SymbolicEntry newStateDescription = new SymbolicEntry(var, value);
      resultStates.add(newStateDescription);
    }
    return resultStates;
  }

  private T
         collectStateDescriptionsFromTransition(List<TransformationRepresentation> transitions) {
    T resultingRegion = regionUtil.createRegion();
    for(TransformationRepresentation transition: transitions){
      if(transition.isApplied() && !transition.isErrorTransition()){
        SymbolicEntry resultingState = transition.getNewState();
        resultingRegion.add(resultingState);
      }
    }
    return resultingRegion;
  }

  private VariableRenamingMap convertTransitionsToVariableRenmaingMap(
          List<TransformationRepresentation> transitions) {
        VariableRenamingMap renamings = new VariableRenamingMap();
    for(TransformationRepresentation transition: transitions){
      renamings.addRenamingsOfTransition(transition);
    }
    return renamings;
  }

  private TransformationRepresentation applySingleTransition(Expression inputRegionExpression, Path possibleTransition, ConstraintSolver solver) {
    JPFLogger logger = JPF.getLogger("psyco");
    TransformationRepresentation transition = ResultPathUtil
            .convertPathToTransition(possibleTransition, depth);
    if(transition == null){
      return null;
    }
    Expression transitionExpression = transition.getExpression();
    Expression resultExpression = ExpressionUtil.and(inputRegionExpression,
            transitionExpression);
    Result result = solver.isSatisfiable(resultExpression);
    if(result == Result.SAT){
      logger.fine("apply transformation: ");
      logger.fine(possibleTransition);
      logger.fine("reachableStates in this Iteration");
      logger.fine(resultExpression);
      transition.setApplied(true);
      if(!transition.isErrorTransition() 
              && !transition.getPrimeNames().isEmpty()){
      transition.setNewState(new SymbolicEntry((Variable<Expression<Boolean>>) transition.getPrimeNames().get(0), resultExpression));
      }
    }
    else{
      logger.fine("cannot use the following tranformation: ");
      logger.fine(transition.getExpression());
      transition.setApplied(false);
    }
    return transition;
  }

  private StringBuilder mergeErrorsOfTransitions(List<TransformationRepresentation> transitions) {
    StringBuilder reachedErrors = new StringBuilder();
    for(TransformationRepresentation transition: transitions){
      reachedErrors.append(transition.getErrors());
    }
    return reachedErrors;
  }

  private T rename(T existingRegion, List<Variable<?>> primeNames, List<Variable<?>> oldNames) {
    T newRegion = regionUtil.createRegion();
    for (int i = 0; i < primeNames.size(); i++){
      Variable name = primeNames.get(i);
      Set<SymbolicEntry> entries = existingRegion.getValuesForEntry(name);
      for(SymbolicEntry entry: entries){
        Expression value = entry.getValue();
        NameMap renameFunc = new NameMap();
        String uniqueName = "unique_" + uniqueCount;
        ++uniqueCount;
        Variable oldName = oldNames.get(i);
        renameFunc.mapNames(oldName.getName(), uniqueName);
        value = ExpressionUtil.renameVars(value, renameFunc);
        renameFunc.mapNames(name.getName(), oldName.getName());
        value = ExpressionUtil.renameVars(value, renameFunc);
        entry = new SymbolicEntry(oldName, value);
        newRegion.add(entry);
      }
    }
    return newRegion;
  }
}
