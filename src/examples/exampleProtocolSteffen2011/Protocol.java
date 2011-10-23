//
// Copyright (C) 2008 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA).  All Rights Reserved.
//
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3.  The NOSA has been approved by the Open Source
// Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
// directory tree for the complete NOSA document.
//
// THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
// KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
// LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
// SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
// A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
// THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
// DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//
package exampleProtocolSteffen2011;

/** This example is a modified version from:
 *  Automata Learning with Automated Alphabet Abstraction Reﬁnement
 *  Falk Howar, Bernhard Steﬀen, and Maik Merten
 *  VMCAI 2011
 */

public class Protocol {
  
  // pdu p, ack; // pdus
  private static int expect = 0; // next expected seq. nr
  private static boolean buffer_empty = true;


  public static void msg(int sequence, int content) {
    System.out.println("Sequence is " + (sequence & 1));
    System.out.println("Expect is " + (expect & 1));
    
    
    if ((buffer_empty) && ((sequence & 1) == (expect & 1) )) {  // this is as expected
      expect++;
      buffer_empty = false;
      // OK message will be passed to upper layer
    } else {
      assert false;
      // message is discarded
    }
  } 
  
  public static void recv_ack(int value) {
    if (buffer_empty) {
      assert false;
    } else {
      if (value == ((expect-1) & 1)) {
         // ack is enabled, message is consumed
        buffer_empty = true;
      } else {
        // not the right sequence
        assert false;
      }
    }        
  }
  
  public static void main(String[] args) {
    msg(0, 6);
 //   recv_ack(0);
    msg(1, 10);
 //   recv_ack(1);
    msg(0, 100);
 //   recv_ack(0);
    
  }
    
}

//while (true) {
//wait_for_event(&event); // wait for event
//switch (event) {
//msg:
//from_lower_layer(&p); // read new message
//if (buffer==0 && // if buf empty and
//(p.seq % 2 == expect % 2)) { // and seq matches
//buffer = p.data;
//expect++; // increment exp. seq. nr
//indicate_to_upper_layer(); // indicate new data
//}
//break;
//recv:
//if (buffer==0) break; // skip if buffer emtpy
//data_to_upper_layer(&buffer); // forward data
//ack.seq = (expect-1) % 2; // ack. delivery
//to_lower_layer(&ack);
//break;
//}
//}
//  
//}
