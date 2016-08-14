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
package dontKnowSimple;

public class Example {
  private static int x = 1;

  // reset method needed by psyco
  public static void internalReset() {
    x = 1;
  }

  public static void m1(int c) {
    if (c < 10) {
      x = c;
    } else {
      x = c * c;
    }
  }

  public static void m2() {
    if (x <= 5) {
      assert false;
    }
  }
}
