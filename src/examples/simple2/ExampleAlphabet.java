package simple2;

import gov.nasa.jpf.symbc.Symbolic;

public class ExampleAlphabet {

  public static class TotallyPsyco extends java.lang.AssertionError {
    private static final long serialVersionUID = 1L;

    TotallyPsyco(String msg) {
      super(msg);
    }
  }

  public static void init() {}

  public static void a_1() {
    if (true) {
      Example.a();
    } else {
      throw new TotallyPsyco("Odd Psyco");
    }
  }

  @Symbolic("true")
  public static int init_0_0 = 0;
  @Symbolic("true")
  public static int init_0_1 = 0;
  public static void init_0() {
    if (true) {
      Example.init(init_0_0, init_0_1);
    } else {
      throw new TotallyPsyco("Odd Psyco");
    }
  }

  @Symbolic("true")
  public static int init_0_3_0 = 0;
  @Symbolic("true")
  public static int init_0_3_1 = 0;
  public static void init_0_3() {
    if (init_0_3_0 == 0) {
      Example.init(init_0_3_0, init_0_3_1);
    } else {
      throw new TotallyPsyco("Odd Psyco");
    }
  }

  public static void b_2() {
    if (true) {
      Example.b();
    } else {
      throw new TotallyPsyco("Odd Psyco");
    }
  }

  @Symbolic("true")
  public static int init_0_4_0 = 0;
  @Symbolic("true")
  public static int init_0_4_1 = 0;
  public static void init_0_4() {
    if (init_0_4_0 != 0 && 1 == init_0_4_0 || init_0_4_0 != 0 && 1 != init_0_4_0) {
      Example.init(init_0_4_0, init_0_4_1);
    } else {
      throw new TotallyPsyco("Odd Psyco");
    }
  }

}
