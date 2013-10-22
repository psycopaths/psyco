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
package gov.nasa.jpf.psyco.filter;

import de.learnlib.api.MembershipOracle;
import de.learnlib.api.Query;
import de.learnlib.oracles.DefaultQuery;
import gov.nasa.jpf.psyco.alphabet.SymbolicMethodSymbol;
import gov.nasa.jpf.psyco.learnlib.SymbolicQueryOutput;
import gov.nasa.jpf.psyco.learnlib.ThreeValuedOracle;
import java.util.Collection;
import java.util.Collections;
import net.automatalib.words.Word;


/**
 *
 * @author falkhowar
 */
public class Cache implements ThreeValuedFilter, ThreeValuedOracle {
  
  private static final MemoizeTable table = new MemoizeTable();
  
  private MembershipOracle<SymbolicMethodSymbol, SymbolicQueryOutput> oracle;

  public Cache(ThreeValuedOracle oracle) {
    this.oracle = oracle;
  }
  
  
  @Override
  public void processQueries(
          Collection<? extends Query<SymbolicMethodSymbol, SymbolicQueryOutput>> clctn) {

    for (Query<SymbolicMethodSymbol, SymbolicQueryOutput> q : clctn) {
      processQuery(q);
    }
  }

  private void processQuery(Query<SymbolicMethodSymbol, SymbolicQueryOutput> q) {
   String[] test = queryToString(q.getInput());
   SymbolicQueryOutput result = table.getSimulatedResult(test);
   if (result == null) {
     DefaultQuery<SymbolicMethodSymbol, SymbolicQueryOutput> defq =
            new DefaultQuery<>(q.getInput());
     
     this.oracle.processQueries(Collections.singleton(defq));
     
     result = defq.getOutput();
     table.setResult(test, result);
   } 
   q.answer(result);   
  }
  
  
  @Override
  public void setNext(MembershipOracle<SymbolicMethodSymbol, SymbolicQueryOutput> mo) {
    this.oracle = mo;
  }

  private String[] queryToString(Word<SymbolicMethodSymbol> query) {
    String[] ret = new String[query.length()];
    int i = 0;
    for (SymbolicMethodSymbol s : query) {
      ret[i++] = s.getId() + "_" + i;
    }
    return ret;
  }  
}
