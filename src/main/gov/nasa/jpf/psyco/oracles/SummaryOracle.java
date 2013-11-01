/*******************************************************************************
 * Copyright (C) 2008 United States Government as represented by the
 * Administrator of the National Aeronautics and Space Administration
 * (NASA).  All Rights Reserved.
 * 
 * This software is distributed under the NASA Open Source Agreement
 * (NOSA), version 1.3.  The NOSA has been approved by the Open Source
 * Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
 * directory tree for the complete NOSA document.
 * 
 * THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
 * KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
 * LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
 * SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
 * A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
 * THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
 * DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
 ******************************************************************************/
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
import java.util.ArrayList;
import java.util.Collection;
import net.automatalib.words.Word;

/**
 *
 */
public class SummaryOracle implements SymbolicExecutionOracle {  
  
  private static class PathQuery {
    final Word<SymbolicMethodSymbol> methods;
    final Word<Path> paths;
    public PathQuery(Word<SymbolicMethodSymbol> methods, Word<Path> paths) {
      this.methods = methods;
      this.paths = paths;
    }   
  }
 
  private final SummaryAlphabet inputs;

  private final ConstraintSolver solver;

  public SummaryOracle(SummaryAlphabet inputs, ConstraintSolver solver) {
    this.inputs = inputs;
    this.solver = solver;
  }  
  
  @Override
  public void processQueries(
          Collection<? extends Query<SymbolicMethodSymbol, SymbolicExecutionResult>> clctn) {
    for (Query<SymbolicMethodSymbol, SymbolicExecutionResult> query : clctn) {
      processQuery(query);
    }
  }
    
  private void processQuery(
          Query<SymbolicMethodSymbol, SymbolicExecutionResult> query) {
    
    ArrayList<Path> ok  = new ArrayList<>(); 
    ArrayList<Path> err = new ArrayList<>(); 
    ArrayList<Path> dk  = new ArrayList<>(); 
    
    Collection<PathQuery> paths = explode(query.getInput());
    for (PathQuery q : paths) {      
      Path p = PathUtil.executeSymbolically(
              q.methods, q.paths, inputs.getInitialValuation());
      
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

  private Collection<PathQuery> explode(Word<SymbolicMethodSymbol> in) {
    ArrayList<PathQuery> queries = new ArrayList<>();
    Word<Path> eps = Word.epsilon();
    explode(in, 0, eps, queries);
    return queries;
  }
  
  private void explode(Word<SymbolicMethodSymbol> in, int pos, 
          Word<Path> prefix, Collection<PathQuery> queries) {
    
    SymbolicMethodSymbol a = in.getSymbol(pos);
    SymbolicExecutionResult summary = this.inputs.getSummary(a);
    pos++;
    
    for (Path p : summary) {
      if (p.getState() == PathState.OK && pos < in.length()) {
        explode(in, pos, prefix.append(p), queries);
      } else {
        queries.add(new PathQuery(
                (pos < in.length() ? in.prefix(pos) : in), prefix.append(p)));
      } 
    }   
  }
  
  private boolean sat(Expression<Boolean> expr) {
    return solver.isSatisfiable(expr) == Result.SAT;
  }  
}
