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
package gov.nasa.jpf.psyco.alphabet;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import de.learnlib.oracles.DefaultQuery;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.constraints.api.ConstraintSolver.Result;
import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.expressions.Negation;
import gov.nasa.jpf.constraints.expressions.PropositionalCompound;
import gov.nasa.jpf.constraints.util.ExpressionUtil;
import gov.nasa.jpf.jdart.constraints.Path;
import gov.nasa.jpf.psyco.learnlib.SymbolicExecutionOracle;
import gov.nasa.jpf.psyco.learnlib.SymbolicExecutionResult;
import gov.nasa.jpf.psyco.learnlib.SymbolicQueryOutput;
import gov.nasa.jpf.psyco.util.PathUtil;
import gov.nasa.jpf.psyco.util.SEResultUtil;
import gov.nasa.jpf.util.JPFLogger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import net.automatalib.words.Word;

public class AlphabetRefiner {
  
  private static JPFLogger logger = JPF.getLogger("psyco");  

  private final SymbolicExecutionOracle oracle;
  
  private final SymbolicMethodAlphabet inputs;
  
  private final ConstraintSolver solver;

  public AlphabetRefiner(SymbolicExecutionOracle oracle, 
          SymbolicMethodAlphabet inputs, ConstraintSolver solver) {
    this.oracle = oracle;
    this.inputs = inputs;
    this.solver = solver;
  }

  public boolean refine(Word<SymbolicMethodSymbol> witness) {
  
    DefaultQuery<SymbolicMethodSymbol, SymbolicExecutionResult> query =
            new DefaultQuery<>(witness);
    oracle.processQueries(Collections.singleton(query));    
    SymbolicExecutionResult result = query.getOutput();
    SymbolicQueryOutput out = new SymbolicQueryOutput(result);
    if (out.isUniform()) {
      return false;
    }
     
    logger.finer("Execution result:" + result);    
    // refine single symbols
    boolean refined = false;
    int ppos = 1;
    for (SymbolicMethodSymbol sms : witness) {
      int pcount = sms.getArity();

      Expression<Boolean> ok  = projectResult(result.getOk(), ppos, pcount);
      Expression<Boolean> err = projectResult(result.getError(), ppos, pcount);
      Expression<Boolean> dk  = projectResult(result.getDontKnow(), ppos, pcount);

      Expression<Boolean> refiner = refineSymbol(sms, ok, err, dk);
      if (refiner != null) {
        refined = true;
        inputs.refine(sms, refiner);
      }
      ppos += pcount;
    }
    
    return refined;
  }
    
  private Expression<Boolean> refineSymbol(SymbolicMethodSymbol sms, 
          Expression<Boolean> ok, Expression<Boolean> error, Expression<Boolean> dontknow) {
   
    logger.finer("Refining: " + sms);
    logger.finer( ((SummaryAlphabet)inputs).getSummary(sms));
    logger.finer("  ok:  " + ok);
    logger.finer("  err: " + error);
    logger.finer("  dk:  " + dontknow);
    
    Expression<Boolean> precondition = sms.getPrecondition();
    Expression<Boolean> refinerOK  = getRefiner(precondition, ok);
    Expression<Boolean> refinerErr = getRefiner(precondition, error);

    int scoreOk = (refinerOK == null ? Integer.MAX_VALUE : refinerOK.toString().length());
    int scoreErr = (refinerErr == null ? Integer.MAX_VALUE : refinerErr.toString().length());
   
    if (scoreOk < scoreErr) {
      return refinerOK;
    } else {
      return refinerErr;
    }
  }
  
  
  private Expression<Boolean> projectResult(Collection<Path> paths, int ppos, int arity) {
    Predicate<Variable<?>> predicate = 
      SEResultUtil.interval(ppos, arity);

    Expression<Boolean> expr  = restrict(paths, predicate);
    if (expr == null) {
      return ExpressionUtil.FALSE;
    }  
    
    Function<String, String> shift = SEResultUtil.shift(ppos, 1, arity);
    expr = ExpressionUtil.renameVars(expr, shift);
    expr = SEResultUtil.stripLeadingTrue(expr);
    if (expr == null) {
      return ExpressionUtil.TRUE;
    }      
    return expr;    
  }
          
  
  private Expression<Boolean> restrict(Collection<Path> paths, 
          Predicate<Variable<?>> predicate) {
    
    Expression<Boolean> ret = null;
    for (Path p : paths) {
      Expression<Boolean> pc = p.getPathCondition();
      pc = removeTautologies(pc);
      pc = ExpressionUtil.restrict(pc, predicate);
      if (pc != null) {
        if (pc.equals(ExpressionUtil.TRUE)) {
          return pc;
        }

        if (ret == null || implies(pc, ret)) {
          ret = pc;
          continue;
        }
        
        if (implies(pc, ret)) {
          ret = pc;
          continue;
        }
        
        ret = ExpressionUtil.or(ret, pc);
      }
    }
    return ret;
  }

  private Expression<Boolean> getRefiner(Expression<Boolean> precondition, 
          Expression<Boolean> pathCondition) { 
 
    if (refines(precondition, pathCondition)) {
      return pathCondition;
    } 
    return null;
    
  }
   
  private Expression<Boolean> removeTautologies(Expression<Boolean> pc) {
    Collection<Expression<Boolean>> path = PathUtil.decomposePath(pc);
    ArrayList<Expression<Boolean>> retain = new ArrayList<>();
    for (Expression<Boolean> expr : path) {
      if (sat(new Negation(expr))) {
        retain.add(expr);
      }
    }
    
    if (retain.isEmpty()) {
      return ExpressionUtil.TRUE;
    }
    
    return ExpressionUtil.and(retain);
  }

  private boolean refines(Expression<Boolean> original, Expression<Boolean> refine) {
    Expression<Boolean> test1 = ExpressionUtil.and(original, new Negation(refine));
    Expression<Boolean> test2 = ExpressionUtil.and(original, refine);
    return sat(test1) && sat(test2);
  }
  
  private boolean implies(Expression<Boolean> phi1, Expression<Boolean> phi2) {
    Expression<Boolean> test1 = ExpressionUtil.and(phi1, new Negation(phi2));
    return !sat(test1);
  }

  private boolean sat(Expression<Boolean> test) {
    return solver.isSatisfiable(test) == Result.SAT;
  }

}
