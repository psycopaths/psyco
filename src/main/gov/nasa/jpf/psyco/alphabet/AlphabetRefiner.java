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
package gov.nasa.jpf.psyco.alphabet;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import de.learnlib.oracles.DefaultQuery;
import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.constraints.api.ConstraintSolver.Result;
import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.expressions.Negation;
import gov.nasa.jpf.constraints.util.ExpressionUtil;
import gov.nasa.jpf.jdart.constraints.Path;
import gov.nasa.jpf.psyco.learnlib.SymbolicExecutionOracle;
import gov.nasa.jpf.psyco.learnlib.SymbolicExecutionResult;
import gov.nasa.jpf.psyco.learnlib.SymbolicQueryOutput;
import gov.nasa.jpf.psyco.util.PathUtil;
import gov.nasa.jpf.psyco.util.SEResultUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import net.automatalib.words.Word;

/**
 *
 * @author falkhowar
 */
public class AlphabetRefiner {
  
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
      // FIXME: we need to replace the implementation of restrict to check for mixed parameters.
      pc = ExpressionUtil.restrict(pc, predicate);
      if (pc != null) {
        ret = (ret != null && !ret.equals(ExpressionUtil.TRUE)) ? 
                ExpressionUtil.or(ret, pc) : pc;
      }
    }
    return ret;
  }

  private Expression<Boolean> getRefiner(Expression<Boolean> precondition, 
          Expression<Boolean> pathCondition) { 
  
    Collection<Expression<Boolean>> path = PathUtil.decomposePath(pathCondition);
    ArrayList<Expression<Boolean>> retain = new ArrayList<>();
    for (Expression<Boolean> expr : path) {
      if (refines(precondition, expr)) {
        retain.add(expr);
      }
    }
    
    if (retain.isEmpty()) {
      return null;
    }
    
    return ExpressionUtil.and(retain);
  }
    
  private boolean refines(Expression<Boolean> original, Expression<Boolean> refine) {
    Expression<Boolean> test1 = ExpressionUtil.and(original, new Negation(refine));
    Expression<Boolean> test2 = ExpressionUtil.and(original, refine);
    return sat(test1) && sat(test2);
  }

  private boolean sat(Expression<Boolean> test) {
    return solver.isSatisfiable(test) == Result.SAT;
  }
  
}
