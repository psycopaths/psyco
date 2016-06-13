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
import gov.nasa.jpf.psyco.search.region.ExpressionRegion;
import gov.nasa.jpf.psyco.search.region.SymbolicEntry;
import gov.nasa.jpf.psyco.search.region.ValuationRegion;
import gov.nasa.jpf.psyco.search.region.util.ExpressionRegionUtil;
import gov.nasa.jpf.psyco.search.util.EnumerativSearchUtil;
import gov.nasa.jpf.psyco.search.region.util.ValuationRegionUtil;
import gov.nasa.jpf.psyco.search.util.SymbolicSearchUtil;
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
  
  public static IterationImage<ExpressionRegion> symbolicBreadthFirstSearch(
          List<Path> transitionSystem, Valuation init,
          ConstraintSolver solver){
    ExpressionRegion reachableRegion = new ExpressionRegion(init);
    ExpressionRegion newRegion = new ExpressionRegion(init);
    ExpressionRegionUtil regionUtil = new ExpressionRegionUtil();
    SymbolicSearchUtil<ExpressionRegion, ExpressionRegionUtil> searchUtil = 
            new SymbolicSearchUtil(new ExpressionRegionUtil());
    int currentDepth = 0;
    StringBuilder reachedErrors = new StringBuilder();
    while(!newRegion.isEmpty()){
      IterationImage<ExpressionRegion> newImage = searchUtil.post(newRegion,
              transitionSystem, solver);
      ExpressionRegion nextReachableStates = newImage.getReachableStates();
      currentDepth = newImage.getDepth();
      reachedErrors.append(newImage.getErrors());
      newRegion = regionUtil.difference(nextReachableStates,
              reachableRegion, solver);
//      reachableRegion = regionUtil.disjunction(reachableRegion, newRegion);
      for(SymbolicEntry entry: newRegion.getRegionEntries()){
        reachableRegion.add(entry);
      }
    }
    return new IterationImage<ExpressionRegion>(reachableRegion,
            reachedErrors,currentDepth);
  }
  
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
    int currentDepth = 0;
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
