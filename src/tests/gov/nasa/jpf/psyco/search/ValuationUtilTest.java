/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search;

import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.api.ValuationEntry;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.types.BuiltinTypes;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
/**
 *
 * @author mmuesly
 */
public class ValuationUtilTest {
  
  Valuation valuationA, valuationB;
  private final int offset = 5;
  public ValuationUtilTest() {
  }

  @Before
  public void setupValuations(){
    valuationA = new Valuation();
    valuationB = new Valuation();
    for (int i = 0; i < offset; i++){
      createVariableAndAddToValuation(valuationA, i);
      createVariableAndAddToValuation(valuationB, i + offset);
    }
    for(int i = 2*offset; i < 3*offset; i++){
      createVariableAndAddToValuation(valuationA, i);
      createVariableAndAddToValuation(valuationB, i);
    }
  }
  
  @Test
  public void valuationIsEmpty(){
    Valuation valuationUnderTest = new Valuation();
    assertTrue(ValuationUtil.isEmpty(valuationUnderTest));
    Variable var = Variable.create(BuiltinTypes.SINT32, "var_1");
    valuationUnderTest.setValue(var, 1);
    assertFalse(ValuationUtil.isEmpty(valuationUnderTest));
  }
  
  @Test
  public void valuationDisjunction(){
    Valuation disjunctedResult = ValuationUtil.disjunction(valuationA,
            valuationB);
    assertFalse(ValuationUtil.isEmpty(disjunctedResult));
    assertEquals(offset*2, disjunctedResult.entries().size());
    for(ValuationEntry entry: valuationB.entries()){
      valuationA.addEntry(entry);
    }
    for(ValuationEntry entry: valuationA.entries()){
      valuationB.addEntry(entry);
    }
    disjunctedResult = ValuationUtil.disjunction(valuationA, valuationB);
    assertTrue(ValuationUtil.isEmpty(disjunctedResult));
  }
  
  @Test
  public void valuationConjunction(){
    Valuation conjunctionResult = ValuationUtil.conjunction(valuationA,
            valuationB);
    assertFalse(ValuationUtil.isEmpty(conjunctionResult));
    assertEquals(offset, conjunctionResult.entries().size());
  }
  
  @Test
  public void disjunctionAndConjunctionConnection(){
    Valuation disjunctionResult = ValuationUtil.disjunction(valuationA,
            valuationB);
    Valuation conjunctionResult = ValuationUtil.conjunction(valuationA,
            valuationB);
    Valuation testConjunction = ValuationUtil.conjunction(disjunctionResult,
            conjunctionResult);
    Valuation testDisjunction = ValuationUtil.disjunction(disjunctionResult,
            conjunctionResult);
    assertTrue(ValuationUtil.isEmpty(testConjunction));
    assertEquals(3*offset, testDisjunction.entries().size());
  }
  
  @Test
  public void valuationDifference(){
    Valuation differenceResultA = ValuationUtil.difference(valuationA,
            valuationB);
    Valuation differenceResultB = ValuationUtil.difference(valuationB,
            valuationA);
    assertEquals(offset, differenceResultA.entries().size());
    assertEquals(offset, differenceResultB.entries().size());
    Valuation conjunction = ValuationUtil.conjunction(differenceResultA,
            differenceResultB);
    assertTrue(ValuationUtil.isEmpty(conjunction));
  }
  
  @Test
  public void valuationExists(){
    Set<Variable<?>> subsetOfVariables = new HashSet<Variable<?>>();
    int count = 0;
    for(ValuationEntry entry: valuationA.entries()){
      if(count == offset){
        break;
      }
      subsetOfVariables.add(entry.getVariable());
      count++;
    }
    Set<Variable<?>> existingSet= ValuationUtil.exists(valuationA,
            subsetOfVariables);
    assertEquals(offset, existingSet.size());
  }
  
  @Test
  public void convertValuationToVariableSet(){
    Valuation valuationUnderTest = new Valuation();
    Variable var1 = Variable.create(BuiltinTypes.SINT32, "var1");
    Variable var2 = Variable.create(BuiltinTypes.SINT32, "var2");
    Variable var3 = Variable.create(BuiltinTypes.SINT32, "var3");
    assertEquals(0, 
            ValuationUtil.convertToVariableSet(valuationUnderTest).size());
    valuationUnderTest.setValue(var1, 1);
    valuationUnderTest.setValue(var1, 2);
    valuationUnderTest.setValue(var2, 3);
    valuationUnderTest.setValue(var3, 0);
    assertEquals(3, 
            ValuationUtil.convertToVariableSet(valuationUnderTest).size());
  }
  
  private void createVariableAndAddToValuation(Valuation valuation, int idAndValue){
    Variable var = Variable.create(BuiltinTypes.SINT32, "var_" +idAndValue);
      valuation.setValue(var, idAndValue);
  }
}
