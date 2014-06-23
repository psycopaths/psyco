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
 *****************************************************************************/

package gov.nasa.jpf.psyco.interpolation;

import com.google.common.base.Function;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.InterpolationSolver;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.expressions.LogicalOperator;
import gov.nasa.jpf.constraints.expressions.Negation;
import gov.nasa.jpf.constraints.expressions.NumericBooleanExpression;
import gov.nasa.jpf.constraints.expressions.NumericComparator;
import gov.nasa.jpf.constraints.expressions.PropositionalCompound;
import gov.nasa.jpf.constraints.types.BuiltinTypes;
import gov.nasa.jpf.constraints.util.ExpressionUtil;
import gov.nasa.jpf.jdart.constraints.Path;
import gov.nasa.jpf.jdart.constraints.PathState;
import gov.nasa.jpf.jdart.constraints.PostCondition;
import gov.nasa.jpf.psyco.alphabet.SummaryAlphabet;
import gov.nasa.jpf.psyco.alphabet.SymbolicMethodSymbol;
import gov.nasa.jpf.psyco.equivalence.IncreasingDepthInterpolationTest;
import gov.nasa.jpf.psyco.exceptions.CounterexampleFound;
import gov.nasa.jpf.psyco.filter.ValidQueryFilter;
import gov.nasa.jpf.psyco.learnlib.SymbolicExecutionResult;
import gov.nasa.jpf.psyco.learnlib.SymbolicQueryOutput;
import gov.nasa.jpf.psyco.util.PathUtil;
import gov.nasa.jpf.psyco.util.SEResultUtil;
import gov.nasa.jpf.util.JPFLogger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.automatalib.automata.transout.MealyMachine;
import net.automatalib.words.Word;

/**
 *
 * @author falk
 */
public final class InterpolationUtil {

  private static final JPFLogger logger = JPF.getLogger("psyco");
  
  private final InterpolationSolver iSolver;
  private final ConstraintSolver cSolver;
  private final Valuation initial;
  private final SummaryAlphabet inputs;
  private final MealyMachine<Object, SymbolicMethodSymbol, ?, SymbolicQueryOutput> model;  
  private final InterpolationCache cache;

  
  public InterpolationUtil(InterpolationSolver iSolver, ConstraintSolver cSolver, SummaryAlphabet inputs, 
          MealyMachine<Object, SymbolicMethodSymbol, ?, SymbolicQueryOutput> model) {
    this.iSolver = iSolver;
    this.cSolver = cSolver;
    this.inputs = inputs;
    this.model = model;

    this.initial = this.inputs.getInitialValuation();
    this.cache = new InterpolationCache(cSolver);
  }
  
  
  /**
   * expands future after a prefix for some depth and computes
   * an interpolant characterizing program states for which the
   * model is conformant after the state reached by the prefix for 
   * this depth.
   * 
   * @param prefix
   * @param path
   * @param depth
   * @return
   * @throws CounterexampleFound 
   */
  public Expression<Boolean> expand(Word<SymbolicMethodSymbol> prefix, 
          Word<Path> path, int depth) throws CounterexampleFound {
    List<Expression<Boolean>> interpolants = new ArrayList<>();
    logger.finer("Expanding prefix " + prefix);
    Object state = model.getState(prefix);
    Expression<Boolean> itp = cache.lookup(depth, state, 
            InterpolationUtil.asExpression(prefix, path, this.inputs.getInitialValuation()));
    if (itp != null) {
      return itp;
    }

    for (SymbolicMethodSymbol a : this.inputs) {
      itp = expand(prefix, path, a, depth-1);
      interpolants.add(itp);
    }
    
    itp = ExpressionUtil.and(interpolants);
    cache.update(depth, state, itp);
    return itp;
  }
  
  private Expression<Boolean> expand(Word<SymbolicMethodSymbol> prefix, 
          Word<Path> path, SymbolicMethodSymbol a, int depth) throws CounterexampleFound {
    
    SymbolicExecutionResult summary = this.inputs.getSummary(a);
    Object state = model.getState(prefix);
    SymbolicQueryOutput out = model.getOutput(state, a);
    Word nextPrefix = prefix.append(a);
    
    if (!ValidQueryFilter.isValid(nextPrefix)) {
      if (out.equals(SymbolicQueryOutput.ERROR)) {
        return ExpressionUtil.TRUE;
      }
      throw new CounterexampleFound(nextPrefix);
    }
    
    List<Expression<Boolean>> interpolants = new ArrayList<>();
    for (Path p : summary) {
      // refinement may block complete paths
      if (cSolver.isSatisfiable(p.getPathCondition()) != ConstraintSolver.Result.SAT) {
        continue;
      }
      
      Word<Path> nextPath = path.append(p);
      boolean sat = sat(nextPrefix, nextPath);
      boolean conforms = conforms(p, out);
      // found ce?
      if (sat && !conforms) {
        logger.finer("Found counerexample: " + nextPrefix + " : " + 
                out + " : " + SymbolicQueryOutput.forPath(p));
        
        throw new CounterexampleFound(nextPrefix);
      }
      // found unsat error
      else if (!sat && !conforms) {
        Expression<Boolean> itp = interpolate(prefix, path, a, p.getPathCondition());
        interpolants.add(itp);         
      }
     // conforms
      else if (sat && conforms) {
        if (depth > 0 && p.getState().equals(PathState.OK)) {
            Expression<Boolean> itp = expand(nextPrefix, nextPath, depth);
            Expression<Boolean> error = InterpolationUtil.makeErrorCondition(p, itp);            
            interpolants.add(interpolate(prefix, path, a, error));
        }
      }
    }
    
    Expression<Boolean> itp = ExpressionUtil.and(
            interpolants.toArray(new Expression[] {}));
    
    //assert (cSolver.isSatisfiable(itp) == Result.SAT);
    return itp;
  }
  

