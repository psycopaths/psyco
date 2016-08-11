/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search;

import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.psyco.search.datastructures.searchImage.EnumerativeImage;
import gov.nasa.jpf.psyco.search.datastructures.region.EnumerativeRegion;
import gov.nasa.jpf.psyco.search.util.region.EnumerativeRegionUtil;
import gov.nasa.jpf.psyco.search.transitionSystem.TransitionSystem;
import gov.nasa.jpf.psyco.search.util.SearchUtil;
import gov.nasa.jpf.psyco.util.PsycoProfiler;

/**
 *
 * @author mmuesly
 */
public class EnumerativeSearchEngine {
    public static EnumerativeImage enumerativBreadthFirstSearch(
          TransitionSystem transitionSystem, Valuation init,
          ConstraintSolver solver){
    EnumerativeRegion newRegion, reachableRegion = new EnumerativeRegion(init);
    EnumerativeRegionUtil regionUtil = new EnumerativeRegionUtil(solver);
    SearchUtil<EnumerativeImage> searchUtil = 
            new SearchUtil<> (regionUtil);
    //If newRegion is empty, it was not possible to reach a new state by 
    //the last iteration. A fix point is reached. This is the termiantion goal.
    EnumerativeImage currentSearchState = new EnumerativeImage(reachableRegion);
    currentSearchState.setPreviousNewStates(reachableRegion);
    while(!currentSearchState.getPreviousNewStates().isEmpty()){
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

//      logState(currentSearchState);
//      if(maxSearchDepth != Integer.MIN_VALUE 
//              && currentSearchState.getDepth() == maxSearchDepth){
//        currentSearchState.setDepth(Integer.MAX_VALUE);
//        break;
//      }
    }
    return currentSearchState;
  }
}
