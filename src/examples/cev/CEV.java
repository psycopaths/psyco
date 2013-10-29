 /*******************************************************************************
 * Copyright (C) 2008 United States Government as represented by the
 * Administrator of the National Aeronautics and Space Administration
 * (NASA).  All Rights Reserved.
 * 
 * This software is distributed under the NASA Open Source Agreement
 * (NOSA), version 1.3.  The NOSA has been approved by the Open Source
 * Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
 * directory tree for the complete NOSA document.
 * 
 * THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
 * KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
 * LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
 * SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
 * A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
 * THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
 * DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
 ******************************************************************************/
package cev;

import gov.nasa.jpf.jdart.Symbolic;

public class CEV {

  public CEV() {}
    
    @Symbolic("true")
    Spacecraft spacecraft_V1 = new Spacecraft();
    
  public void reset(int component) {
  	spacecraft_V1.reset(component);
  }
  
  public void srbIgnition () {
  }
  
  public void failure (int tminus) {
    if (tminus <= 5) {
    	assert false: "PAD abort";
    } else {
    	assert false: "Hold launch";
    }
  }

  public void stage1Separation () {
  	spacecraft_V1.doStage1Separation();
  }

  public  void abort (int altitude, int controlMotorFired) {
  	if (!spacecraft_V1.isDoneStage1()) {
  		spacecraft_V1.doStage1Abort(altitude, controlMotorFired);
  		if (controlMotorFired == 1)
  			spacecraft_V1.doLowActiveAbort();
  		else
  			spacecraft_V1.doLowPassiveAbort();
  	} else if (!spacecraft_V1.isDoneStage2()) {
  		spacecraft_V1.doStage2Abort(altitude);
  		if (controlMotorFired == 0)
  			spacecraft_V1.doLowPassiveAbort();
  	}
  	assert false: "Mission aborted";
  }

  public  void stage2Separation () {
  	spacecraft_V1.doStage2Separation();
  }
  
  public  void tliBurn() {
  	spacecraft_V1.readyForTliBurn();
  }
  
  public  void enterOrbitOps(int earthSensorFailure) {
    if (earthSensorFailure == 1) {
      assert false: "Earth sensor failure. Cannot enter orbit ops";
    }
  }

  public  void deOrbit() {
  	if (spacecraft_V1.readyForDeorbit())
  		spacecraft_V1.internalReset();
  }
  
  public  void teiBurn() {
  	spacecraft_V1.readyForTeiBurn();
  }
  
  public  void lasJettison (int altitude) {
  	spacecraft_V1.doLASjettison(altitude);
  }
  
  public  void lsamRendezvous() {
    if (spacecraft_V1.readyForLSAMrendezvous())
    	spacecraft_V1.doLSAMrendezvous();
  }
  
  public  void loiBurn() {
  }

  public  void doEdsSeparation () {
  	spacecraft_V1.doEDSseparation();
  }
  
  public  void doSMSeparation () {
  	spacecraft_V1.doSMseparation();
  }

  public  void lsamAscentBurn () {
  	spacecraft_V1.doLSAMascentBurn();
  }
  
  public  void lsamAscentRendezvous () {
  	spacecraft_V1.doLSAMascentRendezvous();
  }
  
  public  void eiBurn (int hasCMimbalance, int hasRCSfailure) {
  	spacecraft_V1.doEiBurn(hasCMimbalance, hasRCSfailure);
  }
  
   
}
