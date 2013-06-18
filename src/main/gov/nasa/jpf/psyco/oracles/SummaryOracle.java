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

import gov.nasa.jpf.constraints.api.*;
import gov.nasa.jpf.constraints.api.ConstraintSolver.Result;
import gov.nasa.jpf.constraints.expressions.Constant;
import gov.nasa.jpf.constraints.expressions.LogicalOperator;
import gov.nasa.jpf.constraints.expressions.NumericBooleanExpression;
import gov.nasa.jpf.constraints.expressions.NumericComparator;
import gov.nasa.jpf.constraints.expressions.PropositionalCompound;
import gov.nasa.jpf.constraints.util.ExpressionRestrictor;
import gov.nasa.jpf.jdart.constraints.PostCondition;
import gov.nasa.jpf.learn.basic.ThreeValues;
import gov.nasa.jpf.testing.summaries.MethodSummary;
import gov.nasa.jpf.testing.summaries.MethodSummary.PathState;
import java.util.*;

/**
 *
 */
public class SummaryOracle {
  
  private class QueueElement {
    
    private int length;
    private int paramCount;
    private Expression<Boolean> prefix;

    public QueueElement(int length, int paramCount, Expression<Boolean> prefix) {
      this.length = length;
      this.paramCount = paramCount;
      this.prefix = prefix;
    }       
  }

  private Valuation init;
  
  
  public SummaryOracle(Valuation init, ConstraintSolver solver, MinMax mm) {
    this.init = init;
    this.solver = solver;
    this.minMax = mm;
  }
  
  private ConstraintSolver solver;
  
  private MinMax minMax;
  
  public Set<ThreeValues> query(List<MethodSummary> sequence, Expression<Boolean> precondition) {
    
    Set<ThreeValues> solution = new HashSet<ThreeValues>();
        
    Set<Variable> globals = new HashSet<Variable>();
    Set<Variable> params = new HashSet<Variable>();
    // FIXME: replace by some information obtained from setup description??
    extractNamesFromPrecondition(precondition, globals, params);
    for (MethodSummary ms : sequence) {
      for (MethodSummary.MethodPath p : ms.getOkPaths()) {
        extractNamesFromPrecondition(p.getPathConstraint(), globals, params);        
      }
      for (MethodSummary.MethodPath p : ms.getErrorPaths()) {
        extractNamesFromPrecondition(p.getPathConstraint(), globals, params);        
      }
      for (MethodSummary.MethodPath p : ms.getDontKnowPaths()) {
        extractNamesFromPrecondition(p.getPathConstraint(), globals, params);        
      }
    }
    
    // FIXME: put initial conditions into formula (concrete data values for globals)
    Expression<Boolean> initial = new Constant<Boolean>(Boolean.class, true);
            
    // construct set of feasible paths
    Queue<QueueElement> queue = new LinkedList<QueueElement>();    
    queue.add(new QueueElement(0, 0, initial));        
    while (!queue.isEmpty()) {
      
      // get path prefix from queue and next method from input
      QueueElement prefix = queue.poll();
      MethodSummary next = sequence.get(prefix.length);      
      int paramCount = next.getMethod().getParameterTypes().length;

      // get corresponding prefix of path condition
      ExpressionRestrictor pcr = new ExpressionRestrictor(getParamNames(1, prefix.paramCount + paramCount));           
      Expression preRestricted = pcr.restrict(precondition);
      
      // extend by all paths of next method
      List<MethodSummary.MethodPath> paths = new ArrayList<MethodSummary.MethodPath>();
      paths.addAll(next.getOkPaths());
      paths.addAll(next.getErrorPaths());
      paths.addAll(next.getDontKnowPaths());
      for (MethodSummary.MethodPath path : paths) {
        // rename parameters and globals in path segment
        Expression<Boolean> step = path.getPathConstraint();        
        step = prepareStep(step, prefix.length, prefix.paramCount, paramCount, globals);

        // check if path is satisfiable
        PropositionalCompound sat = new PropositionalCompound(
                prefix.prefix, LogicalOperator.AND, new PropositionalCompound(
                    step, LogicalOperator.AND, preRestricted));
        
        System.out.println("STEP (SAT?): " + sat);
         
        // not satisfiable => does not contribute to solution
        if (!solver.isSatisfiable(sat, minMax).equals(Result.SAT)) {
          continue;
        }
        
        // max length? then add path state to solution
        // OR path state other than ok? stop here
        if (prefix.length +1 >= sequence.size() 
                || !path.getPathState().equals(PathState.OK)) {
          
          switch (path.getPathState()) {            
            case OK:
              solution.add(ThreeValues.TRUE);
              break;
            case ERROR: 
              solution.add(ThreeValues.FALSE);
              break;
            case DONT_KNOW:
              solution.add(ThreeValues.THIRD);
              break;                  
          }
          continue;
        }
        
        // add longer prefix to queue ...
        Expression<Boolean> glue = prepareGlue(prefix.length, path.getPostConditon(), globals);
        PropositionalCompound prefixAndStep = new PropositionalCompound(
                prefix.prefix, LogicalOperator.AND, new PropositionalCompound(
                    step, LogicalOperator.AND, glue));

        System.out.println("NEW PREFIX: " + prefixAndStep);
        
        QueueElement ne = new QueueElement(
                prefix.length +1, prefix.paramCount + paramCount, prefixAndStep);
        
        queue.add(ne);
      } 
      
    }
    
    String log = "Solution: {";
    for (ThreeValues v : solution)
      log += v.toString() + ", ";
    log += "}";
    System.out.println(log);
    
    return solution;
  }

  
//  private Formula generateInitialValuation() {
//    LogicalExpression le = new LogicalExpression(LogicalOperator.AND);
//    for (Entry<String, Object> e : init.getValues().entrySet()) {
//      //FIXME: the cast of e.getValue() will fail in general       
//      le.addExpresion(new Atom(new LinearIntegerConstraint(
//               new SymbolicInteger(e.getKey() + "_0", e.getKey() + "_0", "xx"), Comparator.EQ, new IntegerConstant( (Integer) e.getValue() ))));       
//      
//    }
//    return le;
//  }
  
  
  private Set<Variable> getParamNames(int from, int to) {
    Set<Variable> list = new HashSet<Variable>();
    for (int i=from;i<=to;i++) {
      list.add(new Variable(Integer.class, "temp.MethodTestCase.P" + i));
    }
    return list;
  }
          
  
  private void extractNamesFromPrecondition(final Expression precondition, Set<Variable> globals, Set<Variable> params) {
//    System.out.println("### extract names");
    
    Set<Variable> vars = new HashSet<Variable>();
    precondition.getVariables(vars);
    for (Variable v : vars) {
      if(isParameter(v)) {
//        System.out.println("param: " + name);
        params.add(v);        
      } else {
        globals.add(v);
//        System.out.println("global: " + name);
      }      
    }    
  }
  
