package gov.nasa.jpf.psyco.search.transitionSystem;

import gov.nasa.jpf.constraints.api.ConstraintSolver.Result;
import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.api.ValuationEntry;
import gov.nasa.jpf.constraints.util.ExpressionUtil;
import gov.nasa.jpf.psyco.search.datastructures.EnumerativeImage;
import gov.nasa.jpf.psyco.search.datastructures.StateImage;
import gov.nasa.jpf.psyco.search.region.EnumerativeRegion;
import gov.nasa.jpf.psyco.search.region.EnumerativeState;
import java.util.HashSet;
import java.util.Set;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mmuesly
 */
//public class EnumerativeTransitionHelper{}
public class EnumerativeTransitionHelper extends TransitionHelper{

  @Override
  public StateImage applyTransition(StateImage image, Transition transition) {  
    if(image instanceof EnumerativeImage){
      EnumerativeImage currentSearchStatus = (EnumerativeImage) image;
      int depth = currentSearchStatus.getDepth();
      EnumerativeRegion newRegion = new EnumerativeRegion();
      for(EnumerativeState state: 
              currentSearchStatus.getPreviousNewStates().values()){
        if(satisfiesGuardCondition(state, transition, depth)){
          transition.setIsReached(true);
          EnumerativeState newState = executeTransition(transition, state);
          newRegion.put(HelperMethods.getUniqueStateName(), newState);
        }
      }
    currentSearchStatus.addNewStates(newRegion);
    return currentSearchStatus;
    }
    return null;
  }

  private EnumerativeState executeTransition(Transition transition, EnumerativeState state) {
    Expression resultingExpression = state.toExpression();
    Expression transitionEffects = transition.getTransitionEffectAsTransition();
    resultingExpression = 
            ExpressionUtil.and(resultingExpression, transitionEffects);
    logger.finest("gov.nasa.jpf.psyco.search.transitionSystem."
            + "EnumerativeTransitionHelper.executeTransition()");
    logger.finest(resultingExpression.toString());
    Set oldVariables = new HashSet(transition.stateVariables);
    Valuation result = new Valuation();
    Result res = solver.solve(resultingExpression, result);
    System.out.println("Valuation: " + result.toString());
    Valuation filtered = new Valuation();
    for(ValuationEntry entry: result){
      if(!oldVariables.contains(entry.getVariable())){
        filtered.addEntry(entry);
      }
    }
    if(res == Result.SAT){
      return new EnumerativeState(filtered);
    }else {
      throw new IllegalStateException("Solver could not SAT state result.");
    }
  }
}
