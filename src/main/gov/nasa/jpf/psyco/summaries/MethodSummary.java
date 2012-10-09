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
package gov.nasa.jpf.psyco.summaries;

import gov.nasa.jpf.jdart.ConstraintsTree;
import gov.nasa.jpf.jdart.ConstraintsTree.PostCondition;
import gov.nasa.jpf.jdart.numeric.Expression;
import gov.nasa.jpf.jdart.numeric.IntegerExpression;
import gov.nasa.jpf.psyco.util.*;
import java.lang.reflect.Method;
import java.util.*;
import solvers.Formula;
import solvers.LogicalExpression;
import solvers.LogicalOperator;

/**
 *
 */
public class MethodSummary {

  public static enum PathState {
    OK,ERROR, DONT_KNOW
  }
  
  public static class MethodPath {
    
    private Formula pathConstraint;
    private ConstraintsTree.PostCondition postConditon;
    private PathState pathState;

    public MethodPath(Formula pathConstraint, PostCondition postConditon, PathState pathState) {
      this.pathConstraint = pathConstraint;
      this.postConditon = postConditon;
      this.pathState = pathState;
    }

    public Formula getPathConstraint() {
      return pathConstraint;
    }

    public ConstraintsTree.PostCondition getPostConditon() {
      return postConditon;
    }
    
    public PathState getPathState() {
      return pathState;
    }

    public boolean intersects(Formula other) {      
      LogicalExpression intersection = new LogicalExpression(LogicalOperator.AND);
      intersection.addExpresion(pathConstraint);
      intersection.addExpresion(other);
      return intersection.isSatisfiable();
    }
    
    @Override
    public String toString() {
      return pathState + ": " + pathConstraint.sourcePC() + " -- " + postConditon;
    }
    
  }
  
  private Collection<MethodPath> okPaths;
  private Collection<MethodPath> errorPaths;
  private Collection<MethodPath> dontKnowPaths;
  
  private Method method;
  
  public MethodSummary(Method m, Collection<MethodPath> paths) {
    this.okPaths = new ArrayList<MethodPath>();
    this.errorPaths = new ArrayList<MethodPath>();
    this.dontKnowPaths = new ArrayList<MethodPath>();
    this.method = m;
            
    for (MethodPath p : paths) {
      switch (p.pathState) {      
        case OK: 
          okPaths.add(p); 
          break;
        case ERROR:
          errorPaths.add(p); 
          break;
        case DONT_KNOW:
          dontKnowPaths.add(p); 
          break;                
      }
    }
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Summary of ");
    sb.append(MethodUtil.getMethodSignature(method)).append("\n");
    for (MethodPath p : okPaths)
      sb.append(p).append("\n");
    for (MethodPath p : errorPaths)
      sb.append(p).append("\n");
    for (MethodPath p : dontKnowPaths)
      sb.append(p).append("\n");
    
    return sb.toString();
  }
  
  /**
   * @return the okPaths
   */
  public Collection<MethodPath> getOkPaths() {
    return okPaths;
  }

  /**
   * @return the errorPaths
   */
  public Collection<MethodPath> getErrorPaths() {
    return errorPaths;
  }

  /**
   * @return the dontKnowPaths
   */
  public Collection<MethodPath> getDontKnowPaths() {
    return dontKnowPaths;
  }

  /**
   * @return the method
   */
  public Method getMethod() {
    return method;
  }
  
}
