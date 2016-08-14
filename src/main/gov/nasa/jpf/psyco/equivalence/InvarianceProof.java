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
package gov.nasa.jpf.psyco.equivalence;

import com.google.common.base.Function;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.expressions.Negation;
import gov.nasa.jpf.constraints.util.ExpressionUtil;
import gov.nasa.jpf.jdart.constraints.Path;
import gov.nasa.jpf.jdart.constraints.PostCondition;
import gov.nasa.jpf.psyco.alphabet.SummaryAlphabet;
import gov.nasa.jpf.psyco.alphabet.SymbolicMethodSymbol;
import gov.nasa.jpf.psyco.filter.ValidQueryFilter;
import gov.nasa.jpf.psyco.learnlib.SymbolicQueryOutput;
import gov.nasa.jpf.psyco.util.SEResultUtil;
import gov.nasa.jpf.util.JPFLogger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import net.automatalib.automata.transout.MealyMachine;
import net.automatalib.words.Word;

public class InvarianceProof {
  
  private static final JPFLogger logger = JPF.getLogger("psyco");
  
  private final SummaryAlphabet inputs;
    
  private final ConstraintSolver cSolver;
    
  private final MealyMachine<Object, SymbolicMethodSymbol, ?, SymbolicQueryOutput> model;
  
  private final Map<Object, Expression<Boolean>> predicates;

  public InvarianceProof(SummaryAlphabet inputs, ConstraintSolver cSolver, 
          MealyMachine<Object, SymbolicMethodSymbol, ?, SymbolicQueryOutput> model, 
          Map<Object, Expression<Boolean>> predicates) {
    
    this.inputs = inputs;
    this.cSolver = cSolver;
    this.model = model;
    this.predicates = predicates;
  }
  
  public boolean prove() {    
    return (proveInitial() && proveTransitions());
  }
 
  private boolean proveInitial() {
    
    System.err.println("Initial state:" + this.model.getInitialState());          
    System.err.println("Initial predicate:" + 
            this.predicates.get(this.model.getInitialState()));          
    
    if (!this.predicates.containsKey(this.model.getInitialState())) {
      System.err.println("No predicate for initial state.");          
      return false;
    }
    
    return sat(ExpressionUtil.and(this.predicates.get(this.model.getInitialState()),
            ExpressionUtil.valuationToExpression(this.inputs.getInitialValuation())));
  }

  private boolean proveTransitions() {
    
    Set<Object> visited = new HashSet<>();
    Queue<Word<SymbolicMethodSymbol>> queue = new LinkedList<>();
    Word<SymbolicMethodSymbol> eps = Word.epsilon();
    queue.add(eps);
    visited.add(this.model.getInitialState());   
    while (!queue.isEmpty()) {
      Word<SymbolicMethodSymbol> as = queue.poll();
      Object src = this.model.getState(as);
      for (SymbolicMethodSymbol a : this.inputs) {   
        Word<SymbolicMethodSymbol> nextAs = as.append(a);
        Object dst = this.model.getSuccessor(src, a);
        SymbolicQueryOutput out = this.model.getOutput(src, a);
        if (!ValidQueryFilter.isValid(nextAs)) {
          if (out.equals(SymbolicQueryOutput.ERROR)) {
            continue;
          }
          System.err.println("Counterexample: " + nextAs);          
          return false;
        }
               
        boolean ok = transitionOk(
                this.predicates.get(src), nextAs, out, this.predicates.get(dst));
        
        if (!ok) {
          return false;
        }
        
        if (out.equals(SymbolicQueryOutput.OK) && !visited.contains(dst)) {
          queue.add(nextAs);
          visited.add(dst);
        }
      }
    }
    return true;
  }
  
  private boolean transitionOk(Expression<Boolean> src, Word<SymbolicMethodSymbol> nextAs, 
          SymbolicQueryOutput out, Expression<Boolean> dest) {

    for (Path p : this.inputs.getSummary(nextAs.lastSymbol())) {
      // path unsatisfiable
      if (!sat(ExpressionUtil.and(src, p.getPathCondition()))) {
        continue;
      }
      
      // unconformance reachable
      if (!out.equals(SymbolicQueryOutput.forPath(p))) {
        Valuation mdl = new Valuation();
        cSolver.solve(ExpressionUtil.and(src, p.getPathCondition()), mdl);
        System.err.println("Unconformance reachable:" + 
                "\nPrefix: " +nextAs.prefix(-1) + 
                "\nState: " + src + 
                "\nStep: " +nextAs.suffix(1) + 
                "\nPath: " + p + " : " + out + 
                "\nModel: " + mdl);
        
        return false;
      }
      
      // error? then stop
      if (!out.equals(SymbolicQueryOutput.OK)) {
        return true;
      }
      
      if (dest == null) {
        System.err.println("Missing predicate for destination");
        return false;
      }
      
      // invariant?
      Expression<Boolean> invarTest = ExpressionUtil.and(
              src, p.getPathCondition(), new Negation(connect(p, dest)));
      
      if (sat(invarTest)) {
        System.err.println("Not an invariant:" + invarTest);
        return false;
      }
    }
    
    return true;
  }
  
  private Expression<Boolean> connect(Path p, Expression<Boolean> next) {

    PostCondition post = p.getPostCondition();
    Map<Variable<?>, Expression<?>> map = new HashMap<>();
    for (Entry<Variable<?>, Expression<?>> e : post.getConditions().entrySet()) {
      map.put(e.getKey(), e.getValue());
    }
   
    Function<Variable<?>, Expression<?>> repl = SEResultUtil.func(map);
    return ExpressionUtil.transformVars(next, repl);      
  }
  
  private boolean sat(Expression<Boolean> expr) {
    return cSolver.isSatisfiable(expr) == ConstraintSolver.Result.SAT;
  }
  
}
