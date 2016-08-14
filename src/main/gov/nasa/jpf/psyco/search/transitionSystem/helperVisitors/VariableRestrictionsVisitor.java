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
import gov.nasa.jpf.constraints.expressions.AbstractExpressionVisitor;
import gov.nasa.jpf.constraints.expressions.NumericBooleanExpression;
import java.util.List;

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