  private boolean sat(Word<SymbolicMethodSymbol> word, Word<Path> path) {
    Path combined = PathUtil.executeSymbolically(
            word, path, this.inputs.getInitialValuation());
    
    ConstraintSolver.Result sat = cSolver.isSatisfiable(combined.getPathCondition());
    return sat == ConstraintSolver.Result.SAT;
  }
       
  private boolean conforms(Path p, SymbolicQueryOutput out) {
    return out.equals(SymbolicQueryOutput.forPath(p));
  }  
  
  
  /**
   * calls the interpolation solver to create an interpolant between (prefix, path)
   * and the unsatisfiable and unconformat paths of a. 
   * 
   * @param prefix
   * @param path
   * @param a
   * @param unsatUnconformant
   * @param iSolver
   * @param initial
   * @return 
   */
  public Expression<Boolean> interpolate(Word<SymbolicMethodSymbol> prefix, Word<Path> path, 
          SymbolicMethodSymbol a, Expression<Boolean> unsatUnconformant) {

    List<Expression<Boolean>> terms = new ArrayList<>();
    terms.add(asExpression(prefix, path, initial));         

    int ppos = 1;
    for (SymbolicMethodSymbol s : prefix) {
      ppos += s.getArity();
    }
    
    Function<String, String> shift = SEResultUtil.shift(1, ppos, a.getArity());        
    unsatUnconformant = ExpressionUtil.renameVars(unsatUnconformant, shift);   
    terms.add(unsatUnconformant);
    
    List<Expression<Boolean>> itp = iSolver.getInterpolants(terms);
    return itp.get(0);
  }  
  
  /**
   * creates an error condition from a path with precondition p and postcondition q
   * and an interpolant over-approximating OK paths after (p,q).
   * 
   * Returns: (p and (not (itp[q_r/q_l])))
   * 
   * @param p
   * @param itp
   * @return 
   */
  public static Expression<Boolean> makeErrorCondition(Path p, Expression<Boolean> itp) {

    PostCondition post = p.getPostCondition();
    Map<Variable<?>, Expression<?>> map = new HashMap<>();
    for (Map.Entry<Variable<?>, Expression<?>> e : post.getConditions().entrySet()) {
      map.put(e.getKey(), e.getValue());
    }
   
    Function<Variable<?>, Expression<?>> repl = SEResultUtil.func(map);
    itp = ExpressionUtil.transformVars(itp, repl);    
 
    Expression<Boolean> error = ExpressionUtil.and(
            p.getPathCondition(), new Negation(itp));
    
    return error;
  }
  
  /**
   * creates one expression, concatenating and eliminating all references to
   * state variables along the path. State variables may remain from the 
   * computed post condition, which is also part of the expression.
   * 
   * @param prefix
   * @param path
   * @param initial
   * @return 
   */
  public static Expression<Boolean> asExpression(Word<SymbolicMethodSymbol> prefix, 
          Word<Path> path, Valuation initial) {      
      if (prefix.length() < 1) {
        return ExpressionUtil.valuationToExpression(initial);
      }
    
      Path p = PathUtil.executeSymbolically(prefix, path, initial);      
      return ExpressionUtil.and(p.getPathCondition(), asExpression(p.getPostCondition()) );    
  }
  
  /**
   * turns a post condition into an expression
   * 
   * @param post
   * @return 
   */
  public static Expression<Boolean> asExpression(PostCondition post) {
    List<Expression<Boolean>> list = new ArrayList<>();
    for (Map.Entry<Variable<?>, Expression<?>> e : post.getConditions().entrySet()) {
      list.add(makeEquals(e.getKey(), e.getValue()));
    }
    return ExpressionUtil.and(list.toArray(new Expression[] {}));
  }
  
  /**
   * creates an equality or equivalence for the arguments
   * 
   * @param var
   * @param expr
   * @return 
   */
  public static Expression<Boolean> makeEquals(Variable var, Expression expr) {
      if(BuiltinTypes.BOOL.equals(var.getType())) {
        return new PropositionalCompound( 
                (Expression<Boolean>) var, 
                LogicalOperator.EQUIV, 
                (Expression<Boolean>) expr);
      } else {
         return new NumericBooleanExpression( 
                (Expression) var, 
                 NumericComparator.EQ, 
                (Expression) expr);       
      }    
  }

  /**
   * @return the cache
   */
  public InterpolationCache getCache() {
    return cache;
  }
    
}
