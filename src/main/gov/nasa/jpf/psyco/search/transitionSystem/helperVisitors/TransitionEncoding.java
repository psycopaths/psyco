/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.transitionSystem.helperVisitors;

import gov.nasa.jpf.constraints.expressions.NumericBooleanExpression;
import java.util.HashMap;

/**
 *
 * @author mmuesly
 */
public class TransitionEncoding {
  public static final char numericBooleanExpression = 'A';
  public static final char negation = 'N';
  public static final char constant = 'C';
  public static final char variable = 'V';
  public static final char numericComperator = 'F';
  public static final char numericOperator = 'G';
  public static final char numericCompund = 'D';
  public static final char propositionalCompound = 'P';
  public static final char logicalOpertaor = 'L';
  public static final char unaryMinus = 'U';
  public static final char bitVector = 'B';
  public static final char bitVectorOperator = 'O';
  public static final char bitVectorNegation = 'Q';
  public static final char effect ='E';
  public static final char guard = 'H';
  public static final char transitionBody = 'T';
  public static final char valuation = 'I';
  public static final char valuationEntry = 'W';
  public static final char error = 'K';
  public static final char okTransition = 'J';
  public static final char errorTransition = 'M';
//  public static HashMap<String,String> BackConversion = new HashMap<>();
//  public static HashMap<String, String> initalizeBackConversion(){
//    HashMap<String, String> toReturn = new HashMap<>();
//    toReturn.put(ExpressionEncoding.numericBooleanExpression,
//            "numericBooleanExpression");
//    toReturn.put(ExpressionEncoding.negation,
//            "negation");
//    toReturn.put(ExpressionEncoding.constant,
//            "constant");
//    toReturn.put(ExpressionEncoding.variable,
//            "variable");
//    toReturn.put(ExpressionEncoding.numericComperator,
//           "numericComperator");
//    toReturn.put(ExpressionEncoding.numericOperator,
//            "numericOperator");
//    toReturn.put(ExpressionEncoding.numericCompund,
//            "numericCompund");
//    toReturn.put(ExpressionEncoding.propositionalCompound,
//            "propositionalCompound");
//    toReturn.put(ExpressionEncoding.logicalOpertaor,
//            "logicalOpertaor");
//    toReturn.put(ExpressionEncoding.unaryMinus,
//            "unaryMinus");
//    toReturn.put(ExpressionEncoding.bitVector,
//            "bitVector");
//    toReturn.put(ExpressionEncoding.bitVectorOperator,
//            "bitVectorOperator");
//    toReturn.put(ExpressionEncoding.bitVectorNegation,
//            "bitVectorNegation");
//    return toReturn;
//  }

}
