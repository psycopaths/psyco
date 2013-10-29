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

import gov.nasa.jpf.JPFShell;
import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.constraints.solvers.ConstraintSolverFactory;
import gov.nasa.jpf.jdart.termination.NeverTerminate;
import gov.nasa.jpf.jdart.termination.TerminationStrategy;
import gov.nasa.jpf.jdart.termination.TimedTermination;
import gov.nasa.jpf.jdart.termination.UpToFixedNumber;
import gov.nasa.jpf.psyco.alphabet.SummaryAlphabet;
import gov.nasa.jpf.psyco.alphabet.SymbolicMethodAlphabet;
import gov.nasa.jpf.psyco.alphabet.SymbolicMethodSymbol;
import gov.nasa.jpf.psyco.equivalence.IncreasingDepthExhaustiveTest;
import gov.nasa.jpf.psyco.filter.Cache;
import gov.nasa.jpf.psyco.filter.QueryLogger;
import gov.nasa.jpf.psyco.filter.UniformErrorFilter;
import gov.nasa.jpf.psyco.filter.UniformOKSuffixFilter;
import gov.nasa.jpf.psyco.filter.ValidQueryFilter;
import gov.nasa.jpf.psyco.learnlib.ThreeValuedOracle;
import gov.nasa.jpf.psyco.oracles.JDartOracle;
import gov.nasa.jpf.psyco.oracles.SummaryOracle;
import gov.nasa.jpf.psyco.oracles.SymbolicExecutionOracleWrapper;
import gov.nasa.jpf.psyco.oracles.TerminationCheckOracle;
import gov.nasa.jpf.psyco.utils.summaries.MethodSummarizer;
import gov.nasa.jpf.psyco.utils.summaries.SummaryConfig;
import gov.nasa.jpf.psyco.utils.summaries.SummaryStore;
import gov.nasa.jpf.solver.SolverWrapper;

import gov.nasa.jpf.util.JPFLogger;
import gov.nasa.jpf.util.LogManager;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    try {
      run();
    } catch (IOException ex) {
      Logger.getLogger(Psyco.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
  public void run() throws IOException {

    ConstraintSolverFactory factory = 
            new ConstraintSolverFactory(this.config);
    
    ConstraintSolver solver = new SolverWrapper(factory.createSolver());
    
//    SummaryConfig concolicConf = new SummaryConfig(this.config);
//    SymbolicMethodAlphabet inputs = 
//            new SymbolicMethodAlphabet(concolicConf.getSummaryMethods());
//    JDartOracle seOracle = new JDartOracle(this.config, inputs);

    SummaryStore store = SummaryStore.create(config);
    SummaryAlphabet inputs = new SummaryAlphabet(store, solver);    
    SummaryOracle seOracle = new SummaryOracle(inputs, solver);

    for (SymbolicMethodSymbol sms : inputs) {
      System.out.println(sms);
    }
    
    TerminationStrategy terminate = new TimedTermination(0, 5);
    //TerminationStrategy terminate = new UpToFixedNumber(80);
    
    
    ThreeValuedOracle eqOracle = new ValidQueryFilter(
            new UniformOKSuffixFilter(inputs,
            new UniformErrorFilter(inputs, 
            new Cache(
            new QueryLogger(
            new TerminationCheckOracle(terminate, 
            new SymbolicExecutionOracleWrapper(seOracle)))))));
    
    IncreasingDepthExhaustiveTest eqtest = 
            new IncreasingDepthExhaustiveTest(
            eqOracle, inputs);
    
    InterfaceGenerator gen = new InterfaceGenerator(
            inputs, seOracle, eqtest, terminate, solver);
    
    gen.generateInterface();
    
//    
//    logger.finest("Psyco.run() -- begin");
//
//    // parse config
//    PsycoConfig pconf = new PsycoConfig(config);
//    
//    // prepare data structures
//    Learner learnInterface = null;
//    Teacher3Values teacher = null;
//    Candidate inf = null;
//    boolean newLearningInstance = true;
//    AlphabetRefinement refiner = null;
//    MemoizeTable memoize = null;
//
//    boolean mode = true;
////    boolean mode = config.getBoolean("JPF.isModeSymbolic");
////  FIXME: re-enable this optimization...
////    if (conf.getProperty("optimizeQueries") != null) {
////      Teacher3Values.setOptimize(conf.getBoolean("optimizeQueries"));
////    }
//    
//    String teacherAlpha = "";
//    SimpleProfiler.start("PSYCO-run");
//    
//    
//    if (mode == Teacher3Values.SYMB) {
//      // need to initialize the refiner
//      refiner = new AlphabetRefinement(pconf,config);
//      for (MethodConfig ap : pconf.getAlphabetMethods()) {
//        refiner.addInitialSymbol(ap);
//      }
//      teacherAlpha = refiner.createInitialRefinement();
//    } else {
//      for (MethodConfig ap : pconf.getAlphabetMethods()) {
//        teacherAlpha += (ap.getClassName() + "." + ap.getMethodName());
//        teacherAlpha += ",";       
//      }      
//    }
//
//    config.setProperty("interface.alphabet", teacherAlpha);
//
//    Teacher3Values.setMode(mode); 
//    int depth = pconf.getMaxDepth();
//    if (depth > 0) {
//      Teacher3Values.MAX_DEPTH = depth;
//    } 
//    
//    while (newLearningInstance) {
//      newLearningInstance = false; // unless we need to refine
//      try {
//        /* run the learning algorithm */
//        teacher = new Teacher3Values(config, pconf, refiner, memoize);
//        learnInterface = new TDFALearner(teacher);
//        inf = (Candidate) learnInterface.getAssumption();
//
//      } catch (SETException sx) {
//        sx.printStackTrace();
//      }
//
//      if (teacher.refine()) {
//        config.setProperty("interface.alphabet", teacher.getNewAlphabet());
//        newLearningInstance = true;
//        memoize = teacher.getMemoizeTable();
//      }
//    }
//    
//    SimpleProfiler.stop("PSYCO-run");
//
//
//    File storeResult = File.createTempFile("psyco","result");
//
//    // post process ...    
//    logger.finest("Psyco.run() -- end");
//    logger.info("\n\n===================================================================================================");
//    logger.info("Profiling:\n" + SimpleProfiler.getResults());
//    
//    logger.info("\n\n****** NUMBER OF HITS IS: " + teacher.getMemoizeHits());
//    logger.info("\n\n===================================================================================================");
//    if (inf == null) {
//      logger.info("Interface is null - no environment can help");
//    } else {
//      logger.info("Interface generation completed. ");
//      //Candidate.printCandidateAssumption(inf, teacher.getAlphabet());
//      Candidate.dumpCandidateStateMachine(inf, storeResult.getAbsolutePath() , teacher.getAlphabet());
//      if (mode == Teacher3Values.SYMB) {
//        HashMap<String, String> symbolsToPreconditions = refiner.getSymbolsToPreconditions();
//        HashMap<String, String> symbolsToMethodNames = refiner.getSymbolsToMethodNames();
//        Candidate.dumpCandidateStateMachineAsDot(inf, storeResult.getAbsolutePath(), 
//                teacher.getAlphabet(), symbolsToPreconditions, symbolsToMethodNames);
//        
//        BufferedReader br = new BufferedReader(new FileReader(storeResult.getAbsolutePath() + ".dot"));
//        while (br.ready()) {
//          logger.info(br.readLine());
//        }
//      }
//    }
//    logger.info("===================================================================================================");
  }  
}
