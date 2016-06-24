/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search;

import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.jdart.constraints.Path;
import gov.nasa.jpf.psyco.search.collections.SymbolicImage;
import gov.nasa.jpf.psyco.search.region.SymbolicRegion;
import gov.nasa.jpf.psyco.search.region.util.SymbolicRegionUtil;
import gov.nasa.jpf.psyco.search.util.SymbolicRegionSearchUtil;
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

    public static SymbolicImage symbolicBreadthFirstSearch(
          TransitionSystem transitionSystem,
          ConstraintSolver solver){
    SymbolicRegion reachableRegion = 
            new SymbolicRegion(transitionSystem.getInitValuation());
    SymbolicRegion newRegion = 
            new SymbolicRegion(transitionSystem.getInitValuation());
    SymbolicRegionUtil regionUtil = new SymbolicRegionUtil(solver);
    SymbolicRegionSearchUtil searchUtil = 
            new SymbolicRegionSearchUtil(solver);
    //We start to count interation based on 1. 0 is skipped.
    int currentDepth = 0;
    StringBuilder reachedErrors = new StringBuilder();
    while(!newRegion.isEmpty()){
      ++currentDepth;
      SymbolicImage newImage = searchUtil.post(newRegion,
              transitionSystem);
      newImage.setDepth(currentDepth);
      reachedErrors = appendErrors(newImage, reachedErrors);
      SymbolicRegion nextReachableStates = newImage.getReachableStates();
      newRegion = regionUtil.difference(nextReachableStates,
              reachableRegion, solver);
      reachableRegion = regionUtil.disjunction(reachableRegion, newRegion);
      logState(newImage);
    }
    return new SymbolicImage(reachableRegion, reachedErrors, currentDepth);
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
}
