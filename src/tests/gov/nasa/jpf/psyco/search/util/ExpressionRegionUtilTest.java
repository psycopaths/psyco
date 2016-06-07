/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.util;

import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.constraints.api.ConstraintSolver.Result;
import gov.nasa.jpf.constraints.solvers.nativez3.*;
import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.expressions.Constant;
import gov.nasa.jpf.constraints.expressions.NumericBooleanExpression;
import gov.nasa.jpf.constraints.expressions.NumericComparator;
import gov.nasa.jpf.constraints.solvers.ConstraintSolverFactory;
import gov.nasa.jpf.constraints.types.BuiltinTypes;
import gov.nasa.jpf.constraints.util.ExpressionUtil;
import gov.nasa.jpf.psyco.search.region.ExpressionRegion;
import gov.nasa.jpf.psyco.search.region.SymbolicEntry;
import gov.nasa.jpf.psyco.search.region.util.ExpressionRegionUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mmuesly
 */
public class ExpressionRegionUtilTest {
  
  public ExpressionRegionUtilTest() {
  }
  
  ExpressionRegion regionA, regionB;
  NativeZ3Solver solver;
  ExpressionRegionUtil utilUnderTest;
  
  @Before
  public void setUp() {
    regionA = new ExpressionRegion();
    regionB = new ExpressionRegion();
    Variable var = new Variable(BuiltinTypes.INTEGER, "x");
    Constant zero = new Constant(BuiltinTypes.INTEGER, 0);
    Constant five = new Constant(BuiltinTypes.INTEGER, 5);
    Constant four = new Constant(BuiltinTypes.INTEGER, 4);
    Expression value = 
            new NumericBooleanExpression(var, NumericComparator.LT, four);
    Expression value2 = 
            new NumericBooleanExpression(var, NumericComparator.EQ, four);
    regionA.add(new SymbolicEntry(var, value));
    value = 
            new NumericBooleanExpression(var, NumericComparator.GE, five);
    regionA.add(new SymbolicEntry(var, value));
    regionB.add(new SymbolicEntry(var, value2));
  
    Properties conf = new Properties();    
    conf.setProperty("symbolic.dp","z3");
    ConstraintSolverFactory factory = new ConstraintSolverFactory(conf);
    solver = (NativeZ3Solver)factory.createSolver();
    utilUnderTest = new ExpressionRegionUtil();
  }

  @Test
  public void testDisjunction() throws IOException{
    ExpressionRegion resRegion = utilUnderTest.disjunction(regionA, regionB);
    StringBuilder a = new StringBuilder();
    resRegion.print(a);
    System.out.println("gov.nasa.jpf.psyco.search.util.ExpressionRegionUtilTest.testDisjunction()");
    System.out.println(a.toString());
    Valuation res = new Valuation();
    
    res.print(System.out);
  }
  
  @Test
  public void testConjunction() throws IOException{
    ExpressionRegion resRegion = utilUnderTest.conjunction(regionA, regionB);
    StringBuilder a = new StringBuilder();
    resRegion.print(a);
    System.out.println("gov.nasa.jpf.psyco.search.util.ExpressionRegionUtilTest.testConjunction()");
    System.out.println(a.toString());
    Valuation res = new Valuation();
    Result result = solver.solve(resRegion.toExpression(), res);
    System.out.println(result);
    assertEquals(Result.UNSAT, result);
    Constant five = new Constant(BuiltinTypes.INTEGER, 5);
    Variable var = new Variable(BuiltinTypes.INTEGER, "x");
    regionB.add(new SymbolicEntry(var ,
            new NumericBooleanExpression(var, NumericComparator.EQ, five)));
    resRegion = utilUnderTest.conjunction(regionA, regionB);
    resRegion.print(System.out);
    result = solver.solve(resRegion.toExpression(), res);
    System.out.println(result);
    res.print(System.out);
    assertEquals(Result.SAT, result);
  }
  
  @Test 
  public void testDifference() throws IOException{
    ExpressionRegion resRegion = utilUnderTest.difference(regionA, regionB);
    System.out.println("gov.nasa.jpf.psyco.search.util.ExpressionRegionUtilTest.testDifference()");
    resRegion.print(System.out);
    System.out.println();
    Valuation res = new Valuation();
    Result result = solver.solve(resRegion.toExpression(), res);
    assertEquals(Result.SAT, result);
    System.out.println(result);
    Constant five = new Constant(BuiltinTypes.INTEGER, 5);
    Variable var = new Variable(BuiltinTypes.INTEGER, "x");
    regionB.add(new SymbolicEntry(var ,
            new NumericBooleanExpression(var, NumericComparator.EQ, five)));
    res.print(System.out);
    System.out.println();
    System.out.println("second run");
    resRegion.print(System.out);
    System.out.println();
    result = solver.solve(resRegion.toExpression(), res);
    assertEquals(Result.SAT, result);
    System.out.println(result);
    res.print(System.out);
    System.out.println();
  }
  
