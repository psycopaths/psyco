/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.util;

import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.api.ValuationEntry;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.types.BuiltinTypes;
import gov.nasa.jpf.psyco.search.region.util.ValuationUtil;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    disjunctedResult = ValuationUtil.disjunction(valuationA, valuationA);
    assertTrue(ValuationUtil.isEmpty(disjunctedResult));
  }
  
  @Test
  public void valuationConjunction(){
    Valuation conjunctionResult = ValuationUtil.conjunction(valuationA,
            valuationB);
    assertFalse(ValuationUtil.isEmpty(conjunctionResult));
    assertEquals(offset, conjunctionResult.entries().size());
    conjunctionResult = ValuationUtil.conjunction(valuationA, valuationA);
    assertFalse(ValuationUtil.isEmpty(conjunctionResult));
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
    Valuation existingRegion= ValuationUtil.exists(valuationA,
            subsetOfVariables);
    assertEquals(offset, existingRegion.entries().size());
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
  
  @Test
  public void valuationRenameOneEntryPerVariable(){
    List<Variable<?>> toBeRenamed = new ArrayList<Variable<?>>();
    List<Variable<?>> newVariableNames = new ArrayList<Variable<?>>();
    List<Variable<?>> notToBeRenamed = new ArrayList<Variable<?>>();
    Valuation regionToBeRenamed = new Valuation();
    int ongoingCounter = 0;
    int countNotRenamedVariables = 3;
    int countRenamdeVariables = 3;
    for(int i = ongoingCounter;
            i < ongoingCounter + countNotRenamedVariables; i++){
      Variable var = Variable.create(BuiltinTypes.SINT32, "var_" +i);
      regionToBeRenamed.setValue(var, i);
      notToBeRenamed.add(var);
    }
    ongoingCounter += countNotRenamedVariables;
    for(int i = ongoingCounter; 
            i < ongoingCounter + countRenamdeVariables; i++){
      Variable var = Variable.create(BuiltinTypes.SINT32, "var_" +i);
      regionToBeRenamed.setValue(var, i);
      toBeRenamed.add(var);
      Variable replacementVar = Variable.create(BuiltinTypes.SINT32,
              "var_" + (i+200));
      newVariableNames.add(replacementVar);
    }
    Valuation renamedRegion = ValuationUtil.rename(regionToBeRenamed,
            toBeRenamed, newVariableNames);
    Set<Variable<?>> renamedVariables = ValuationUtil.
            convertToVariableSet(renamedRegion);
    for(int i = 0; i < toBeRenamed.size(); i++){
      Variable oldVariable = toBeRenamed.get(i);
      assertFalse(renamedVariables.contains(oldVariable));
      assertEquals(regionToBeRenamed.getValue(oldVariable),
            renamedRegion.getValue(newVariableNames.get(i)));
    }
    for(Variable var: notToBeRenamed){
      assertEquals(regionToBeRenamed.getValue(var),
              renamedRegion.getValue(var));
    }
  }

  @Test
  public void valuationRenameTwoEntriesPerVariable(){
    //Preparation for this partial test case
    List<Variable<?>> toBeRenamed = new ArrayList<Variable<?>>();
    List<Variable<?>> newVariableNames = new ArrayList<Variable<?>>();
    List<Variable<?>> notToBeRenamed = new ArrayList<Variable<?>>();
    Valuation regionToBeRenamed = new Valuation();
    int ongoingCounter = 0;
    int countNotRenamedVariables = 3;
    int countRenamdeVariables = 3;
    for(int i = ongoingCounter;
            i < ongoingCounter + countNotRenamedVariables; i++){
      Variable var = Variable.create(BuiltinTypes.SINT32, "var_" +i);
      regionToBeRenamed.setValue(var, i);
      regionToBeRenamed.setValue(var, i*2);
      notToBeRenamed.add(var);
    }
    ongoingCounter += countNotRenamedVariables;
    for(int i = ongoingCounter; 
            i < ongoingCounter + countRenamdeVariables; i++){
      Variable var = Variable.create(BuiltinTypes.SINT32, "var_" +i);
      regionToBeRenamed.setValue(var, i);
      regionToBeRenamed.setValue(var, i*2);
      toBeRenamed.add(var);
      Variable replacementVar = Variable.create(BuiltinTypes.SINT32,
              "var_" + (i+200));
      newVariableNames.add(replacementVar);
    }
    Valuation renamedRegion = ValuationUtil.rename(regionToBeRenamed,
            toBeRenamed, newVariableNames);
    
    //Old variable Names are removed
    Set<Variable<?>> renamedVariables = ValuationUtil.
            convertToVariableSet(renamedRegion);
    for(int i = 0; i < toBeRenamed.size(); i++){
      Variable oldVariable = toBeRenamed.get(i);
      assertFalse(renamedVariables.contains(oldVariable));
    }
    //Size has not changed
    assertEquals(regionToBeRenamed.entries().size(),
            renamedRegion.entries().size());
    
    //All entries containing the old Variable name exist 
    //now with the new variable name.
    checkThatAllValuationEntrysExist(regionToBeRenamed, renamedRegion,
            toBeRenamed, newVariableNames);
    
    //Untouched entries still exist
    checkThatAllValuationEntrysExist(regionToBeRenamed, renamedRegion,
            notToBeRenamed, notToBeRenamed);
  }
  private void checkThatAllValuationEntrysExist(Valuation oldRegion,
          Valuation newRegion, List<Variable<?>> oldVariables, List<Variable<?>> newVariables){
    for(int i = 0; i < oldVariables.size(); i++){
      Variable var = oldVariables.get(i);
      for(ValuationEntry entry: oldRegion){
        if(entry.getVariable().equals(var)){
          assertTrue(entryExists(newRegion,
                newVariables.get(i), entry.getValue()));
        }
      }
    }
  }
  
  private boolean entryExists(Valuation region, Variable var, Object value){
    for(ValuationEntry entry: region.entries()){
      if(entry.getValue() == value && entry.getVariable().equals(var)){
        return true;
      }
    }
    return false;
  }
  private void createVariableAndAddToValuation(Valuation valuation, int idAndValue){
    Variable var = Variable.create(BuiltinTypes.SINT32, "var_" +idAndValue);
      valuation.setValue(var, idAndValue);
  }
}
