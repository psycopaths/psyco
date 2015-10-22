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
import gov.nasa.jpf.jdart.config.ConcolicConfig;
import gov.nasa.jpf.jdart.termination.NeverTerminate;
import gov.nasa.jpf.jdart.termination.TerminationStrategy;
import java.util.Arrays;
import java.util.Collection;

public class PsycoConfig {

  private final Config config;
  private int maxDepth = -1;
  private boolean usePOR = false;
  private boolean useMemorization = false;
  private boolean useSuffixFilter = false;
  private boolean useSummaries = false;
  private TerminationStrategy termination = new NeverTerminate();

  public PsycoConfig(Config conf) {
    this.config = conf;
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

  /**
   * @return the termination
   */
  public TerminationStrategy getTermination() {
    return termination;
  }
  
  public int getMaxDepth() {
    return this.maxDepth;
  }  

  public Collection<String> getPOR() {
    return Arrays.asList(this.config.getString(
            "psyco.por.config").trim().split(";"));
  }
}
