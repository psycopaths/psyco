/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.region;

import gov.nasa.jpf.constraints.api.ConstraintSolver.Result;
import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.ValuationEntry;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.expressions.Constant;
import gov.nasa.jpf.constraints.expressions.NumericBooleanExpression;
import gov.nasa.jpf.constraints.expressions.NumericComparator;
import gov.nasa.jpf.psyco.search.SolverInstance;

/**
 *
 * @author mmuesly
 */
public class SymbolicEntry extends ValuationEntry<Expression<Boolean>>{

  public SymbolicEntry(Variable<Expression<Boolean>> variable, Expression<Boolean> value) {
    super(variable, value);
  }
  @Override
  public boolean equals(Object obj) {
    if(obj instanceof SymbolicEntry){
      SymbolicEntry testEntry = (SymbolicEntry) obj;
      if(testEntry.getVariable().equals(this.getVariable())){
        Expression test = new NumericBooleanExpression(
                testEntry.getValue() , NumericComparator.EQ, this.getValue());
        Result res = SolverInstance.getInstance().isSatisfiable(test);
        if (res == Result.SAT){
          return true;
        }else if(res == Result.DONT_KNOW){
            throw new IllegalStateException("Cannot decide Equality");
        }
      }
    }
    return false;
  }

  public static SymbolicEntry create(ValuationEntry entry){
    Variable entryVariable = entry.getVariable();
    Expression entryValue = 
            new Constant(entryVariable.getType(), entry.getValue());
    Expression<Boolean> value = 
            new NumericBooleanExpression(entryVariable,
                    NumericComparator.EQ, entryValue);
    return new SymbolicEntry(entryVariable, value);
  }
}
