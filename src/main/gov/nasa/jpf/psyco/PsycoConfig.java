/**
 * *****************************************************************************
 * Copyright (C) 2008 United States Government as represented by the
 * Administrator of the National Aeronautics and Space Administration (NASA).
 * All Rights Reserved.
 *
 * This software is distributed under the NASA Open Source Agreement (NOSA),
 * version 1.3. The NOSA has been approved by the Open Source Initiative. See
 * the file NOSA-1.3-JPF at the top of the distribution directory tree for the
 * complete NOSA document.
 *
 * THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY KIND,
 * EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT LIMITED TO, ANY
 * WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO SPECIFICATIONS, ANY
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
 * FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL BE
 * ERROR FREE, OR ANY WARRANTY THAT DOCUMENTATION, IF PROVIDED, WILL CONFORM TO
 * THE SUBJECT SOFTWARE.
 *****************************************************************************
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

/**
 *
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
    if (config.hasValue("psyco.interpolation")) {
      useInterpolation = config.getBoolean("psyco.interpolation");
    }  }

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
}
