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
package gov.nasa.jpf.psyco.equivalence;

import com.google.common.base.Function;
import de.learnlib.oracles.DefaultQuery;
import de.learnlib.statistics.HistogramDataSet;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.constraints.api.ConstraintSolver.Result;
import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.InterpolationSolver;
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
import gov.nasa.jpf.jdart.termination.TerminationStrategy;
import gov.nasa.jpf.psyco.alphabet.SummaryAlphabet;
import gov.nasa.jpf.psyco.alphabet.SymbolicMethodSymbol;
import gov.nasa.jpf.psyco.exceptions.Terminate;
import gov.nasa.jpf.psyco.filter.ValidQueryFilter;
import gov.nasa.jpf.psyco.learnlib.SymbolicEquivalenceTest;
import gov.nasa.jpf.psyco.learnlib.SymbolicExecutionResult;
import gov.nasa.jpf.psyco.learnlib.SymbolicQueryOutput;
import gov.nasa.jpf.psyco.learnlib.ThreeValuedOracle;
import gov.nasa.jpf.psyco.util.PathUtil;
import gov.nasa.jpf.psyco.util.SEResultUtil;
import gov.nasa.jpf.util.JPFLogger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.automatalib.automata.transout.MealyMachine;
import net.automatalib.words.Word;

/**
 *
 * @author falk
 */
public class IncreasingDepthInterpolationTest implements SymbolicEquivalenceTest {
  
  private static class CounterExampleFound extends Exception {
    
    private final Word<SymbolicMethodSymbol> counterexample;

    public CounterExampleFound(Word<SymbolicMethodSymbol> counterexample) {
      this.counterexample = counterexample;
    }
  }
  
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
  
  private static class Cache {
    
    private final Map<Object, StateCache> caches = new HashMap<>();
    private final ConstraintSolver cSolver;
    private HistogramDataSet misses;
    private HistogramDataSet hits;
    

    public Cache(ConstraintSolver cSolver) {
      this.cSolver = cSolver;
      this.clear();
    }
    
    Expression<Boolean> lookup(int depth, Object state, Expression<Boolean> expr) {
      StateCache sc = caches.get(state);
      if (sc == null) {
        misses.addDataPoint( (long) depth);
        return null;
      }
      Expression<Boolean> cache = sc.lookup(depth);
      if (cache == null) {
        misses.addDataPoint( (long) depth);
        return null;
      }

      if (hit(expr, cache)) {
        hits.addDataPoint( (long) depth);
        return cache;
      }
      
      misses.addDataPoint( (long) depth);
      return null;
    }
    
    void update(int depth, Object state, Expression<Boolean> expr) {
      StateCache sc = caches.get(state);
      if (sc == null) {
        sc = new StateCache();
        this.caches.put(state, sc);
      }
      sc.update(depth, expr);
    }
    
    final void clear() {
      this.caches.clear();
      misses = new HistogramDataSet("misses", "#");
      hits = new HistogramDataSet("hits", "#");
    }
    
    private boolean hit(Expression<Boolean> path, Expression<Boolean> cache) {
      Result res = cSolver.isSatisfiable(ExpressionUtil.and(
              path, new Negation(cache)));
      
      return res == Result.UNSAT;
    }
    
    void log() {
      logger.info(misses.getDetails());
      logger.info(hits.getDetails());
    }
  }
  
  private static final JPFLogger logger = JPF.getLogger("psyco");

  private final Cache cache;
  
  private final int kMax;
  
  private final SummaryAlphabet inputs;

  private final ThreeValuedOracle oracle;
    
  private final ConstraintSolver cSolver;
  
  private final InterpolationSolver iSolver;

  private final TerminationStrategy termination;
  
  private int k = 2;
    
  private MealyMachine<Object, SymbolicMethodSymbol, ?, SymbolicQueryOutput> model;

  public IncreasingDepthInterpolationTest(int kMax, SummaryAlphabet inputs, 
          ThreeValuedOracle oracle, ConstraintSolver cSolver, InterpolationSolver iSolver, 
          TerminationStrategy termination) {
    this.kMax = kMax;
    this.inputs = inputs;
    this.oracle = oracle;
    this.cSolver = cSolver;
    this.iSolver = iSolver;
    this.termination = termination;
    this.cache = new Cache(cSolver);
  }

  public IncreasingDepthInterpolationTest(SummaryAlphabet inputs, 
          ThreeValuedOracle oracle, ConstraintSolver cSolver, InterpolationSolver iSolver, 
          TerminationStrategy termination) {
    this(-1, inputs, oracle, cSolver, iSolver, termination);
  }  
    
  @Override
  public void logStatistics() {
    logger.info("EQ Test depth completed: " + (k-1));
    logger.info("EQ Test max depth: " + kMax);
  }  

  @Override
  public DefaultQuery<SymbolicMethodSymbol, SymbolicQueryOutput> findCounterExample(
          MealyMachine<?, SymbolicMethodSymbol, ?, SymbolicQueryOutput> a, Collection<? extends SymbolicMethodSymbol> clctn) {

    this.model = (MealyMachine<Object, SymbolicMethodSymbol, ?, SymbolicQueryOutput>)a;
    DefaultQuery<SymbolicMethodSymbol, SymbolicQueryOutput> ce = null;
    this.cache.clear();
    
    try {      
      while (true) {
        ce = check(k);
        if (ce != null) {
          return ce;
        }
        logger.info("==== completed depth " + k);
        this.cache.log();
        k++;
        if (deepEnough()) {          
          return null;
        }
      }
      
    } catch (Terminate t) {
      return null;
    }
  }

