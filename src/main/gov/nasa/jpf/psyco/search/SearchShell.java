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
import gov.nasa.jpf.psyco.search.collections.IterationImage;
import gov.nasa.jpf.psyco.search.collections.SymbolicImage;
import gov.nasa.jpf.psyco.search.region.ValuationRegion;
import gov.nasa.jpf.psyco.search.transitionSystem.SymbolicTransitionHelper;
import gov.nasa.jpf.psyco.search.transitionSystem.TransitionHelper;
import gov.nasa.jpf.psyco.util.PsycoProfiler;
import gov.nasa.jpf.psyco.util.ResultSaver;

import gov.nasa.jpf.util.JPFLogger;
import gov.nasa.jpf.util.LogManager;
import gov.nasa.jpf.util.SimpleProfiler;
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
  private String folderName = "default";
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
    updateFolderName(pconf);
    
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
    executeSearch(pconf, store, solver);
    PsycoProfiler.stop("PSYCO-search");
    PsycoProfiler.stop("PSYCO-run");
    logger.info("Profiling:\n" + PsycoProfiler.getResults());
    PsycoProfiler.writeRunToFolder(folderName);
  } 

  private void executeSearch(PsycoConfig pconf, SummaryStore store,
          ConstraintSolver solver){
    if(pconf.shouldUseEnumerativeSearch()){
      executeEnumerativeSearch(store, solver);
    }
    if(pconf.shouldUseSymbolicSearch()){
      executeSymbolicSearch(store, solver, pconf.getMaxSearchDepth());
    }
  }

  private void executeEnumerativeSearch(SummaryStore store,
          ConstraintSolver solver){
    logger.info("Start enumerative search");
    Valuation initValuation = fix_init_valuation(store.getInitialValuation());
    IterationImage<ValuationRegion> searchResult =
            EnumerativeSearchEngine.enumerativBreadthFirstSearch(
              convertTransitionPaths(store), 
              initValuation,
              solver);
    logger.info("Enumerative search done. Here is the result:");
    StringBuilder searchResultString = new StringBuilder();
    try {
      searchResult.print(searchResultString);
    } catch (IOException ex) {
      Logger.getLogger(SearchShell.class.getName()).log(Level.SEVERE, null, ex);
    }
    logger.info(searchResultString.toString());
    logger.info();
  }

  private void executeSymbolicSearch(SummaryStore store,
          ConstraintSolver solver, int maxDepth){
    logger.info("Start symbolic search");
    Valuation initValuation = fix_init_valuation(store.getInitialValuation());
    TransitionHelper helper = new SymbolicTransitionHelper();
    SolverInstance.getInstance().setSolver(solver);
    TransitionSystem transitionSystem = 
            new TransitionSystem(initValuation,
                    convertTransitionPaths(store), helper);
    logger.info(transitionSystem.toString());
    SymbolicImage searchResult =
            SymbolicSearchEngine.symbolicBreadthFirstSearch(
            transitionSystem,
            solver, maxDepth);
    logger.info("symbolic search terminated for following reason:");
    if(searchResult.getDepth() == Integer.MAX_VALUE){
      logger.info("Symbolic search hit predefined max"
              + " depth value and was interrupted.");
    }else{
      logger.info("Symbolic search done and terminated by fix point");
    }
    logger.info("However, here is the result:");
    
    StringBuilder searchResultString = new StringBuilder();
    try {
      searchResult.print(searchResultString);
    } catch (IOException ex) {
      Logger.getLogger(SearchShell.class.getName()).log(Level.SEVERE, null, ex);
    }
    logger.info(searchResultString.toString());
    logger.info("");
    ResultSaver.writeResultToFolder(searchResult, folderName);
//    logger.info("CSV:");
//    logger.info(searchResult.toCSV());
  }

  private List<Path> convertTransitionPaths(SummaryStore store) {
    List<Path> paths = new ArrayList<>();
    
    Set<String> keys = store.getConcolicMethodIds();
    for(String id: keys){
      MethodSummary summary = store.getSummary(id);
      for(Path p: summary){
        paths.add(p);
      }
    }
    return paths;
  }

  /**
  *Fix me! The jDart transition system includes method parameter to the
  * inital state right know. This should not happen.
  */
  private Valuation fix_init_valuation(Valuation initialValuation) {
    Valuation result = new Valuation();
    for(ValuationEntry entry: initialValuation){
      if(entry.getVariable().getName().startsWith("this")){
        result.addEntry(entry);
      }
    }
    return result;
  }

  private void updateFolderName(PsycoConfig pconf){
    folderName = pconf.getResultFolderName();
    folderName = "result" + File.separator + folderName + File.separator;
    File file = new File(folderName);
    if(!file.exists()){
      file.mkdirs();
    }
  }
}