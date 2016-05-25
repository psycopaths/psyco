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
  
  private void createVariableAndAddToValuation(Valuation valuation, int idAndValue){
    Variable var = Variable.create(BuiltinTypes.SINT32, "var_" +idAndValue);
      valuation.setValue(var, idAndValue);
  }
}
