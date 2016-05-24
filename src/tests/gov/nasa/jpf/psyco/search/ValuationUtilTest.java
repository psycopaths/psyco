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

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
/**
 *
 * @author mmuesly
 */
public class ValuationUtilTest {
  
  public ValuationUtilTest() {
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
    Valuation valuationA = new Valuation();
    Valuation valuationB = new Valuation();
    int offset = 20;
    for (int i = 0; i < offset/2; i++){
      Variable var = Variable.create(BuiltinTypes.SINT32, "var_"+i);
      valuationA.setValue(var, i);
      valuationB.setValue(var, i + offset);
    }
    Valuation disjunctedResult = ValuationUtil.disjunction(valuationA, valuationB);
    assertFalse(ValuationUtil.isEmpty(disjunctedResult));
    for(ValuationEntry entry: valuationB.entries()){
      valuationA.addEntry(entry);
    }
    for(ValuationEntry entry: valuationA.entries()){
      valuationB.addEntry(entry);
    }
    disjunctedResult = ValuationUtil.disjunction(valuationA, valuationB);
    assertTrue(ValuationUtil.isEmpty(disjunctedResult));
  }
}
