/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.region;

import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.ValuationEntry;
import gov.nasa.jpf.constraints.api.Variable;

/**
 *
 * @author mmuesly
 */
public class SymbolicEntry extends ValuationEntry<Expression>{

  public SymbolicEntry(Variable<Expression> variable, Expression value) {
    super(variable, value);
  }
  
  
  @Override
  public boolean equals(Object obj) {
    throw new UnsupportedOperationException("Equals on expression"+
            " Entrys is not defined yet");
  }
}
