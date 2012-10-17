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
package gov.nasa.jpf.psyco.test.target;

import gov.nasa.jpf.psyco.target.ProgramExecutive;
import gov.nasa.jpf.util.test.TestJPF;
import org.junit.Test;

/**
 *
 */
public class ProgramExecutiveTest extends TestJPF {

  // import gov.nasa.jpf.psyco.Target.ProgramExecutive;
  @Test
  public void testSequence() {
    String[] invokeSpecs = new String[3];
    invokeSpecs[0] = "sequence";
    invokeSpecs[1] = "gov.nasa.jpf.psyco.test.Target.abc:b";
    invokeSpecs[2] = "gov.nasa.jpf.psyco.test.Target.abc:b";


    try {
      ProgramExecutive.main(invokeSpecs);
      
    } catch (Throwable x){ // we treat all unhandled exceptions as test failures
      Throwable cause = x.getCause();
      if (cause != null){
        cause.printStackTrace();
      }
      fail("ProgramExecutive did throw unhandled exception: " + x);
    }

  }

  @Test
  public void testAutomaton() {
  }
}
