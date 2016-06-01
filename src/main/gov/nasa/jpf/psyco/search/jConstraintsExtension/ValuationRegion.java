/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.jConstraintsExtension;

import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.api.ValuationEntry;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.expressions.Constant;
import gov.nasa.jpf.constraints.expressions.Negation;
import gov.nasa.jpf.constraints.expressions.NumericBooleanExpression;
import gov.nasa.jpf.constraints.expressions.NumericComparator;
import gov.nasa.jpf.constraints.types.BuiltinTypes;
import gov.nasa.jpf.constraints.types.Type;
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
public class ValuationRegion extends Region<ValuationEntry>{
  private Set<ValuationEntry> region;

  public ValuationRegion(){
    region = new HashSet<>();
  }

  public ValuationRegion(Valuation aValuation){
    region = new HashSet<>();
    for(ValuationEntry entry: aValuation.entries()){
      region.add(entry);
    }
  }
  @Override
  public void add(ValuationEntry toAdd){
    region.add(toAdd);
  }

  @Override
  public List<ValuationEntry> getRegionEntries() {
    return new ArrayList<>(region);
  }

  @Override
  public Set<ValuationEntry> getValuesForEntry(Variable entry) {
    Set<ValuationEntry> result = new HashSet<>();
    for(ValuationEntry currentEntry: region){
      if(currentEntry.getVariable().equals(entry)){
        result.add(currentEntry);
      }
    }
    return result;
  }

  @Override
  public boolean isEmpty() {
    return region.isEmpty();
  }

  @Override
  public void print(Appendable a) throws IOException {
    for(ValuationEntry entry: region){
      //a.append("This Region contains the following var: values :\n");
      a.append(entry.getVariable().getName());
      a.append(": ");
      a.append(entry.getValue().toString());
      a.append("\n");
    }
  }

  /**
   * It is adapted from Expression Util. More or less equal the same.
   * @return 
   */
  @Override
  public Expression toExpression() {
    Expression result = null;
    for(ValuationEntry entry: region){
      Variable var = entry.getVariable();
      Type type = var.getType();
      Object value = entry.getValue();
      Expression<Boolean> newExpr;
      if(BuiltinTypes.BOOL.equals(type)) {
        Expression<Boolean> bvar = var.as(BuiltinTypes.BOOL);
        if((Boolean)value) {
          newExpr = bvar;
        }
      else {
        newExpr = new Negation(bvar);
      }
    }
    else {
      Constant cnst = Constant.create(type, value);
      newExpr = new NumericBooleanExpression(var, NumericComparator.EQ, cnst);
    }
    result = (result == null) ? newExpr : ExpressionUtil.and(newExpr, result);
    } 
    return result;
  }

}
