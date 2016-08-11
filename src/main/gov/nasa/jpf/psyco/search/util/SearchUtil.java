/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.util;

import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.psyco.search.SymbolicSearchEngine;
import gov.nasa.jpf.psyco.search.datastructures.searchImage.StateImage;
import gov.nasa.jpf.psyco.search.datastructures.region.Region;
import gov.nasa.jpf.psyco.search.util.region.RegionUtil;
import gov.nasa.jpf.psyco.search.transitionSystem.TransitionSystem;
import gov.nasa.jpf.psyco.util.PsycoProfiler;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 *
 * @author mmuesly
 */
public class SearchUtil<T extends StateImage> {
  private RegionUtil util;
  private long uniqueCount = 1L;
  private long uniqueStateCount;
  private Logger logger;

  public SearchUtil(RegionUtil util){
    this.logger = Logger.getLogger(SymbolicSearchEngine.getSearchLoggerName());
    this.util = util;
  }

  public T post(T currentSearchState,
          TransitionSystem transitionSystem){
    Set<Variable<?>> variablesInPreviousState = 
            util.convertToVariableSet(currentSearchState.getReachableStates());
    T iterationResult = 
            applyIterationOfTheTransitionSystem(currentSearchState,
                    transitionSystem);
    PsycoProfiler.newStates(iterationResult.getDepth(),
            iterationResult.getNewStates().size());
    Region existingRegion = 
            util.exists(iterationResult.getNewStates(),
                    variablesInPreviousState);
    PsycoProfiler.startRenamingProfiler(currentSearchState.getDepth());

    Region renamedRegion = 
            rename(existingRegion, variablesInPreviousState);
    PsycoProfiler.stopRenamingProfiler(currentSearchState.getDepth());
    iterationResult.setNewStates(renamedRegion);
    return iterationResult;
  }

  private T applyIterationOfTheTransitionSystem(
          T alreadyReachedStates,
          TransitionSystem transitionSystem) {
    alreadyReachedStates = (T) transitionSystem.applyOn(alreadyReachedStates);
    return alreadyReachedStates;
  }

  private Region rename(Region existingRegion, Set<Variable<?>> variablesInPreviousState) {
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
