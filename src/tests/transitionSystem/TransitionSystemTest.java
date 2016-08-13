/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transitionSystem;

import gov.nasa.jpf.constraints.expressions.Constant;
import gov.nasa.jpf.constraints.expressions.NumericComparator;
import gov.nasa.jpf.constraints.expressions.Constant;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.expressions.NumericBooleanExpression;
import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.api.ValuationEntry;
import gov.nasa.jpf.constraints.expressions.LogicalOperator;
import gov.nasa.jpf.constraints.expressions.NumericCompound;
import gov.nasa.jpf.constraints.expressions.NumericOperator;
import gov.nasa.jpf.constraints.expressions.PropositionalCompound;
import gov.nasa.jpf.constraints.types.BuiltinTypes;
import gov.nasa.jpf.jdart.constraints.Path;
import gov.nasa.jpf.jdart.constraints.PathResult;
import gov.nasa.jpf.jdart.constraints.PostCondition;
import gov.nasa.jpf.psyco.search.transitionSystem.TransitionSystem;
import gov.nasa.jpf.psyco.search.transitionSystem.TransitionSystemLoader;
import java.io.File;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mmuesly
 */
public class TransitionSystemSaveAndLoadTest {
  
  public TransitionSystemSaveAndLoadTest() {
  }
  
  @Before
  public void setUp() {
  }

  /*
  *If this test fails, you probably need to update jConstraints. In this case
  *it is very likely, that you have not yet the latest changes.
  */
  @Test
  public void comperatorTest(){
    NumericComparator test = NumericComparator.valueOf("LE");
    NumericComparator test2 = NumericComparator.fromString("<=");
    assertEquals(test2, test);
  }

  @Test
  public void loadTest(){
    TransitionSystem system = TransitionSystemLoader.load("src" 
            + File.separator + "resources" + File.separator 
            + "transitionSystem" + File.separator + "transitionSystem.ts");
    System.out.println(system.completeToString());
    assertNotNull(system.getTransitions());
    assertEquals(2, system.getTransitions().size());
    assertEquals(1, system.getConsideredErrorPaths().size());
    assertEquals(1, system.getConsideredOKPaths().size());
  }

  @Test
  public void saveAndLoadTest(){
    TransitionSystem system = createTransitionSystem();
    File file = new File("test");
    if(!file.exists()){
      file.mkdir();
    }
    String testFile = "test" + File.separator + "transitionSystem.ts";
    system.writeToFile(testFile);
    TransitionSystem system2 = TransitionSystemLoader.load(testFile);
    System.out.println("transitionSystem.TransitionSystemSaveAndLoadTest.saveAndLoadTest()");
    System.out.println(system.completeToString());
    System.out.println(system2.completeToString());
    assertEquals(system.completeToString(), system2.completeToString());
  }
  
  private TransitionSystem createTransitionSystem(){
    Variable var = new Variable(BuiltinTypes.SINT32, "x");
    Variable var1 = new Variable(BuiltinTypes.SINT32, "y");
    Constant<Integer> constant = Constant.create(BuiltinTypes.SINT32, 5);
    Constant<Integer> constant1 = Constant.create(BuiltinTypes.SINT32, 1);
    Constant constant2 = Constant.create(BuiltinTypes.SINT32, 15);
    Constant constant3 = Constant.create(BuiltinTypes.SINT32, 15*15);
    Expression guard1 = NumericBooleanExpression.create(var,
            NumericComparator.GT, constant);
    Expression guard2 = NumericBooleanExpression.create(var1,
            NumericComparator.NE, constant3);
    Expression guard = 
            new PropositionalCompound(guard1, LogicalOperator.AND, guard2);
    Expression effect = 
            new NumericCompound(var, NumericOperator.PLUS, constant1);
    Expression effect2 = 
            new NumericCompound(var1, NumericOperator.MUL, constant2);
    PostCondition post = new PostCondition();
    post.addCondition(var, effect);
    post.addCondition(var1, effect2);
    Path p = new Path(guard, new PathResult.OkResult(null, post));
    Expression guard3 = NumericBooleanExpression.create(var1,
            NumericComparator.EQ, constant3);
    Path p2 = new Path(guard3,
            new PathResult.ErrorResult(null,
                    "java.lang.RuntimeException", null));
    Expression guard4 = 
            new NumericBooleanExpression(var, NumericComparator.LT, constant);
    Path p3 = new Path(guard4, 
            new PathResult.ErrorResult(null, "NotEnabled", null));
    Expression guard5 = 
            new NumericBooleanExpression(var, NumericComparator.EQ, constant2);
    Expression effect3 = new Constant(BuiltinTypes.SINT32, 1);
    post = new PostCondition();
    post.addCondition(var, effect3);
    post.addCondition(var1, var1);
    Path p4 = new Path(guard5, new PathResult.OkResult(null, post));
    Valuation initValuation = new Valuation();
    initValuation.addEntry(new ValuationEntry(var,7));
    initValuation.addEntry(new ValuationEntry(var1,1));
    TransitionSystem system = new TransitionSystem();
    system.setInitValuation(initValuation);
    system.add(p);
    system.add(p2);
    system.add(p3);
    system.add(p4);
    return system;
  }
}
