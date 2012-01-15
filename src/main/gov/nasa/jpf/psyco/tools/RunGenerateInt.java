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
import gov.nasa.jpf.JPF;

import java.util.HashMap;
import java.util.Vector;
import gov.nasa.jpf.JPFShell;
import gov.nasa.jpf.learn.basic.Candidate;
import gov.nasa.jpf.learn.basic.Learner;
import gov.nasa.jpf.learn.TDFA.MemoizeTable;
import gov.nasa.jpf.learn.TDFA.TDFALearner;
import gov.nasa.jpf.learn.basic.SETException;
import gov.nasa.jpf.psyco.oracles.Teacher3Values;
import gov.nasa.jpf.psyco.refinement.AlphabetRefinement;

import gov.nasa.jpf.util.JPFLogger;
import gov.nasa.jpf.util.LogManager;

public class RunGenerateInt implements JPFShell {
  private JPFLogger logger = JPF.getLogger("teacher");
  Config conf;

  public RunGenerateInt(Config conf) {
    this.conf = conf;
    LogManager.init(conf);
  }

  public void start(String[] args) {

    Learner learnInterface = null;
    Teacher3Values teacher = null;
    Candidate inf = null;
    boolean newLearningInstance = true;
    AlphabetRefinement refiner = null;
    MemoizeTable memoize = null;


    boolean mode = conf.getBoolean("JPF.isModeSymbolic");
    if (conf.getProperty("optimizeQueries") != null) {
      Teacher3Values.setOptimize(conf.getBoolean("optimizeQueries"));
    }
    
    String teacherAlpha = "";
    
    long time1 = System.currentTimeMillis();
    
    if (mode == Teacher3Values.SYMB) {
      // need to initialize the refiner
      refiner = new AlphabetRefinement(conf.getProperty("example.path"),
              conf.getProperty("sut.package"), conf.getProperty("sut.class"));
      for (String ap : conf.getStringArray("interface.alphabet")) {
        // now I have pairs method_name:#parameters    
        String[] methodFields = ap.split(":");
        
        if (methodFields.length == 2) {
          teacherAlpha += (methodFields[0]);
          teacherAlpha += ",";
          refiner.addInitialSymbol(methodFields[0], Integer.parseInt(methodFields[1]));
        }
      }
      teacherAlpha = refiner.createInitialRefinement();
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
    
    Teacher3Values.setMode(mode); 
    int depth = conf.getInt("conjecture.Depth");
    if (depth > 0) {
      Teacher3Values.maxDepth = depth;
    } 
    
    while (newLearningInstance) {
      newLearningInstance = false; // unless we need to refine
      try {
        /* run the learning algorithm */
        teacher = new Teacher3Values(conf, refiner, memoize);
        learnInterface = new TDFALearner(teacher);
        inf = (Candidate) learnInterface.getAssumption();

      } catch (SETException sx) {
        sx.printStackTrace();
      }

      if (teacher.refine()) {
        conf.setProperty("interface.alphabet", teacher.getNewAlphabet());
        newLearningInstance = true;
        memoize = teacher.getMemoizeTable();
      }
    }
    
    long time2 = System.currentTimeMillis();


    String storeResult = conf.getProperty("interface.outputFile");

    logger.info("\n\n Total time is: " + (time2-time1)  );
    logger.info("\n\n****** NUMBER OF HITS IS: " + teacher.getMemoizeHits());
    logger.info("\n\n********************************************");
    if (inf == null) {
      logger.info("Interface is null - no environment can help");
    } else {
      logger.info("Interface generation completed. ");
      Candidate.printCandidateAssumption(inf, teacher.getAlphabet());
      Candidate.dumpCandidateStateMachine(inf, storeResult, teacher.getAlphabet());
      if (mode == Teacher3Values.SYMB) {
        HashMap<String, String> symbolsToPreconditions = refiner.getSymbolsToPreconditions();
        HashMap<String, String> symbolsToMethodNames = refiner.getSymbolsToMethodNames();
        Candidate.dumpCandidateStateMachineAsDot(inf, storeResult, teacher.getAlphabet(), symbolsToPreconditions, symbolsToMethodNames);
      }
    }
    logger.info("********************************************");
  }
}
