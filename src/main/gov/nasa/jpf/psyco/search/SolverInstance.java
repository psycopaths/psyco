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
package gov.nasa.jpf.psyco.search;

import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.constraints.api.ConstraintSolver.Result;
import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Valuation;

/**
 * It is a singleton class, to provide the solver within the search algorithm.
 * The solve must be set in upfront.
 */
public class SolverInstance {

  private static SolverInstance instance = null;

  private ConstraintSolver solver = null;

  public static SolverInstance getInstance() {
    if (instance == null) {
      instance = new SolverInstance();
    }
    return instance;
  }

  public void setSolver(ConstraintSolver solver) {
    this.solver = solver;
  }

  public Result isSatisfiable(Expression expr) {
    return solver.isSatisfiable(expr);
  }

  public Result solve(Expression expr, Valuation res) {
    return solver.solve(expr, res);
  }
}