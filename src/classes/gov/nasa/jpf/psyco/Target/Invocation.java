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

import java.lang.reflect.Method;
import java.lang.ClassNotFoundException;
import java.lang.NoSuchMethodException;

/**
 *
 * Specification needs to be extended to include target instance specs
 */
public class Invocation {
  String cName, mName;
  Class invokedClass;
  Method invokedMethod;

  Invocation(String spec){
    if (parseSpec(spec)) {
      try{
        invokedClass = Class.forName(cName);
      }catch(ClassNotFoundException e1){
        System.err.println("Class not found:" + cName);
      };
      try {      
        invokedMethod = invokedClass.getDeclaredMethod(mName);
      } catch(NoSuchMethodException e2){
        System.err.println("Method not found for name" + mName);
      };
    } else {
      // TODO throw some exception 
    }
  }
      
    //if (!parseSpec(spec)){
      //throw new ConfigException(spec);
  

  boolean parseSpec(String s){
    java.util.StringTokenizer st = new java.util.StringTokenizer(s, ":");
    //TODO - think about making the class name optional
    if (st.hasMoreTokens())
      cName = st.nextToken();
    else
      return(false);
    if (st.hasMoreTokens())
      mName = st.nextToken();
    else
      return(false);
    return true;
    // TODO arguments?
  }

  
  Class get_class(){
    return invokedClass;
  }
  
  Method get_method() {
    return invokedMethod;
  }
  
  Object[] get_Arguments() {
    return new Object[0];
  }
  
}

