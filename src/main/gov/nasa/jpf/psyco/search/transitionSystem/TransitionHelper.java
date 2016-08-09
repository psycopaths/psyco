/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.transitionSystem;

import gov.nasa.jpf.psyco.search.SolverInstance;
import gov.nasa.jpf.psyco.search.datastructures.StateImage;

/**
 *
 * @author mmuesly
 */
public interface TransitionHelper {
  SolverInstance solver = SolverInstance.getInstance();
  public StateImage applyTransition(StateImage image, Transition transition);

  public StateImage applyError(StateImage alreadyReachedStates, Transition aThis);
}
