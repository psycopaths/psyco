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
package gov.nasa.jpf.psyco.path.learnlib;

import de.learnlib.api.MembershipOracle;
import de.learnlib.api.Query;
import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.constraints.api.ConstraintSolver.Result;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.jdart.constraints.Path;
import gov.nasa.jpf.jdart.constraints.PathState;
import gov.nasa.jpf.psyco.alphabet.SymbolicMethodSymbol;
import gov.nasa.jpf.psyco.filter.ValidQueryFilter;
import gov.nasa.jpf.psyco.util.PathUtil;
import java.util.Collection;
import net.automatalib.words.Word;

/**
 *
 * @author falk
 */
public class PathQueryOracle implements MembershipOracle<PathSymbol, PathQueryOutput> {

  private final Valuation initialValuation;
  
  private final ConstraintSolver cSolver;

  public PathQueryOracle(Valuation initialValuation, ConstraintSolver cSolver) {
    this.initialValuation = initialValuation;
    this.cSolver = cSolver;
  }  
  
  @Override
  public void processQueries(Collection<? extends Query<PathSymbol, PathQueryOutput>> clctn) {
    for (Query<PathSymbol, PathQueryOutput> q : clctn) {
      q.answer(processQuery(q.getInput()));
    }
  }
  
  private PathQueryOutput processQuery(Word<PathSymbol> query) {
    System.out.println("MQ: " + query);
    if (query.length() < 1) {
      return PathQueryOutput.SAT;
    }
    
    Word<SymbolicMethodSymbol> smsWord = toSMSWord(query);
    Word<Path> pathWord = toPathWord(query);
    if (!ValidQueryFilter.isValid(smsWord) || !isValid(pathWord)) {
      return PathQueryOutput.UNSAT;
    }
    
    Path test = PathUtil.executeSymbolically(
            smsWord, toPathWord(query), initialValuation);
    
    Result res = cSolver.isSatisfiable(test.getPathCondition());
    return PathQueryOutput.fromResult(res);
  }
  
  private static Word<Path> toPathWord(Word<PathSymbol> word) {
    Word<Path> ret = Word.epsilon();
    for (PathSymbol ps : word) {
      ret = ret.append(ps.getPath());
    }
    return ret;
  }
  
  private static Word<SymbolicMethodSymbol> toSMSWord(Word<PathSymbol> word) {
    Word<SymbolicMethodSymbol> ret = Word.epsilon();
    for (PathSymbol ps : word) {
      ret = ret.append(ps.getMethod());
    }
    return ret;
  }
  
  public static boolean isValidWord(Word<PathSymbol> word) {
    Word<SymbolicMethodSymbol> smsWord = toSMSWord(word);
    Word<Path> pathWord = toPathWord(word);
    return ValidQueryFilter.isValid(smsWord) && isValid(pathWord);
  }
  
  private static boolean isValid(Word<Path> pathWord) {
    if (pathWord.length() < 2) {
      return true;
    }
    for (int i=0; i<pathWord.length()-1; i++) {
      if (pathWord.getSymbol(i).getState() != PathState.OK) {
        return false;
      }
    }
    return true;
  }
  
}
