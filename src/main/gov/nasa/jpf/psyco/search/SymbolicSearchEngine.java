/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search;

import gov.nasa.jpf.psyco.search.transitionSystem.TransitionSystem;
import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.jdart.constraints.Path;
import gov.nasa.jpf.psyco.search.collections.SymbolicImage;
import gov.nasa.jpf.psyco.search.region.SymbolicRegion;
import gov.nasa.jpf.psyco.search.region.util.SymbolicRegionUtil;
import gov.nasa.jpf.psyco.search.util.SymbolicRegionSearchUtil;
import gov.nasa.jpf.util.SimpleProfiler;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *This class implements a Symbolic Breadth-First search
 *The Basis for the Algorithmen is taken from 
 *Alur, R.:2015. 3.4.2 Symbolic Breadth-First Search. 
 *In: Alur, R. Principles of Cyber-Physical Systems. 
 *Cambridge, London: The MIT Press
 *I cite: 
 * Input: A transition system T given by regions Init for the initial states
 * and Trans for transitions, and a region q for the property.
 * Output: If q is reachable in T, return true, else false
 * 
 * reg Reach := Init;
 * reg New := Init;
 * while isEmtpy(new) = 0 do{
 *  if isEmpty(Conj(New, q)) = 0 then return true;
 *  New := Diff(Post(New,Trans),Reach);
 *  Reach := Disj(Reach, New);
 * }
 * return false;
 *@author mmuesly
 */
public class SymbolicSearchEngine {
  static String loggerName = "psyco";
  Logger logger = Logger.getLogger(loggerName);
  private static String currentProfilerRun = null;

  public static SymbolicImage symbolicBreadthFirstSearch(
          TransitionSystem transitionSystem,
          ConstraintSolver solver){
    SymbolicRegion reachableRegion = 
            new SymbolicRegion(transitionSystem.getInitValuation());
    SymbolicRegion newRegion = 
            new SymbolicRegion(transitionSystem.getInitValuation());
    boolean isLimitedTransitionSystem = transitionSystem.isLimited();
    logLimit(isLimitedTransitionSystem);
    SymbolicRegionUtil regionUtil = new SymbolicRegionUtil(solver);
    SymbolicRegionSearchUtil searchUtil = 
            new SymbolicRegionSearchUtil(solver);
    //We start to count interation based on 1. 0 is skipped.
    int currentDepth = 0;
    StringBuilder reachedErrors = new StringBuilder();
    SymbolicImage currentSearchState = 
            new SymbolicImage(reachableRegion, reachedErrors, currentDepth);
    currentSearchState.setPreviousNewStates(newRegion);
    while(!currentSearchState.getPreviousNewStates().isEmpty()){
      SymbolicImage newImage = searchUtil.post(currentSearchState,
              transitionSystem);
      SymbolicRegion nextReachableStates = newImage.getNewStates();
      startDifferenceProfiler(newImage.getDepth());
      newRegion = regionUtil.difference(nextReachableStates,
              reachableRegion, solver);
      stopDifferenceProfiler();
      reachableRegion = regionUtil.disjunction(reachableRegion, newRegion);
      newImage.setReachableStates(reachableRegion);
      newImage.setPreviousNewStates(newRegion);
      newImage.setNewStates(null);
      currentSearchState = newImage;
      logState(currentSearchState);
//      if(currentSearchState.getDepth() == 4)
//        break;
    }
    return currentSearchState;
  }
    
  private static void logState(SymbolicImage newImage){
    Logger logger = Logger.getLogger("psyco");
    StringBuilder builder= new StringBuilder();
    try {
      newImage.print(builder);
      logger.fine(builder.toString());
    } catch (IOException ex) {
      Logger.getLogger(SymbolicSearchEngine.class.getName())
              .log(Level.SEVERE, null, ex);
    }
  }
  
  private static StringBuilder appendErrors(SymbolicImage newImage,
          StringBuilder reachedErrors){
    reachedErrors.append("In depth ");
    reachedErrors.append(newImage.getDepth());
    reachedErrors.append(":\n");
    reachedErrors.append(newImage.getErrors());
    return reachedErrors;
  }

  private static void logLimit(boolean limitedTransitionSystem) {
    Logger logger = Logger.getLogger("psyco");
    if(!limitedTransitionSystem){
      logger.info("");
      logger.info("The Transition system is not finite.");
      logger.info("It is very likely, that the search does not terminate.");
      logger.info("");
    }else{
      logger.info("");
      logger.info("The Transition system seems to be finite.");
      logger.info("The search should terminate.");
      logger.info("");
    }
  }
  
  public static String getSearchLoggerName(){
    return loggerName;
  }

    private static void startDifferenceProfiler(int depth){
    if(currentProfilerRun != null){
      SimpleProfiler.stop(currentProfilerRun);
    }
    currentProfilerRun = "differencInDepth-" + depth;
    SimpleProfiler.start(currentProfilerRun);
  }

  private static void stopDifferenceProfiler(){
    SimpleProfiler.stop(currentProfilerRun);
    currentProfilerRun = null;
  }
}
