/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.transitionSystem;

import gov.nasa.jpf.constraints.expressions.NumericBooleanExpression;
import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.expressions.AbstractExpressionVisitor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author mmuesly
 */
class VariableAssignmentVisitor extends 
        AbstractExpressionVisitor<Expression<?>,
            HashMap<Variable, List<Expression>>>{
  
  @Override
  public <E> Expression visit(NumericBooleanExpression expr,
            HashMap<Variable, List<Expression>> data){
    Expression left = expr.getLeft(), right = expr.getRight();
    for(Variable var: data.keySet()){
      if(left.equals(var)){
        List<Expression> list = 
                data.getOrDefault(var, new ArrayList<Expression>());
        list.add(right);
      }
    }
    return expr;
  }
  
  @Override
  protected <E> Expression<?> defaultVisit(Expression<E> expression, HashMap<Variable, List<Expression>>  data) {
    for(Expression expr:expression.getChildren()){
      expr.accept(this, data);
    }
    //System.err.println("Visit: " + expression);
    return expression;
  }
}
