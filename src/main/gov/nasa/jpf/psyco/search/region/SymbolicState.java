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
import gov.nasa.jpf.constraints.expressions.Constant;
import gov.nasa.jpf.constraints.expressions.NumericBooleanExpression;
import gov.nasa.jpf.constraints.expressions.NumericComparator;
import gov.nasa.jpf.constraints.types.BuiltinTypes;
import gov.nasa.jpf.constraints.util.ExpressionUtil;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author mmuesly
 */
public class SymbolicState extends HashSet<SymbolicEntry>{

  public SymbolicState(){
    super();
  }

  public SymbolicState(Valuation initValuation){
    for(ValuationEntry entry: initValuation.entries()){
      SymbolicEntry newStateEntry = SymbolicEntry.create(entry);
      add(newStateEntry);
    }
  }
  
  public Set<SymbolicEntry> getEntriesForVariable(Variable var){
    Set<SymbolicEntry> entriesForVariable = new HashSet<>();
    for(SymbolicEntry entry: this){
      if(entry.getVariable().equals(var)){
        entriesForVariable.add(entry);
      }
    }
    return entriesForVariable;
  }
  
  public void addEntry(SymbolicEntry toAdd){
    add(toAdd);
  }
  
  public Expression<Boolean> toExpression(){
    Expression<Boolean> expr = null;
    for(SymbolicEntry entry: this){
      expr = expr != null ? ExpressionUtil.and(expr, entry.getValue()):
              entry.getValue();
    }
    return expr;
  }
}
