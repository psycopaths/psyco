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

public class CEV {
  //--------------------------------- our controlled objects

  public CEV() {
    
  }
  
  public void reset(int component) {
  	Spacecraft.reset(component);
  }
  
  public static void srbIgnition () {
  }
  
  public static void failure (int tminus) {
    if (tminus <= 5) {
    	assert false: "PAD abort";
    } else {
    	assert false: "Hold launch";
    }
  }

  public static void stage1Separation () {
  	Spacecraft.doStage1Separation();
  }

  public static void abort (int altitude, int controlMotorFired) {
  	if (!Spacecraft.doneStage1) {
  		Spacecraft.doStage1Abort(altitude, controlMotorFired);
  		if (controlMotorFired == 1)
  			Spacecraft.doLowActiveAbort();
  		else
  			Spacecraft.doLowPassiveAbort();
  	} else if (!Spacecraft.doneStage2) {
  		Spacecraft.doStage2Abort(altitude);
  		if (controlMotorFired == 0)
  			Spacecraft.doLowPassiveAbort();
  	}
  	assert false: "Mission aborted";
  }

  public static void stage2Separation () {
  	Spacecraft.doStage2Separation();
  }
  
  public static void tliBurn() {
  	Spacecraft.readyForTliBurn();
  }
  
  public static void enterOrbitOps(int earthSensorFailure) {
    if (earthSensorFailure == 1) {
      assert false: "Earth sensor failure. Cannot enter orbit ops";
    }
  }

  public static void deOrbit() {
  	if (Spacecraft.readyForDeorbit())
  		Spacecraft.internalReset();
  }
  
  public static void teiBurn() {
  	Spacecraft.readyForTeiBurn();
  }
  
  public static void lasJettison (int altitude) {
  	Spacecraft.doLASjettison(altitude);
  }
  
  public static void lsamRendezvous() {
    if (Spacecraft.readyForLSAMrendezvous())
    	Spacecraft.doLSAMrendezvous();
  }
  
  public static void loiBurn() {
  }

  public static void doEdsSeparation () {
  	Spacecraft.doEDSseparation();
  }
  
  public static void doSMSeparation () {
  	Spacecraft.doSMseparation();
  }

  public static void lsamAscentBurn () {
  	Spacecraft.doLSAMascentBurn();
  }
  
  public static void lsamAscentRendezvous () {
  	Spacecraft.doLSAMascentRendezvous();
  }
  
  public static void eiBurn (int hasCMimbalance, int hasRCSfailure) {
  	Spacecraft.doEiBurn(hasCMimbalance, hasRCSfailure);
  }
  
  public static void internalReset() {
  	Spacecraft.internalReset();
  }
}
