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
package issta2013.abp;

/** This example is a modified version from:
 *  Automata Learning with Automated Alphabet Abstraction Reﬁnement
 *  Falk Howar, Bernhard Steffen, and Maik Merten
 *  VMCAI 2011
 */

public class IntProtocol {
  
  // pdu p, ack; // pdus
  private int expect = 0; // next expected seq. nr
  private int buffer_empty = 1;
  

  public void msg(int sequence, int content) {
    //System.out.println("Sequence is " + (sequence & 1));
    //System.out.println("Expect is " + (expect & 1));
    
    if ((buffer_empty == 1) && ((sequence %2 ) == (expect % 2) )) {  // this is as expected
      //expect++;
      expect++;
      buffer_empty = 1-buffer_empty;
      // OK message will be passed to upper layer
    } else {
      assert false;
      // message is discarded
    }
  } 
  
  public void recv_ack(int value) {
    if (buffer_empty==1) {
      assert false;
    } else {
      if (value == ((expect-1) % 2)) {
         // ack is enabled, message is consumed
        buffer_empty = 1-buffer_empty;
      } else {
        // not the right sequence
        assert false;
      }
    }        
  }
  
}