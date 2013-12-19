/*******************************************************************************
 * Copyright (C) 2008 United States Government as represented by the
 * Administrator of the National Aeronautics and Space Administration
 * (NASA).  All Rights Reserved.
 * 
 * This software is distributed under the NASA Open Source Agreement
 * (NOSA), version 1.3.  The NOSA has been approved by the Open Source
 * Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
 * directory tree for the complete NOSA document.
 * 
 * THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
 * KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
 * LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
 * SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
 * A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
 * THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
 * DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
 ******************************************************************************/
package gov.nasa.jpf.psyco;

import gov.nasa.jpf.JPF;
import gov.nasa.jpf.psyco.alphabet.SummaryAlphabet;
import gov.nasa.jpf.psyco.alphabet.SymbolicMethodAlphabet;
import gov.nasa.jpf.psyco.filter.Cache;
import gov.nasa.jpf.psyco.filter.InterpolationCache;
import gov.nasa.jpf.psyco.filter.MethodExecutionFilter;
import gov.nasa.jpf.psyco.filter.PORFilter;
import gov.nasa.jpf.psyco.filter.QueryLogger;
import gov.nasa.jpf.psyco.filter.UniformErrorFilter;
import gov.nasa.jpf.psyco.filter.UniformOKSuffixFilter;
import gov.nasa.jpf.psyco.filter.ValidQueryFilter;
import gov.nasa.jpf.psyco.learnlib.QueryCounter;
import gov.nasa.jpf.psyco.learnlib.SymbolicExecutionOracle;
import gov.nasa.jpf.psyco.learnlib.ThreeValuedOracle;
import gov.nasa.jpf.psyco.oracles.RefinementCheckOracle;
import gov.nasa.jpf.psyco.oracles.SymbolicExecutionOracleWrapper;
import gov.nasa.jpf.psyco.oracles.TerminationCheckOracle;
import gov.nasa.jpf.util.JPFLogger;
import java.util.ArrayList;
import java.util.List;

/**
 * an oracle provider has the membership oracles necessary for psyco.
 * 
 * @author falkhowar
 */
public class DefaultOracleProvider {
  
  protected static final JPFLogger logger = JPF.getLogger("psyco");  
    
  private final List<QueryCounter> logs = new ArrayList<>();
          
  private final SymbolicExecutionOracle back;
  
  private ThreeValuedOracle oracle;
  
  private MethodExecutionFilter filter = null;
  
  private final SymbolicMethodAlphabet inputs;

  public DefaultOracleProvider(SymbolicExecutionOracle back, SymbolicMethodAlphabet inputs, PsycoConfig pconf) {
    this.back = back;
    this.inputs = inputs;
    initialize(pconf);
  }
   
  protected final void initialize(PsycoConfig pconf) {

    ThreeValuedOracle sink = new SymbolicExecutionOracleWrapper(back);
    
    oracle = new QueryLogger(
             new RefinementCheckOracle(
             new TerminationCheckOracle(pconf.getTermination(), sink)));
    
    QueryCounter count;
    
    if (pconf.isUseMemorization()) {
      count = new QueryCounter(oracle, "Queries after cache");
      oracle = count;
      this.logs.add(count);      
      oracle = new Cache(oracle);
    }
    
    if (pconf.isUseSuffixFilter() && inputs instanceof SummaryAlphabet) {
      count = new QueryCounter(oracle, "Queries after suffix filter");
      oracle = count;
      this.logs.add(count);      
      oracle = new UniformOKSuffixFilter( (SummaryAlphabet)inputs,
               new UniformErrorFilter( (SummaryAlphabet)inputs, oracle));
    }
        
    count = new QueryCounter(oracle, "Valid L*/EQ Queries");
    oracle = count;
    this.logs.add(count);
    
    oracle = new ValidQueryFilter(oracle);
    
    // filter
    
    if (pconf.isUsePOR()) {
      this.filter = new PORFilter(pconf.getPOR(), inputs);
    }
  }
  
  /**
   * this is the three valued oracle used by lstar 
   * (and some of the equivalence tests)
   * @return 
   */
  public ThreeValuedOracle getThreeValuedOracle() {
    return oracle;
  }
  
  /**
   * 
   * @return 
   */
  public SymbolicExecutionOracle getSymbolicExecutionOracle() {
    return back;
  }
  
  public MethodExecutionFilter getExecutionFilter() {
    return filter;
  }
  
  public SymbolicMethodAlphabet getInputs() {
    return inputs;
  }

  public void logStatistics() {
    for (QueryCounter counter : this.logs) {
      logger.info(counter.getStatisticalData().getSummary());
    }
  }
  
}
