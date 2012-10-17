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
import gov.nasa.jpf.constraints.expressions.Constant;
import gov.nasa.jpf.jdart.ConcolicConfig;
import gov.nasa.jpf.jdart.ConcolicConfig.MethodConfig;

/**
 *
 */
public class MethodSymbol {
  
  /**
   * reference to actual method
   */
  private ConcolicConfig.MethodConfig method;
  
  /**
   * local preconditon 
   */
  private Expression<Boolean> precondition;
  
  private String name;
  
  public MethodSymbol(MethodConfig method, Expression<Boolean> precondition, String name) {
    this.method = method;
    this.precondition = precondition;
    this.name = name;
  }

  public MethodSymbol(MethodConfig method, String name) {
    this(method, new Constant(Boolean.class, true), name);
  }
  
  /**
   * @return the method
   */
  public ConcolicConfig.MethodConfig getMethod() {
    return method;
  }

  /**
   * @return the precondition
   */
  public Expression<Boolean> getPrecondition() {
    return precondition;
  }
  
  public String getSymbolName() {
    return name;
  }
  
  public String getOriginalMethodName() {
    return this.method.getMethodName();
  }
  
  public int getNumParams() {
    return this.getMethod().getParamNames().length;
  }
  
  @Override
  public String toString() {
    return this.name + "[" + this.precondition + "]";
  }
}
