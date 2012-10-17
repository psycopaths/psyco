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
package gov.nasa.jpf.psyco;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;

import java.util.HashMap;
import java.util.Vector;
import gov.nasa.jpf.JPFShell;
import gov.nasa.jpf.jdart.*;
import gov.nasa.jpf.jdart.ConcolicConfig.MethodConfig;
import gov.nasa.jpf.learn.basic.Candidate;
import gov.nasa.jpf.learn.basic.Learner;
import gov.nasa.jpf.learn.TDFA.MemoizeTable;
import gov.nasa.jpf.learn.TDFA.TDFALearner;
import gov.nasa.jpf.learn.basic.SETException;
import gov.nasa.jpf.psyco.oracles.Teacher3Values;
import gov.nasa.jpf.psyco.refinement.AlphabetRefinement;
import gov.nasa.jpf.util.ConfigUtil;

import gov.nasa.jpf.util.JPFLogger;
import gov.nasa.jpf.util.LogManager;
import gov.nasa.jpf.util.SimpleProfiler;

public class Psyco implements JPFShell {

  private Config config;
  
  private JPFLogger logger;  

  public Psyco(Config conf) {
    this.config = conf;
    LogManager.init(conf);
    logger = JPF.getLogger("psyco");
  }

  @Override
  public void start(String[] strings) {
    run();
  }
  
  public void run() {
    
    logger.finest("Psyco.run() -- begin");

    // parse config
    PsycoConfig pconf = new PsycoConfig(config);
    
    // prepare data structures
    Learner learnInterface = null;
    Teacher3Values teacher = null;
    Candidate inf = null;
    boolean newLearningInstance = true;
    AlphabetRefinement refiner = null;
    MemoizeTable memoize = null;

    boolean mode = true;
//    boolean mode = config.getBoolean("JPF.isModeSymbolic");
//  FIXME: re-enable this optimization...
//    if (conf.getProperty("optimizeQueries") != null) {
//      Teacher3Values.setOptimize(conf.getBoolean("optimizeQueries"));
//    }
    
    String teacherAlpha = "";
    SimpleProfiler.start("PSYCO-run");
    
    
    if (mode == Teacher3Values.SYMB) {
      // need to initialize the refiner
      refiner = new AlphabetRefinement(pconf,config);
      for (MethodConfig ap : pconf.getAlphabetMethods()) {
        refiner.addInitialSymbol(ap);
      }
      teacherAlpha = refiner.createInitialRefinement();
    } else {
      for (MethodConfig ap : pconf.getAlphabetMethods()) {
        teacherAlpha += (ap.getClassName() + "." + ap.getMethodName());
        teacherAlpha += ",";       
      }      
    }

    config.setProperty("interface.alphabet", teacherAlpha);

    Teacher3Values.setMode(mode); 
    int depth = pconf.getMaxDepth();
    if (depth > 0) {
      Teacher3Values.maxDepth = depth;
    } 
    
    while (newLearningInstance) {
      newLearningInstance = false; // unless we need to refine
      try {
        /* run the learning algorithm */
        teacher = new Teacher3Values(config, pconf, refiner, memoize);
        learnInterface = new TDFALearner(teacher);
        inf = (Candidate) learnInterface.getAssumption();

      } catch (SETException sx) {
        sx.printStackTrace();
      }

      if (teacher.refine()) {
        config.setProperty("interface.alphabet", teacher.getNewAlphabet());
        newLearningInstance = true;
        memoize = teacher.getMemoizeTable();
      }
    }
    
    SimpleProfiler.stop("PSYCO-run");


    String storeResult = "/tmp/psyco.result";

    // post process ...    
    logger.finest("Psyco.run() -- end");
    logger.info("Profiling:\n" + SimpleProfiler.getResults());
    
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
