/**
 * *****************************************************************************
 * Copyright (C) 2008 United States Government as represented by the
 * Administrator of the National Aeronautics and Space Administration (NASA).
 * All Rights Reserved.
 *
 * This software is distributed under the NASA Open Source Agreement (NOSA),
 * version 1.3. The NOSA has been approved by the Open Source Initiative. See
 * the file NOSA-1.3-JPF at the top of the distribution directory tree for the
 * complete NOSA document.
 *
 * THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY KIND,
 * EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT LIMITED TO, ANY
 * WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO SPECIFICATIONS, ANY
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
 * FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL BE
 * ERROR FREE, OR ANY WARRANTY THAT DOCUMENTATION, IF PROVIDED, WILL CONFORM TO
 * THE SUBJECT SOFTWARE.
 *****************************************************************************
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
