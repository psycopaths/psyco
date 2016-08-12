/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 *
 * @author mmuesly
 */
public class SearchEngine {
  private Logger logger;
  private String folderName = "default";
  PsycoConfig pconf; 
  public SearchEngine(PsycoConfig pconf){
    this.pconf = pconf;
    logger = JPFLogger.getLogger(HelperMethods.getLoggerName());
    updateFolderName(pconf);
  }
  public StateImage executeSearch(PsycoConfig pconf, SummaryStore store,
          ConstraintSolver solver){
    if(pconf.shouldUseEnumerativeSearch()){
      PsycoProfiler.reset();
      executeEnumerativeSearch(store, solver);
    }
    if(pconf.shouldUseSymbolicSearch()){
      PsycoProfiler.reset();
      executeSymbolicSearch(store, solver, pconf.getMaxSearchDepth());
    }
    return null;
  }

  private void executeEnumerativeSearch(SummaryStore store,
          ConstraintSolver solver){
    SolverInstance.getInstance().setSolver(solver);
    logger.info("Start enumerative search");
    Valuation initValuation = fix_init_valuation(store.getInitialValuation());
    TransitionHelper helper = new EnumerativeTransitionHelper();
    TransitionSystem system = new TransitionSystem(initValuation,
                    convertTransitionPaths(store), helper);
    logger.info(system.toString());
    String transitionSystemFile = folderName +"/transitionSystem.ts";
    system.writeToFile(transitionSystemFile);
    EnumerativeImage searchResult =
            EnumerativeSearchEngine.enumerativBreadthFirstSearch(
              system,
              initValuation, solver);
    logger.info("Enumerative search done. Here is the result:");
    StringBuilder searchResultString = new StringBuilder();
    try {
      searchResult.print(searchResultString);
    } catch (IOException ex) {
      Logger.getLogger(SearchShell.class.getName()).log(Level.SEVERE, null, ex);
    }
    logger.info(searchResultString.toString());
    logger.info(PsycoProfiler.getResults());
    logger.info("");
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
    ResultSaver.writeResultToFolder(searchResult, transitionSystem, folderName);
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
  * inital state right know. This should not happen. This works as a work around
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

  public void saveProfilerResults(){
    PsycoProfiler.writeRunToFolder(folderName);
  }
}
