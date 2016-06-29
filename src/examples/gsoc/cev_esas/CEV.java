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

  @Symbolic("true")
  int internalState = cevStatesAscent;

  @Symbolic("true")
  int internalAscentState = ascentStatesPrelaunchCheck;

  @Symbolic("true")
  int internalEarthOrbitState = earthOrbitStatesInsertion;

  @Symbolic("true")
  int internalLunarOpsState = lunarOpsStatesInsertion;

  @Symbolic("true")
  int internalEntryState = entryStatesEntryInterface;

  @Symbolic("true")
  int internalLunarLandingCEVState = 
          lunarLandingCEVStatesOrbitOpsLoiter;

  @Symbolic("true")
  int internalLunarLandingLSAMState = 
          lunarLandingLSAMStatesLunarDescent;
  
  public void srbIgnition(){
    if(internalState == cevStatesAscent){
      if(internalAscentState == ascentStatesPrelaunchCheck){
        internalAscentState = ascentStatesFirstStage;
        return;
      }
    }
    assert false;
  }
  
  //@Params("10|1") - This was in the state model,
  //but is not add to the new modelation.
  public void failure (int tminus) {
    if(internalState == cevStatesAscent){
      if(internalAscentState == ascentStatesPrelaunchCheck){  
        if (tminus <= 5) {
          internalAscentState = ascentStatesPadAbort;
        } else {
          internalAscentState = ascentStatesHoldLaunch;
        }
        return;
      }
    }
    assert false;
  }

  public void stage1Separation() {
    if(internalState == cevStatesAscent){
      if(internalAscentState == ascentStatesFirstStage){
        spacecraft.doStage1Separation();
        internalAscentState = ascentStatesSecondStage;
        return;
      }
    }
    assert false;
  }

  //@Params("<5000|120000|200000>, <true|false>") -not yet in the model
  public void abort (int altitude, boolean controlMotorFired) {
    if(internalState == cevStatesAscent){
      if(internalAscentState == ascentStatesFirstStage){
        spacecraft.doStage1Abort(altitude,controlMotorFired);
        if (altitude <= 120000) {
          if (controlMotorFired) {
            internalAscentState = ascentStatesAbortLowActiveLAS;
          } else {
            internalAscentState = ascentStatesAbortPassiveLAS;
          }
        }
        if (altitude >= 120000) { // <ERR> interval overlap -> ambiguity
          //TODO: Are this errors supposed to be found?
          //Should this reamin or not?
            internalAscentState = ascentStatesAbortHighActiveLAS;
          // <ERR> forgotten controlMotor branch
        }
        return;
      }
      if(internalAscentState == ascentStatesSecondStage){
        spacecraft.doStage2Abort(controlMotorFired);
        
        if (controlMotorFired) {
          internalAscentState = ascentStatesAbortHighActiveLAS;
        } else {
          internalAscentState = ascentStatesAbortPassiveLAS;
        }
        return;
      }
      
    }
    //assert hasNextState() : errors.log("abort command did not enter abort state");
    //TODO: What does hasNextState?
    assert false : errors.log("abort command did not enter abort state");
  }

  public void lasJettison() {
    if(internalState == cevStatesAscent){
      if(internalAscentState == ascentStatesSecondStage){
      spacecraft.doLASjettison();
      return;
      }
    }
    assert false;
  }
  
  public void stage2Separation () {
    if(internalState == cevStatesAscent){
      if(internalAscentState == ascentStatesSecondStage){
        spacecraft.doStage2Separation();
        internalState = cevStatesEarthOrbit;
        return;
      }
    }
    assert false;
  }

  public void abortPassiveLAScompletion() {
    if(internalState == cevStatesAscent){
      if(internalAscentState == ascentStatesAbortPassiveLAS){
        spacecraft.doLowPassiveAbort();
        internalState = cevStatesEntry;
        internalEntryState = entryStatesChuteSequence;
        return;
      }
    }
    assert false;
  }

  public void completion() {
    if(internalState == cevStatesAscent){
      if(internalAscentState == ascentStatesHoldLaunch){
        internalState = cevStatesEndState;
        return;
      }
      if(internalAscentState == ascentStatesPadAbort){
        internalState = cevStatesEndState;
        return;
      }
      if(internalAscentState == ascentStatesAbortPassiveLAS){
        spacecraft.doLowPassiveAbort();
        internalState = cevStatesEntry;
        internalEntryState = entryStatesChuteSequence;
        return;
      }
      if(internalAscentState == ascentStatesAbortLowActiveLAS){
        assert failures.noLAS_CNTRLfailure() : errors.log("active LAS with failed control motor");
        spacecraft.doLowActiveAbort();
        internalState = cevStatesEntry;
        internalEntryState = entryStatesChuteSequence;
        return;
      }
      if(internalAscentState == ascentStatesAbortHighActiveLAS){
        assert failures.noLAS_CNTRLfailure() : errors.log("active LAS with failed control motor");
        internalState = cevStatesEntry;
        internalEntryState = entryStatesChuteSequence;
        return;
      }
    }
    if(internalState == cevStatesEarthOrbit){
      if(internalEarthOrbitState == earthOrbitStatesInsertion){
       if (failures.noEARTH_SENSORfailure()){
          internalEarthOrbitState = earthOrbitStatesOrbitOps;
        } else {
          internalEarthOrbitState = earthOrbitStatesSafeHold;
          // <ERR> never reached
        }
        return;
      }
    }
    if(internalState == cevStatesLunarOps){
      if(internalLunarOpsState == lunarOpsStatesInsertion){
        internalLunarOpsState = lunarOpsStatesLunarOrbit;
        return;
      }
      if(internalLunarOpsState == lunarOpsStatesLunarLanding){
        internalLunarOpsState = lunarOpsStatesLunarOrbit;
      }
    }
    if(internalState == cevStatesEntry){
      if(internalEntryState == entryStatesEntryInterface){
        internalEntryState = entryStatesNominalEntry;
        return;
      }
      if(internalEntryState == entryStatesNominalEntry){
        internalEntryState = entryStatesChuteSequence;
        return;
      }
      if(internalEntryState == entryStatesChuteSequence){
        assert spacecraft.readyForChuteSequence() : errors.last();
        internalEntryState = entryStatesLanding;
        return;
      }
      if(internalEntryState == entryStatesLanding){
        internalState = cevStatesEndState;
        return;
      }
      if(internalEntryState == entryStatesAbortEntryBallistic){
        internalEntryState = entryStatesChuteSequence;
        return;
      }
      if(internalEntryState == entryStatesAbortEntryFixedBank){
        internalEntryState = entryStatesChuteSequence;
        return;
      }
      if(internalState == cevStatesLunarOps){
        if(internalLunarOpsState == lunarOpsStatesLunarLanding){
          if(internalLunarLandingLSAMState 
                  == lunarLandingLSAMStatesLunarDescent){
            internalLunarLandingLSAMState = lunarLandingLSAMStatesSurfaceOps;
            return;
          }
        }
      }
    }
    assert false;
  }

  public void lsamRendezvous() {
    if(internalState == cevStatesEarthOrbit){
      if(internalEarthOrbitState == earthOrbitStatesOrbitOps){
        assert spacecraft.readyForLSAMrendezvous() : errors.last();
        spacecraft.doLSAMrendezvous();
        return;
      }
    }
    assert false;
  }

  public void tliBurn() {
    if(internalState == cevStatesEarthOrbit){
      if(internalEarthOrbitState == earthOrbitStatesOrbitOps){
        assert spacecraft.readyForTliBurn() : errors.last();
        internalState=cevStatesTransitEarthMoon;
        return;
      }
    }
    assert false;
  }

  public void deOrbit() {
    if(internalState == cevStatesEarthOrbit){
      if(internalEarthOrbitState == earthOrbitStatesOrbitOps){
        assert spacecraft.readyForDeorbit() : errors.last();
        internalState = cevStatesEntry;
        return;
      }
      if(internalEarthOrbitState == earthOrbitStatesSafeHold){
        assert spacecraft.readyForDeorbit() : errors.last();
        internalState = cevStatesEntry;
        return;
      }
    }
    assert false;
  }

  public void enterOrbitOps() {
    if(internalState == cevStatesEarthOrbit){
      if(internalEarthOrbitState == earthOrbitStatesSafeHold){
        if (failures.noEARTH_SENSORfailure()){
          internalEarthOrbitState = earthOrbitStatesOrbitOps;
        }
        return;
      }
    }
    assert false;
  }

  public void edsSeparation() {
    if(internalState == cevStatesTransitEarthMoon){
      spacecraft.doEDSseparation();
      return;
    }
    assert false;
  }

  public void loiBurn () {
    if(internalState == cevStatesTransitEarthMoon){
      internalState = cevStatesLunarOps;
      return;
    }
    assert false;
  }

  public void lsamSeparation() {
    if(internalState == cevStatesLunarOps){
      if(internalLunarOpsState == lunarOpsStatesLunarOrbit){
        internalLunarOpsState = lunarOpsStatesLunarLanding;
        return;
      }
    }
    assert false;
  }

  public void teiBurn() {
    if(internalState == cevStatesLunarOps){
      if(internalLunarOpsState == lunarOpsStatesLunarOrbit){
        assert spacecraft.readyForTeiBurn() : errors.last();
        internalState = cevStatesTransitMoonEarth;
      }
    }
    assert false;
  }

  public void smSeparation() {
    if(internalState == cevStatesTransitMoonEarth){
      spacecraft.doSMseparation();
      return;
    }
    assert false;
  }

  public void eiBurn (boolean cmImbalance, boolean rcsFailure) {
    if(internalState == cevStatesTransitMoonEarth){
      // spacecraft should now only consist of CM
      assert spacecraft.readyForEiBurn() : errors.last();
      spacecraft.doEiBurn(cmImbalance, rcsFailure);
      if (cmImbalance) {
        internalState = cevStatesEntry;
        internalEntryState = entryStatesAbortEntryBallistic;
      } else if (rcsFailure) {
        internalState = cevStatesEntry;
        internalEntryState = entryStatesAbortEntryFixedBank;
      } else {
        internalState = cevStatesEntry;
      }
      return;
    }
    assert false;
  }

  public void lsamAscentBurn () {
    if(internalState == cevStatesLunarOps){
      if(internalLunarOpsState == lunarOpsStatesLunarLanding){
        if(internalLunarLandingLSAMState == lunarLandingLSAMStatesSurfaceOps){
          spacecraft.doLSAMascentBurn();
          internalLunarLandingLSAMState = lunarLandingLSAMStatesLunarAscent;
          return;
        }
      }
    }
    assert false;
  }
  
  public void lsamAscentRendezvous () {
    if(internalState == cevStatesLunarOps){
      if(internalLunarOpsState == lunarOpsStatesLunarLanding){
        if(internalLunarLandingCEVState == lunarLandingCEVStatesOrbitOpsLoiter){
          internalState = cevStatesEndState;
        }
        if(internalLunarLandingLSAMState == lunarLandingLSAMStatesLunarAscent){
          spacecraft.doLSAMascentRendezvous();
          internalState = cevStatesEndState;
        }
        return;
      }
    }
    assert false;
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