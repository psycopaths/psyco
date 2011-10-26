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
package simple_refine2;

public class Example {
  private static int x = 0;
  private static int y = 0;

  
  // used for java execution - not for learning
  public static void internalReset() {
    x = 0;
    y = 0;
  }
  
  public static void init(int p, int q) {
    System.out.println("-------- In init!");
    x = p;
    y = q;
  }
  
  

  public static void a() {
    System.out.println("-------- In a!");
    if (x == 0) {
      y = 10;
    } else if (x == 1) {
      y = 11;
    } else {
      y = 12;
    }
  }

  public static void b() {
    System.out.println("-------- In b!");
    if (y != 10) {
      assert false;
    }
  }
}
