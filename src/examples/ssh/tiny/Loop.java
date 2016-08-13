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
package ssh.tiny;

import gov.nasa.jpf.jdart.Symbolic;

/**
 *
 * @author falk
 */
public class Loop {

  @Symbolic("true")
  private int e = 0;

  @Symbolic("true")
  private int s = 2;

  public Loop() {

  }

  public void doLoop() {

    if (s == 2) {
      if (e == 0) {
        e = 1;
      }
      s = 3;
    } else if (s == 3) {
      if (e == 1) {
        e = 2;
      }
      s = 4;
    } else if (s == 4) {
      assert (e != 3);
      s = 5;
    }
  }

}
