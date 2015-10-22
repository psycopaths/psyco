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
