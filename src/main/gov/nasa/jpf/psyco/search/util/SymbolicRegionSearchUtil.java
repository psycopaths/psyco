/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.util;

import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.psyco.search.transitionSystem.TransitionSystem;
import gov.nasa.jpf.psyco.search.collections.SymbolicImage;
import gov.nasa.jpf.psyco.search.region.SymbolicRegion;
import gov.nasa.jpf.psyco.search.region.util.SymbolicRegionUtil;
import java.util.ArrayList;
import java.util.List;
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
  
  public SymbolicImage post(SymbolicImage currentSearchState,
          TransitionSystem transitionSystem){
    Set<Variable<?>> variablesInPreviousState = 
            util.convertToVariableSet(currentSearchState.getReachableStates());
    SymbolicImage iterationResult = 
            applyIterationOfTheTransitionSystem(currentSearchState,
                    transitionSystem);
    SymbolicRegion existingRegion = 
            util.exists(iterationResult.getNewStates(),
                    variablesInPreviousState);
    SymbolicRegion renamedRegion = 
            rename(existingRegion, variablesInPreviousState);
    iterationResult.setNewStates(renamedRegion);
    return iterationResult;
  }

  private SymbolicImage applyIterationOfTheTransitionSystem(
          SymbolicImage alreadyReachedStates,
          TransitionSystem transitionSystem) {
    alreadyReachedStates = (SymbolicImage) transitionSystem.applyOn(alreadyReachedStates);
    return alreadyReachedStates;
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
