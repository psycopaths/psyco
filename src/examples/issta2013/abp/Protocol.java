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
package issta2013.abp;

import gov.nasa.jpf.jdart.Symbolic;

/** This example is a modified version from:
 *  Automata Learning with Automated Alphabet Abstraction Reﬁnement
 *  Falk Howar, Bernhard Steﬀen, and Maik Merten
 *  VMCAI 2011
 */

public class Protocol {
  
  // pdu p, ack; // pdus
  @Symbolic("true")
  private int expect = 0; // next expected seq. nr
  @Symbolic("true")
  private int buffer_empty = 1;

  public Protocol() {}

  public void msg(int sequence, int content) {
    //System.out.println("Sequence is " + (sequence & 1));
    //System.out.println("Expect is " + (expect & 1));
    
    if ((buffer_empty == 1) && ((sequence %2 ) == (expect % 2) )) {  // this is as expected
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