/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.transitionSystem;

import gov.nasa.jpf.constraints.api.ConstraintSolver.Result;
import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.util.ExpressionUtil;
import gov.nasa.jpf.psyco.search.SolverInstance;
import gov.nasa.jpf.psyco.search.datastructures.StateImage;
import gov.nasa.jpf.psyco.search.region.Region;
import gov.nasa.jpf.psyco.search.region.State;
import gov.nasa.jpf.util.JPFLogger;
import java.util.logging.Logger;

/**
 *
 * @author mmuesly
 */
public abstract class TransitionHelper {
  protected SolverInstance solver = SolverInstance.getInstance();
  protected final Logger logger = 
          JPFLogger.getLogger(HelperMethods.getLoggerName());
  public abstract StateImage applyTransition(StateImage image, Transition transition);

  public StateImage applyError(StateImage searchStatus,
          Transition transition) {
      int depth = searchStatus.getDepth();
      String error = transition.getError();
      Region<?, State<?>> reachableStates = searchStatus.getReachableStates();
      for(State state: reachableStates.values()){
        if(satisfiesGuardCondition(state, transition, depth)
                && !transition.isReached()){
          transition.setIsReached(true);
          searchStatus.addErrorInCurrentDepth(error);
        }
      }
    return searchStatus;
  }

  protected boolean satisfiesGuardCondition(State state,
          Transition transition, int depth) throws IllegalStateException{
    Expression guardTest = state.toExpression();
    guardTest = ExpressionUtil.and(guardTest, transition.getGuardCondition());
    Result res = solver.isSatisfiable(guardTest);
    if(null != res){
      switch (res) {
        case SAT:
          return true;
        case DONT_KNOW:
          throw new IllegalStateException(
                  "Cannot handle DONT_KNOW in the guard test.");
        default:
          return false;
      }
    }
    throw new IllegalStateException(
            "The solver result is not allowed to be null.");
  }
}
