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

/**
 *
 * @author mmuesly
 */
public class EnumerativeState extends State<ValuationEntry>{

  public EnumerativeState(Valuation initValuation) {
    for(ValuationEntry entry: initValuation.entries()){
      this.add(entry);
    }
  }

  public EnumerativeState() {
    super();
  }

  @Override
  public Expression<Boolean> toExpression() {
    Valuation toConvert = new Valuation();
    for(ValuationEntry entry: this){
      toConvert.addEntry(entry);
    }
    return ExpressionUtil.valuationToExpression(toConvert);
  }

  @Override
  public State<ValuationEntry> createEmptyState() {
    return new EnumerativeState();
  }
  
}
