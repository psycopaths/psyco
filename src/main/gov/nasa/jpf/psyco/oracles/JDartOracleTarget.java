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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 * @author falkhowar
 */
public class JDartOracleTarget {
  
  private static String[] _query = null; // jdart cannot handle parameters in sequence
  
  private static void query() throws Throwable {

    Class clazz = Class.forName(JDartOracle.ALPHABET_CLASS);
    //Object inst = clazz.newInstance();
    for (int i = 0; i < _query.length; i++) {    
      Method m = clazz.getMethod(_query[i]);
      //System.out.println(m);
      try {
        m.invoke(null);
      } catch (InvocationTargetException ex) {
        throw ex.getCause();
      }
    }
  }
  
  public static void main(String[] args) throws Throwable {
    _query = args;
    try {
     query();
    } catch (Throwable e) {

      // FIX: catch is needed due to bug in jdart. 
      // Without catch any of these exceptions will lead to and endless loop
      // JDart Issue #26
      throw e;
    }
  }
    
}
