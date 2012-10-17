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
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class ExpressionRestrictor extends ExpressionVisitor<Expression> {

  private Set<Variable> restriction;
  
  private boolean mixedParameters;
  
  public ExpressionRestrictor(Set<Variable> restricted) {
    this.restriction = restricted;
  }

  @Override
  protected Expression visit(Variable vrbl, Expression exprsn) {
    if (restriction.contains(vrbl)) {
      return vrbl.createCopy();
    }      
    return null;
  }

  @Override
  protected Expression visit(Constant cnstnt, Expression exprsn) {
    return cnstnt.createCopy();
  }

  @Override
  protected Expression visit(Negation ngtn, Expression exprsn) {
    Expression neg = visit(ngtn.getNegated(), ngtn);
    if (neg == null) {
      return null;
    }
    return new Negation(neg);
  }

  @Override
  protected Expression visit(NumericBooleanExpression nbe, Expression exprsn) {
    Expression left = visit(nbe.getLeft(),nbe);
    Expression right = visit(nbe.getRight(),nbe);
    // this is the case where we have mixed parameters maybe
    if (left == null || right == null) {
      if (left == null && right == null) {
        return null;
      }
      
      Expression test = (left != null) ? left : right;
      if (countVars(test) > 0) {
        this.mixedParameters = true;
      }
      return null;
    }    
    return new NumericBooleanExpression(left, nbe.getComparator(), right);
  }

  @Override
  protected Expression visit(NumericCast nc, Expression exprsn) {
    Expression n = visit(nc.getCasted(), nc);
    if (n == null) {
      return null;
    }
    return new NumericCast(n,nc.getType(),nc.getCastOperation());
  }

  @Override
  protected Expression visit(NumericCompound nc, Expression exprsn) {
    Expression left = visit(nc.getLeft(),nc);
    Expression right = visit(nc.getRight(),nc);
    // this is the case where we have mixed parameters maybe
    if (left == null || right == null) {
      if (left == null && right == null) {
        return null;
      }
      
      Expression test = (left != null) ? left : right;
      if (countVars(test) > 0) {
        this.mixedParameters = true;
      }
      return null;
    }    
    return new NumericCompound(left, nc.getOperator(), right);
  }

  @Override
  protected Expression visit(PropositionalCompound pc, Expression exprsn) {
    Expression left = visit(pc.getLeft(),pc);
    Expression right = visit(pc.getRight(),pc);
    // this is the case where we have mixed parameters maybe
    if (left == null || right == null) {
      if (left == null && right == null) {
        return null;
      }
      
      return (left != null) ? left : right;
    }    
    return new PropositionalCompound(left, pc.getOperator(), right);
  }

  @Override
  protected Expression visit(UnaryMinus um, Expression exprsn) {
    Expression neg = visit(um.getNegated(), um);
    if (neg == null) {
      return null;
    }
    return new UnaryMinus(neg);
  }
  
  
  private int countVars(Expression e) {
    Set<Variable> vars = new HashSet<Variable>();
    e.getVariables(vars);
    return vars.size();
  }  

  public Expression restrict(Expression e) {
    this.mixedParameters = false;
    Expression ret = visit(e, null);
    if (ret == null) {
      return new Constant(Boolean.class, true);
    }
    return ret;
  }
  
  public boolean hasMixedParameters() {
    return mixedParameters;
  }
  
}
