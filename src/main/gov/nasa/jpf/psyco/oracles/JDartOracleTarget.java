/*
 * Copyright (C) 2015, United States Government, as represented by the 
 * Administrator of the National Aeronautics and Space Administration.
 * All rights reserved.
 *
 * The PSYCO: A Predicate-based Symbolic Compositional Reasoning environment 
 * platform is licensed under the Apache License, Version 2.0 (the "License"); you 
 * may not use this file except in compliance with the License. You may obtain a 
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0. 
 *
 * Unless required by applicable law or agreed to in writing, software distributed 
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 * specific language governing permissions and limitations under the License.
 */
package gov.nasa.jpf.psyco.oracles;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class JDartOracleTarget {

  // jdart cannot handle parameters in sequence
  private static String[] _query = null;

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