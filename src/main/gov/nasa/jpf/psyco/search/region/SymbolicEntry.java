/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.region;

import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.api.ValuationEntry;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.expressions.Constant;
import gov.nasa.jpf.constraints.expressions.NumericBooleanExpression;
import gov.nasa.jpf.constraints.expressions.NumericComparator;
import gov.nasa.jpf.constraints.expressions.NumericCompound;
import gov.nasa.jpf.constraints.types.BuiltinTypes;
import gov.nasa.jpf.constraints.util.ExpressionUtil;

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
    throw new UnsupportedOperationException("Equals on expression"+
            " Entrys is not defined yet");
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
