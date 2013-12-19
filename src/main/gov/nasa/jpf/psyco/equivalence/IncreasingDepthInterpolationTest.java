package gov.nasa.jpf.psyco.equivalence;

import de.learnlib.oracles.DefaultQuery;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.constraints.api.ConstraintSolver.Result;
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
import gov.nasa.jpf.util.JPFLogger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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
  
  private static final JPFLogger logger = JPF.getLogger("psyco");

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
    
    try {      
      while (true) {
        ce = check(k);
        if (ce != null) {
          return ce;
        }
        logger.info("==== completed depth " + k);
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
    for (SymbolicMethodSymbol a : this.inputs) {
    
      // cache lookup
      
      interpolants.add(expand(prefix, path, a, depth-1));
    
      // cache update
    
    }
    return ExpressionUtil.and(interpolants);
  }
  
  private Expression<Boolean> expand(Word<SymbolicMethodSymbol> prefix, 
          Word<Path> path, SymbolicMethodSymbol a, int depth) throws CounterExampleFound {
    
    SymbolicExecutionResult summary = this.inputs.getSummary(a);
    Object state = model.getState(prefix);
    SymbolicQueryOutput out = model.getOutput(state, a);
    Word nextPrefix = prefix.append(a);
    //SymbolicQueryOutput 
    
    if (!ValidQueryFilter.isValid(nextPrefix)) {
      if (out.equals(SymbolicQueryOutput.ERROR)) {
        return ExpressionUtil.TRUE;
      }
      throw new CounterExampleFound(nextPrefix);
    }
    
    List<Expression<Boolean>> interpolants = new ArrayList<>();
    for (Path p : summary) {
      Word<Path> nextPath = path.append(p);
      boolean sat = sat(nextPrefix, nextPath);
      boolean conforms = conforms(p, out);
      // found ce?
      if (sat && !conforms) {
        logger.finer("Found counerexample: " + nextPrefix + " : " + 
                out + " : " + SymbolicQueryOutput.forPath(p));
        throw new CounterExampleFound(nextPrefix);
      }
      
      if (depth < 1 || !p.getState().equals(PathState.OK)) {
         if (!sat && !conforms) {
           interpolants.add(p.getPathCondition());
         }
      } else {
          interpolants.add(new Negation(expand(nextPrefix, nextPath, depth)));
      }
    }
    
    return interpolate(prefix, path, 
            ExpressionUtil.or(interpolants.toArray(new Expression[] {})));
  }
  
  private Expression<Boolean> interpolate(Word<SymbolicMethodSymbol> prefix, Word<Path> path, 
          Expression<Boolean> unsatUnconformant) {
    
    if (prefix.length() >= 1) {

      Path p = PathUtil.executeSymbolically(
            prefix, path, this.inputs.getInitialValuation());
      
      List<Expression<Boolean>> terms = new ArrayList<>();
      terms.add(ExpressionUtil.and(
              p.getPathCondition(), asExpression(p.getPostCondition())) );
      terms.add(unsatUnconformant);

      System.err.println(Arrays.toString(terms.toArray()));
      
      List<Expression<Boolean>> itp = iSolver.getInterpolants(terms);
      
      System.err.println(Arrays.toString(itp.toArray()));
    
      //throw new RuntimeException();
    }
    
    
    
    return ExpressionUtil.TRUE;
  }  
  
  private Expression<Boolean> asExpression(PostCondition post) {
    List<Expression<Boolean>> list = new ArrayList<>();
    for (Entry<Variable<?>, Expression<?>> e : post.getConditions().entrySet()) {
      if(BuiltinTypes.BOOL.equals(e.getKey().getType())) {
        list.add(new PropositionalCompound( 
                (Expression<Boolean>) e.getKey(), 
                LogicalOperator.EQUIV, 
                (Expression<Boolean>) e.getValue()));
      } else {
         list.add(new NumericBooleanExpression( 
                (Expression) e.getKey(), 
                 NumericComparator.EQ, 
                (Expression) e.getValue()));       
      }
    }
    return ExpressionUtil.and(list.toArray(new Expression[] {}));
  }
  
  
  private boolean sat(Word<SymbolicMethodSymbol> word, Word<Path> path) {
    Path combined = PathUtil.executeSymbolically(
            word, path, this.inputs.getInitialValuation());
    
    logger.finer("Path: " + combined);
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
