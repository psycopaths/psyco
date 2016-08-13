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
package gov.nasa.jpf.psyco.search.transitionSystem.helperVisitors;

import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.expressions.AbstractExpressionVisitor;
import gov.nasa.jpf.constraints.expressions.Negation;
import gov.nasa.jpf.constraints.expressions.NumericBooleanExpression;
import gov.nasa.jpf.constraints.expressions.NumericCompound;
import gov.nasa.jpf.constraints.expressions.PropositionalCompound;
import gov.nasa.jpf.psyco.search.datastructures.VariableReplacementMap;

public class VariableReplacementVisitor 
      extends AbstractExpressionVisitor<Expression<?>, VariableReplacementMap>{

  @Override
  public Expression<?> visit(PropositionalCompound expr,
          VariableReplacementMap data){
    Expression left = expr.getLeft(), right = expr.getRight();
    left = check(left, data);
    right = check(right, data);
    return new PropositionalCompound(left, expr.getOperator(), right);
  }

  @Override
  public <E> Expression<?> visit(NumericCompound<E> expr,
          VariableReplacementMap data){
    Expression left = expr.getLeft(), right = expr.getRight();
    left = check(left, data);
    right = check(right, data);
    expr = new NumericCompound(left, expr.getOperator(), right);
    return expr;
  }

  @Override
  public <E> Expression<?> visit(NumericBooleanExpression expr,
          VariableReplacementMap data){
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
  protected <E> Expression<?> defaultVisit(Expression<E> expression,
          VariableReplacementMap data) {
    for(Expression expr:expression.getChildren()){
      expr.accept(this, data);
    }
    return expression;
  }

  private Expression check(Expression expr,
          VariableReplacementMap data) {
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
