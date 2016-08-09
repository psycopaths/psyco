/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.datastructures;

import com.google.common.base.Function;
import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Variable;
import java.util.HashMap;

/**
 *
 * @author mmuesly
 */
public class VariableReplacementMap
        extends HashMap<Variable, Expression<Boolean>>
        implements Function<Variable<?>, Expression<Boolean>>{

  @Override
  public Expression<Boolean> apply(Variable<?> variable) {
    return getOrDefault(variable, (Expression) variable);
  }
}