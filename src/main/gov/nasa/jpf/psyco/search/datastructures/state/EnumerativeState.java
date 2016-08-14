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

public class EnumerativeState extends State<ValuationEntry>{

  public EnumerativeState(Valuation initValuation) {
    for(ValuationEntry entry: initValuation.entries()){
      this.add(entry);
    }
  }

  public EnumerativeState() {
    super();
  }

  @Override
  public Expression<Boolean> toExpression() {
    Valuation toConvert = new Valuation();
    for(ValuationEntry entry: this){
      toConvert.addEntry(entry);
    }
    return ExpressionUtil.valuationToExpression(toConvert);
  }

  @Override
  public State<ValuationEntry> createEmptyState() {
    return new EnumerativeState();
  }
  
}
