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
package issta2013.cev;

import gov.nasa.jpf.jdart.Symbolic;

/**
 * class modeling behavior and configuration of the spacecraft
 *
 * 'Spacecraft' is an example of a controlled object that is referenced from the
 * state chart
 */
public class Spacecraft {
//	static public ErrorLog errors;

  static final int STAGE1 = 0;
  static final int STAGE2 = 1;
  static final int LAS = 2;
  static final int CM = 3;
  static final int SM = 4;
  static final int LSAM_ASCENT = 5;
  static final int LSAM_DESCENT = 6;
  static final int EDS = 7;
  @Symbolic("true")
  private int dockedComponents = 0;
  @Symbolic("true")
  private boolean doneStage1 = false;
  @Symbolic("true")
  private boolean doneStage2 = false;
  @Symbolic("true")
  private boolean lasDocked = true;
  @Symbolic("true")
  private boolean cmDocked = true;
  @Symbolic("true")
  private boolean smDocked = true;
  @Symbolic("true")
  private boolean lsamAscentDocked = false;
  @Symbolic("true")
  private boolean lsamDescentDocked = false;
  @Symbolic("true")
  private boolean edsDocked = false;

  Spacecraft() {
    doneStage1 = false;
    doneStage2 = false;
    lasDocked = true;
    cmDocked = true;
    smDocked = true;
    lsamAscentDocked = false;
    lsamDescentDocked = false;
    edsDocked = false;

    dockedComponents = 3;
  }

  public void internalReset() {
    doneStage1 = false;
    doneStage2 = false;
    lasDocked = true;
    cmDocked = true;
    smDocked = true;
    lsamAscentDocked = false;
    lsamDescentDocked = false;
    edsDocked = false;

    dockedComponents = 3;
  }

  void reset(int component) {

//    switch (component) {
//      case LAS:
//        lasDocked = true;
//        dockedComponents++;
//        break;
//      case STAGE1:
//        doneStage1 = false;
//        break;
//      case STAGE2:
//        doneStage2 = false;
//        break;
//      case CM:
//        cmDocked = true;
//        dockedComponents++;
//        break;
//      case SM:
//        smDocked = true;
//        break;
//      case LSAM_ASCENT:
//        lsamAscentDocked = false;
//        dockedComponents--;
//        break;
//      case LSAM_DESCENT:
//        lsamDescentDocked = false;
//        dockedComponents--;
//        break;
//      case EDS:
//        edsDocked = false;
//        dockedComponents--;
//        break;
//    }


    if (component == LAS) {
      lasDocked = true;
      dockedComponents++;
    } else if (component == STAGE1) {
      doneStage1 = false;
    }
  	else if (component == STAGE2) {
      doneStage2 = false;
    }
  	else if (component == CM) {
  		cmDocked = true;
  		dockedComponents++;
  	} else if (component == SM) {
      smDocked = true;
    }
  	else if (component == LSAM_ASCENT) {
  		lsamAscentDocked = false;
  		dockedComponents--;
  	} else if (component == LSAM_DESCENT) {
  		lsamDescentDocked = false;
  		dockedComponents--;
  	} else if (component == EDS) {
  		edsDocked = false;
  		dockedComponents--;
  	}
  }

  void doStage1Separation() {
    doneStage1 = true;
  }

  void doStage1Abort(int altitude, int lasControlMotorFired) {
    if (lasControlMotorFired == 0) {
      //assert false: "LAS control failure";
      throw new RuntimeException("LAS control failure");
    }
  }

  void doLowActiveAbort() {
    if (lasDocked) {
      dockedComponents--;
    }
    if (smDocked) {
      dockedComponents--;
    }

    lasDocked = false;
    smDocked = false;
    doneStage1 = true;
    doneStage2 = true;
  }

  public void doLowPassiveAbort() {
    if (lasDocked) {
      dockedComponents--;
    }
    if (smDocked) {
      dockedComponents--;
    }

    lasDocked = false;
    smDocked = false;
    doneStage1 = true;
    doneStage2 = true;
  }

  void doStage2Abort(int lasControlMotorFired) {
    if (lasControlMotorFired == 0) {
      //assert false: "LAS control failure";
      throw new RuntimeException("LAS control failure");
    }
  }

  void doStage2Separation() {
    doneStage2 = true;
  }

  // that's nominal, if the LAS is not required anymore
  void doLASjettison(int altitude) {
    if (!lasDocked) {
      //assert false : "las jettison without docked las";
      throw new RuntimeException("las jettison without docked las");
    } else if (altitude < 100000) {
      //assert false :"las jettison at altitudes under 100000 ft prohibited";
      throw new RuntimeException("las jettison at altitudes under 100000 ft prohibited");
    }
    lasDocked = false;
    dockedComponents--;
  }

  void doLSAMrendezvous() {
    lsamAscentDocked = true;
    lsamDescentDocked = true;
    edsDocked = true;

    dockedComponents += 3;
  }

  boolean doEDSseparation() {
    if (!edsDocked) {
      //assert false : "eds separation without eds attached";
      throw new RuntimeException("eds separation without eds attached");
      //return false;
    }
    edsDocked = false;
    dockedComponents--;

    return true;
  }

  void doLSAMascentBurn() {
    lsamDescentDocked = false;

    dockedComponents--;
  }

  void doLSAMascentRendezvous() {
    lsamAscentDocked = false;

    dockedComponents--;
  }

  void doSMseparation() {
    if (!smDocked) {
      //assert false : "sm separation without sm attached";
      throw new RuntimeException("sm separation without sm attached");
    }
    smDocked = false;

    dockedComponents--;
  }

  void doEiBurn(int hasCMimbalance, int hasRCSfailure) {
    if (hasRCSfailure == 0) {
      //assert false: "CM RCS failure";
      throw new RuntimeException("CM RCS failure");
    }
  }
  //--- assertions

  boolean readyForLSAMrendezvous() {
    if (lasDocked) {
      //assert false : "lsamRendezvous with las attached";
      throw new RuntimeException("lsamRendezvous with las attached");
    }
    return true;
  }

  boolean readyForDeorbit() {
    if (lasDocked || smDocked || lsamAscentDocked || edsDocked) {
      //assert false : "deorbit with docked components";
      throw new RuntimeException("deorbit with docked components");
    }
    return true;
  }

  boolean readyForTliBurn() {
    if (!edsDocked) {
      //assert false: "tliBurn without EDS";
      throw new RuntimeException("tliBurn without EDS");
    }
    return true;
  }

  boolean readyForTeiBurn() {
    if (lsamAscentDocked || lsamDescentDocked) {
      //assert false: "teiBurn with LSAM components docked";
      throw new RuntimeException("teiBurn with LSAM components docked");
    }
    return true;
  }

  boolean readyForEiBurn() {
    if (cmDocked && dockedComponents == 1) {
      return true;
    } else {
      //assert false: "eiBurn with components docked to CM";
      //return false;
      throw new RuntimeException("eiBurn with components docked to CM");
    }
  }

  boolean readyForChuteSequence() {
    if (cmDocked && (dockedComponents == 1)) {
      return true;
    } else {
      //assert false: "chute sequence with components docked to CM";
      //return false;
      throw new RuntimeException("chute sequence with components docked to CM");
    }
  }

  /**
   * @return the doneStage1
   */
  public boolean isDoneStage1() {
    return doneStage1;
  }

  /**
   * @return the doneStage2
   */
  public boolean isDoneStage2() {
    return doneStage2;
  }
}
