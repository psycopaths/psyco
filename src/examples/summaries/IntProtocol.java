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
package summaries;

import gov.nasa.jpf.jdart.Symbolic;

/** This example is a modified version from:
 *  Automata Learning with Automated Alphabet Abstraction Reﬁnement
 *  Falk Howar, Bernhard Steﬀen, and Maik Merten
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
