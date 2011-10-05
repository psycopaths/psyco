package simple2;

import gov.nasa.jpf.symbc.Symbolic;

public class ExampleAlphabet {

  public static void init() {}

  public static Boolean a_1() {
    if (true) {
      Example.a();
      return true;
    } else {
      return false;
    }
  }

  @Symbolic("true")
  public static int init_0_0 = 0;
  @Symbolic("true")
  public static int init_0_1 = 0;
  public static Boolean init_0() {
    if (true) {
      Example.init(init_0_0, init_0_1);
      return true;
    } else {
      return false;
    }
  }

  @Symbolic("true")
  public static int init_0_3_0 = 0;
  @Symbolic("true")
  public static int init_0_3_1 = 0;
  public static Boolean init_0_3() {
    if (init_0_3_0 == 0) {
      Example.init(init_0_3_0, init_0_3_1);
      return true;
    } else {
      return false;
    }
  }

  public static Boolean b_2() {
    if (true) {
      Example.b();
      return true;
    } else {
      return false;
    }
  }

  @Symbolic("true")
  public static int init_0_4_0 = 0;
  @Symbolic("true")
  public static int init_0_4_1 = 0;
  public static Boolean init_0_4() {
    if (init_0_4_0 != 0 && 1 == init_0_4_0 || init_0_4_0 != 0 && 1 != init_0_4_0) {
      Example.init(init_0_4_0, init_0_4_1);
      return true;
    } else {
      return false;
    }
  }

}
