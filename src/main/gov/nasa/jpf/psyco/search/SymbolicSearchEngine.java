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
import gov.nasa.jpf.psyco.search.util.SymbolicSearchUtil;
import gov.nasa.jpf.psyco.search.util.ValuationUtil;
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
  
  public static boolean symbolicBreadthFirstSearchReachability(
          List<Path> transitionSystem, Valuation init,
          Valuation propertiRegionQ, ConstraintSolver solver){
    Valuation reachableRegion = init;
    Valuation newRegion = init;
    
      System.out.println("gov.nasa.jpf.psyco.search.SymbolicSearchEngine.symbolicBreadthFirstSearchReachability()");
      System.out.println("start:");
      try {
        reachableRegion.print(System.out);
        System.out.println();
      } catch (IOException ex) {
        Logger.getLogger(SymbolicSearchEngine.class.getName()).log(Level.SEVERE, null, ex);
      }
    //If newRegion is empty, it was not possible to reach a new state by 
    //the last iteration. A fix point is reached. This is the termiantion goal.
    while(!ValuationUtil.isEmpty(newRegion)){
//      Valuation conjunctionNewAndPropertiQ = 
//        ValuationUtil.conjunction(newRegion, propertiRegionQ);
      Valuation conjunctionNewAndPropertiQ = newRegion;
      if(ValuationUtil.isEmpty(conjunctionNewAndPropertiQ)){
        return true;
      }
      Valuation nextReachableStates = SymbolicSearchUtil.post(newRegion,
              transitionSystem, solver);
      Expression result = ExpressionUtil.valuationToExpression(nextReachableStates);
      newRegion = ValuationUtil.difference(nextReachableStates,
              reachableRegion);
      reachableRegion = ValuationUtil.disjunction(reachableRegion, newRegion);
      System.out.println("gov.nasa.jpf.psyco.search.SymbolicSearchEngine.symbolicBreadthFirstSearchReachability()");
      System.out.println("result:");
      try {
      System.out.println("new:");
      newRegion.print(System.out);
      System.out.println();
      System.out.println("reachable:");
      reachableRegion.print(System.out);
      System.out.println();
      System.out.flush();
    } catch (IOException ex) {
      Logger.getLogger(SymbolicSearchEngine.class.getName()).log(Level.SEVERE, null, ex);
    }
    }
    return false;
  }
}
