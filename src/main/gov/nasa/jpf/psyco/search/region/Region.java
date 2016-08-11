/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.region;

import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.api.ValuationEntry;
import gov.nasa.jpf.constraints.util.ExpressionUtil;
import java.io.IOException;
import java.util.HashMap;
/**
 *
 * @author mmuesly
 */
public abstract class Region<E extends ValuationEntry, T extends State<E>>
        extends HashMap<String, T>{
  public Region(){};

  public Region(Valuation initValuation){
    addInitialValuation(initValuation);
  }

  public Expression<Boolean> toExpression() {
    Expression returnExpression = null;
    for(T state: values()){
      Expression stateExpression = state.toExpression();
      returnExpression = 
              returnExpression == null ? 
              stateExpression 
              : ExpressionUtil.or(stateExpression, returnExpression);
    }
    return returnExpression;
  }

  public void print(Appendable a) throws IOException{
    for(String stateName: keySet()){
      a.append("stateName: ");
      a.append(stateName);
      a.append("\n");
      for(E entry: get(stateName)){
        a.append(entry.getVariable().getName());
        a.append(": ");
        a.append(entry.getValue().toString());
        a.append("\n");
      }
      a.append("\n");
    }
  }
  public abstract void addInitialValuation(Valuation initValuation);
  public abstract Region createNewRegion();
}
