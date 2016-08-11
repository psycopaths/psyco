/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.transitionSystem.helperVisitors;

import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.expressions.AbstractExpressionVisitor;
import gov.nasa.jpf.constraints.expressions.NumericBooleanExpression;
import java.util.List;

/**
 *
 * @author mmuesly
 */
public class VariableRestrictionsVisitor extends AbstractExpressionVisitor<Expression<?>,List<NumericBooleanExpression>> {
  
  @Override
  public <E> Expression<?> visit(NumericBooleanExpression expr, List<NumericBooleanExpression> data){
    data.add(expr);
    return expr;
  }
  
  @Override
  protected <E> Expression<?> defaultVisit(Expression<E> expression, List<NumericBooleanExpression> data) {
    for(Expression expr:expression.getChildren()){
      expr.accept(this, data);
    }
    //System.err.println("Visit: " + expression);
    return expression;
  }
}
