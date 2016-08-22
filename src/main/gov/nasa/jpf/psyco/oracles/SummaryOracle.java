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
package gov.nasa.jpf.psyco.oracles;

import de.learnlib.api.Query;
import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.constraints.api.ConstraintSolver.Result;
import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.jdart.constraints.Path;
import gov.nasa.jpf.jdart.constraints.PathState;
import gov.nasa.jpf.psyco.alphabet.SummaryAlphabet;
import gov.nasa.jpf.psyco.alphabet.SymbolicMethodSymbol;
import gov.nasa.jpf.psyco.learnlib.SymbolicExecutionOracle;
import gov.nasa.jpf.psyco.learnlib.SymbolicExecutionResult;
import gov.nasa.jpf.psyco.util.PathUtil;
import gov.nasa.jpf.psyco.util.PathUtil.PathQuery;
import java.util.ArrayList;
import java.util.Collection;
import net.automatalib.words.Word;

public class SummaryOracle implements SymbolicExecutionOracle {  
  
  private final SummaryAlphabet inputs;

  private final ConstraintSolver solver;

  public SummaryOracle(SummaryAlphabet inputs, ConstraintSolver solver) {
    this.inputs = inputs;
    this.solver = solver;
  }  
  
  @Override
  public void processQueries(
          Collection<? extends 
                Query<SymbolicMethodSymbol, SymbolicExecutionResult>> clctn) {
    for (Query<SymbolicMethodSymbol, SymbolicExecutionResult> query : clctn) {
      processQuery(query);
    }
  }
    
  private void processQuery(
          Query<SymbolicMethodSymbol, SymbolicExecutionResult> query) {
    
    ArrayList<Path> ok  = new ArrayList<>(); 
    ArrayList<Path> err = new ArrayList<>(); 
    ArrayList<Path> dk  = new ArrayList<>(); 
    
    Collection<PathQuery> paths = PathUtil.explode(query.getInput(), inputs);
    for (PathQuery q : paths) {      
      Path p = PathUtil.executeSymbolically(
              q.getMethods(), q.getPaths(), inputs.getInitialValuation());
      
      // TODO: maybe add model to path 
      if (!sat(p.getPathCondition())) {
        continue;
      }
      switch (p.getState()) {
        case OK:
          ok.add(p);
          break;
        case ERROR:
          err.add(p);
          break;
        case DONT_KNOW:
          dk.add(p);
          break;
      }
    }
    
    query.answer(new SymbolicExecutionResult(ok, err, dk));
  }

  private boolean sat(Expression<Boolean> expr) {
    return solver.isSatisfiable(expr) == Result.SAT;
  }
}