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


public class ExampleAlphabet {

<<<<<<< local
    // [true]
    public static Boolean init_0() {
        if (true) {
            Example.init(parInitP, parInitQ);
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
=======
  // [true]
  public static boolean init_0(int p, int q) {
    System.out.println("-------- In init_0!");
    if (true) {
      Example.init(p, q);
      return true;
    } else {
      return false;
>>>>>>> other
    }
<<<<<<< local
                    
    // [q != 10]
    public static Boolean init_00() {
        if (parInitQ != 10) {
            Example.init(parInitP, parInitQ);
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
=======
  }

  // [q != 10]
  public static boolean init_00(int p, int q) {
    if (q != 10) {
      Example.init(p, q);
      return true;
    } else {
      return false;
>>>>>>> other
    }
<<<<<<< local
                    
    // [q == 10]
    public static Boolean init_01() {
        if (parInitQ == 10) {
            Example.init(parInitP, parInitQ);
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
=======
  }

  // [q == 10]
  public static boolean init_01(int p, int q) {
    if (q == 10) {
      Example.init(p, q);
      return true;
    } else {
      return false;
>>>>>>> other
    }
  }

<<<<<<< local
    // [q != 10 && p != 0]
    public static Boolean init_000() {
        if (parInitQ != 10 && parInitP != 0) {
            Example.init(parInitP, parInitQ);
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
=======
  // [q != 10 && p != 0]
  public static boolean init_000(int p, int q) {
    if (q != 10 && p != 0) {
      Example.init(p, q);
      return true;
    } else {
      return false;
>>>>>>> other
    }
  }

<<<<<<< local
    // [q != 10 && p == 0]
    public static Boolean init_001() {
        if (parInitQ != 10 && parInitP == 0) {
            Example.init(parInitP, parInitQ);
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
=======
  // [q != 10 && p == 0]
  public static boolean init_001(int p, int q) {
    if (q != 10 && p == 0) {
      Example.init(p, q);
      return true;
    } else {
      return false;
>>>>>>> other
    }
  }

<<<<<<< local
    // [q == 10 && p != 0]
    public static Boolean init_010() {
        if (parInitQ == 10 && parInitP != 0) {
            Example.init(parInitP, parInitQ);
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
=======
  // [q == 10 && p != 0]
  public static boolean init_010(int p, int q) {
    if (q == 10 && p != 0) {
      Example.init(p, q);
      return true;
    } else {
      return false;
>>>>>>> other
    }
  }

<<<<<<< local
    // [q == 10 && p == 0]
    public static Boolean init_011() {
        if (parInitQ == 10 && parInitP == 0) {
            Example.init(parInitP, parInitQ);
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
=======
  // [q == 10 && p == 0]
  public static boolean init_011(int p, int q) {
    if (q == 10 && p == 0) {
      Example.init(p, q);
      return true;
    } else {
      return false;
>>>>>>> other
    }
  }

<<<<<<< local
    // [true]
    public static Boolean a_0() {
        if (true) {
            Example.a();
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
=======
  // [true]
  public static boolean a_0() {
    if (true) {
      Example.a();
      return true;
    } else {
      return false;
>>>>>>> other
    }
  }

<<<<<<< local
    // [true]
    public static Boolean b_0() {
        if (true) {
            Example.b();
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }    
=======
  // [true]
  public static boolean b_0() {
    if (true) {
      Example.b();
      return true;
    } else {
      return false;
    }
  }    
>>>>>>> other
}
