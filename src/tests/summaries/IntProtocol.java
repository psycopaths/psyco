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
package summaries;

import gov.nasa.jpf.jdart.Symbolic;

/** This example is a modified version from:
 *  Automata Learning with Automated Alphabet Abstraction Refinement
 *  Falk Howar, Bernhard Steffen, and Maik Merten
 *  VMCAI 2011
 */

public class IntProtocol {
 
  @Symbolic("true")  
  private int buffer_empty = 1;
  
  // pdu p, ack; // pdus
  @Symbolic("true")  
  private int expect = 0; // next expected seq. nr
 

  public void msg( int P1,  int P2) {
  //System.out.println("expect = " + expect);    
    if (P1 < 0) {
      return;
    }
  
  //System.out.println("expect = " + expect);
  int prevExpect = expect;
  //System.out.println("prevExpect = " + prevExpect + ", P1 = " + P1);
  if (expect > 0) {
      prevExpect--;
  }

  System.out.println("prevExpect = " + prevExpect + ", P1 = " + P1);
    
    if ((buffer_empty==1) && P1 == prevExpect ) {  // this is as expected
      expect++;
      buffer_empty = 1-buffer_empty;
      // OK message will be passed to upper layer
    } else {
      assert false;
      // message is discarded
    }
  } 

  public void recv_ack(int P1) {
    if (buffer_empty==1) {
      assert false;
    } else {
      if (P1 == ((expect-1) % 2)) {
         // ack is enabled, message is consumed
        buffer_empty = 1-buffer_empty;
      } else {
        // not the right P1
        assert false;
      }
    }        
  }
}