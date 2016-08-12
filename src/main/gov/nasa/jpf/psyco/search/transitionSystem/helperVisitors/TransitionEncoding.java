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
  public static String numericBooleanExpression = "A";
  public static String negation = "N";
  public static String constant = "C";
  public static String variable = "V";
  public static String numericComperator = "F";
  public static String numericOperator = "G";
  public static String numericCompund = "D";
  public static String propositionalCompound = "P";
  public static String logicalOpertaor = "L";
  public static String unaryMinus = "U";
  public static String bitVector = "B";
  public static String bitVectorOperator = "O";
  public static String bitVectorNegation = "Q";
  public static String effect ="E";
  public static String guard = "H";
  public static String transitionBody = "T";
  public static String valuation = "I";
  public static String valuationEntry = "W";
    public static String error = "H";
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
