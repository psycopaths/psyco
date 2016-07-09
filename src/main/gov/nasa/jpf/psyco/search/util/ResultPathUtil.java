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
import gov.nasa.jpf.jdart.constraints.PathResult.ErrorResult;
import gov.nasa.jpf.jdart.constraints.PathResult.OkResult;
import gov.nasa.jpf.psyco.exceptions.RenamingException;
import gov.nasa.jpf.psyco.search.transitionSystem.TransformationRepresentation;
import gov.nasa.jpf.psyco.search.region.SymbolicEntry;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mmuesly
 */
public class ResultPathUtil {
  
  public static TransformationRepresentation convertPathToTransition(Path path, int depth){
    PathResult pathResult = path.getPathResult();
    if(pathResult instanceof OkResult){
      return convertOkPathToTransition(path);
    }
    if(pathResult instanceof ErrorResult){
      return convertErrorPathToTransition(path, depth);
    }
    return null;
  }
  public static TransformationRepresentation convertOkPathToTransition(Path path){
    TransformationRepresentation transition = new TransformationRepresentation();
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

  private static TransformationRepresentation convertErrorPathToTransition(Path path, int depth) {
    TransformationRepresentation transition = new TransformationRepresentation();
    ErrorResult result = path.getErrorResult();
    Expression transitionExpression = path.getPathCondition();
    transition.setExpresion(transitionExpression);
    transition.addError(result.getExceptionClass(), depth);
    transition.setErrorTransition(true);
    return transition;
  }
}
