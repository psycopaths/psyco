//
// Copyright (C) 2008 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA).  All Rights Reserved.
//
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3.  The NOSA has been approved by the Open Source
// Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
// directory tree for the complete NOSA document.
//
// THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
// KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
// LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
// SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
// A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
// THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
// DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//
package gov.nasa.jpf.psyco.refinement;

import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.expressions.LogicalOperator;
import gov.nasa.jpf.constraints.expressions.PropositionalCompound;
import gov.nasa.jpf.psyco.compiler.MethodWrapper;
import gov.nasa.jpf.psyco.compiler.Parameter;
import java.util.*;


/**
 * membership query in psyco format
 */
public class ConstrainedMethodSequence {
  
  public static class ConstrainedMethod {
    private MethodSymbol symbol;
    private Expression<Boolean> assumption;

    ConstrainedMethod(MethodSymbol symbol, Expression<Boolean> assumption) {
      this.symbol = symbol;
      this.assumption = assumption;
    }

    /**
     * @return the symbol
     */
    public MethodSymbol getSymbol() {
      return symbol;
    }

    /**
     * @return the assumption
     */
    public Expression<Boolean> getAssumption() {
      return assumption;
    }

    @Override
    public String toString() {
      return symbol + "[" + assumption + "]";
    }
  }
  
  private List<ConstrainedMethod> sequence;
  
  public ConstrainedMethodSequence(String packageName) {
    this.sequence = new LinkedList<ConstrainedMethod>();
  }

  public void addMethod(MethodSymbol s, Expression<Boolean> a) {
    this.sequence.add(new ConstrainedMethod(s, a));
  }
  

  public List<Parameter> getParameters() {
    List<Parameter> pList = new LinkedList<Parameter>();
    int i=1;
    for (ConstrainedMethod cm : sequence) {
      for (Class<?> c : cm.getSymbol().getMethod().getParamTypes()) {
        pList.add(new Parameter(c.getName(), "P_" + i));
        i++;
      }
    }
    return pList;
  }
  
  public List<MethodWrapper> getSteps() {
    List<MethodWrapper> steps = new LinkedList<MethodWrapper>();
    int i=1;
    for (ConstrainedMethod m : sequence) {
      // create call
      int params = m.getSymbol().getMethod().getParamNames().length;
      String call = m.getSymbol().getMethod().getClassName() + "." + m.getSymbol().getMethod().getMethodName() + "(";
      for (int j=i;j<i+params;j++) {
        call += "P_" + j + ",";
      }
      if (m.getSymbol().getMethod().getParamNames().length > 0) {
        call = call.substring(0, call.length()-1);
      }
      call = call + ")";
      
      // create condition
      Expression<Boolean> precondition = m.getSymbol().getPrecondition();
      Set<Variable> vars = new HashSet<Variable>();
      precondition.getVariables(vars);

      Map<Expression,Expression> rename = new HashMap<Expression,Expression>();
      for (Variable v : vars) {
        int id = Integer.parseInt(v.getName().substring(2));
        rename.put(v,new Variable(v.getType(), "P_" + (id+i-1) ));
      }
      
      precondition = precondition.replaceTerms(rename);
      precondition = new PropositionalCompound(
              precondition, LogicalOperator.AND, m.getAssumption());
            
      MethodWrapper w = new MethodWrapper(call, precondition.toString());
      steps.add(w);
      i += params;
    }
    return steps;
  }
  
  public List<ConstrainedMethod> getSymbols() {
    return this.sequence;
  }
  
  @Override
  public String toString() {
    return sequence.toString();
  }
  
}
