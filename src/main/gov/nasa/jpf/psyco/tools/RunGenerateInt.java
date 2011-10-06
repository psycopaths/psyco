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
package gov.nasa.jpf.psyco.tools;

import gov.nasa.jpf.Config;
import java.util.Vector;
import gov.nasa.jpf.JPFShell;
import gov.nasa.jpf.learn.classic.Candidate;
import gov.nasa.jpf.learn.classic.SETException;
import gov.nasa.jpf.learn.classic.SETLearner;
import gov.nasa.jpf.psyco.refinement.AlphabetRefinement;


import gov.nasa.jpf.psyco.oracles.TeacherClassic;
import gov.nasa.jpf.util.LogManager;

public class RunGenerateInt implements JPFShell {

  Config conf;

  public RunGenerateInt(Config conf) {
    this.conf = conf;
    LogManager.init(conf);
  }

  public void start(String[] args) {

    SETLearner learnInterface = null;
    TeacherClassic teacher = null;
    Candidate inf = null;
    boolean newLearningInstance = true;
    AlphabetRefinement refiner = null;


    String mode = conf.getProperty("JPF.mode");
    String teacherAlpha = "";
    
    
    if (mode.equals(TeacherClassic.SYMB)) {
      // need to initialize the refiner
      refiner = new AlphabetRefinement(conf.getProperty("example.path"),
              conf.getProperty("sut.package"), conf.getProperty("sut.class"));
      for (String ap : conf.getStringArray("interface.alphabet")) {
        // now I have pairs method_name:#parameters    
        String[] methodFields = ap.split(":");
        
        if (methodFields.length == 2) {
          teacherAlpha += (methodFields[0]);
          teacherAlpha += ",";
          refiner.addInitialSymbol(methodFields[0], Integer.getInteger(methodFields[1]));
        }
      }
    } else {
      for (String ap : conf.getStringArray("interface.alphabet")) {
        // now I have pairs method_name:#parameters    
        String[] methodFields = ap.split(":");
        
        if (methodFields.length >= 1) {
          teacherAlpha += (methodFields[0]);
          teacherAlpha += ",";
        }
      }      
    }

    conf.setProperty("interface.alphabet", teacherAlpha);
    TeacherClassic.setMode(conf.getProperty("JPF.mode"));
    

    while (newLearningInstance) {
      newLearningInstance = false; // unless we need to refine
      try {
        /* run the learning algorithm */
        teacher = new TeacherClassic(conf, refiner);
        learnInterface = new SETLearner(teacher);
        inf = (Candidate) learnInterface.getAssumption();

      } catch (SETException sx) {
        sx.printStackTrace();
      }

      if (teacher.refine()) {
        conf.setProperty("interface.alphabet", teacher.getNewAlphabet());
        conf.setProperty("sut.class", AlphabetRefinement.REFINED_CLASS_NAME);
        newLearningInstance = true;
      }
    }


    String storeResult = conf.getProperty("interface.outputFile");


    System.out.println("\n\n********************************************");
    if (inf == null) {
      System.out.println("Interface is null - no environment can help");
    } else {
      System.out.print("Interface generation completed. ");
      Candidate.printCandidateAssumption(inf, teacher.getAlphabet());
      Candidate.dumpCandidateStateMachine(inf, storeResult, teacher.getAlphabet());
    }
    System.out.println("********************************************");

  }
  

}
