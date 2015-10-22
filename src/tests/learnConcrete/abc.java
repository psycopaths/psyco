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
package learnConcrete;

public class abc {

static int x = 0;

public static void a() {
  System.out.println("In method a...");
  x++;
}

public static void b() {
  System.out.println("In method b...");
//  if (x == 1) {
    x++;
 // } else {
 //   assert false;
 // }
}

public static void c() {
  System.out.println("In method c...");
  if (x == 2) {
    x = 0;
  } else {
    assert false;
  }
}
}
