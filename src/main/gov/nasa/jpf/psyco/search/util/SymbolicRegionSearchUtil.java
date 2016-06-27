/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.util;

import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.constraints.api.ConstraintSolver.Result;
import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.expressions.Constant;
import gov.nasa.jpf.constraints.expressions.NumericBooleanExpression;
import gov.nasa.jpf.constraints.expressions.NumericComparator;
import gov.nasa.jpf.constraints.types.BuiltinTypes;
import gov.nasa.jpf.constraints.util.ExpressionUtil;
import gov.nasa.jpf.jdart.constraints.Path;
import gov.nasa.jpf.jdart.constraints.PostCondition;
import gov.nasa.jpf.psyco.search.TransitionSystem;
import gov.nasa.jpf.psyco.search.collections.SymbolicImage;
import gov.nasa.jpf.psyco.search.region.SymbolicEntry;
import gov.nasa.jpf.psyco.search.region.SymbolicRegion;
import gov.nasa.jpf.psyco.search.region.SymbolicState;
import gov.nasa.jpf.psyco.search.region.util.SymbolicRegionUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 *
 * @author mmuesly
 */
public class SymbolicRegionSearchUtil {
  private SymbolicRegionUtil util;
  private ConstraintSolver solver;
  private long uniqueCount = 1L;
  private long uniqueStateCount;
  private Logger logger;
  public SymbolicRegionSearchUtil(ConstraintSolver solver){
    this.solver = solver;
    this.util = new SymbolicRegionUtil(solver);
    this.logger = Logger.getLogger("psyco");
  }
  
  public SymbolicImage post(SymbolicRegion alreadyReachedStates,
          TransitionSystem transitionSystem){
    //uniqueStateCount = 0L;
    Set<Variable<?>> variablesInPreviousState = 
            util.convertToVariableSet(alreadyReachedStates);
    SymbolicImage iterationResult = 
            applyIterationOfTheTransitionSystem(alreadyReachedStates,
                    transitionSystem);
    SymbolicRegion existingRegion = 
            util.exists(iterationResult.getReachableStates(),
                    variablesInPreviousState);
    SymbolicRegion renamedRegion = 
            rename(existingRegion, variablesInPreviousState);
    return new SymbolicImage(renamedRegion, iterationResult.getErrors(), -1);
  }

  private SymbolicImage applyIterationOfTheTransitionSystem(
          SymbolicRegion alreadyReachedStates,
          TransitionSystem transitionSystem) {
    SymbolicRegion iterationResult = new SymbolicRegion();
    StringBuilder errors = new StringBuilder();
    for(Path p : transitionSystem.getConsideredOKPaths()){
      applyOkPath(p, alreadyReachedStates, iterationResult);
    }
    for(Path p: transitionSystem.getConsideredErrorPaths()){
      applyErrorPath(p, alreadyReachedStates, errors);
    }
    return new SymbolicImage(iterationResult, errors, -1);
  }

  private void applyOkPath(Path p, SymbolicRegion alreadyReachedStates,
          SymbolicRegion iterationResult) {
    for(SymbolicState possibleState: alreadyReachedStates.values()){
      logger.finer("nextPath: " + p.toString());
      applyOkPathOnState(p, possibleState, iterationResult);
    }
  }

  private void applyOkPathOnState(Path p, SymbolicState testState,
          SymbolicRegion iterationResult){
    Result solverResult = testPathCondition(testState, p.getPathCondition());
    if(solverResult == Result.SAT){
      SymbolicState resultState = applyOkPostCondition(testState,
              p.getPathCondition(), p.getPostCondition());
            logger.finer("gov.nasa.jpf.psyco.search.util.SymbolicRegionSearchUtil.applyOkPathOnState()");
            logger.finer("applyTransition: " + p.toString());
            Expression res = resultState.toExpression();
            if(res != null){
              logger.finest("transitionResult: " + res.toString());
            }
      String stateName = getStateName();
      iterationResult.put(stateName, resultState);
    }
    if(solverResult == Result.DONT_KNOW){
      logger.severe("Cannot verifiy, wheter this transition can be used:");
      logger.severe(p.toString());
      logger.severe("termiante.");
      System.exit(1);
    }
  }

  private SymbolicState applyOkPostCondition(SymbolicState testState,
          Expression pathCondition, PostCondition postCondition) {
    SymbolicState resultState = new SymbolicState();
    Set<Variable<?>> stateVariable = util.convertToVariableSet(testState);
    Map<Variable<?>, Expression<?>> transitionEffekts = postCondition.getConditions();
    for(Variable var: stateVariable){
      Expression transitionEffekt = transitionEffekts.getOrDefault(var, null);
      Set<SymbolicEntry> entriesForVar = testState.getEntriesForVariable(var);
      SymbolicEntry newEntry = 
              applyTranisitionResultOnVariable(var, entriesForVar,
                      pathCondition, transitionEffekt);
      Expression value = newEntry.getValue();
      logger.finest("gov.nasa.jpf.psyco.search.util.SymbolicRegionSearchUtil.applyOkPostCondition()");
      logger.finest(newEntry.getVariable() + ": " + newEntry.getValue());
      resultState.add(newEntry);
    }
    return resultState;
  }
  
