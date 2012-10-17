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

import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.learn.basic.ThreeValues;
import gov.nasa.jpf.psyco.summaries.MethodSummary;
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
  
  
  public SummaryOracle(Valuation init) {
    this.init = init;
  }
  
  
  public Set<ThreeValues> query(List<MethodSummary> sequence, Expression<Boolean> precondition) {
    
//    Set<ThreeValues> solution = new HashSet<ThreeValues>();
//        
//    Set<String> globals = new HashSet<String>();
//    Set<String> params = new HashSet<String>();
//    // FIXME: replace by some information obtained from setup description??
//    extractNamesFromPrecondition(precondition, globals, params);
//    for (MethodSummary ms : sequence) {
//      for (MethodSummary.MethodPath p : ms.getOkPaths()) {
//        extractNamesFromPrecondition(p.getPathConstraint(), globals, params);        
//      }
//      for (MethodSummary.MethodPath p : ms.getErrorPaths()) {
//        extractNamesFromPrecondition(p.getPathConstraint(), globals, params);        
//      }
//      for (MethodSummary.MethodPath p : ms.getDontKnowPaths()) {
//        extractNamesFromPrecondition(p.getPathConstraint(), globals, params);        
//      }
//    }
//    
//    // FIXME: put initial conditions into formula (concrete data values for globals)
//    Formula initial = new TrueConstant();
//            
//    // construct set of feasible paths
//    Queue<QueueElement> queue = new LinkedList<QueueElement>();    
//    queue.add(new QueueElement(0, 0, initial));        
//    while (!queue.isEmpty()) {
//      
//      // get path prefix from queue and next method from input
//      QueueElement prefix = queue.poll();
//      MethodSummary next = sequence.get(prefix.length);      
//      int paramCount = next.getMethod().getParameterTypes().length;
//
//      // get corresponding prefix of path condition
//      ExpressionRestrictor pcr = new ExpressionRestrictor(getParamNames(1, prefix.paramCount + paramCount));
//      Formula preRestricted = pcr.walkOver(precondition);
//      
//      // extend by all paths of next method
//      List<MethodSummary.MethodPath> paths = new ArrayList<MethodSummary.MethodPath>();
//      paths.addAll(next.getOkPaths());
//      paths.addAll(next.getErrorPaths());
//      paths.addAll(next.getDontKnowPaths());
//      for (MethodSummary.MethodPath path : paths) {
//        // rename parameters and globals in path segment
//        Formula step = path.getPathConstraint();        
//        step = prepareStep(step, prefix.length, prefix.paramCount, paramCount, globals);
//
//        // check if path is satisfiable
//        LogicalExpression sat = new LogicalExpression(LogicalOperator.AND);
//        sat.addExpresion(prefix.prefix);
//        sat.addExpresion(step);
//        sat.addExpresion(preRestricted);
//        
//        System.out.println("STEP (SAT?): " + sat.sourcePC());
//         
//        // not satisfiable => does not contribute to solution
//        if (!sat.isSatisfiable()) {
//          continue;
//        }
//        
//        // max length? then add path state to solution
//        // OR path state other than ok? stop here
//        if (prefix.length +1 >= sequence.size() 
//                || !path.getPathState().equals(PathState.OK)) {
//          
//          switch (path.getPathState()) {            
//            case OK:
//              solution.add(ThreeValues.TRUE);
//              break;
//            case ERROR: 
//              solution.add(ThreeValues.FALSE);
//              break;
//            case DONT_KNOW:
//              solution.add(ThreeValues.THIRD);
//              break;                  
//          }
//          continue;
//        }
//        
//        // add longer prefix to queue ...
//        Formula glue = prepareGlue(prefix.length, path.getPostConditon(), globals);
//        
//        LogicalExpression prefixAndStep = new LogicalExpression(LogicalOperator.AND);
//        prefixAndStep.addExpresion(prefix.prefix);
//        prefixAndStep.addExpresion(step);
//        prefixAndStep.addExpresion(glue);
//
//        System.out.println("NEW PREFIX: " + prefixAndStep.sourcePC());
//        
//        QueueElement ne = new QueueElement(
//                prefix.length +1, prefix.paramCount + paramCount, prefixAndStep);
//        
//        queue.add(ne);
//      } 
//      
//    }
//    
//    String log = "Solution: {";
//    for (ThreeValues v : solution)
//      log += v.toString() + ", ";
//    log += "}";
//    System.out.println(log);
//    
//    return solution;
    return null;
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
//  
//  
//  private List<String> getParamNames(int from, int to) {
//    List<String> list = new ArrayList<String>();
//    for (int i=from;i<=to;i++) {
//      list.add("P" + i);
//    }
//    return list;
//  }
//          
//  
//  private void extractNamesFromPrecondition(final Formula precondition, Set<String> globals, Set<String> params) {
////    System.out.println("### extract names");
//    FormulaNameCollector pcn = new FormulaNameCollector();
//    pcn.walkOver(precondition);
//    Set<String> names = pcn.getNames();
//    for (String name : names) {
//      if(isParameter(name)) {
////        System.out.println("param: " + name);
//        params.add(name);        
//      } else {
//        globals.add(name);
////        System.out.println("global: " + name);
//      }      
//    }    
//  }
//  
//  private boolean isParameter(String name) {
//    return name.matches("P\\d+");
//  }
//  
//  
//  private Formula prepareStep(Formula step, int stepNo, int prefixParams, int stepParams, Collection<String> globals) {
//    
//    Map<String,String> rename = new HashMap<String,String>();  
//    for (String g : globals) {
//      rename.put(g, g + "_" + stepNo);
//    }
//    for (int i=1; i <=stepParams; i++) {
//      rename.put("P" + i, "P" + (prefixParams + i));
//    }
//    
//    FormulaRenamer r = new FormulaRenamer(rename);
//    return r.walkOver(step);
//  }
//  
//  private Formula prepareGlue(int stepNo, ConstraintsTree.PostCondition post, Collection<String> globals) {
//    
//    Map<String,String> rename = new HashMap<String,String>();  
//    for (String g : globals) {
//      rename.put(g, g + "_" + stepNo);
//    }
//        
//    LogicalExpression glue = new LogicalExpression(LogicalOperator.AND);    
//    for (String g : globals) {
//      IntegerExpression right;
//      if (post.getConditions().containsKey(g)) {
//        ExpressionRenamer expn = new ExpressionRenamer(rename);
//        // FIXME: this cast can break in general
//        right = expn.walkOver( (IntegerExpression) post.getConditions().get(g));
//      } else {
//        right = new SymbolicInteger(g + "_" +  stepNo, g + "_" + stepNo, "xx");
//      }
//      
//       glue.addExpresion(new Atom(new LinearIntegerConstraint(
//               new SymbolicInteger(g + "_" + (stepNo+1), g + "_" + (stepNo+1), "xx"), Comparator.EQ, right)));       
//    }   
//    
//    if (glue.getExpressions().isEmpty()) {
//      return new TrueConstant();
//    }
//            
//    return glue;
//  }
//            
}
