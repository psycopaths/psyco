
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
package gsoc.cev_esas;

import gov.nasa.jpf.jdart.Symbolic;
import java.util.EnumSet;

/**
 * class modeling behavior and configuration of the spacecraft
 * 
 * 'Spacecraft' is an example of a controlled object that is referenced from
 * the state chart
 */
public class Spacecraft {
  
  Failures failures;
  ErrorLog errors;
  @Symbolic("true")
  boolean cStage1, cStage2, cLAS, cCM, cSM, cEDS, cLSAM_ASCENT, cLSAM_DESCENT;
//  enum Component { STAGE1, STAGE2, LAS, CM, SM, EDS, LSAM_ASCENT, LSAM_DESCENT };
//  EnumSet<Component> configuration;

  public Spacecraft (Failures failures, ErrorLog errors) {
    cStage1 = true;
    cStage2 = true;
    cLAS = true;
    cCM = true;
    cSM = true;
    cEDS = false;
    cLSAM_ASCENT = false;
//    cLSAM_DESCENT = false;
//    configuration = EnumSet.of( Component.STAGE1,
//                                Component.STAGE2,
//                                Component.LAS,
//                                Component.CM,
//                                Component.SM);
    
    this.failures = failures;
    this.errors = errors;
  }
  
  
  //--- actions
  
  public void doStage1Separation () {
//    configuration.remove(Component.STAGE1);
    cStage1 = false;
  }
  
  // that's nominal, if the LAS is not required anymore
  public void doLASjettison () {
    //configuration.remove(Component.LAS);
    cLAS = false;
  }
    
  public void doStage2Separation () {
//    configuration.remove(Component.STAGE2);
    cStage2 = false;
  }
  
  public void doLSAMrendezvous () {
    cLSAM_ASCENT = true;
    cLSAM_DESCENT = true;
    cEDS = true;
//    configuration.add(Component.LSAM_ASCENT);
//    configuration.add(Component.LSAM_DESCENT);
//    configuration.add(Component.EDS);
  }

  public void doEDSseparation () {
//    configuration.remove(Component.EDS);    
    cEDS = false;
  }
  
  public void doLSAMascentBurn () {
//    configuration.remove(Component.LSAM_DESCENT);
    cLSAM_DESCENT = false;
  }
  
  public void doLSAMascentRendezvous () {
//    configuration.remove(Component.LSAM_ASCENT);    
    cLSAM_ASCENT = false;
  }
  
  public void doSMseparation () {
//    configuration.remove(Component.SM);   
    cSM = false;
  }
  
  public void doEiBurn (boolean hasCMimbalance, boolean hasRCSfailure){
    if (!hasRCSfailure) {
      failures.setCM_RCSfailure();
    }
  }
  
  //--- off nominal
  
  public void doStage1Abort (int altitude, boolean lasControlMotorFired){
    if (!lasControlMotorFired){
      failures.setLAS_CNTRLfailure();
    }
  }
  
  public void doLowActiveAbort () {
//    configuration.remove(Component.LAS);
//    configuration.remove(Component.SM);
//    configuration.remove(Component.STAGE1);
//    configuration.remove(Component.STAGE2);
    cLAS = false;
    cSM = false;
    cStage1 = false;
    cStage2 = false;
  }

  public void doLowPassiveAbort () {
//    configuration.remove(Component.LAS);
//    configuration.remove(Component.SM);
//    configuration.remove(Component.STAGE1);
//    configuration.remove(Component.STAGE2);
    cLAS = false;
    cSM = false;
    cStage1 = false;
    cStage2 = false;
  }
  
  public void doStage2Abort (boolean lasControlMotorFired){
    if (!lasControlMotorFired){
      failures.setLAS_CNTRLfailure();
    }
  }

  
  //--- assertions
  
  public boolean readyForLSAMrendezvous() {
//    if (configuration.contains(Component.LAS)){
    if (cLAS){
      errors.log("lsamRendezvous with LAS attached");
      return false;
    } else {
      return true;
    }
  }

  public boolean readyForDeorbit () {
//    if ( configuration.contains(Component.LAS) ||   
//         configuration.contains(Component.SM) ||
//         configuration.contains(Component.LSAM_ASCENT) ||
//         configuration.contains(Component.EDS)){
    if ( cLAS ||   
         cSM ||
         cLSAM_ASCENT ||
         cEDS){
      
      errors.log("deorbit with docked components: " + configuration());
      return false;
    } else {
      return true;
    }
  }

  public boolean readyForTliBurn () {
    if (!cEDS){
      errors.log("tliBurn without EDS");
      return false;
    } else {
      return true;
    }
  }
  
  public boolean readyForTeiBurn () {
    if (cLSAM_ASCENT ||
        cLSAM_DESCENT){
      errors.log("teiBurn with LSAM components docked");
      return false;
    } else {
      return true;
    }
  }
  
  public boolean readyForEiBurn () {
    if (cCM && (configurationsize() == 1)){
      return true;
    } else {
      errors.log("eiBurn with components docked to CM: " + configuration());
      return false;
    }
  }
  
  public boolean readyForChuteSequence() {
    if (cCM && (configurationsize() == 1)){
      return true;
    } else {
      errors.log("chute sequence with components docked to CM: " + configuration());
      return false;
    }
  }
  
  private String configuration(){
    String config = "";
    if(cStage1){
      config += "Stage1\n";
    }
    if(cStage2){
      config += "Stage2\n";
    }
    if(cLAS){
      config += "LAS\n";
    }
    if(cCM){
      config += "CM\n";
    }
    if(cSM){
      config += "SM\n";
    }
    if(cEDS){
      config += "EDS\n";
    }
    if(cLSAM_ASCENT){
      config += "LSAM_ASCENT\n";
    }
    if(cLSAM_DESCENT){
      config += "LSAM_DESCENT\n";
    }
    return config;
  }
  
  private int configurationsize(){
    int config = 0;
    if(cStage1){
      config++;
    }
    if(cStage2){
      config++;
    }
    if(cLAS){
      config++;
    }
    if(cCM){
      config++;
    }
    if(cSM){
      config++;
    }
    if(cEDS){
      config++;
    }
    if(cLSAM_ASCENT){
      config++;
    }
    if(cLSAM_DESCENT){
      config++;
    }
    return config;
  }
  
  public boolean isDoneStage1(){
    return cStage1;
  }
  
  public boolean isDoneStage2(){
    return cStage2;
  }
}