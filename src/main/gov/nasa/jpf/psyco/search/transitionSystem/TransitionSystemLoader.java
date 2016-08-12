/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.transitionSystem;

import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.api.ValuationEntry;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.expressions.BitvectorExpression;
import gov.nasa.jpf.constraints.expressions.BitvectorNegation;
import gov.nasa.jpf.constraints.expressions.BitvectorOperator;
import gov.nasa.jpf.constraints.expressions.Constant;
import gov.nasa.jpf.constraints.expressions.LogicalOperator;
import gov.nasa.jpf.constraints.expressions.Negation;
import gov.nasa.jpf.constraints.expressions.NumericBooleanExpression;
import gov.nasa.jpf.constraints.expressions.NumericComparator;
import gov.nasa.jpf.constraints.expressions.NumericCompound;
import gov.nasa.jpf.constraints.expressions.NumericOperator;
import gov.nasa.jpf.constraints.expressions.PropositionalCompound;
import gov.nasa.jpf.constraints.expressions.UnaryMinus;
import gov.nasa.jpf.constraints.types.BuiltinTypes;
import gov.nasa.jpf.jdart.constraints.Path;
import gov.nasa.jpf.jdart.constraints.PathResult;
import gov.nasa.jpf.jdart.constraints.PostCondition;
import gov.nasa.jpf.psyco.search.transitionSystem.helperVisitors.TransitionEncoding;
import gov.nasa.jpf.psyco.search.util.HelperMethods;
import gov.nasa.jpf.util.JPFLogger;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.util.logging.Logger;

public class TransitionSystemLoader {
  public String fileName;
  public String currentLine;
  private Logger logger = JPFLogger.getLogger(HelperMethods.getLoggerName());
  public TransitionSystemLoader(String fileName){
    this.fileName = fileName;
  }

  public TransitionSystem parseFile(){
    try(BufferedReader reader = new BufferedReader(new FileReader(fileName))){
      TransitionSystem tSystem = new TransitionSystem(null);
      String line;
      while((line = reader.readLine()) != null){
        currentLine = line;
        System.out.println("gov.nasa.jpf.psyco.search.transitionSystem.TransitionSystemLoader.parseFile()");
        System.out.println(currentLine);
        if(nextTokenIs(TransitionEncoding.valuation)){
          Valuation initValuation = parseInitialValuation();
          System.out.println("gov.nasa.jpf.psyco.search.transitionSystem.TransitionSystemLoader.parseFile()");
          System.out.println(initValuation.toString());
          tSystem.setInitValuation(initValuation);
        }
        if(nextTokenIs(TransitionEncoding.okTransition)){
          Transition t = parseOkTransition();
          System.out.println("gov.nasa.jpf.psyco.search.transitionSystem.TransitionSystemLoader.parseFile()");
          System.out.println(t.toString());
          tSystem.add(t);
        }
        if(nextTokenIs(TransitionEncoding.errorTransition)){
          Transition t = parseErrorTransition();
          System.out.println("gov.nasa.jpf.psyco.search.transitionSystem.TransitionSystemLoader.parseFile()");
          System.out.println(t.toString());
          tSystem.add(t);
        }
      }
      return tSystem;
    } catch (IOException ex) {
      logger.severe(ex.toString());
    }
    return null;
  }

  
  private Transition parseErrorTransition() {
    currentLine = currentLine.substring(2);
    Expression guard = null;
    if(nextTokenIs(TransitionEncoding.guard)){
      guard = parseGuard();
    }
    currentLine = currentLine.substring(4);
    int index = currentLine.indexOf(';');
    String error = currentLine.substring(0,index);
    currentLine = currentLine.substring(index + 1).replace("\n","");
    if(!currentLine.equals(";;")){
      System.out.println("gov.nasa.jpf.psyco.search.transitionSystem.TransitionSystemLoader.parseErrorTransition()");
      System.out.println(currentLine);
      throw new IllegalStateException("The error Transition is not parsed corretly");
    }
    Path p = new Path(guard, new PathResult.ErrorResult(null, error, null));
    return new Transition(p);
  }

