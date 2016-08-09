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
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author mmuesly
 */
public class SymbolicState extends State<SymbolicEntry>{

  public SymbolicState(){
    super();
  }

  public SymbolicState(Valuation initValuation){
    this();
    for(ValuationEntry entry: initValuation.entries()){
      SymbolicEntry newStateEntry = SymbolicEntry.create(entry);
      add(newStateEntry);
    }
  }

  @Override
  public Expression<Boolean> toExpression(){
    Expression<Boolean> expr = null;
    for(SymbolicEntry entry: this){
      expr = expr != null ? ExpressionUtil.and(expr, entry.getValue()):
              entry.getValue();
    }
    return expr;
  }
}