  private boolean isParameter(Variable v) {
    return v.getName().matches("temp.MethodTestCase.P\\d+");
  }
  
  
  private Expression<Boolean> prepareStep(Expression<Boolean> step, int stepNo, int prefixParams, int stepParams, Collection<Variable> globals) {
    
    Map<Expression,Expression> rename = new HashMap<Expression,Expression>();  
    for (Variable g : globals) {
      rename.put(g, new Variable(g.getType(),g.getName() + "_" + stepNo));
    }
    for (int i=1; i <=stepParams; i++) {
      rename.put(
              new Variable(Integer.class, "temp.MethodTestCase.P" + i), 
              new Variable(Integer.class, "temp.MethodTestCase.P" + (prefixParams + i)));
    }
    
    return step.replaceTerms(rename);
  }
  
  private Expression<Boolean> prepareGlue(int stepNo, PostCondition post, Collection<Variable> globals) {
    
    Map<Expression,Expression> rename = new HashMap<Expression,Expression>();  
    for (Variable g : globals) {
      rename.put(g, new Variable(g.getType(),g.getName() + "_" + stepNo));
    }
        
    Expression<Boolean> glue = null;
    for (Variable g : globals) {
      Expression right = post.getConditions().get(g).replaceTerms(rename);
//      if (post.getConditions().containsKey(g)) {
//        // FIXME: this cast can break in general
//      } else {
//        right = new SymbolicInteger(g + "_" +  stepNo, g + "_" + stepNo, "xx");
//      }
      
      NumericBooleanExpression add = new NumericBooleanExpression(
          new Variable(g.getType(), g.getName() + "_" + (stepNo+1)), NumericComparator.EQ, right);
      
      if (glue == null) {
        glue = add;
      } else {
        glue = new PropositionalCompound(glue, LogicalOperator.AND, add);
      }
    }   
    
    if (glue == null) {
      return new Constant<Boolean>(Boolean.class,true);
    }
            
    return glue;
  }
            
}