  private Transition parseOkTransition(){
    currentLine = currentLine.substring(2);
    Expression guard;
    if(nextTokenIs(TransitionEncoding.guard)){
      guard = parseGuard();
    }else{
      throw new IllegalStateException("The input file is malformed." 
              + "Missign Guard in Transition");
    }
    PostCondition post = new PostCondition();
    if(nextTokenIs(TransitionEncoding.transitionBody)){
      currentLine = currentLine.substring(2);
      
      while(nextTokenIs(TransitionEncoding.effect)){
        post = parseTransitionEffect(post);
      }
      if(!currentLine.replace("\n", "").equals(";;")){
        throw new IllegalStateException(
                "The transition Line cannot be parsed entirely");
      }
    }
    Path p = new Path(guard, new PathResult.OkResult(null, post));
    return new Transition(p);
  }

  
  private PostCondition parseTransitionEffect(PostCondition post) {
    currentLine = currentLine.substring(2);
    Variable effectedVar = null;
    if(nextTokenIs(TransitionEncoding.variable)){
      effectedVar = parseVariable();
      currentLine = currentLine.substring(1);
    }
    Expression effect = parseNextExpression();
    if(currentLine.startsWith(";")){
      currentLine = currentLine.substring(1);
    }
    post.addCondition(effectedVar, effect);
    return post;
  }

  private BitvectorExpression parseBitVectorExpression(){
    currentLine = currentLine.substring(2);
    Expression left = parseNextExpression();
    currentLine = currentLine.substring(1);
    BitvectorOperator operator;
    if(nextTokenIs(TransitionEncoding.bitVectorOperator)){
      currentLine = currentLine.substring(1);
      int operatorEnde = currentLine.indexOf(";");
      String readOperator = currentLine.substring(0, operatorEnde);
      operator = BitvectorOperator.valueOf(readOperator);
      currentLine = currentLine.substring(operatorEnde + 1);
    }else{
      throw new IllegalStateException(
              "The next token must be an BitVectorOperator");
    }
    currentLine = currentLine.substring(1);
    Expression right = parseNextExpression();
    return new BitvectorExpression(left, operator, right);
  }

  private BitvectorNegation parseBitvectorNeagtion(){
    currentLine = currentLine.substring(2);
    Expression negated = parseNextExpression();
    currentLine = currentLine.substring(1);
    return new BitvectorNegation(negated);
  }

  private Constant parseConstant(){
    currentLine = currentLine.substring(2);
    int endValue = currentLine.indexOf(':');
    String value = currentLine.substring(0, endValue);
    currentLine = currentLine.substring(endValue + 1);
    endValue = currentLine.indexOf(';');
    String type = currentLine.substring(0, endValue);
    System.out.println("gov.nasa.jpf.psyco.search.transitionSystem.TransitionSystemLoader.parseConstant()");
    System.out.println("type: " + type);
    System.out.println("value:" + value);
    currentLine = currentLine.substring(endValue + 1);
    if(type.endsWith("BuiltinTypes$SInt32Type")){
      System.out.println("gov.nasa.jpf.psyco.search.transitionSystem.TransitionSystemLoader.parseConstant()");
      System.out.println(Integer.parseInt(value));
      return new Constant(BuiltinTypes.SINT32, Integer.parseInt(value));
    }else if(type.endsWith("BuiltinTypes$BoolType")){
      System.out.println("gov.nasa.jpf.psyco.search.transitionSystem.TransitionSystemLoader.parseConstant()");
      System.out.println(Boolean.parseBoolean(value));
      return new Constant(BuiltinTypes.BOOL, Boolean.parseBoolean(value));
    }else{
      throw new IllegalStateException("This Type is currently not supported");
    }
  }

  private Negation parseNegation(){
    currentLine = currentLine.substring(2);
    Expression negated = parseNextExpression();
    currentLine = currentLine.substring(1);
    return new Negation(negated);
  }

  private NumericBooleanExpression parseNumericBooleanExpression(){
    currentLine = currentLine.substring(2);
    Expression left = parseNextExpression();
    currentLine = currentLine.substring(3);
    int endOperator = currentLine.indexOf(';');
    String operator = currentLine.substring(0, endOperator);
    NumericComparator op = NumericComparator.fromString(operator);
    currentLine = currentLine.substring(endOperator + 2);
    System.out.println("gov.nasa.jpf.psyco.search.transitionSystem.TransitionSystemLoader.parsePropositionalCompound()");
    System.out.println(currentLine);
    Expression right = parseNextExpression();
    System.out.println("gov.nasa.jpf.psyco.search.transitionSystem.TransitionSystemLoader.parseNumericBooleanExpression()");
    System.out.println("right: " + right);
    currentLine = currentLine.substring(1);
    return new NumericBooleanExpression(left, op, right);
  }

  private NumericCompound parseNumericCompound(){
    currentLine = currentLine.substring(2);
    Expression left = parseNextExpression();
    currentLine = currentLine.substring(3);
    int endOperator = currentLine.indexOf(';');
    String operator = currentLine.substring(0, endOperator);
    NumericOperator op = NumericOperator.fromString(operator);
    currentLine = currentLine.substring(endOperator + 2);
    System.out.println("gov.nasa.jpf.psyco.search.transitionSystem.TransitionSystemLoader.parsePropositionalCompound()");
    System.out.println(currentLine);
    Expression right = parseNextExpression();
    System.out.println("gov.nasa.jpf.psyco.search.transitionSystem.TransitionSystemLoader.parseNumericCompound()");
    System.out.println("right: " + right);
    currentLine = currentLine.substring(1);
    return new NumericCompound(left, op, right);
  }