  private DefaultQuery<SymbolicMethodSymbol, SymbolicQueryOutput> check(int k) {
    logger.fine("Checking conformance for depth " + k);
    try {
      Word<SymbolicMethodSymbol> eps = Word.epsilon();
      Word<Path> empty = Word.epsilon();
      expand(eps, empty, k);
    } catch (CounterExampleFound ce) {
      DefaultQuery<SymbolicMethodSymbol, SymbolicQueryOutput> ret = 
              new DefaultQuery<>(ce.counterexample);
      oracle.processQueries(Collections.singletonList(ret));
      return ret;
    }
    return null;
  }
  
  private Expression<Boolean> expand(Word<SymbolicMethodSymbol> prefix, 
          Word<Path> path, int depth) throws CounterExampleFound {
    List<Expression<Boolean>> interpolants = new ArrayList<>();
    logger.finer("Expanding prefix " + prefix);
    Object state = model.getState(prefix);
    Expression<Boolean> itp = cache.lookup(depth, state, asExpression(prefix, path));
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
          Word<Path> path, SymbolicMethodSymbol a, int depth) throws CounterExampleFound {
    
    SymbolicExecutionResult summary = this.inputs.getSummary(a);
    Object state = model.getState(prefix);
    SymbolicQueryOutput out = model.getOutput(state, a);
    Word nextPrefix = prefix.append(a);
    
    if (!ValidQueryFilter.isValid(nextPrefix)) {
      if (out.equals(SymbolicQueryOutput.ERROR)) {
        return ExpressionUtil.TRUE;
      }
      throw new CounterExampleFound(nextPrefix);
    }
    
    List<Expression<Boolean>> interpolants = new ArrayList<>();
    for (Path p : summary) {
      // refinement may block complete paths
      if (cSolver.isSatisfiable(p.getPathCondition()) != Result.SAT) {
        continue;
      }
      
      Word<Path> nextPath = path.append(p);
      boolean sat = sat(nextPrefix, nextPath);
      boolean conforms = conforms(p, out);
      // found ce?
      if (sat && !conforms) {
        logger.finer("Found counerexample: " + nextPrefix + " : " + 
                out + " : " + SymbolicQueryOutput.forPath(p));
        
        throw new CounterExampleFound(nextPrefix);
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
            Expression<Boolean> error = makeErrorCondition(p, itp);            
            interpolants.add(interpolate(prefix, path, a, error));
        }
      }
    }
    
    Expression<Boolean> itp = ExpressionUtil.and(
            interpolants.toArray(new Expression[] {}));
    
    //assert (cSolver.isSatisfiable(itp) == Result.SAT);
    return itp;
  }
  
  private Expression<Boolean> interpolate(Word<SymbolicMethodSymbol> prefix, Word<Path> path, 
          SymbolicMethodSymbol a, Expression<Boolean> unsatUnconformant) {

    List<Expression<Boolean>> terms = new ArrayList<>();
    terms.add(asExpression(prefix, path));         

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
  
  private Expression<Boolean> makeErrorCondition(Path p, Expression<Boolean> itp) {

    PostCondition post = p.getPostCondition();
    Map<Variable<?>, Expression<?>> map = new HashMap<>();
    for (Entry<Variable<?>, Expression<?>> e : post.getConditions().entrySet()) {
      map.put(e.getKey(), e.getValue());
    }
   
    Function<Variable<?>, Expression<?>> repl = SEResultUtil.func(map);
    itp = ExpressionUtil.transformVars(itp, repl);    
 
    Expression<Boolean> error = ExpressionUtil.and(
            p.getPathCondition(), new Negation(itp));
    
    return error;
  }
  
  private Expression<Boolean> asExpression(Word<SymbolicMethodSymbol> prefix, Word<Path> path) {      
      if (prefix.length() < 1) {
        return ExpressionUtil.valuationToExpression(
                this.inputs.getInitialValuation());        
      }
    
      Path p = PathUtil.executeSymbolically(
            prefix, path, this.inputs.getInitialValuation());
      
      return ExpressionUtil.and(p.getPathCondition(), asExpression(p.getPostCondition()) );    
  }
  
  private Expression<Boolean> asExpression(PostCondition post) {
    List<Expression<Boolean>> list = new ArrayList<>();
    for (Entry<Variable<?>, Expression<?>> e : post.getConditions().entrySet()) {
      list.add(asExpression(e.getKey(), e.getValue()));
    }
    return ExpressionUtil.and(list.toArray(new Expression[] {}));
  }
  
  private Expression<Boolean> asExpression(Variable var, Expression expr) {
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
  
  private boolean sat(Word<SymbolicMethodSymbol> word, Word<Path> path) {
    Path combined = PathUtil.executeSymbolically(
            word, path, this.inputs.getInitialValuation());
    
    Result sat = cSolver.isSatisfiable(combined.getPathCondition());
    return sat == Result.SAT;
  }
       
  private boolean conforms(Path p, SymbolicQueryOutput out) {
    return out.equals(SymbolicQueryOutput.forPath(p));
  }

  public int getCurrentK() {
    return k;
  }
  
  private boolean deepEnough() {
    return (kMax > 0 ) && (k > kMax);
  }

}
