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
import gov.nasa.jpf.psyco.alphabet.SymbolicMethodSymbol;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author falkhowar
 */
public abstract class MethodExecutionFilter implements 
        MembershipOracle.DFAMembershipOracle<SymbolicMethodSymbol> {

  private final MethodExecutionFilter next;

  public MethodExecutionFilter() {
    this.next = null;
  }

  public MethodExecutionFilter(MethodExecutionFilter next) {
    this.next = next;
  }
  
  @Override
  public final void processQueries(
          Collection<? extends Query<SymbolicMethodSymbol, Boolean>> clctn) {
    
    ArrayList<Query<SymbolicMethodSymbol, Boolean>> queries = new ArrayList<>();
    for (Query<SymbolicMethodSymbol, Boolean> q : clctn) {
      boolean test = evaluateQuery(q);
      if (!test) {
        q.answer(false);
        continue;
      }
      
      if (next == null) {
        q.answer(true);
        continue;
      }
      
      queries.add(q);
    }
    
    if (!queries.isEmpty()) {
      next.processQueries(queries);
    }
  }
  
  public abstract boolean evaluateQuery(Query<SymbolicMethodSymbol, Boolean> query); 
  
}
