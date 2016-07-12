/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.transitionSystem;

import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.expressions.AbstractExpressionVisitor;
import gov.nasa.jpf.constraints.expressions.Negation;
import gov.nasa.jpf.constraints.expressions.NumericBooleanExpression;
import gov.nasa.jpf.constraints.expressions.NumericComparator;
import gov.nasa.jpf.constraints.expressions.NumericCompound;
import gov.nasa.jpf.constraints.expressions.NumericOperator;
import gov.nasa.jpf.constraints.expressions.PropositionalCompound;
import gov.nasa.jpf.psyco.search.collections.VariableReplacementMap;
import java.util.List;

/**
 *
 * @author mmuesly
 */
public class VariableReplacementVisitor extends AbstractExpressionVisitor<Expression<?>, VariableReplacementMap> {

  @Override
  public Expression<?> visit(PropositionalCompound expr, VariableReplacementMap data){
    System.out.println("gov.nasa.jpf.psyco.search.transitionSystem.VariableReplacementVisitor.visit()");
    System.out.println("propsitionalCompound: " + expr);
    Expression left = expr.getLeft(), right = expr.getRight();
    left = check(left, data);
    right = check(right, data);
    return new PropositionalCompound(left, expr.getOperator(), right);
  }

  @Override
  public <E> Expression<?> visit(NumericCompound<E> expr, VariableReplacementMap data){
    System.out.println("gov.nasa.jpf.psyco.search.transitionSystem.VariableReplacementVisitor.visit()");
    System.out.println("numaericCompound: " + expr);
    Expression left = expr.getLeft(), right = expr.getRight();
    left = check(left, data);
    right = check(right, data);
    expr = new NumericCompound(left, expr.getOperator(), right);
    return expr;
  }

  @Override
  public <E> Expression<?> visit(NumericBooleanExpression expr, VariableReplacementMap data){
    Expression left = expr.getLeft(), right = expr.getRight();
    left = check(left, data);
    right = check(right, data);
    expr = new NumericBooleanExpression(left, expr.getComparator(), right);
    return expr;
  }

  @Override
  public Expression visit(Negation expr, VariableReplacementMap data){
    Expression innerValue = expr.getNegated();
    innerValue = check(innerValue, data);
    return new Negation(innerValue);
  }
  @Override
  protected <E> Expression<?> defaultVisit(Expression<E> expression, VariableReplacementMap data) {
    System.out.println("defaultVisit: " + expression + " class: " + expression.getClass());
    for(Expression expr:expression.getChildren()){
      expr.accept(this, data);
    }
    return expression;
  }

  private Expression check(Expression expr, VariableReplacementMap data) {
    System.out.println("gov.nasa.jpf.psyco.search.transitionSystem.VariableReplacementVisitor.check()");
    System.out.println("check: " + expr);
    if(expr instanceof Variable){
      return data.getOrDefault(expr, expr);
    }else if(expr instanceof NumericCompound){
      return this.visit((NumericCompound) expr, data);
    }else if(expr instanceof PropositionalCompound){
      return visit((PropositionalCompound) expr, data);
    }else if(expr instanceof NumericBooleanExpression){
      return visit((NumericBooleanExpression) expr, data);
    }else if(expr instanceof Negation){
      return visit((Negation) expr, data);
    }
    return expr;
  }
}
