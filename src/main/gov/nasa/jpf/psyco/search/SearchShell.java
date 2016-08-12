/*
 * Copyright (C) 2016, United States Government, as represented by the 
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
package gov.nasa.jpf.psyco.search;

import gov.nasa.jpf.psyco.search.transitionSystem.TransitionSystem;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;

import gov.nasa.jpf.JPFShell;
import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.constraints.solvers.ConstraintSolverFactory;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.api.ValuationEntry;
import gov.nasa.jpf.jdart.constraints.Path;
import gov.nasa.jpf.jdart.summaries.MethodSummary;
import gov.nasa.jpf.jdart.summaries.SummaryStore;
import gov.nasa.jpf.psyco.PsycoConfig;
import gov.nasa.jpf.psyco.search.datastructures.searchImage.EnumerativeImage;
import gov.nasa.jpf.psyco.search.datastructures.searchImage.SymbolicImage;
import gov.nasa.jpf.psyco.search.transitionSystem.EnumerativeTransitionHelper;
import gov.nasa.jpf.psyco.search.transitionSystem.SymbolicTransitionHelper;
import gov.nasa.jpf.psyco.search.transitionSystem.TransitionHelper;
import gov.nasa.jpf.psyco.util.PsycoProfiler;
import gov.nasa.jpf.psyco.util.ResultSaver;

import gov.nasa.jpf.util.JPFLogger;
import gov.nasa.jpf.util.LogManager;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author mmuesly
 */
public class SearchShell implements JPFShell {

  private final Config config;
  private final JPFLogger logger; 
  public SearchShell(Config conf) {
    this.config = conf;
    LogManager.init(conf);
    logger = JPF.getLogger("psyco");
  }

  @Override
  public void start(String[] strings) {
    try {
      run();
    } catch (IOException ex) {
      logger.log(Level.SEVERE, null, ex);
    }
  }

  public void run() throws IOException {

    PsycoProfiler.start("PSYCO-run");
    PsycoConfig pconf = new PsycoConfig(config);
    
    ConstraintSolverFactory factory = 
            new ConstraintSolverFactory(this.config);
    
    ConstraintSolver solver = factory.createSolver();

//    SymbolicMethodAlphabet inputs = null;
//    SymbolicExecutionOracle seOracle = null;
    SummaryStore store = SummaryStore.create(config);
    if(store == null){
      System.exit(1);
    }

    PsycoProfiler.start("PSYCO-search");
    SearchEngine searchEngine = new SearchEngine(pconf);
    searchEngine.executeSearch(pconf, store, solver);
    PsycoProfiler.stop("PSYCO-search");
    PsycoProfiler.stop("PSYCO-run");
    logger.info("Profiling:\n" + PsycoProfiler.getResults());
    searchEngine.saveProfilerResults();
  } 


}