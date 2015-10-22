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
package gov.nasa.jpf.psyco.alphabet;

import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.constraints.api.ConstraintSolver.Result;
import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.expressions.Negation;
import gov.nasa.jpf.constraints.util.ExpressionUtil;
import gov.nasa.jpf.jdart.config.ConcolicMethodConfig;
import gov.nasa.jpf.jdart.config.ParamConfig;
import gov.nasa.jpf.jdart.constraints.Path;
import gov.nasa.jpf.jdart.summaries.MethodSummary;
import gov.nasa.jpf.jdart.summaries.SummaryStore;
import gov.nasa.jpf.psyco.learnlib.SymbolicExecutionResult;
import gov.nasa.jpf.psyco.util.SEResultUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SummaryAlphabet extends SymbolicMethodAlphabet {
  
  private Map<SymbolicMethodSymbol, SymbolicExecutionResult> summaries =
          new HashMap<>();
  
  private final ConstraintSolver solver;
  
  private final Valuation initialValuation;
  
  public SummaryAlphabet(SummaryStore store, ConstraintSolver solver) {
    this.solver = solver;
    this.initialValuation = store.getInitialValuation();
    for (ConcolicMethodConfig c : store.getConcolicMethodConfigs()) {
      SymbolicMethodSymbol s = 
              this.addSymbol(c.getId(), c, ExpressionUtil.TRUE);
      
      SymbolicExecutionResult r = 
      summaries.put(s, importSummary(c, store.getSummary(c)));
    }    
  }
  

  @Override
  public boolean refine(SymbolicMethodSymbol sms, Expression<Boolean> refiner) {
    
    SymbolicExecutionResult result = this.summaries.remove(sms);
    this.remove(sms);
    
    Expression<Boolean> guard1 = SEResultUtil.stripLeadingTrue(
            ExpressionUtil.and(sms.getPrecondition(), refiner));
    
    Expression<Boolean> guard2 = SEResultUtil.stripLeadingTrue(
            ExpressionUtil.and(sms.getPrecondition(), new Negation(refiner)));
    
    SymbolicMethodSymbol sms1 = this.addSymbol(
            sms.getId() + "_1", sms.getConcolicMethodConfig(), guard1);
                
    SymbolicMethodSymbol sms2 = this.addSymbol(
            sms.getId() + "_2", sms.getConcolicMethodConfig(), guard2);
    
    this.summaries.put(sms1, refineResult(result, guard1));
    this.summaries.put(sms2, refineResult(result, guard2));

    return true;
  }
  
  public SymbolicExecutionResult getSummary(SymbolicMethodSymbol sms) {
    return this.summaries.get(sms);
  }
  
  private SymbolicExecutionResult importSummary(
          ConcolicMethodConfig c, MethodSummary summary) {
    
    Map<String, String> renaming = new HashMap<>();
    int pos = 1;
    for (ParamConfig p : c.getParams()) {
      renaming.put(p.getName(), "P" + pos);
      pos++;
    }
    
    SymbolicExecutionResult result = new SymbolicExecutionResult(
            summary.getOkPaths(), summary.getErrorPaths(), summary.getDontKnowPaths());
    
    return SEResultUtil.rename(result, SEResultUtil.func(renaming));
  } 

  private SymbolicExecutionResult refineResult(
          SymbolicExecutionResult result, Expression<Boolean> precondition) {

    return new SymbolicExecutionResult( 
            refinePaths(result.getOk(), precondition),
            refinePaths(result.getError(), precondition),
            refinePaths(result.getDontKnow(), precondition));   
  }
  
  private Collection<Path> refinePaths(
          Collection<Path> ps, Expression<Boolean> precondition) {
    ArrayList<Path> ret = new ArrayList<>();
    for (Path p : ps) {
      Path pRet = refinePath(p, precondition);
      if (pRet != null) {
        ret.add(pRet);
      }
    }
    return ret;
  }
  
  private Path refinePath(Path p, Expression<Boolean> precondition) {
    Expression<Boolean> pc = ExpressionUtil.and(
            p.getPathCondition(), precondition);
    
    if (!(solver.isSatisfiable(pc) == Result.SAT)) {
      return null;
    }
    
    Expression<Boolean> implied = ExpressionUtil.and(
            p.getPathCondition(), new Negation(precondition));
            
    if (solver.isSatisfiable(implied) == Result.SAT) {
      return new Path(pc, p.getPathResult());
    }

    return p;
  } 
  
  public Valuation getInitialValuation() {
    return initialValuation;
  }
          
}
