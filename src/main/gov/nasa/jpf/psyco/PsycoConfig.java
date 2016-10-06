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
import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.constraints.api.InterpolationSolver;
import gov.nasa.jpf.jdart.config.ConcolicConfig;
import gov.nasa.jpf.jdart.termination.NeverTerminate;
import gov.nasa.jpf.jdart.termination.TerminationStrategy;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;

/**
 * PsycoConfig is a container for the Psyco configuration.
 * The used equivalence test method and Psycos search behavior can be modified.
 * Further it is possible to controll result files produced by PSYCO.
 * The following values can be overwritten in the experiment setting, a file ending with .jpf.
 * psyco.depth set the maximal interface enrollment depth. (default infinite)
 * psyco.termination saves the reason, why PSYCO terminated 
 *      during interface learning.
 * psyco.summaries determines whether Psyco should use summaries or not. (default false)
 * psyco.memorize determines whether Psyco should use memorize feature. (default false)
 * psyco.suffixes determines whether Psyco should use a suffix filer. (default false)
 * psyco.por determines whether Psyco should use por filter (default false)
 * psyco.symbolicSearch determines whether Psyco should use symbolicSearch 
 *            to set maxDepth before interface generation. (default true)
 * psyco.enumerativeSearch determines whether Psyco should use enumerativeSearch
 *            to set maxDepth before interface generation. (default true)
 * psyco.resultFolderName the folder in which the result of the psyco run are written.
 *            It is by default prefixed with ./result.
 * psyco.maxSearchDepth it is possible to define an upper bound for the search if desired.
 * psyco.interpolation enables interpolation as equivalence test. (default false)
 * psyco.saveSearchResult determines whether to save searchResult or not. (default false)
 * psyco.saveTransitionSystem determines whether to save the transition System or not. (default false)
 * psyco.saveModel determines whether to write learning result into a file or not. (default false)
 */

public class PsycoConfig {

  private final Config config;
  private int maxDepth = -1;
  private boolean usePOR = false;
  private boolean useMemorization = false;
  private boolean useSuffixFilter = false;
  private boolean useSummaries = false;
  private boolean useInterpolation = false;
  private TerminationStrategy termination = new NeverTerminate();
  private boolean symbolicSearch = true;
  private boolean enumerativeSearch = false;
  private String resultFolderName="default";
  private int maxSearchDepth = Integer.MIN_VALUE;
  private boolean saveResult = false;
  private boolean saveModel = false;
  private boolean saveTransitionSystem = false;

  private boolean useCPAchecker = false;
  private String[] cpaCheckerParams = null; 
  private String cpaCommand;
  
  private final ConstraintSolver constraintSolver;
  private final InterpolationSolver interpolationSolver;

  public PsycoConfig(Config config, ConstraintSolver constraintSolver, 
          InterpolationSolver interpolationSolver) {
    this.config = config;
    this.constraintSolver = constraintSolver;
    this.interpolationSolver = interpolationSolver;
    initialize();
  }
  
  private void initialize() {
    if (config.hasValue("psyco.depth")) {
      maxDepth = config.getInt("psyco.depth");
    }
    if (config.hasValue("psyco.termination")) {
      termination = ConcolicConfig.parseTerminationStrategy(
              config.getProperty("psyco.termination"));
    }
    if (config.hasValue("psyco.summaries")) {
      useSummaries = config.getBoolean("psyco.summaries");
    }
    if (config.hasValue("psyco.memorize")) {
      useMemorization = config.getBoolean("psyco.memorize");
    }
    if (config.hasValue("psyco.suffixes")) {
      useSuffixFilter = config.getBoolean("psyco.suffixes");
    }
    if (config.hasValue("psyco.por")) {
      usePOR = config.getBoolean("psyco.por");
    }
    if(config.hasValue("psyco.symbolicSearch")){
      symbolicSearch = config.getBoolean("psyco.symbolicSearch");
    }
    if(config.hasValue("psyco.enumerativeSearch")){
      enumerativeSearch = config.getBoolean("psyco.enumerativeSearch");
    }
    if(config.hasValue("psyco.resultFolderName")){
      resultFolderName = "result" + File.separator 
              + config.getString("psyco.resultFolderName");
      if(!resultFolderName.endsWith(File.separator)){
        resultFolderName += File.separator;
      }
    }
    
    if (config.hasValue("psyco.cpachecker")) {
      useCPAchecker = config.getBoolean("psyco.cpachecker");
      if (useCPAchecker) {
        cpaCommand = config.getString("psyco.cpacommand");
        cpaCheckerParams = config.getStringArray("psyco.cpaparams");
      }
    }
    
    if(config.hasValue("psyco.maxSearchDepth")){
      maxSearchDepth = config.getInt("psyco.maxSearchDepth");
    }
    if (config.hasValue("psyco.interpolation")) {
      useInterpolation = config.getBoolean("psyco.interpolation");
    }
    if (config.hasValue("psyco.saveSearchResult")) {
      saveResult = config.getBoolean("psyco.saveSearchResult");
    }
    
    if(config.hasValue("psyco.saveTransitionSystem")){
      saveTransitionSystem = config.getBoolean("psyco.saveTransitionSystem");
    }
    if(config.hasValue("psyco.saveModel")){
      saveModel = config.getBoolean("psyco.saveModel");
    }
  }

  /**
   * @return the config
   */
  public Config getConfig() {
    return config;
  }

  /**
   * @return the usePOR
   */
  public boolean isUsePOR() {
    return usePOR;
  }

  /**
   * @return the useMemorization
   */
  public boolean isUseMemorization() {
    return useMemorization;
  }

  /**
   * @return the useSuffixFilter
   */
  public boolean isUseSuffixFilter() {
    return useSuffixFilter;
  }

  /**
   * @return the useSummaries
   */
  public boolean isUseSummaries() {
    return useSummaries;
  }

  public boolean shouldUseSymbolicSearch(){
    return symbolicSearch;
  }
  
  public boolean shouldUseEnumerativeSearch(){
    return enumerativeSearch;
  }
  /**
   * @return the termination
   */
  public TerminationStrategy getTermination() {
    return termination;
  }
  
  public int getMaxDepth() {
    return this.maxDepth;
  }  

  public int getMaxSearchDepth(){
    return this.maxSearchDepth;
  }

  public void updateMaxDepth(int maxDepth) {
    this.maxDepth = maxDepth;
  }

  public boolean isSaveSearchResult(){
    return saveResult;
  }

  public boolean isSaveTransitionSystem(){
    return saveTransitionSystem;
  }

  public boolean isSaveModel(){
    return saveModel;
  }

  public Collection<String> getPOR() {
    return Arrays.asList(this.config.getString(
            "psyco.por.config").trim().split(";"));
  }

  public String getResultFolderName() {
    return resultFolderName;
  }

  /**
   * @return the useInterpolation
   */

  public boolean isUseInterpolation() {
    return useInterpolation;
  }

  /**
   * @return the constraintSolver
   */
  public ConstraintSolver getConstraintSolver() {
    return constraintSolver;
  }

  /**
   * @return the interpolationSolver
   */
  public InterpolationSolver getInterpolationSolver() {
    return interpolationSolver;
  }

  /**
   * @return the useCPAchecker
   */
  public boolean isUseCPAchecker() {
    return useCPAchecker;
  }

  /**
   * @return the cpaCheckerParams
   */
  public String[] getCpaCheckerParams() {
    return cpaCheckerParams;
  }
  
  public String getCpaCommand() {
    return cpaCommand;
  }
}
