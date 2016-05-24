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
}