  private SymbolicEntry applyTranisitionResultOnVariable(Variable var,
          Set<SymbolicEntry> oldValues, Expression preCondition,
          Expression transitionEffekt){
    String newName = var.getName() + "'";
    Variable newVar = new Variable(var.getType(), newName);
    Expression newValue = null;
    //Just add the pre condition if it differs from constant true.
    if(!isPathConditionConstantTrue(preCondition)){
      newValue = preCondition;
    }
    for(SymbolicEntry setEntry: oldValues){
      Expression entryValue = setEntry.getValue();
      newValue = newValue == null? entryValue :
              ExpressionUtil.and(newValue, entryValue);
    }
    logger.finest("transitionEffekt: " + transitionEffekt);
    logger.finest("transitionEffektClass: " + transitionEffekt.getClass());
//    if(transitionEffekt instanceof Constant){
//      newValue = NumericBooleanExpression.create(newVar,
//                    NumericComparator.EQ, transitionEffekt);
//    }else 
//    if(transitionEffekt instanceof Variable && transitionEffekt.equals(var)){
//      //There is no new effekt
//      if(newValue instanceof NumericBooleanExpression){
//        NumericBooleanExpression test = (NumericBooleanExpression) newValue;
//        newValue = NumericBooleanExpression.create(newVar,
//                    NumericComparator.EQ, test.getRight());
//      }
//    }else 
      if(transitionEffekt != null){
    transitionEffekt = 
            NumericBooleanExpression.create(newVar,
                    NumericComparator.EQ, transitionEffekt);
    newValue = (newValue == null ? transitionEffekt :
              ExpressionUtil.and(newValue, transitionEffekt));
    }
    logger.finest("gov.nasa.jpf.psyco.search.util.SymbolicRegionSearchUtil.applyTranisitionResultOnVariable()");
    logger.finest("newVar:" + newVar.getName().toString());
    logger.finest("newValue:" + newValue.toString());
    return new SymbolicEntry(newVar, newValue);
  }

  private void applyErrorPath(Path p, SymbolicRegion alreadyReachedStates, StringBuilder errors) {
    for(SymbolicState possibleState: alreadyReachedStates.values()){
      applyErrorPathOnState(p, possibleState, errors);
    }
  }

  private void applyErrorPathOnState(Path p, SymbolicState state, StringBuilder errors){
    Result solverResult = testPathCondition(state, p.getPathCondition());
    if(solverResult == Result.SAT){
      errors.append(p.getErrorResult().getExceptionClass());
      errors.append("\n");
    }
    if(solverResult == Result.DONT_KNOW){
      logger.severe("Cannot verifiy, wheter this transition can be used:");
      logger.severe(p.toString());
      logger.severe("termiante.");
      System.exit(1);
    }
  }

  private String getStateName() {
    String stateName = "state_" + uniqueStateCount;
    ++uniqueStateCount;
    return stateName;
  }

  private SymbolicRegion rename(SymbolicRegion existingRegion, Set<Variable<?>> variablesInPreviousState) {
    List<Variable<?>> primeNames = new ArrayList<>();
    List<Variable<?>> variableNames = new ArrayList<>();
    for(Variable var: variablesInPreviousState){
      String primeName = var.getName() + "'";
      Variable primeVar = new Variable(var.getType(), primeName);
      primeNames.add(primeVar);
      variableNames.add(var);
    }
    return util.rename(existingRegion, primeNames, variableNames);
  }

  private Result testPathCondition(SymbolicState testState,
          Expression<Boolean> pathCondition){
    logger.finer("gov.nasa.jpf.psyco.search.util.SymbolicRegionSearchUtil.testPathCondition()");
    if(isPathConditionConstantTrue(pathCondition)){
      return Result.SAT;
    }
    Expression<Boolean> transitionConditionTest = 
            testState.isEmpty()? null : testState.toExpression();
    transitionConditionTest = 
            transitionConditionTest != null ?
            ExpressionUtil.and(transitionConditionTest, pathCondition) 
            : pathCondition;
    long start = System.currentTimeMillis();
    Result res = solver.isSatisfiable(transitionConditionTest);
    long stop = System.currentTimeMillis();
    logger.finer(pathCondition.getClass().toString());
    logger.finer("Time condition test: " + Long.toString(stop - start) + " in millis");
    return res;
  }

  private boolean isPathConditionConstantTrue(
          Expression<Boolean> pathCondition){
    if(pathCondition instanceof Constant 
            && pathCondition.getType() == BuiltinTypes.BOOL){
      Constant constant = ((Constant)pathCondition);
      if (constant.getValue() == Boolean.TRUE){
        return true;
      }
    }
    return false;
  }
}
