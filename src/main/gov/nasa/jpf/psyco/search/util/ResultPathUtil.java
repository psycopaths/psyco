/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.util;

import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.expressions.NumericBooleanExpression;
import gov.nasa.jpf.constraints.expressions.NumericComparator;
import gov.nasa.jpf.constraints.util.ExpressionUtil;
import gov.nasa.jpf.jdart.constraints.Path;
import gov.nasa.jpf.jdart.constraints.PathResult;
import gov.nasa.jpf.jdart.constraints.PathResult.OkResult;
import gov.nasa.jpf.psyco.exceptions.RenamingException;
import gov.nasa.jpf.psyco.search.Transition;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mmuesly
 */
public class ResultPathUtil {
  
  public static Transition convertPathToTransition(Path path){
    PathResult pathResult = path.getPathResult();
    if(pathResult instanceof OkResult){
      return convertOkPathToTransition(path);
    }
    return null;
  }
  public static Transition convertOkPathToTransition(Path path){
    Transition transition = new Transition();
    Expression transitionExpression = path.getPathCondition();
    OkResult result = path.getOkResult();
    Map<Variable<?>,Expression<?>> postConditions = 
            result.getPostCondition().getConditions();
    for(Variable key: postConditions.keySet()){
      Expression resultingExpression = postConditions.get(key);
      Variable newKey = new Variable(key.getType(), key.getName() + "'");
      try {
        transition.addRenamingPair(key, newKey);
      } catch (RenamingException ex) {
        Logger.getLogger("psyco").log(Level.SEVERE, null, ex);
      }
      resultingExpression = NumericBooleanExpression.create(
              newKey,
              NumericComparator.EQ,
              resultingExpression);
      transitionExpression = ExpressionUtil.and(transitionExpression,
              resultingExpression);
    }
    transition.setExpresion(transitionExpression);
    return transition;
  }
}
