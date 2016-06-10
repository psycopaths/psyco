/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.region;

import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.api.ValuationEntry;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.util.ExpressionUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author mmuesly
 */
public class ExpressionRegion extends Region<SymbolicEntry> {

  public ExpressionRegion(){
    super();
  }
  
  public ExpressionRegion(Valuation initValue){
    super(initValue);
  }
  
  @Override
  protected void setInitValuation(Valuation initValue) {
    Valuation singleValueToTransform;
    for(ValuationEntry entry: initValue){
      Variable var = entry.getVariable();
      singleValueToTransform = new Valuation();
      singleValueToTransform.addEntry(entry);
      Expression<Boolean> value = 
              ExpressionUtil.valuationToExpression(singleValueToTransform);
      SymbolicEntry newEntry = new SymbolicEntry(var, value);
      add(newEntry);
    }
  }

  public List<Expression<Boolean>> getValuesForEntryAsList(Variable entry){
    List<Expression<Boolean>> result = new ArrayList<>();
    for(SymbolicEntry currentEntry: region){
      if(currentEntry.getVariable().equals(entry)){
        result.add(currentEntry.getValue());
      }
    }
    return result;
  }
  @Override
  public Expression toExpression() {
    Expression<Boolean> expr = null;
    for(SymbolicEntry entry: region){
      expr = expr != null ? ExpressionUtil.or(expr, entry.getValue()):
              entry.getValue();
    }
    return expr;
  }
}
