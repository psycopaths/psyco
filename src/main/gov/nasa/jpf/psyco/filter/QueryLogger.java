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
import gov.nasa.jpf.psyco.learnlib.SymbolicQueryOutput;
import gov.nasa.jpf.psyco.learnlib.ThreeValuedOracle;
import java.util.ArrayList;
import java.util.Collection;
import net.automatalib.words.Word;

/**
 *
 * @author falkhowar
 */
public class QueryLogger implements ThreeValuedFilter, ThreeValuedOracle {

  private static class LoggingQuery extends Query<SymbolicMethodSymbol, SymbolicQueryOutput> {

    private final Query<SymbolicMethodSymbol, SymbolicQueryOutput> query;

    public LoggingQuery(Query<SymbolicMethodSymbol, SymbolicQueryOutput> query) {
      this.query = query;
    }
    
    @Override
    public Word<SymbolicMethodSymbol> getPrefix() {
      return query.getPrefix();
    }

    @Override
    public Word<SymbolicMethodSymbol> getSuffix() {
      return query.getSuffix();
    }

    @Override
    public void answer(SymbolicQueryOutput o) {
      System.out.println("MQ: " + query.getInput() + " : " + o);
      query.answer(o);
    }
    
  }
  
  private MembershipOracle<SymbolicMethodSymbol, SymbolicQueryOutput> oracle;

  public QueryLogger(MembershipOracle<SymbolicMethodSymbol, SymbolicQueryOutput> oracle) {
    this.oracle = oracle;
  }
  
  @Override
  public void setNext(MembershipOracle<SymbolicMethodSymbol, SymbolicQueryOutput> mo) {
    this.oracle = mo;
  }

  @Override
  public void processQueries(Collection<? extends Query<SymbolicMethodSymbol, SymbolicQueryOutput>> clctn) {
    Collection<Query<SymbolicMethodSymbol, SymbolicQueryOutput>> lCol = new ArrayList<>();
    for (Query<SymbolicMethodSymbol, SymbolicQueryOutput> query : clctn) {
      lCol.add(new LoggingQuery(query));
    }
    this.oracle.processQueries(lCol);
  }
  
}
