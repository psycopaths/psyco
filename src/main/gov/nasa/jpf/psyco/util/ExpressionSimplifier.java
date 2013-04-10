//
// Copyright (C) 2008 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA).  All Rights Reserved.
//
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3.  The NOSA has been approved by the Open Source
// Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
// directory tree for the complete NOSA document.
//
// THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
// KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
// LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
// SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
// A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
// THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
// DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//
package gov.nasa.jpf.psyco.util;

import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.ExpressionVisitor;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.expressions.*;

/**
 * copies a formula
 * 
 */
public class ExpressionSimplifier extends ExpressionVisitor<Expression> {


  @Override
  protected Expression visit(Variable vrbl, Expression exprsn) {
    return vrbl.createCopy();
  }

  @Override
  protected Expression visit(Constant cnstnt, Expression exprsn) {
    return cnstnt.createCopy();
  }

  @Override
  protected Expression visit(Negation ngtn, Expression exprsn) {
    Expression newNegated = visit(ngtn.getNegated(), ngtn);
    if (newNegated instanceof Constant) {
      Constant<Boolean> cb = (Constant<Boolean>) newNegated;
      if (cb.getValue()) {
        return new Constant<Boolean>(Boolean.class, false);
      } else {
        return new Constant<Boolean>(Boolean.class, true);        
      }
    }
    
    if (newNegated instanceof NumericBooleanExpression) {
      NumericBooleanExpression nbe = (NumericBooleanExpression) newNegated;
      switch (nbe.getComparator()) {
        case NE:
          return new NumericBooleanExpression(nbe.getLeft(), NumericComparator.EQ, nbe.getRight());
        case EQ:
          return new NumericBooleanExpression(nbe.getLeft(), NumericComparator.NE, nbe.getRight());
        case LT:
          return new NumericBooleanExpression(nbe.getLeft(), NumericComparator.GE, nbe.getRight());
        case LE:
          return new NumericBooleanExpression(nbe.getLeft(), NumericComparator.GT, nbe.getRight());
        case GT:
          return new NumericBooleanExpression(nbe.getLeft(), NumericComparator.LE, nbe.getRight());
        case GE:
          return new NumericBooleanExpression(nbe.getLeft(), NumericComparator.LT, nbe.getRight());
      }
    }
    
    if (newNegated instanceof Negation) {
      return ((Negation)newNegated).getNegated();
    }
    
    return new Negation(newNegated);
  }

  @Override
  protected Expression visit(NumericBooleanExpression nbe, Expression exprsn) {
    Expression left = visit(nbe.getLeft(),nbe);
    Expression right = visit(nbe.getRight(),nbe);    
    if (left instanceof Constant && right instanceof Constant) {
      return new Constant(Boolean.class,nbe.evaluate(null));
    }    
    return new NumericBooleanExpression(left, nbe.getComparator(), right);
  }

  @Override
  protected Expression visit(NumericCast nc, Expression exprsn) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  protected Expression visit(NumericCompound nc, Expression exprsn) {
    Expression left = visit(nc.getLeft(),nc);
    Expression right = visit(nc.getRight(),nc);    
    if (left instanceof Constant && right instanceof Constant) {
      return new Constant(nc.getType(),nc.evaluate(null));
    }
    return new NumericCompound(left, nc.getOperator(), right);
  }

  @Override
  protected Expression visit(PropositionalCompound pc, Expression exprsn) {
    Expression left = visit(pc.getLeft(),pc);
    Expression right = visit(pc.getRight(),pc);
    if (left instanceof Constant && right instanceof Constant) {
      Constant<Boolean> lc = (Constant<Boolean>)left;
      Constant<Boolean> rc = (Constant<Boolean>)right;
      Expression<Boolean> test = new PropositionalCompound(left, pc.getOperator(), right);
      return new Constant<Boolean>(Boolean.class, test.evaluate(null));
    }
    else if (left instanceof Constant) {
      Constant<Boolean> lc = (Constant<Boolean>)left;
      switch (pc.getOperator()) {
        case AND:
          return lc.getValue() ? right : new Constant<Boolean>(Boolean.class,false);
        case OR:
          return lc.getValue() ? new Constant<Boolean>(Boolean.class,true) : right;
        default:
          return new PropositionalCompound(left, pc.getOperator(), right);
      }
    }
    else if (right instanceof Constant) {
      Constant<Boolean> rc = (Constant<Boolean>)right;
      switch (pc.getOperator()) {
        case AND:
          return rc.getValue() ? left : new Constant<Boolean>(Boolean.class,false);
        case OR:
          return rc.getValue() ? new Constant<Boolean>(Boolean.class,true) : left;
        default:
          return new PropositionalCompound(left, pc.getOperator(), right);
      }      
    }
    return new PropositionalCompound(left, pc.getOperator(), right);
  }

  @Override
  protected Expression visit(UnaryMinus um, Expression exprsn) {
    Expression newNegated = visit(um.getNegated(), um);
    if (newNegated instanceof Constant) {
      Constant<Number> cn = (Constant<Number>) newNegated;
      if (cn.getType().equals(Integer.class)) {
        return new Constant(cn.getType(), -cn.getValue().intValue());
      }
      else if (cn.getType().equals(Double.class)) {
        return new Constant(cn.getType(), -cn.getValue().doubleValue());
      }
      else if (cn.getType().equals(Long.class)) {
        return new Constant(cn.getType(), -cn.getValue().longValue());
      }
      else if (cn.getType().equals(Float.class)) {
        return new Constant(cn.getType(), -cn.getValue().floatValue());
      }      
    }
    return new UnaryMinus(newNegated);    
  }
    
  
  public Expression simplify(Expression e) {
    return visit(e,null);
  }

  @Override
  protected Expression visit(QuantifierExpression qe, Expression exprsn) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
