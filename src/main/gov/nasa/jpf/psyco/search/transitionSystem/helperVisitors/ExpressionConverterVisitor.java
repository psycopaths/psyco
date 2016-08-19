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

import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.expressions.AbstractExpressionVisitor;
import gov.nasa.jpf.constraints.expressions.BitvectorExpression;
import gov.nasa.jpf.constraints.expressions.BitvectorNegation;
import gov.nasa.jpf.constraints.expressions.BitvectorOperator;
import gov.nasa.jpf.constraints.expressions.CastExpression;
import gov.nasa.jpf.constraints.expressions.Constant;
import gov.nasa.jpf.constraints.expressions.IfThenElse;
import gov.nasa.jpf.constraints.expressions.LogicalOperator;
import gov.nasa.jpf.constraints.expressions.Negation;
import gov.nasa.jpf.constraints.expressions.NumericBooleanExpression;
import gov.nasa.jpf.constraints.expressions.NumericComparator;
import gov.nasa.jpf.constraints.expressions.NumericCompound;
import gov.nasa.jpf.constraints.expressions.NumericOperator;
import gov.nasa.jpf.constraints.expressions.PropositionalCompound;
import gov.nasa.jpf.constraints.expressions.QuantifierExpression;
import gov.nasa.jpf.constraints.expressions.UnaryMinus;
import gov.nasa.jpf.constraints.expressions.functions.FunctionExpression;
import java.util.HashMap;
/**
 * During encoding of the transition system, this visitor is used to 
 * convert a Expression to a storable String.
 * It can be restored using the TransitionSystemLoader.
 */
public class ExpressionConverterVisitor
        extends AbstractExpressionVisitor<String, HashMap<Class, String>> {

  @Override
  public <E> String visit(Variable<E> v, HashMap<Class, String> data) {
    String name = v.getName();
    String type = data.get(v.getType().getClass());
    return TransitionEncoding.variable + ":" + name + ":" + type + ";";
  }

  @Override
  public <E> String visit(Constant<E> c, HashMap<Class, String> data) {
    String value = c.getValue().toString();
    String type = data.get(c.getType().getClass());
    return TransitionEncoding.constant + ":" + value + ":" + type + ";";
  }

  @Override
  public String visit(Negation n, HashMap<Class, String> data) {
    return TransitionEncoding.negation + ":" + n.getNegated() + ";";
  }

  @Override
  public <E> String visit(NumericBooleanExpression n,
          HashMap<Class, String> data) {
    String left = visit(n.getLeft(), data);
    String right = visit(n.getRight(), data);
    String operator = convert(n.getComparator());
    return TransitionEncoding.numericBooleanExpression + ":"
            + left + ":" + operator + ":" + right + ";";
  }

  @Override
  public <F, E> String visit(CastExpression<F, E> cast,
          HashMap<Class, String> data) {
    throw new IllegalStateException("Cast expression are not allowed so"
            + "far in the transitionSystem.");
  }

  @Override
  public <E>
          String visit(NumericCompound<E> n, HashMap<Class, String> data) {
    String left = visit(n.getLeft(), data);
    String right = visit(n.getRight(), data);
    String operator = convert(n.getOperator());
    return TransitionEncoding.numericCompund + ":"
            + left + ":" + operator + ":" + right + ";";
  }

  @Override
  public <E> String visit(IfThenElse<E> n, HashMap<Class, String> data) {
    throw new IllegalStateException("If-Then-Else is not supposed to be part"
            + " of the transiton system."
            + "Instead there should be one transition per path.");
  }

  @Override
  public String visit(PropositionalCompound n, HashMap<Class, String> data) {
    String left = visit(n.getLeft(), data);
    String right = visit(n.getRight(), data);
    String operator = convert(n.getOperator());
    return TransitionEncoding.propositionalCompound + ":" + left + ":"
            + operator + ":" + right + ";";
  }

  @Override
  public <E> String visit(UnaryMinus<E> n, HashMap<Class, String> data) {
    String negated = visit(n.getNegated());
    return TransitionEncoding.unaryMinus + ":" + negated + ";";
  }

  @Override
  public String visit(QuantifierExpression q, HashMap<Class, String> data) {
    throw new IllegalStateException(
            "Quantifier must not be part of the TransitionSystem.");
  }

  @Override
  public <E> String visit(FunctionExpression<E> f,
          HashMap<Class, String> data) {
    throw new IllegalStateException(
            "FunctionExpression are not expected in the TransitionSystem."
            + "Don't know how to handle them.");
  }

  @Override
  public <E> String visit(
          BitvectorExpression<E> bv, HashMap<Class, String> data) {
    String left = visit(bv.getLeft(), data);
    String right = visit(bv.getRight(), data);
    String operator = convert(bv.getOperator());
    return TransitionEncoding.bitVector + ":" + left + ":" + operator
            + ":" + right + ";";

  }

  @Override
  public <E> String visit(
          BitvectorNegation<E> n, HashMap<Class, String> data) {
    String negated = visit(n.getNegated());
    return TransitionEncoding.bitVectorNegation + ":" + negated + ";";
  }

  private String convert(NumericComparator comparator) {
    return TransitionEncoding.numericComperator + ":"
            + comparator.toString() + ";";
  }

  private String convert(NumericOperator operator) {
    return TransitionEncoding.numericOperator + ":"
            + operator.toString() + ";";
  }

  private String convert(LogicalOperator operator) {
    return TransitionEncoding.logicalOpertaor + ":"
            + operator.toString() + ";";
  }

  private String convert(BitvectorOperator operator) {
    return TransitionEncoding.bitVectorOperator + ":"
            + operator.toString() + ";";
  }
}