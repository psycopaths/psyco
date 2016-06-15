/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.region;

import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.util.ExpressionUtil;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author mmuesly
 */
public class SymbolicRegion extends HashMap<String, SymbolicState>{

  public SymbolicRegion(){
    super();
  }
  
  public SymbolicRegion(Valuation initValuation){
    this();
    SymbolicState initState = new SymbolicState(initValuation);
    put("initState", initState);
  }
  
  public Expression<Boolean> toExpression(){
    Expression<Boolean> expr = null;
    for(String stateName: keySet()){
      SymbolicState state = get(stateName);
      expr = expr != null ? ExpressionUtil.or(expr, state.toExpression()):
              state.toExpression();
    }
    return expr;
  }

  public void print(Appendable a) throws IOException {
    for(String stateName: keySet()){
      a.append("stateName: ");
      a.append(stateName);
      a.append("\n");
      for(SymbolicEntry entry: get(stateName)){
        a.append(entry.getVariable().getName());
        a.append(": ");
        a.append(entry.getValue().toString());
        a.append("\n");
      }
    }
  }
}