  private PropositionalCompound parsePropositionalCompound(){
    currentLine = currentLine.substring(2);
    Expression left = parseNextExpression();
    currentLine = currentLine.substring(3);
    int endOperator = currentLine.indexOf(';');
    String operator = currentLine.substring(0, endOperator);
    LogicalOperator op = LogicalOperator.fromString(operator);
    currentLine = currentLine.substring(endOperator + 2);
    System.out.println("gov.nasa.jpf.psyco.search.transitionSystem.TransitionSystemLoader.parsePropositionalCompound()");
    System.out.println(currentLine);
    Expression right = parseNextExpression();
    System.out.println("gov.nasa.jpf.psyco.search.transitionSystem.TransitionSystemLoader.parsePropositionalCompound()");
    System.out.println("right: " + right);
    currentLine = currentLine.substring(1);
    return new PropositionalCompound(left, op, right);
  }

  private UnaryMinus parseUnaryMinus(){
    currentLine = currentLine.substring(2);
    Expression unaryExpression = parseNextExpression();
    currentLine = currentLine.substring(1);
    return UnaryMinus.create(unaryExpression);
  }

  private Valuation parseInitialValuation(){
    currentLine = currentLine.substring(2);
    Valuation result = new Valuation();
    while(nextTokenIs(TransitionEncoding.valuationEntry)){
      currentLine = currentLine.substring(2);
      Variable var = parseVariable();
      currentLine = currentLine.substring(1);
      int endValue = currentLine.indexOf(';');
      String value = currentLine.substring(0, endValue);
      currentLine = currentLine.substring(endValue + 1);
      ValuationEntry entry = 
              new ValuationEntry(var, var.getType().parse(value));
      result.addEntry(entry);
    }
    currentLine = currentLine.substring(1).replace("\n","");
    if(!currentLine.isEmpty()){
      System.out.println("gov.nasa.jpf.psyco.search.transitionSystem.TransitionSystemLoader.parseInitialValuation()");
      System.out.println("current Line: ");
      System.out.println(currentLine);
      throw new IllegalStateException("Valuation not finished");
    }
    return result;
  }

  public static TransitionSystem load(String fileName){
    TransitionSystemLoader loader = new TransitionSystemLoader(fileName);
    return loader.parseFile();
  }

  private Expression parseGuard() {
    currentLine = currentLine.substring(2);
    Expression guard = parseNextExpression();
    currentLine = currentLine.substring(1);
    return guard;
  }

  private Variable parseVariable() {
    currentLine = currentLine.substring(2);
    int endName = currentLine.indexOf(':');
    String name = currentLine.substring(0, endName);
    currentLine = currentLine.substring(endName);
    endName = currentLine.indexOf(';');
    String type = currentLine.substring(0, endName);
    currentLine = currentLine.substring(endName + 1);
    if(type.endsWith("BuiltinTypes$SInt32Type")){
      return Variable.create(BuiltinTypes.SINT32, name);
    }else if(type.endsWith("BuiltinTypes$BoolType")){
      return Variable.create(BuiltinTypes.BOOL, name);
    }else{
      throw new IllegalStateException("This Type is currently not supported");
    }
    
  }

  private boolean nextTokenIs(char tokenType){
    String prefix = String.valueOf(tokenType);
    return currentLine.startsWith(prefix);
  }

  private Expression parseNextExpression() {
    char nextExpressionType = currentLine.charAt(0);
    switch(nextExpressionType){
      case TransitionEncoding.bitVector:
        return parseBitVectorExpression(); 
      case TransitionEncoding.bitVectorNegation:
        return parseBitvectorNeagtion(); 
      case TransitionEncoding.constant:
        return parseConstant(); 
      case TransitionEncoding.negation:
        return parseNegation(); 
      case TransitionEncoding.numericBooleanExpression:
        return parseNumericBooleanExpression();
      case TransitionEncoding.numericCompund:
        return parseNumericCompound();
      case TransitionEncoding.propositionalCompound:
        return parsePropositionalCompound();
      case TransitionEncoding.unaryMinus:
        return parseUnaryMinus();
      case TransitionEncoding.variable:
        return parseVariable();
      default:
        System.out.println("gov.nasa.jpf.psyco.search.transitionSystem.TransitionSystemLoader.parseNextExpression()");
        System.out.println("line: " + currentLine);
        throw new IllegalStateException("Cannot parse next Expression.");
    }
  }
}
