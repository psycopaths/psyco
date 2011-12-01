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
package CEV;

public class CEV {
  //--------------------------------- our controlled objects
	
  public static void reset(int component) {
  	Spacecraft.reset(component);
  }
  
  public static void srbIgnition () {
  }
  
  public static void stage1Separation () {
  	Spacecraft.doStage1Separation();
  }

  public static void stage2Separation () {
  	Spacecraft.doStage2Separation();
  }
  
  public static void lasJettison (int altitude) {
  	Spacecraft.doLASjettison(altitude);
  }
  
  public static void lsamRendezvous() {
    if (Spacecraft.readyForLSAMrendezvous())
    	Spacecraft.doLSAMrendezvous();
  }

  public static void doEdsSeparation () {
  	Spacecraft.doEDSseparation();
  }
  
  public static void doSMSeparation () {
  	Spacecraft.doSMseparation();
  }

  public static void readyForDeorbit () {
    if (Spacecraft.readyForDeorbit())
    	Spacecraft.internalReset();
  }
  
  public static void lsamAscentBurn () {
  	Spacecraft.doLSAMascentBurn();
  }
  
  public static void internalReset() {
  	Spacecraft.internalReset();
  }
}
