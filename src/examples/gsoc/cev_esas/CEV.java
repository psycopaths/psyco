/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gsoc.cev_esas;

/**
 *
 * @author mmuesly
 */
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

import gov.nasa.jpf.jdart.Symbolic;

public class CEV {
  
  //--------------------------------- our controlled objects
  @Symbolic("true")
  ErrorLog errors = new ErrorLog();

  @Symbolic("true")
  Failures failures = new Failures(errors);

  @Symbolic("true")
  Spacecraft spacecraft = new Spacecraft(failures,errors);
    
  //--------------------------------- our state model
  static int ascentStatesPrelaunchCheck = 0;
  static int ascentStatesFirstStage = 1;
  static int ascentStatesSecondStage = 2;
  static int ascentStatesHoldLaunch = 3;
  static int ascentStatesPadAbort = 4;
  static int ascentStatesAbortPassiveLAS = 5;
  static int ascentStatesAbortLowActiveLAS = 6;
  static int ascentStatesAbortHighActiveLAS = 7;
  
//  public enum AscentStates{PrelaunchCheck, FirstStage, SecondStage,HoldLaunch,
//      PadAbort, AbortPassiveLAS, AbortLowActiveLAS, AbortHighActiveLAS};
  static int earthOrbitStatesInsertion = 0;
  static int earthOrbitStatesOrbitOps = 1;
  static int earthOrbitStatesSafeHold = 2;
//public enum EarthOrbitStates{Insertion, ObitOps, SafeHold };

  static int lunarOpsStatesInsertion = 0;
  static int lunarOpsStatesLunarOrbit = 1;
  static int lunarOpsStatesLunarLanding = 2;
//  public enum LunarOpsStates{Insertion, LunarOrbit, LunarLanding};
  
  static int entryStatesEntryInterface = 0;
  static int entryStatesNominalEntry = 1;
  static int entryStatesChuteSequence = 2;
  static int entryStatesLanding = 3;
  static int entryStatesAbortEntryBallistic = 4;
  static int entryStatesAbortEntryFixedBank = 5;
//  public enum EntryStates{EntryInterface, NominalEntry, ChuteSequence, Landing,
//      AbortEntryBallistic, AbortEntryFixedBank};
  static int cevStatesAscent = 0;
  static int cevStatesEarthOrbit = 1;
  static int cevStatesTransitEarthMoon = 2;
  static int cevStatesLunarOps = 3;
  static int cevStatesTransitMoonEarth = 4;
  static int cevStatesEntry = 5;
  static int cevStatesEndState = 6;
  
//  public enum CEVStates{Ascent, EarthOrbit, TransitEarthMoon, LunarOps,
//      TransitMoonEarth, Entry, EndState};

  static int lunarLandingCEVStatesOrbitOpsLoiter = 0;

  //public enum LunarLandingCEVStates{ObitOpsLoiter};

  static int lunarLandingLSAMStatesLunarDescent = 0;
  static int lunarLandingLSAMStatesSurfaceOps = 1;
  static int lunarLandingLSAMStatesLunarAscent = 2;
  //public enum LunarLandingLSAMStates{LunarDescent, SurfaceOps, LunarAscent};

  int internalState = cevStatesAscent;

  int internalAscentState = ascentStatesPrelaunchCheck;

  int internalEarthOrbitState = earthOrbitStatesInsertion;

  int internalLunarOpsState = lunarOpsStatesInsertion;

  int internalEntryState = entryStatesEntryInterface;

  int internalLunarLandingCEVState = 
          lunarLandingCEVStatesOrbitOpsLoiter;

  int internalLunarLandingLSAMState = 
          lunarLandingLSAMStatesLunarDescent;
  
  public void srbIgnition(){
  }
  
  //@Params("10|1") - This was in the state model,
  //but is not add to the new modelation.
  public void failure (int tminus) {
    if (tminus <= 5) {
      assert false: "PAD abort";
    } else {
      assert false: "Hold launch";
    }
  }

