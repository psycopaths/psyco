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
package gov.nasa.jpf.psyco.compiler;

/**
 *
 */
public class MethodWrapper {
    
  private String call;
  
  private String precondition;
  
  private String wrapper = null;

  public MethodWrapper(String call, String precondition) {
    this.call = call;
    this.precondition = precondition;
  }

  public MethodWrapper(String call, String precondition, String wrapper) {
    this.call = call;
    this.precondition = precondition;
    this.wrapper = wrapper;
  }
  
  /**
   * @return the call
   */
  public String getCall() {
    return call;
  }

  /**
   * @return the precondition
   */
  public String getPrecondition() {
    return precondition;
  }
  
  /**
   * @return the wrapper
   */
  public String getWrapper() {
    return wrapper;
  }
  
  
  
}
