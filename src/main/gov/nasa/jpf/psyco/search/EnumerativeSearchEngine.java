/*
 * Copyright (C) 2015, United States Government, as represented by the 
 * Administrator of the National Aeronautics and Space Administration.
 * All rights reserved.
 *
 * The PSYCO: A Predicate-based Symbolic Compositional Reasoning environment 
 * platform is licensed under the Apache License, Version 2.0 (the "License"); you 
 * may not use this file except in compliance with the License. You may obtain a 
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0. 
 *
 * Unless required by applicable law or agreed to in writing, software distributed 
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 * specific language governing permissions and limitations under the License.
 */
package gov.nasa.jpf.psyco.search;

import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.psyco.search.datastructures.searchImage.EnumerativeImage;
import gov.nasa.jpf.psyco.search.datastructures.region.EnumerativeRegion;
import gov.nasa.jpf.psyco.search.util.region.EnumerativeRegionUtil;
import gov.nasa.jpf.psyco.search.transitionSystem.TransitionSystem;
import gov.nasa.jpf.psyco.search.util.SearchUtil;
import gov.nasa.jpf.psyco.util.PsycoProfiler;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is an enumerative breadth-first search.
 */
public class EnumerativeSearchEngine {

  public static EnumerativeImage enumerativBreadthFirstSearch(
          TransitionSystem transitionSystem,
          ConstraintSolver solver,
          int maxSearchDepth) {
    SolverInstance.getInstance().setSolver(solver);
    EnumerativeRegion newRegion, reachableRegion
            = new EnumerativeRegion(transitionSystem.getInitValuation());
    EnumerativeRegionUtil regionUtil = new EnumerativeRegionUtil(solver);
    SearchUtil<EnumerativeImage> searchUtil
            = new SearchUtil<>(regionUtil);
    EnumerativeImage currentSearchState
            = new EnumerativeImage(reachableRegion);
    currentSearchState.setPreviousNewStates(reachableRegion);
    //If newRegion is empty, it was not possible to reach a new state by 
    //the last iteration. A fix point is reached. This is the termiantion goal.
    while (!currentSearchState.getPreviousNewStates().isEmpty()) {
      EnumerativeImage newImage = searchUtil.post(currentSearchState,
              transitionSystem);
      EnumerativeRegion nextReachableStates = newImage.getNewStates();

      PsycoProfiler.startDiffProfiler(newImage.getDepth());
      newRegion = regionUtil.difference(nextReachableStates,
              reachableRegion);
      PsycoProfiler.stopDiffProfieler(newImage.getDepth());

      reachableRegion = regionUtil.disjunction(reachableRegion, newRegion);
      newImage.setReachableStates(reachableRegion);
      newImage.setReachableStates(reachableRegion);
      newImage.setPreviousNewStates(newRegion);
      newImage.setNewStates(null);
      currentSearchState = newImage;

      logState(currentSearchState);
      if (maxSearchDepth != Integer.MIN_VALUE
              && currentSearchState.getDepth() == maxSearchDepth) {
        currentSearchState.setDepth(Integer.MAX_VALUE);
        break;
      }
    }
    return currentSearchState;
  }

  private static void logState(EnumerativeImage newImage) {
    Logger logger = Logger.getLogger("psyco");
    StringBuilder builder = new StringBuilder();
    try {
      newImage.print(builder);
      logger.fine(builder.toString());
    } catch (IOException ex) {
      Logger.getLogger(SymbolicSearchEngine.class.getName())
              .log(Level.SEVERE, null, ex);
    }
  }
}