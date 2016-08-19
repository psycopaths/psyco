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

import gov.nasa.jpf.constraints.api.ConstraintSolver.Result;
import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.ValuationEntry;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.expressions.Constant;
import gov.nasa.jpf.constraints.expressions.NumericBooleanExpression;
import gov.nasa.jpf.constraints.expressions.NumericComparator;
import gov.nasa.jpf.psyco.search.SolverInstance;

public class SymbolicEntry extends ValuationEntry<Expression<Boolean>> {

  public SymbolicEntry(Variable<Expression<Boolean>> variable,
          Expression<Boolean> value) {
    super(variable, value);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof SymbolicEntry) {
      SymbolicEntry testEntry = (SymbolicEntry) obj;
      if (testEntry.getVariable().equals(this.getVariable())) {
        Expression test = new NumericBooleanExpression(
                testEntry.getValue(), NumericComparator.EQ, this.getValue());
        Result res = SolverInstance.getInstance().isSatisfiable(test);
        if (res == Result.SAT) {
          return true;
        } else if (res == Result.DONT_KNOW) {
          throw new IllegalStateException("Cannot decide Equality");
        }
      }
    }
    return false;
  }

  public static SymbolicEntry create(ValuationEntry entry) {
    Variable entryVariable = entry.getVariable();
    Expression entryValue
            = new Constant(entryVariable.getType(), entry.getValue());
    Expression<Boolean> value
            = new NumericBooleanExpression(entryVariable,
                    NumericComparator.EQ, entryValue);
    return new SymbolicEntry(entryVariable, value);
  }
}