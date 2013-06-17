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
package simple;

import gov.nasa.jpf.jdart.Symbolic;

public class ExampleAlphabet {

  public static class TotallyPsyco extends java.lang.AssertionError {
    private static final long serialVersionUID = 1L;

    TotallyPsyco(String msg) {
      super(msg);
    }
  }

  @Symbolic("true")
  public static int init_0_0 = 0;
  @Symbolic("true")
  public static int init_0_1 = 0;

  public static void init() {}

  // [true]
  public static void init_0() {
    System.out.println("-------- In init_0!");
    if (true) {
      Example.init(init_0_0, init_0_1);
    } else {
      throw new TotallyPsyco("Odd Psyco");
    }
  }

  // [q != 10]
  public static void init_00() {
    if (init_0_1 != 10) {
      Example.init(init_0_0, init_0_1);
    } else {
      throw new TotallyPsyco("Odd Psyco");
    }
  }

  // [q == 10]
  public static void init_01() {
    if (init_0_1 == 10) {
      Example.init(init_0_0, init_0_1);
    } else {
      throw new TotallyPsyco("Odd Psyco");
    }
  }

  // [q != 10 && p != 0]
  public static void init_000() {
    if (init_0_1 != 10 && init_0_0 != 0) {
      Example.init(init_0_0, init_0_1);
    } else {
      throw new TotallyPsyco("Odd Psyco");
    }
  }

  // [q != 10 && p == 0]
  public static void init_001() {
    if (init_0_1 != 10 && init_0_0 == 0) {
      Example.init(init_0_0, init_0_1);
    } else {
      throw new TotallyPsyco("Odd Psyco");
    }
  }

  // [q == 10 && p != 0]
  public static void init_010() {
    if (init_0_1 == 10 && init_0_0 != 0) {
      Example.init(init_0_0, init_0_1);
    } else {
      throw new TotallyPsyco("Odd Psyco");
    }
  }

  // [q == 10 && p == 0]
  public static void init_011() {
    if (init_0_1 == 10 && init_0_0 == 0) {
      Example.init(init_0_0, init_0_1);
    } else {
      throw new TotallyPsyco("Odd Psyco");
    }
  }

  // [true]
  public static void a_0() {
    if (true) {
      Example.a();
    } else {
      throw new TotallyPsyco("Odd Psyco");
    }
  }

  // [true]
  public static void b_0() {
    if (true) {
      Example.b();
    } else {
      throw new TotallyPsyco("Odd Psyco");
    }
  }    
}
