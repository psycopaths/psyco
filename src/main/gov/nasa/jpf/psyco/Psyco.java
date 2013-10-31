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
import gov.nasa.jpf.jdart.summaries.SummaryConfig;
import gov.nasa.jpf.jdart.summaries.SummaryStore;
import gov.nasa.jpf.psyco.alphabet.SummaryAlphabet;
import gov.nasa.jpf.psyco.alphabet.SymbolicMethodAlphabet;
import gov.nasa.jpf.psyco.alphabet.SymbolicMethodSymbol;
import gov.nasa.jpf.psyco.equivalence.IncreasingDepthExhaustiveTest;
import gov.nasa.jpf.psyco.learnlib.SymbolicEquivalenceTest;
import gov.nasa.jpf.psyco.learnlib.SymbolicExecutionOracle;
import gov.nasa.jpf.psyco.oracles.JDartOracle;
import gov.nasa.jpf.psyco.oracles.SummaryOracle;
import gov.nasa.jpf.solver.SolverWrapper;

import gov.nasa.jpf.util.JPFLogger;
import gov.nasa.jpf.util.LogManager;
import gov.nasa.jpf.util.SimpleProfiler;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.automatalib.automata.transout.MealyMachine;
import net.automatalib.util.graphs.dot.GraphDOT;

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

    SimpleProfiler.start("PSYCO-run");
    PsycoConfig pconf = new PsycoConfig(config);
    
    ConstraintSolverFactory factory = 
            new ConstraintSolverFactory(this.config);
    
    ConstraintSolver solver = new SolverWrapper(factory.createSolver());

    SymbolicMethodAlphabet inputs = null;
    SymbolicExecutionOracle seOracle = null;
    if (!pconf.isUseSummaries()) {    
      SummaryConfig concolicConf = new SummaryConfig(this.config);
      inputs = new SymbolicMethodAlphabet(
              concolicConf.getSummaryMethods());
      seOracle = new JDartOracle(this.config, inputs);
    }
    else {
      SummaryStore store = SummaryStore.create(config);
      inputs = new SummaryAlphabet(store, solver);    
      seOracle = new SummaryOracle( (SummaryAlphabet)inputs, solver);
    }
    
    int sigma = inputs.size();
    logger.info("Methods -------------------------------------------------------");
    for (SymbolicMethodSymbol sms : inputs) {
      logger.info(sms);
    }
    logger.info("---------------------------------------------------------------");

    OracleProvider provider = new OracleProvider(seOracle, inputs, pconf);
            
    SymbolicEquivalenceTest eqtest = null;    
    // TODO: this should be parameterized later
    eqtest = new IncreasingDepthExhaustiveTest(provider, pconf);
    
    InterfaceGenerator gen = new InterfaceGenerator(
            provider, pconf, eqtest, solver);
    
    MealyMachine model = gen.generateInterface();
    
    SimpleProfiler.stop("PSYCO-run");

    logger.info("Model ---------------------------------------------------------");
    GraphDOT.write(model, inputs, System.out);
    logger.info("---------------------------------------------------------------");
    logger.info();
    logger.info("Stats ---------------------------------------------------------");
    logger.info("States: " + model.size());
    logger.info("Inputs: " + inputs.size());
    logger.info("Refinements: " + (inputs.size() - sigma));
    provider.logStatistics();
    eqtest.logStatistics();
    logger.info("---------------------------------------------------------------");    
    logger.info("Profiling:\n" + SimpleProfiler.getResults());
  }  
}
