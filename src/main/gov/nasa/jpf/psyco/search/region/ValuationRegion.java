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
import gov.nasa.jpf.constraints.expressions.Negation;
import gov.nasa.jpf.constraints.expressions.NumericBooleanExpression;
import gov.nasa.jpf.constraints.expressions.NumericComparator;
import gov.nasa.jpf.constraints.types.BuiltinTypes;
import gov.nasa.jpf.constraints.types.Type;
import gov.nasa.jpf.constraints.util.ExpressionUtil;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author mmuesly
 */
public class ValuationRegion extends Region<ValuationEntry>{
  public ValuationRegion(){
    super();
  }

  public ValuationRegion(Valuation aValuation){
    super(aValuation);
  }

  /**
   * It is adapted from Expression Util. More or less equal the same.
   * @return 
   */
  @Override
  public Expression toExpression() {
    Expression result = null;
    for(ValuationEntry entry: region){
      Variable var = entry.getVariable();
      Type type = var.getType();
      Object value = entry.getValue();
      Expression<Boolean> newExpr;
      if(BuiltinTypes.BOOL.equals(type)) {
        Expression<Boolean> bvar = var.as(BuiltinTypes.BOOL);
        if((Boolean)value) {
          newExpr = bvar;
        }
      else {
        newExpr = new Negation(bvar);
      }
    }
    else {
      Constant cnst = Constant.create(type, value);
      newExpr = new NumericBooleanExpression(var, NumericComparator.EQ, cnst);
    }
    result = (result == null) ? newExpr : ExpressionUtil.and(newExpr, result);
    } 
    return result;
  }

  @Override
  protected void setInitValuation(Valuation initValue) {
    for(ValuationEntry entry: initValue){
      add(entry);
    }
  }

}
