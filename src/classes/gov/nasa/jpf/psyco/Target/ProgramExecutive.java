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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.Target;

/**
 *
 * @author dimitra
 */
import java.lang.reflect.Method;
import java.lang.IllegalAccessException;
import java.lang.reflect.InvocationTargetException;

public class ProgramExecutive {

  static final String SEQ = "sequence";
  static final String AUT = "automaton";

  // executes a sequence for queries
  static void sequence(String[] invokeSpecs) throws IllegalAccessException, InvocationTargetException {

    // ignore first argument so start at 1
    for (int i = 1; i < invokeSpecs.length; i++) {
      Invocation invoke = new Invocation(invokeSpecs[i]);
      Class cls = invoke.get_class();
      Method m = invoke.get_method();
      Object[] args = invoke.get_Arguments();
      
      m.invoke(null,args); // null needs to be changed if I handle instances
      
      
      /** don't catch exception so that we have obervable failures
      try {
        m.invoke(null,args); // null needs to be changed if I handle instances
      } catch (IllegalAccessException e1) {
        System.err.println("IllegalAccessException during query handling" + invokeSpecs[i]);
      } catch (InvocationTargetException e2) {
        Throwable cause = e2.getCause();
        System.err.println("InvocationTargetException during execution of " + invokeSpecs[i]);
        cause.printStackTrace();
      }
      **/
    }
  }

  // executes automaton for conjectures
  static void automaton(String[] invokeSpecs) {
    // TODO implement it for conjectures
  }

  public static void main(String[] invokeSpecs) throws IllegalAccessException, InvocationTargetException {
    int lg = invokeSpecs.length;

    if (invokeSpecs.length > 0) {
      String zero = invokeSpecs[0];
      if (zero.equalsIgnoreCase(SEQ)) {
        sequence(invokeSpecs);
      } else if (zero.equalsIgnoreCase(AUT)) {
        automaton(invokeSpecs);
      } else {
        System.err.println("Requires to specify whether we are to execute a query or a conjecture");
      }
    } 
  }
  
}
