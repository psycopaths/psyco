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
import java.util.Arrays;
import java.util.Collection;

public class PsycoConfig {

  public static enum EqTestType {bfs, interpolation, blast}
  private final Config config;
  private int maxDepth = -1;
  private boolean usePOR = false;
  private boolean useMemorization = false;
  private boolean useSuffixFilter = false;
  private boolean useSummaries = false;

  private EqTestType eqTestType = EqTestType.bfs;
  private TerminationStrategy termination = new NeverTerminate();
  private boolean symbolicSearch = true;
  private boolean enumerativeSearch = false;
  private String resultFolderName="default";
  private int maxSearchDepth = Integer.MIN_VALUE;
  
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
      resultFolderName = config.getString("psyco.resultFolderName");
    }
    
    if(config.hasValue("psyco.maxSearchDepth")){
      maxSearchDepth = config.getInt("psyco.maxSearchDepth");
    }
    if (config.hasValue("psyco.eqtest")) {
      eqTestType = config.getEnum("psyco.eqtest", 
              EqTestType.values(), eqTestType.bfs);
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

  public Collection<String> getPOR() {
    return Arrays.asList(this.config.getString(
            "psyco.por.config").trim().split(";"));
  }

  public String getResultFolderName() {
    return resultFolderName;
  }

  /**
   * @return type of eq test to perform
   */
  public EqTestType getEqTestType() {
    return eqTestType;
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
}
