/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search;

import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.jdart.constraints.Path;
import gov.nasa.jpf.psyco.search.collections.IterationImage;
import gov.nasa.jpf.psyco.search.region.ValuationRegion;
import gov.nasa.jpf.psyco.search.region.util.ValuationRegionUtil;
import gov.nasa.jpf.psyco.search.util.EnumerativSearchUtil;
import java.util.List;

/**
 *
 * @author mmuesly
 */
public class EnumerativeSearchEngine {
    public static IterationImage<ValuationRegion> enumerativBreadthFirstSearch(
          List<Path> transitionSystem, Valuation init,
          ConstraintSolver solver){
    ValuationRegion reachableRegion = new ValuationRegion(init);
    ValuationRegion newRegion = new ValuationRegion(init);
    ValuationRegionUtil regionUtil = new ValuationRegionUtil();
    EnumerativSearchUtil<ValuationRegion, ValuationRegionUtil> searchUtil = 
            new EnumerativSearchUtil<> (regionUtil);
    //If newRegion is empty, it was not possible to reach a new state by 
    //the last iteration. A fix point is reached. This is the termiantion goal.
    int currentDepth = 1;
    StringBuilder reachedErrors = new StringBuilder();
    while(!newRegion.isEmpty()){
      IterationImage<ValuationRegion> imageResult = searchUtil.post(newRegion,
              transitionSystem, solver);
      currentDepth = imageResult.getDepth();
      reachedErrors.append(imageResult.getErrors());
      newRegion = regionUtil.difference(imageResult.getReachableStates(),
              reachableRegion);
      reachableRegion = regionUtil.disjunction(reachableRegion, newRegion);
    }
    IterationImage<ValuationRegion> result =
            new IterationImage<>(reachableRegion, reachedErrors, currentDepth);
    return result;
  }
}
