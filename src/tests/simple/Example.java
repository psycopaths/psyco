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
package simple;

public class Example {
  private static int x = 0;
  private static int y = 0;

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
    if (y == 10) {
      assert false;
    }
  }
}
