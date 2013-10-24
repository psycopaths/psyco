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
import gov.nasa.jpf.psyco.learnlib.SymbolicExecutionResult;
import gov.nasa.jpf.psyco.util.SEResultUtil;
import gov.nasa.jpf.psyco.utils.summaries.MethodSummary;
import gov.nasa.jpf.psyco.utils.summaries.SummaryStore;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author falkhowar
 */
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
            pc, new Negation(p.getPathCondition()));
            
    if (!(solver.isSatisfiable(implied) == Result.SAT)) {
      return p;
    }

    return new Path(pc, p.getPathResult());
  } 
  
  public Valuation getInitialValuation() {
    return initialValuation;
  }
          
}
