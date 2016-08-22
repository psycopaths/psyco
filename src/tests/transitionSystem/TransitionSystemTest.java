/*
 * Copyright (C) 2015, United States Government, as represented by the 
 * Administrator of the National Aeronautics and Space Administration.
 * All rights reserved.
 *
 * The PSYCO: A Predicate-based Symbolic Compositional Reasoning environment 
 * platform is licensed under the Apache License, Version 2.0 (the "License"); you 
 * may not use this file except in compliance with the License. You may obtain a 
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0. 
 *
 * Unless required by applicable law or agreed to in writing, software distributed 
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 * specific language governing permissions and limitations under the License.
 */
package transitionSystem;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.constraints.api.ConstraintSolver;
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
import gov.nasa.jpf.constraints.solvers.ConstraintSolverFactory;
import gov.nasa.jpf.constraints.types.BuiltinTypes;
import gov.nasa.jpf.jdart.constraints.Path;
import gov.nasa.jpf.jdart.constraints.PathResult;
import gov.nasa.jpf.jdart.constraints.PostCondition;
import gov.nasa.jpf.psyco.PsycoConfig;
import gov.nasa.jpf.psyco.search.EnumerativeSearchEngine;
import gov.nasa.jpf.psyco.search.SymbolicSearchEngine;
import gov.nasa.jpf.psyco.search.datastructures.searchImage.SearchIterationImage;
import gov.nasa.jpf.psyco.search.transitionSystem.EnumerativeTransitionHelper;
import gov.nasa.jpf.psyco.search.transitionSystem.SymbolicTransitionHelper;
import gov.nasa.jpf.psyco.search.transitionSystem.Transition;
import gov.nasa.jpf.psyco.search.transitionSystem.TransitionHelper;
import gov.nasa.jpf.psyco.search.transitionSystem.TransitionSystem;
import gov.nasa.jpf.psyco.search.transitionSystem.TransitionSystemLoader;
import java.io.File;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TransitionSystemTest {
  
  public TransitionSystemTest() {
  }
  ConstraintSolver solver;
  @Before
  public void setUp() {
    String[] args = {};
    Config conf = new Config(args);
    conf.setProperty("symbolic.dp", "NativeZ3");
    conf.setProperty("symbolic.dp.z3.bitvectors", "false");
    conf.setProperty("log.finest", "psyco");
    ConstraintSolverFactory factory = new ConstraintSolverFactory(conf);
    solver = factory.createSolver();
    PsycoConfig pconf = new PsycoConfig(conf, solver, null);
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
    assertNotNull(system.getTransitions());
    assertEquals(2, system.getTransitions().size());
    assertEquals(1, system.getConsideredErrorTransitions().size());
    assertEquals(1, system.getConsideredOkTransitions().size());
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
    assertEquals(system.completeToString(), system2.completeToString());
  }

  @Test
  public void transitionSystemTest1(){
    TransitionSystem system = createTransitionSystem();
    TransitionHelper symbolicHelper = new SymbolicTransitionHelper();
    system.setHelper(symbolicHelper);
    SearchIterationImage image = 
            SymbolicSearchEngine.symbolicBreadthFirstSearch(system,
                    solver, Integer.MIN_VALUE);
    TransitionHelper enumerativeHelper = new EnumerativeTransitionHelper();
    system.setHelper(enumerativeHelper);
    SearchIterationImage image2 =
            EnumerativeSearchEngine.enumerativBreadthFirstSearch(
            system, solver, Integer.MIN_VALUE);
    assertEquals(3, image.getDepth());
    List<Transition> errorTransition = system.getConsideredErrorTransitions();
    for(Transition t: errorTransition){
      if(t.isReached()){
        assertEquals("java.lang.RuntimeException", t.getError());
      }else{
        assertEquals("NotEnabled", t.getError());
      }
    }
    List<Transition> okTransition = system.getConsideredOkTransitions();
    int counter = 0;
    for(Transition t: okTransition){
      if(t.isReached()){
        counter ++;
      }else{counter--;}
    }
    assertEquals(0, counter);
    assertEquals(3, image.getReachableStates().size());
    assertEquals(3, image2.getReachableStates().size());
  }

  @Test
  public void transitionSystemTest2(){
    TransitionSystem system = createTransitionSystem2();
    TransitionHelper symbolicHelper = new SymbolicTransitionHelper();
    system.setHelper(symbolicHelper);
    SearchIterationImage image = null;
    try{
      image = 
            SymbolicSearchEngine.symbolicBreadthFirstSearch(system,
                    solver, Integer.MIN_VALUE);
    }catch(IllegalStateException ex){
      assertFalse(true);
    }
    assertEquals(2, image.getDepth());
    List<Transition> errorTransition = system.getConsideredErrorTransitions();
    for(Transition t: errorTransition){
       if(t.isReached()){
        assertEquals("java.lang.RuntimeException", t.getError());
      }else{
        assertEquals("NotEnabled", t.getError());
      }
    }
    List<Transition> okTransition = system.getConsideredOkTransitions();
    int counter = 0;
    for(Transition t: okTransition){
        if(t.isReached()){
          counter ++;
        }else{counter--;}
    }
    assertEquals(0, counter);
    assertEquals(2, image.getReachableStates().size());
  }

  @Test
  public void transitionSystemTest3(){
    TransitionSystem system = createTransitionSystem3();
    TransitionHelper symbolicHelper = new SymbolicTransitionHelper();
    system.setHelper(symbolicHelper);
    SearchIterationImage image = 
            SymbolicSearchEngine.symbolicBreadthFirstSearch(system,
                    solver, Integer.MIN_VALUE);
    assertEquals(2, image.getDepth());
    List<Transition> errorTransition = system.getConsideredErrorTransitions();
    for(Transition t: errorTransition){
      assertTrue(t.isReached());
    }
    List<Transition> okTransition = system.getConsideredOkTransitions();
    int counter = 0;
    for(Transition t: okTransition){
        if(t.isReached()){
          counter ++;
        }else{counter--;}
    }
    assertEquals(2, counter);
    assertEquals(2, image.getReachableStates().size());
  }

  private TransitionSystem createTransitionSystem(){
    Variable var = new Variable(BuiltinTypes.SINT32, "this.x");
    Variable var1 = new Variable(BuiltinTypes.SINT32, "this.y");
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

  private TransitionSystem createTransitionSystem2(){
    Variable var = new Variable(BuiltinTypes.SINT32, "this.x");
    Variable parameter = new Variable(BuiltinTypes.SINT32, "p");
    Constant<Integer> constant = Constant.create(BuiltinTypes.SINT32, 5);
    Constant<Integer> constant1 = Constant.create(BuiltinTypes.SINT32, 1);
    Constant constant2 = Constant.create(BuiltinTypes.SINT32, 15);
    Constant constant3 = Constant.create(BuiltinTypes.SINT32, 15*15);
    Constant constant4 = Constant.create(BuiltinTypes.SINT32, 300);
    Constant constant5 = Constant.create(BuiltinTypes.SINT32, 0);
    Expression guard1 = NumericBooleanExpression.create(var,
            NumericComparator.GT, constant);
    NumericCompound part = NumericCompound.create(var,
            NumericOperator.PLUS, parameter);
    Expression guard2 = NumericBooleanExpression.create(part, 
            NumericComparator.LT, constant4);
    Expression guard6 = NumericBooleanExpression.create(parameter,
            NumericComparator.GT, constant2);
    Expression guard7 = NumericBooleanExpression.create(part, 
            NumericComparator.GT, constant5);
    Expression guard = 
            new PropositionalCompound(guard1, LogicalOperator.AND, guard6);
    guard = new PropositionalCompound(guard, LogicalOperator.AND, guard2);
    guard = new PropositionalCompound(guard, LogicalOperator.AND, guard7);
    Expression effect = 
            new NumericCompound(var, NumericOperator.PLUS, parameter);
    PostCondition post = new PostCondition();
    post.addCondition(var, effect);
    Path p = new Path(guard, new PathResult.OkResult(null, post));
    Expression guard3 = NumericBooleanExpression.create(var,
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
    Path p4 = new Path(guard5, new PathResult.OkResult(null, post));
    Valuation initValuation = new Valuation();
    initValuation.addEntry(new ValuationEntry(var,7));
    TransitionSystem system = new TransitionSystem();
    system.setInitValuation(initValuation);
    system.add(p);
    system.add(p2);
    system.add(p3);
    system.add(p4);
    return system;
  }

  private TransitionSystem createTransitionSystem3(){
    Variable var = new Variable(BuiltinTypes.SINT32, "this.x");
    Variable parameter = new Variable(BuiltinTypes.SINT32, "p");
    Constant<Integer> constant = Constant.create(BuiltinTypes.SINT32, 5);
    Constant<Integer> constant1 = Constant.create(BuiltinTypes.SINT32, 1);
    Constant constant2 = Constant.create(BuiltinTypes.SINT32, 15);
    Constant constant3 = Constant.create(BuiltinTypes.SINT32, 15*15);
    Expression guard1 = NumericBooleanExpression.create(var,
            NumericComparator.GT, constant);
    Expression effect = 
            new NumericCompound(var, NumericOperator.PLUS, parameter);
    PostCondition post = new PostCondition();
    post.addCondition(var, effect);
    Path p = new Path(guard1, new PathResult.OkResult(null, post));
    Expression guard3 = NumericBooleanExpression.create(var,
            NumericComparator.GT, constant3);
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
    Path p4 = new Path(guard5, new PathResult.OkResult(null, post));
    Valuation initValuation = new Valuation();
    initValuation.addEntry(new ValuationEntry(var,7));
    TransitionSystem system = new TransitionSystem();
    system.setInitValuation(initValuation);
    system.add(p);
    system.add(p2);
    system.add(p3);
    system.add(p4);
    return system;
  }
}