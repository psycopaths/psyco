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
package gov.nasa.jpf.psyco.filter;

import de.learnlib.api.Query;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.constraints.api.ConstraintSolver.Result;
import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.InterpolationSolver;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.api.ValuationEntry;
import gov.nasa.jpf.constraints.expressions.Constant;
import gov.nasa.jpf.constraints.util.ExpressionUtil;
import gov.nasa.jpf.jdart.constraints.Path;
import gov.nasa.jpf.jdart.constraints.PathResult;
import gov.nasa.jpf.jdart.constraints.PathState;
import gov.nasa.jpf.jdart.constraints.PostCondition;
import gov.nasa.jpf.psyco.alphabet.SummaryAlphabet;
import gov.nasa.jpf.psyco.alphabet.SymbolicMethodSymbol;
import gov.nasa.jpf.psyco.learnlib.SymbolicExecutionResult;
import gov.nasa.jpf.psyco.learnlib.SymbolicQueryOutput;
import gov.nasa.jpf.psyco.learnlib.ThreeValuedOracle;
import gov.nasa.jpf.psyco.util.PathUtil;
import gov.nasa.jpf.util.JPFLogger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.automatalib.words.Word;

/**
 *
 * @author falkhowar
 */
public class InterpolationCache implements ThreeValuedOracle {
  
  private static JPFLogger logger = JPF.getLogger("psyco");
  
  private static class SuffixCache {
    private Expression<Boolean> ok;
    private Expression<Boolean> error;
    private Expression<Boolean> dontKnow;
  }
  
  private class ExecutionTree {
  
    private final Word<Path> pPrefix;
    
    private final Word<SymbolicMethodSymbol> sPrefix;
    
    private final SymbolicQueryOutput output;
    
    private final List<Path> infeasible = new ArrayList<>();
    
    private final Collection<ExecutionTree> feasible = new ArrayList<>();
    
    public ExecutionTree(Word<Path> pPrefix, Word<SymbolicMethodSymbol> sPrefix, 
            Word<SymbolicMethodSymbol> suffix) {
      this.pPrefix = pPrefix;
      this.sPrefix = sPrefix;
      // end of recursion
      if (suffix.length() < 1 || (pPrefix.length() > 0 
              && pPrefix.lastSymbol().getState() != PathState.OK)) {        
        this.output = SymbolicQueryOutput.forPath(pPrefix.lastSymbol());
      } else {
        // try to use cache
        SymbolicQueryOutput out = lookup(pPrefix, sPrefix, suffix);
        if (out.isUniform()) {
          this.output = out;
        } else {
          // compute sub-trees
          this.output = initialize(suffix);
          if (this.output.isUniform()) {
            // update cache
            update(pPrefix, sPrefix, suffix, this);
          }
        }
      }
    }
    
    public List<Path> getInfeasiblePaths() {
      List<Path> ret = new ArrayList<>(this.infeasible);
      for (ExecutionTree t : feasible) {
        ret.addAll(t.getInfeasiblePaths());        
      }
      return ret;
    }
    
    public SymbolicQueryOutput getOutput() {
      return this.output;
    }
    
    private SymbolicQueryOutput initialize(Word<SymbolicMethodSymbol> suffix) {
      
      List<SymbolicQueryOutput> outputs = new ArrayList<>();
      SymbolicMethodSymbol next = suffix.firstSymbol();    
      SymbolicExecutionResult summary = inputs.getSummary(next);    
      for (Path p : summary) {
        // TODO: join path with
        Word<Path> pNext = pPrefix.append(p);
        Word<SymbolicMethodSymbol> sNext = sPrefix.append(next);        
        Path joined = PathUtil.executeSymbolically(sNext, pNext, initial);
        if (!sat(joined.getPathCondition())) {
          this.infeasible.add(p);
          continue;
        }

        ExecutionTree child = new ExecutionTree(pNext, sNext, suffix.subWord(1));
        outputs.add(child.output);
        // TODO: is the key important?  
        this.feasible.add(child);
      }
      
      return new SymbolicQueryOutput(outputs.toArray(
              new SymbolicQueryOutput[] {}));
    }
    
  }
  
  
  
  private final Map<Word<SymbolicMethodSymbol>, SuffixCache> cache = new HashMap<>();
  
  private final InterpolationSolver iSolver;
  
  private final ConstraintSolver cSolver;
  
  private final Valuation initial;
  
  private final SummaryAlphabet inputs;

  public InterpolationCache(InterpolationSolver iSolver, ConstraintSolver cSolver, SummaryAlphabet inputs) {
    this.iSolver = iSolver;
    this.cSolver = cSolver;
    this.inputs = inputs;
    this.initial = this.inputs.getInitialValuation();
  }


  @Override
  public void processQueries(
          Collection<? extends Query<SymbolicMethodSymbol, SymbolicQueryOutput>> clctn) {

    for (Query<SymbolicMethodSymbol, SymbolicQueryOutput> q : clctn) {
      processQuery(q);
    }
  }
  
  private void processQuery(Query<SymbolicMethodSymbol, SymbolicQueryOutput> query) {        
    Word<SymbolicMethodSymbol> sEps = Word.epsilon();
    Word<Path> pEps = Word.epsilon();    
    ExecutionTree tree = new ExecutionTree(pEps, sEps, query.getInput());
    query.answer(tree.getOutput());
  }

  private SymbolicQueryOutput lookup(Word<Path> pPrefix, Word<SymbolicMethodSymbol> sPrefix,
          Word<SymbolicMethodSymbol> suffix) {    
    // TODO: try to find a cached result
    return SymbolicQueryOutput.NONE;
  }
  
  private void update(Word<Path> pPrefix, Word<SymbolicMethodSymbol> sPrefix, 
          Word<SymbolicMethodSymbol> suffix, ExecutionTree tree) {

    Path joined;
    if (sPrefix.length() < 1) {
      joined = new Path(ExpressionUtil.TRUE, PathResult.ok(
              null, asPostCondition(initial)));
    } else {
      joined = PathUtil.executeSymbolically(sPrefix, pPrefix, initial);      
    }    
          
    System.out.println();    
    System.out.println("Searching for interpolant after: " + joined);
    SymbolicQueryOutput out = tree.getOutput();
    for (Path p : tree.getInfeasiblePaths()) {
      if (!SymbolicQueryOutput.forPath(p).equals(out)) {
        System.out.println( "  " + p);
      }
    }    
    System.out.println();    
  }
  
  private boolean sat(Expression<Boolean> test) {
    return cSolver.isSatisfiable(test) == Result.SAT;
  }
  
  private PostCondition asPostCondition(Valuation initial) {
    PostCondition post = new PostCondition();
    for (ValuationEntry e : initial) {
      post.addCondition(e.getVariable(), 
              new Constant<>(e.getVariable().getType(), e.getValue()));
    }
    return post;
  }  
  
}
