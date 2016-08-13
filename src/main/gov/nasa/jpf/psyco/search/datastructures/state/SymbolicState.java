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
package gov.nasa.jpf.psyco.search.datastructures.state;

import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.api.ValuationEntry;
import gov.nasa.jpf.constraints.util.ExpressionUtil;

public class SymbolicState extends State<SymbolicEntry>{

  public SymbolicState(){
    super();
  }

  public SymbolicState(Valuation initValuation){
    this();
    for(ValuationEntry entry: initValuation.entries()){
      SymbolicEntry newStateEntry = SymbolicEntry.create(entry);
      add(newStateEntry);
    }
  }

  @Override
  public Expression<Boolean> toExpression(){
    Expression<Boolean> expr = null;
    for(SymbolicEntry entry: this){
      expr = expr != null ? ExpressionUtil.and(expr, entry.getValue()):
              entry.getValue();
    }
    return expr;
  }

  @Override
  public State<SymbolicEntry> createEmptyState() {
    return new SymbolicState();
  }
}
