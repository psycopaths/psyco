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
package gov.nasa.jpf.psyco.search;

import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.api.ValuationEntry;
import gov.nasa.jpf.jdart.constraints.Path;
import gov.nasa.jpf.jdart.summaries.MethodSummary;
import gov.nasa.jpf.jdart.summaries.SummaryStore;
import gov.nasa.jpf.psyco.PsycoConfig;
import gov.nasa.jpf.psyco.search.datastructures.searchImage.EnumerativeImage;
import gov.nasa.jpf.psyco.search.datastructures.searchImage.StateImage;
import gov.nasa.jpf.psyco.search.datastructures.searchImage.SymbolicImage;
import gov.nasa.jpf.psyco.search.transitionSystem.EnumerativeTransitionHelper;
import gov.nasa.jpf.psyco.search.util.HelperMethods;
import gov.nasa.jpf.psyco.search.transitionSystem.SymbolicTransitionHelper;
import gov.nasa.jpf.psyco.search.transitionSystem.TransitionHelper;
import gov.nasa.jpf.psyco.search.transitionSystem.TransitionSystem;
import gov.nasa.jpf.psyco.util.PsycoProfiler;
import gov.nasa.jpf.psyco.util.ResultSaver;
import gov.nasa.jpf.util.JPFLogger;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The search engine is the central hook-up point for search algorithms and extensions.
 */
public class SearchEngine {

  private Logger logger;
  private String folderName = "default";
  PsycoConfig pconf;

  public SearchEngine(PsycoConfig pconf) {
    this.pconf = pconf;
    logger = JPFLogger.getLogger(HelperMethods.getLoggerName());
    updateFolderName(pconf);
  }

  public StateImage executeSearch(SummaryStore store,
          ConstraintSolver solver) {
    if (pconf.shouldUseEnumerativeSearch()) {
      PsycoProfiler.reset();
      executeEnumerativeSearch(store, solver);
    }
    if (pconf.shouldUseSymbolicSearch()) {
      PsycoProfiler.reset();
      executeSymbolicSearch(store, solver);
    }
    return null;
  }

  private void executeEnumerativeSearch(SummaryStore store,
          ConstraintSolver solver) {
    logger.info("Start enumerative search");
    Valuation initValuation = fix_init_valuation(store.getInitialValuation());
    TransitionHelper helper = new EnumerativeTransitionHelper();
    TransitionSystem system = new TransitionSystem(initValuation,
            convertTransitionPaths(store), helper);
    logger.info(system.toString());
    if(pconf.isSaveTransitionSystem()){
      String transitionSystemFile = pconf.getResultFolderName() 
              + "/transitionSystem.ts";
      system.writeToFile(transitionSystemFile);
    }
    EnumerativeImage searchResult
            = EnumerativeSearchEngine.enumerativBreadthFirstSearch(
                    system,
                    solver, pconf.getMaxSearchDepth());
    logger.info("Enumerative search done. Here is the result:");
    StringBuilder searchResultString = new StringBuilder();
    try {
      searchResult.print(searchResultString);
    } catch (IOException ex) {
      Logger.getLogger(SearchShell.class.getName())
              .log(Level.SEVERE, null, ex);
    }

    logger.fine(searchResultString.toString());
    logger.info("Enumerative Search determined:");
    logger.info("Max search depth k = " + searchResult.getDepth());
    if (searchResult.getDepth() != Integer.MAX_VALUE) {
      logger.info("Set Psyco maxDepth to k.");
      pconf.updateMaxDepth(searchResult.getDepth());
    }
    if (pconf.isSaveSearchResult()) {
      String prefix = "enumerative-";
      ResultSaver.writeResultToFolder(searchResult, folderName,
              prefix);
      PsycoProfiler.writeRunToFolder(folderName, prefix);
    }
  }

  private void executeSymbolicSearch(SummaryStore store,
          ConstraintSolver solver) {
    logger.info("Start symbolic search");
    Valuation initValuation = fix_init_valuation(store.getInitialValuation());
    TransitionHelper helper = new SymbolicTransitionHelper();
    SolverInstance.getInstance().setSolver(solver);
    TransitionSystem transitionSystem
            = new TransitionSystem(initValuation,
                    convertTransitionPaths(store), helper);
    if(pconf.isSaveTransitionSystem()){
      String transitionSystemFile = pconf.getResultFolderName() 
              + "/transitionSystem.ts";
      transitionSystem.writeToFile(transitionSystemFile);
    }
    logger.fine(transitionSystem.toString());
    SymbolicImage searchResult
            = SymbolicSearchEngine.symbolicBreadthFirstSearch(
                    transitionSystem,
                    solver, pconf.getMaxSearchDepth());
    logger.info("symbolic search terminated for following reason:");
    if (searchResult.getDepth() == Integer.MAX_VALUE) {
      logger.info("Symbolic search hit predefined max"
              + " depth value and was interrupted.");
    } else {
      logger.info("Symbolic search done and terminated by fix point");
    }
    logger.info("However, here is the result:");

    StringBuilder searchResultString = new StringBuilder();
    try {
      searchResult.print(searchResultString);
    } catch (IOException ex) {
      logger.severe(ex.getMessage());
    }
    logger.fine(searchResultString.toString());
    logger.info("Symbolic Search determined:");
    logger.info("Max search depth k = " + searchResult.getDepth());
    if (searchResult.getDepth() != Integer.MAX_VALUE) {
      logger.info("Set Psyco maxDepth to k.");
      pconf.updateMaxDepth(searchResult.getDepth());
    }
    if (pconf.isSaveSearchResult()) {
      String prefix = "symbolic-";
      ResultSaver.writeResultToFolder(searchResult,
              transitionSystem, folderName, prefix);
      PsycoProfiler.writeRunToFolder(folderName, prefix);
    }
  }

  private List<Path> convertTransitionPaths(SummaryStore store) {
    List<Path> paths = new ArrayList<>();
    Set<String> keys = store.getConcolicMethodIds();
    for (String id : keys) {
      MethodSummary summary = store.getSummary(id);
      for (Path p : summary) {
        paths.add(p);
      }
    }
    return paths;
  }

  /**
   * Fix me! The jDart transition system includes method parameter to the 
   * inital state right know. This should not happen.
   * This works as a work around, but may be improved.
   */
  private Valuation fix_init_valuation(Valuation initialValuation) {
    Valuation result = new Valuation();
    for (ValuationEntry entry : initialValuation) {
      if (entry.getVariable().getName().startsWith("this")) {
        result.addEntry(entry);
      }
    }
    return result;
  }

  private void updateFolderName(PsycoConfig pconf) {
    folderName = pconf.getResultFolderName();
    File file = new File(folderName);
    if (!file.exists()) {
      file.mkdirs();
    }
  }

  public void saveProfilerResults() {
    PsycoProfiler.writeRunToFolder(folderName);
  }
}