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
          List<Path> transitionSystem, Valuation init,
          ConstraintSolver solver){
    Logger logger = Logger.getLogger("psyco");
    Valuation res = new Valuation();
    //transform the initial valuation into a form with explicit values.
    //logger statements are needed.
//    logger.finest((solver.solve(
//            ExpressionUtil.valuationToExpression(init), res)).toString());
//    logger.finest(res.toString());
    SymbolicRegion reachableRegion = new SymbolicRegion(init);
    SymbolicRegion newRegion = new SymbolicRegion(init);
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
      SymbolicRegion nextReachableStates = newImage.getReachableStates();
      reachedErrors.append("In depth ");
      reachedErrors.append(currentDepth);
      reachedErrors.append(":\n");
      reachedErrors.append(newImage.getErrors());
      newRegion = regionUtil.difference(nextReachableStates,
              reachableRegion, solver);
      reachableRegion = regionUtil.disjunction(reachableRegion, newRegion);
      //print
      StringBuilder builder= new StringBuilder();
      try {
        newImage.setDepth(currentDepth);
        newImage.print(builder);
      } catch (IOException ex) {
        Logger.getLogger(SymbolicSearchEngine.class.getName()).log(Level.SEVERE, null, ex);
      }
      logger.fine(builder.toString());
    }
    return new SymbolicImage(reachableRegion, reachedErrors, currentDepth);
  }
}
