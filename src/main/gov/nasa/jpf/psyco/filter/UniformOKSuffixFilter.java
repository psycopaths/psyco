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
import gov.nasa.jpf.psyco.alphabet.SummaryAlphabet;
import gov.nasa.jpf.psyco.alphabet.SymbolicMethodSymbol;
import gov.nasa.jpf.psyco.learnlib.SymbolicQueryOutput;
import gov.nasa.jpf.psyco.learnlib.ThreeValuedOracle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import net.automatalib.words.Word;

/**
 *
 * @author falkhowar
 */
public class UniformOKSuffixFilter implements ThreeValuedFilter, ThreeValuedOracle {

  private MembershipOracle<SymbolicMethodSymbol, SymbolicQueryOutput> oracle;
  
  private SummaryAlphabet inputs;

  public UniformOKSuffixFilter(SummaryAlphabet inputs, 
          MembershipOracle<SymbolicMethodSymbol, SymbolicQueryOutput> oracle) {
    this.oracle = oracle;
    this.inputs = inputs;
  }
  
  @Override
  public void setNext(MembershipOracle<SymbolicMethodSymbol, SymbolicQueryOutput> mo) {
    this.oracle = mo;
  }

  @Override
  public void processQueries(Collection<? extends Query<SymbolicMethodSymbol, SymbolicQueryOutput>> clctn) {
    for (Query<SymbolicMethodSymbol, SymbolicQueryOutput> query : clctn) {
      processQuery(query);
    }
  }
  
  private void processQuery(Query<SymbolicMethodSymbol, SymbolicQueryOutput> query) {
    Word<SymbolicQueryOutput> out = getPotentialOutput(query.getInput());
    int uniformIdx = getUniformIndex(out);
    if (uniformIdx == 0) {
      query.answer(SymbolicQueryOutput.OK);
      return;
    }
      
    if (uniformIdx == out.length()) {
      this.oracle.processQueries(Collections.singleton(query));
      return;
    }
    
    Word<SymbolicMethodSymbol> prefix = query.getInput().prefix(uniformIdx);
    DefaultQuery<SymbolicMethodSymbol, SymbolicQueryOutput> dQuery = 
            new DefaultQuery<>(prefix);
    
    this.oracle.processQueries(Collections.singleton(dQuery));
    query.answer(dQuery.getOutput());
  }
  
  private int getUniformIndex(Word<SymbolicQueryOutput> out) {
    for (int i=out.length(); i> 0; i--) {
      SymbolicQueryOutput o = out.getSymbol(i-1);
      if (!o.isUniform() || !o.equals(SymbolicQueryOutput.OK)) {
        return i;
      }
    }
    return 0;
  }
  
  private Word<SymbolicQueryOutput> getPotentialOutput(Word<SymbolicMethodSymbol> in) {
    ArrayList<SymbolicQueryOutput> list = new ArrayList<>();
    for (SymbolicMethodSymbol sms : in) {
      list.add(new SymbolicQueryOutput(inputs.getSummary(sms)));
    }
    return Word.fromList(list);
  }
  
}
