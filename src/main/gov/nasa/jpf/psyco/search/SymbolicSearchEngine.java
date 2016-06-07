/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search;

import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.util.ExpressionUtil;
import gov.nasa.jpf.jdart.constraints.Path;
import gov.nasa.jpf.psyco.search.region.ValuationRegion;
import gov.nasa.jpf.psyco.search.util.EnumerativSearchUtil;
import gov.nasa.jpf.psyco.search.region.util.ValuationRegionUtil;
import gov.nasa.jpf.psyco.search.region.util.ValuationUtil;
import gov.nasa.jpf.solver.SolverWrapper;
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
  
  public static ValuationRegion enumerativBreadthFirstSearch(
          List<Path> transitionSystem, Valuation init,
          ConstraintSolver solver){
    ValuationRegion reachableRegion = new ValuationRegion(init);
    ValuationRegion newRegion = new ValuationRegion(init);
    ValuationRegionUtil regionUtil = new ValuationRegionUtil();
    EnumerativSearchUtil<ValuationRegion, ValuationRegionUtil> searchUtil = 
            new EnumerativSearchUtil<> (regionUtil);
    //If newRegion is empty, it was not possible to reach a new state by 
    //the last iteration. A fix point is reached. This is the termiantion goal.
    while(!newRegion.isEmpty()){
      ValuationRegion nextReachableStates = searchUtil.post(newRegion,
              transitionSystem, solver, new ValuationRegion());
      newRegion = regionUtil.difference(nextReachableStates,
              reachableRegion);
      reachableRegion = regionUtil.disjunction(reachableRegion, newRegion);
    }
    return reachableRegion;
  }
}
