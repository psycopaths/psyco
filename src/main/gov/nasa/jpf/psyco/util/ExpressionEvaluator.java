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
 *
 */
public class ExpressionEvaluator extends ExpressionVisitor<Expression> {

  @Override
  protected Expression visit(Variable vrbl, Expression exprsn) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  protected Expression visit(Constant cnstnt, Expression exprsn) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  protected Expression visit(Negation ngtn, Expression exprsn) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  protected Expression visit(NumericBooleanExpression nbe, Expression exprsn) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  protected Expression visit(NumericCast nc, Expression exprsn) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  protected Expression visit(NumericCompound nc, Expression exprsn) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  protected Expression visit(PropositionalCompound pc, Expression exprsn) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  protected Expression visit(UnaryMinus um, Expression exprsn) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

}