  public void stage1Separation() {
        spacecraft.doStage1Separation();
  }

  //@Params("<5000|120000|200000>, <true|false>") -not yet in the model
  public void abort (int altitude, boolean controlMotorFired) {
    if (!spacecraft.isDoneStage1()) {
      spacecraft.doStage1Abort(altitude, controlMotorFired);
    if (controlMotorFired)
      spacecraft.doLowActiveAbort();
    else
      spacecraft.doLowPassiveAbort();
  	}else if (!spacecraft.isDoneStage2()) {
      spacecraft.doStage2Abort(controlMotorFired);
      if (controlMotorFired)
        spacecraft.doLowPassiveAbort();
  	}
  	assert false: "Mission aborted";
  }

  public void lasJettison() {
      spacecraft.doLASjettison();
   }
  
  public void stage2Separation () {
    spacecraft.doStage2Separation();
  }

  public void abortPassiveLAScompletion() {
    spacecraft.doLowPassiveAbort();
  }

  public void completion() {
        assert failures.noLAS_CNTRLfailure() : errors.log("active LAS with failed control motor");
        spacecraft.doLowActiveAbort();
       if (!failures.noEARTH_SENSORfailure()){
         assert false;
       }
//       assert spacecraft.readyForChuteSequence() : errors.last();
  }

  public void lsamRendezvous() {
    assert spacecraft.readyForLSAMrendezvous() : errors.last();
    spacecraft.doLSAMrendezvous();
  }

  public void tliBurn() {
    assert spacecraft.readyForTliBurn() : errors.last();
  }

  public void deOrbit() {
    assert spacecraft.readyForDeorbit() : errors.last();
  }

  public void enterOrbitOps() {
    if (!failures.noEARTH_SENSORfailure()){
      assert false: "Earth sensor failure. Cannot enter orbit ops";
    }
  }

  public void edsSeparation() {
    spacecraft.doEDSseparation();
  }

  public void loiBurn () {
  }

  public void lsamSeparation() {
  }

  public void teiBurn() {
    assert spacecraft.readyForTeiBurn() : errors.last();
  }

  public void smSeparation() {
    spacecraft.doSMseparation();
  }

  public void eiBurn (boolean cmImbalance, boolean rcsFailure) {
    if(spacecraft.readyForEiBurn()){
      spacecraft.doEiBurn(cmImbalance, rcsFailure);
    }
  }

  public void lsamAscentBurn () {
    spacecraft.doLSAMascentBurn();
  }
  
  public void lsamAscentRendezvous () {
    spacecraft.doLSAMascentRendezvous();
  }
  
//  class LunarOps extends State {
//           
//    
//    class LunarLanding extends State {
//
//      //--- CEV region
//      class OrbitOpsLoiter extends State {
//        public void lsamAscentRendezvous () {
//          setEndState();
//        }
//      }  OrbitOpsLoiter orbitOpsLoiter = makeInitial(new OrbitOpsLoiter()); // CEV region
//
//      //--- LSAM region
//      class LunarDescent extends State {
//        public void completion () {
//          setNextState(surfaceOps);
//        }
//      }  LunarDescent lunarDescent = makeInitial(new LunarDescent()); // LSAM region
//          
//      class SurfaceOps extends State {
//        public void lsamAscentBurn () {
//          spacecraft.doLSAMascentBurn();
//          
//          setNextState(lunarAscent);
//        }
//      }  SurfaceOps surfaceOps = new SurfaceOps();
//      
//      class LunarAscent extends State {
//        public void lsamAscentRendezvous () {
//          spacecraft.doLSAMascentRendezvous();
//          
//          setEndState();
//        }
//      }  LunarAscent lunarAscent = new LunarAscent();
//    }  LunarLanding lunarLanding = new LunarLanding();
//    
//  }  LunarOps lunarOps = new LunarOps();
}