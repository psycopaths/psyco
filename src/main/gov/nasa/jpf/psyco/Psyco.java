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
package gov.nasa.jpf.psyco;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;

import gov.nasa.jpf.JPFShell;
import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.constraints.api.InterpolationSolver;
import gov.nasa.constraints.solvers.smtinterpol.SMTInterpolSolver;
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
    
    ConstraintSolverFactory factory = 
            new ConstraintSolverFactory(this.config);
    
    ConstraintSolver cSolver = new SolverWrapper(factory.createSolver());
    InterpolationSolver iSolver = new SMTInterpolSolver();   
    
    PsycoConfig pconf = new PsycoConfig(config, cSolver, iSolver);

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
      inputs = new SummaryAlphabet(store, cSolver);    
      seOracle = new SummaryOracle( (SummaryAlphabet)inputs, cSolver);
    }
    
    int sigma = inputs.size();
    logger.info("Methods -------------------------------------------------------");
    for (SymbolicMethodSymbol sms : inputs) {
      logger.info(sms);
    }
    logger.info("---------------------------------------------------------------");

    DefaultOracleProvider provider = 
            new DefaultOracleProvider(seOracle, inputs, pconf);
            
    SymbolicEquivalenceTest eqtest = null;    
    // TODO: this should be parameterized later
    eqtest = new IncreasingDepthExhaustiveTest(provider, pconf);
    
    InterfaceGenerator gen = new InterfaceGenerator(
            provider, pconf, eqtest);
    
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
    logger.info("Termination: " + pconf.getTermination().getReason());
    provider.logStatistics();
    eqtest.logStatistics();
    logger.info("---------------------------------------------------------------");    
    logger.info("Profiling:\n" + SimpleProfiler.getResults());
  }  
}