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
package gov.nasa.jpf.psyco.interpolation;

import de.learnlib.statistics.HistogramDataSet;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.expressions.Negation;
import gov.nasa.jpf.constraints.util.ExpressionUtil;
import gov.nasa.jpf.util.JPFLogger;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author falk
 */
public class InterpolationCache {

  private static class StateCache {

    private final Map<Integer, Expression<Boolean>> ok = new HashMap<>();

    Expression<Boolean> lookup(int depth) {
      return ok.get(depth);
    }

    void update(int depth, Expression<Boolean> expr) {
      Expression<Boolean> state = ok.get(depth);
      state = (state == null) ? expr : ExpressionUtil.or(state, expr);
      ok.put(depth, state);
    }

    void clear() {
      ok.clear();
    }

  }

  private static final JPFLogger logger = JPF.getLogger("psyco");

  private final Map<Object, StateCache> caches = new HashMap<>();
  private final ConstraintSolver cSolver;
  private static HistogramDataSet misses;
  private static HistogramDataSet hits;

  public InterpolationCache(ConstraintSolver cSolver) {
    this.cSolver = cSolver;
    this.clear();
  }

  public Expression<Boolean> lookup(int depth, Object state, Expression<Boolean> expr) {
    StateCache sc = caches.get(state);
    if (sc == null) {
      misses.addDataPoint((long) depth);
      return null;
    }
    Expression<Boolean> cache = sc.lookup(depth);
    if (cache == null) {
      misses.addDataPoint((long) depth);
      return null;
    }

    if (hit(expr, cache)) {
      hits.addDataPoint((long) depth);
      return cache;
    }

    misses.addDataPoint((long) depth);
    return null;
  }

  public void update(int depth, Object state, Expression<Boolean> expr) {
    StateCache sc = caches.get(state);
    if (sc == null) {
      sc = new StateCache();
      this.caches.put(state, sc);
    }
    sc.update(depth, expr);
  }

  public final void clear() {
    this.caches.clear();
    if (misses == null) {
      misses = new HistogramDataSet("misses", "#");
      hits = new HistogramDataSet("hits", "#");
    }
  }

  private boolean hit(Expression<Boolean> path, Expression<Boolean> cache) {
    ConstraintSolver.Result res = cSolver.isSatisfiable(ExpressionUtil.and(
            path, new Negation(cache)));

    return res == ConstraintSolver.Result.UNSAT;
  }

  @Override
  public String toString() {
    return "" + misses.getDetails() + hits.getDetails();
  }

}
