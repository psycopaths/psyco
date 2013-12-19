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
