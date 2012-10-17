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
package gov.nasa.jpf.psyco.util;

import java.lang.reflect.Method;

/**
 *
 */
public class MethodUtil {
  
  
  public static String getMethodSignature(Method m) {
    String sig = "";
    sig += m.getDeclaringClass().getName() + "." + m.getName() + "(";

    Class<?>[] pTypes = m.getParameterTypes();
    for (int i=0; i<pTypes.length; i++) {    
      if (pTypes[i].equals(int.class)) {
        sig += "I";
      } else {
        throw new RuntimeException("Type " + pTypes[i].getName() + " not supported currently");
      }
    }
      
    sig += ")";
    if (m.getReturnType().equals(Void.TYPE)) {
      sig += "V";
    } else {
      throw new RuntimeException("Type " + m.getReturnType().getName() + " not supported currently");
    }
    
    return sig;
  }
  
  public static String getPerturbPattern(Method m) {
    String sig = "";
    sig += m.getDeclaringClass().getName() + "." + m.getName() + "(";

    Class<?>[] pTypes = m.getParameterTypes();
    for (int i=0; i<pTypes.length; i++) {
      if (pTypes[i].equals(int.class)) {
        sig += "int";
      } else {
        throw new RuntimeException("Type " + pTypes[i].getName() + " not supported currently");
      }
      if (i<pTypes.length-1)
        sig += ",";
    }
      
    sig += ")";

    return sig;    
  }
  
  public static String getSymbolicPattern(Method m) {
    String sig = "";
    sig += m.getDeclaringClass().getName() + "." + m.getName() + "(";

    Class<?>[] pTypes = m.getParameterTypes();
    for (int i=0; i<pTypes.length; i++) {
      sig += "P" + (i+1);
      if (i<pTypes.length-1)
        sig += "#";
    }
      
    sig += ")";

    return sig;        
  }
  
  
  
}
