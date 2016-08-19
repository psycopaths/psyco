/*
 * Copyright (C) 2015, United States Government, as represented by the 
 * Administrator of the National Aeronautics and Space Administration.
 * All rights reserved.
 *
 * The PSYCO: A Predicate-based Symbolic Compositional Reasoning environment 
 * platform is licensed under the Apache License, Version 2.0 (the "License"); you 
 * may not use this file except in compliance with the License. You may obtain a 
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0. 
 *
 * Unless required by applicable law or agreed to in writing, software distributed 
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 * specific language governing permissions and limitations under the License.
 */
package gov.nasa.jpf.psyco.search.transitionSystem;

import gov.nasa.jpf.psyco.search.util.HelperMethods;
import gov.nasa.jpf.constraints.api.ConstraintSolver.Result;
import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.api.ValuationEntry;
import gov.nasa.jpf.constraints.util.ExpressionUtil;
import gov.nasa.jpf.psyco.search.datastructures.searchImage.EnumerativeImage;
import gov.nasa.jpf.psyco.search.datastructures.searchImage.StateImage;
import gov.nasa.jpf.psyco.search.datastructures.region.EnumerativeRegion;
import gov.nasa.jpf.psyco.search.datastructures.state.EnumerativeState;
import java.util.HashSet;
import java.util.Set;

public class EnumerativeTransitionHelper extends TransitionHelper {

  @Override
  public StateImage applyTransition(StateImage image, Transition transition) {
    if (image instanceof EnumerativeImage) {
      EnumerativeImage currentSearchStatus = (EnumerativeImage) image;
      int depth = currentSearchStatus.getDepth();
      EnumerativeRegion newRegion = new EnumerativeRegion();
      for (EnumerativeState state
              : currentSearchStatus.getPreviousNewStates().values()) {
        if (satisfiesGuardCondition(state, transition, depth)) {
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

  private EnumerativeState executeTransition(Transition transition,
          EnumerativeState state) {
    Expression resultingExpression = state.toExpression();
    Expression transitionEffects
            = transition.getTransitionEffectAsTransition();
    resultingExpression
            = ExpressionUtil.and(resultingExpression, transitionEffects);
    logger.finest("gov.nasa.jpf.psyco.search.transitionSystem."
            + "EnumerativeTransitionHelper.executeTransition()");
    logger.finest(resultingExpression.toString());
    Set oldVariables = new HashSet(transition.stateVariables);
    Valuation result = new Valuation();
    Result res = solver.solve(resultingExpression, result);
    logger.finest("Valuation: " + result.toString());
    Valuation filtered = new Valuation();
    for (ValuationEntry entry : result) {
      if (!oldVariables.contains(entry.getVariable())) {
        filtered.addEntry(entry);
      }
    }
    if (res == Result.SAT) {
      return new EnumerativeState(filtered);
    } else {
      throw new IllegalStateException("Solver could not SAT state result.");
    }
  }
}