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
package gov.nasa.jpf.psyco.path;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.JPFShell;
import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.constraints.api.InterpolationSolver;
import gov.nasa.jpf.constraints.solvers.ConstraintSolverFactory;
import gov.nasa.jpf.jdart.constraints.Path;
import gov.nasa.jpf.jdart.summaries.SummaryStore;
import gov.nasa.jpf.psyco.PsycoConfig;
import gov.nasa.jpf.psyco.alphabet.SummaryAlphabet;
import gov.nasa.jpf.psyco.alphabet.SymbolicMethodSymbol;
import gov.nasa.jpf.psyco.learnlib.SymbolicExecutionResult;
import gov.nasa.jpf.psyco.path.equivalence.IncreasingDepthExhaustiveTest;
import gov.nasa.jpf.psyco.path.learnlib.PathEquivalenceTest;
import gov.nasa.jpf.psyco.path.learnlib.PathQueryOracle;
import gov.nasa.jpf.psyco.path.learnlib.PathSymbol;
import gov.nasa.jpf.solver.SolverWrapper;
import gov.nasa.jpf.util.JPFLogger;
import gov.nasa.jpf.util.LogManager;
import gov.nasa.jpf.util.SimpleProfiler;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.automatalib.automata.transout.MealyMachine;
import net.automatalib.util.graphs.dot.GraphDOT;
import net.automatalib.words.Alphabet;
import net.automatalib.words.impl.SimpleAlphabet;

public class PsycoPath implements JPFShell {

  private Config config;
  
  private JPFLogger logger;  

  public PsycoPath(Config conf) {
    this.config = conf;
    LogManager.init(conf);
    logger = JPF.getLogger("psyco");
  }

  @Override
  public void start(String[] strings) {
    try {
      run();
    } catch (IOException ex) {
      Logger.getLogger(PsycoPath.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
  private Alphabet<PathSymbol> buildAlphabet(SummaryAlphabet inputs) 
          throws IOException {
    
    Alphabet<PathSymbol> ret = new SimpleAlphabet<>();
    for (SymbolicMethodSymbol sms : inputs) {
      SymbolicExecutionResult summary = inputs.getSummary(sms);
      for (Path p : summary) {
        ret.add(new PathSymbol(p, sms));
      }
    }
    return ret;
  }
  
  public void run() throws IOException {

    SimpleProfiler.start("PSYCO-run");
    
    ConstraintSolverFactory factory = 
            new ConstraintSolverFactory(this.config);
    
    ConstraintSolver cSolver = new SolverWrapper(factory.createSolver());
    InterpolationSolver iSolver = (InterpolationSolver) 
            factory.createSolver("smtinterpol");   
    PsycoConfig pconf = new PsycoConfig(config, cSolver, iSolver);

    SummaryStore store = SummaryStore.create(config);
    SummaryAlphabet summaries = new SummaryAlphabet(store, cSolver);        
    Alphabet<PathSymbol> inputs = buildAlphabet(summaries);
    
    PathQueryOracle oracle = new PathQueryOracle(
            summaries.getInitialValuation(), cSolver);

    int sigma = inputs.size();
            
    PathEquivalenceTest eqtest;
    eqtest = new IncreasingDepthExhaustiveTest(oracle, inputs, pconf);
    
    ModelGenerator gen = new ModelGenerator(oracle, eqtest, inputs);    
    MealyMachine model = gen.generateModel();
    
    SimpleProfiler.stop("PSYCO-run");

    logger.info("Model ---------------------------------------------------------");
    GraphDOT.write(model, inputs, System.out);
    logger.info("---------------------------------------------------------------");
    logger.info();
    logger.info("Stats ---------------------------------------------------------");
    logger.info("States: " + model.size());
    logger.info("Inputs: " + inputs.size());
    logger.info("Termination: " + pconf.getTermination().getReason());
    eqtest.logStatistics();
    logger.info("---------------------------------------------------------------");    
    logger.info("Profiling:\n" + SimpleProfiler.getResults());

  }  
}