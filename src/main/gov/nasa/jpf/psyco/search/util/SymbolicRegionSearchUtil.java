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
import gov.nasa.jpf.constraints.expressions.NumericBooleanExpression;
import gov.nasa.jpf.constraints.expressions.NumericComparator;
import gov.nasa.jpf.constraints.util.ExpressionUtil;
import gov.nasa.jpf.jdart.constraints.Path;
import gov.nasa.jpf.jdart.constraints.PathResult;
import gov.nasa.jpf.jdart.constraints.PathResult.ErrorResult;
import gov.nasa.jpf.jdart.constraints.PathResult.OkResult;
import gov.nasa.jpf.jdart.constraints.PostCondition;
import gov.nasa.jpf.psyco.search.collections.SymbolicImage;
import gov.nasa.jpf.psyco.search.region.SymbolicEntry;
import gov.nasa.jpf.psyco.search.region.SymbolicRegion;
import gov.nasa.jpf.psyco.search.region.SymbolicState;
import gov.nasa.jpf.psyco.search.region.util.SymbolicRegionUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mmuesly
 */
public class SymbolicRegionSearchUtil {
  private SymbolicRegionUtil util;
  private ConstraintSolver solver;
  private long uniqueCount = 1L;
  
  private Logger logger;
  public SymbolicRegionSearchUtil(ConstraintSolver solver){
    this.solver = solver;
    this.util = new SymbolicRegionUtil(solver);
    this.logger = Logger.getLogger("psyco");
  }
  
  public SymbolicImage post(SymbolicRegion alreadyReachedStates,
          List<Path> transitionSystem){
    Set<Variable<?>> variablesInPreviousState = 
            util.convertToVariableSet(alreadyReachedStates);
    SymbolicImage iterationResult = 
            applyIterationOfTheTransitionSystem(alreadyReachedStates, transitionSystem);
    SymbolicRegion existingRegion = 
            util.exists(iterationResult.getReachableStates(),
                    variablesInPreviousState);
    SymbolicRegion renamedRegion = 
            rename(existingRegion, variablesInPreviousState);
    return new SymbolicImage(renamedRegion, iterationResult.getErrors(), -1);
  }

  private SymbolicImage applyIterationOfTheTransitionSystem(
          SymbolicRegion alreadyReachedStates, List<Path> transitionSystem) {
    SymbolicRegion iterationResult = new SymbolicRegion();
    StringBuilder errors = new StringBuilder();
    for(Path p : transitionSystem){
      applySingleTransition(p, alreadyReachedStates, iterationResult, errors);
    }
    return new SymbolicImage(iterationResult, errors, -1);
  }

  private void applySingleTransition(Path p,
          SymbolicRegion alreadyReachedStates,
          SymbolicRegion iterationResult, StringBuilder errors) {
    PathResult result = p.getPathResult();
    if(result instanceof OkResult){
      applyOkPath(p, alreadyReachedStates, iterationResult);
    }
    if(result instanceof ErrorResult){
      applyErrorPath(p, alreadyReachedStates, errors);
    }
  }

  private void applyOkPath(Path p, SymbolicRegion alreadyReachedStates, SymbolicRegion iterationResult) {
    for(SymbolicState possibleState: alreadyReachedStates.values()){
      applyOkPathOnState(p, possibleState, iterationResult);
    }
  }

  private void applyOkPathOnState(Path p, SymbolicState testState,
          SymbolicRegion iterationResult){
    Expression<Boolean> transitionConditionTest =
            testState.toExpression();
    Expression<Boolean> pathCondition = p.getPathCondition();
    transitionConditionTest = 
            ExpressionUtil.and(transitionConditionTest, pathCondition);
    Result solverResult = solver.isSatisfiable(transitionConditionTest);
    if(solverResult == Result.SAT){
      SymbolicState resultState = applyOkPostCondition(testState,
              pathCondition, p.getPostCondition());
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
              applyTranisitionResultOnVariable(var, entriesForVar, testState,
                      pathCondition, transitionEffekt);
      resultState.add(newEntry);
    }
    return resultState;
  }
  
  private SymbolicEntry applyTranisitionResultOnVariable(Variable var,
          Set<SymbolicEntry> oldValues,
          SymbolicState testState, Expression preCondition,
          Expression transitionEffekt){
    String newName = var.getName() + "'";
    Variable newVar = new Variable(var.getType(), newName);
    Expression newValue = preCondition;
    for(SymbolicEntry setEntry: oldValues){
      Expression entryValue = setEntry.getValue();
      newValue = newValue == null? entryValue :
              ExpressionUtil.and(newValue, entryValue);
    }
    transitionEffekt = 
            NumericBooleanExpression.create(newVar,
                    NumericComparator.EQ, transitionEffekt);
    if(transitionEffekt != null){
      newValue = (newValue == null ? transitionEffekt :
              ExpressionUtil.and(newValue, transitionEffekt));
    }
    return new SymbolicEntry(newVar, newValue);
  }

  private void applyErrorPath(Path p, SymbolicRegion alreadyReachedStates, StringBuilder errors) {
    for(SymbolicState possibleState: alreadyReachedStates.values()){
      applyErrorPathOnState(p, possibleState, errors);
    }
  }

  private void applyErrorPathOnState(Path p, SymbolicState state, StringBuilder errors){
    Expression condition = p.getPathCondition();
    Expression testCondition = state.toExpression();
    testCondition = ExpressionUtil.and(testCondition, condition);
    Result solverResult = solver.isSatisfiable(testCondition);
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
    String stateName = "state_" + uniqueCount;
    ++uniqueCount;
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


}
