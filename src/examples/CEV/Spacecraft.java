//
// Copyright (C) 2007 United States Government as represented by the
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

//
// DISCLAIMER - this file is part of the 'ESAS' demonstration project. As
// such, it is only intended for demonstration purposes, does not contain
// or refer to actual NASA flight software, and is solely derived from
// publicly available information. For further details, please refer to the
// README-ESAS file that is included in this distribution.
//

package CEV;

/**
 * class modeling behavior and configuration of the spacecraft
 * 
 * 'Spacecraft' is an example of a controlled object that is referenced from
 * the state chart
 */
public class Spacecraft {	
//	static public ErrorLog errors;
  
  static int STAGE1 = 0;
  static int STAGE2 = 1;
  static int LAS = 2;
  static int CM = 3;
  static int SM = 4;
  static int LSAM_ASCENT = 5;
  static int LSAM_DESCENT = 6;
  static int EDS = 7;
  
  static boolean doneStage1 = false;
  static boolean doneStage2 = false;
  static boolean lasDocked = true;
  static boolean cmDocked = true;
  static boolean smDocked = true;
  static boolean lsamAscentDocked = false;
  static boolean lsamDescentDocked = false;
  static boolean edsDocked = false;
  
  //--- actions
  
  public static void internalReset() {
    doneStage1 = false;
    doneStage2 = false;
    lasDocked = true;
    cmDocked = true;
    smDocked = true;
    lsamAscentDocked = false;
    lsamDescentDocked = false;
    edsDocked = false;  	
  }
  
  public static void reset(int component) {
    if (component == LAS)
      lasDocked = true;
    else if (component == STAGE1)
  		doneStage1 = false;
  	else if (component == STAGE2)
  		doneStage2 = false;
  	else if (component == CM)
  		cmDocked = true;
  	else if (component == SM)
  		smDocked = true;
  	else if (component == LSAM_ASCENT)
  		lsamAscentDocked = false;
  	else if (component == LSAM_DESCENT)
  		lsamDescentDocked = false;
  	else if (component == EDS)
  		edsDocked = false;
  }
  
  public static void doStage1Separation () {
  	doneStage1 = true;
  }
  
  public static void doStage2Separation () {
  	doneStage2 = true;
  }
  
  // that's nominal, if the LAS is not required anymore
  public static void doLASjettison (int altitude) {
  	if (!lasDocked) {
  		assert false : "las jettison without docked las";
  	} else if (altitude < 100000) {
  		assert false :"las jettison at altitudes under 100000 ft prohibited";
  	}
  	lasDocked = false;
  }
  
  public static void doLSAMrendezvous () {
  	lsamAscentDocked = true;
  	lsamDescentDocked = true;
  	edsDocked = true;
  }
  
  public static boolean doEDSseparation () {
  	if (!edsDocked) {
  		assert false : "eds separation without eds attached";
  		return false;
  	}
    edsDocked = false;
    return true;
  }
  
  public static void doLSAMascentBurn () {
  	lsamDescentDocked = false;
  }
  
  public static void doLSAMascentRendezvous () {
  	lsamAscentDocked = false;
  }
  
  public static void doSMseparation () {
  	if (!smDocked) {
  		assert false : "sm separation without sm attached";
  	}
  	smDocked = false;
  }
  
  //--- assertions
  
  public static boolean readyForLSAMrendezvous() {
    if (lasDocked) {
    	assert false : "lsamRendezvous with las attached";
    }
    return true;
  }

  public static boolean readyForDeorbit () {
    if (lasDocked || smDocked || lsamAscentDocked || edsDocked) {  
    	assert false : "deorbit with docked components";
    }
    return true;
  }
}