  @Test
  public void testDifferenceNoSuccess(){
    Variable var = new Variable(BuiltinTypes.INTEGER, "x");
    Constant five = new Constant(BuiltinTypes.INTEGER, 5);
    Constant two = new Constant(BuiltinTypes.INTEGER, 3);
    regionA = new ExpressionRegion();
    regionB = new ExpressionRegion();
    Expression value = new NumericBooleanExpression(var,
            NumericComparator.GE, five);
    Expression valueTwo = new NumericBooleanExpression(var,
            NumericComparator.GE, two);
    regionA.add(new SymbolicEntry(var, value));
    regionB.add(new SymbolicEntry(var, valueTwo));
    ExpressionRegion resRegion = utilUnderTest.difference(regionA, regionB);
    Valuation res = new Valuation();
    Result result = solver.solve(resRegion.toExpression(), res);
    assertEquals(Result.UNSAT, result);
    System.out.println("gov.nasa.jpf.psyco.search.util.ExpressionRegionUtilTest.testDifferenceNoSuccess()");
    System.out.println(result);
  }
  
  @Test
  public void testDifferenceIncludedRange() throws IOException{
    Variable var = new Variable(BuiltinTypes.INTEGER, "x");
    Constant five = new Constant(BuiltinTypes.INTEGER, 5);
    Constant two = new Constant(BuiltinTypes.INTEGER, 3);
    regionA = new ExpressionRegion();
    regionB = new ExpressionRegion();
    Expression value = new NumericBooleanExpression(var,
            NumericComparator.LE, five);
    Expression valueTwo = new NumericBooleanExpression(var,
            NumericComparator.GE, two);
    regionA.add(new SymbolicEntry(var, value));
    regionB.add(new SymbolicEntry(var, valueTwo));
    ExpressionRegion resRegion = utilUnderTest.difference(regionA, regionB);
    Valuation res = new Valuation();
    Result result = solver.solve(resRegion.toExpression(), res);
    assertEquals(Result.SAT, result);
    System.out.println("gov.nasa.jpf.psyco.search.util.ExpressionRegionUtilTest.testDifferenceIncludedRange()");
    res.print(System.out);
    System.out.println();
    System.out.println(result);
    System.out.println(resRegion.toExpression());
    System.out.println(ExpressionUtil.simplify(resRegion.toExpression()));
  }
  
  @Test
  public void testExists(){
    Set<Variable<?>> excludedVariables = 
            utilUnderTest.convertToVariableSet(regionA);
    assertTrue(utilUnderTest.exists(regionA, excludedVariables).isEmpty());
    Variable var = new Variable(BuiltinTypes.INTEGER, "y");
    Constant five = new Constant(BuiltinTypes.INTEGER, 5);
    Expression value = new NumericBooleanExpression(var,
            NumericComparator.LE, five);
    regionA.add(new SymbolicEntry(var, five));
    assertFalse(utilUnderTest.exists(regionA, excludedVariables).isEmpty());
  }
  
  @Test public void testRename(){
    Variable varY = new Variable(BuiltinTypes.INTEGER, "y");
    Variable varS = new Variable(BuiltinTypes.INTEGER, "s");
    Constant five = new Constant(BuiltinTypes.INTEGER, 5);
    Expression value = new NumericBooleanExpression(varY,
            NumericComparator.LE, five);
    regionA.add(new SymbolicEntry(varY, value));
    List<Variable<?>> oldNames = new ArrayList<>();
    List<Variable<?>> newNames = new ArrayList<>();
    oldNames.add(varY);
    newNames.add(varS);
    ExpressionRegion renameResult = 
            utilUnderTest.rename(regionA, oldNames, newNames);
    Set<Variable<?>> varInRegion = 
            utilUnderTest.convertToVariableSet(renameResult);
    assertFalse(varInRegion.contains(varY));
    assertTrue(varInRegion.contains(varS));
    assertEquals(2, varInRegion.size());
    Set<SymbolicEntry> valueY = renameResult.getValuesForEntry(varY);
    assertTrue(valueY.isEmpty());
    Set<SymbolicEntry> valueS = renameResult.getValuesForEntry(varS);
    assertFalse(valueS.isEmpty());
    System.out.println("gov.nasa.jpf.psyco.search.util.ExpressionRegionUtilTest.testRename()");
    for(SymbolicEntry entry: valueS){
      Expression valueExpr = entry.getValue();
      System.out.println(valueExpr.toString());
      assertTrue(valueExpr.toString().contains("s"));
      assertFalse(valueExpr.toString().contains("y"));
    }
  }
}
