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
package simple2;

import gov.nasa.jpf.jdart.Symbolic;

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
