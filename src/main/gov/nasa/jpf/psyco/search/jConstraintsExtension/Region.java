/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.jConstraintsExtension;

import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.util.AbstractPrintable;
import java.util.List;
import java.util.Set;

/**
 *
 * @author mmuesly
 * @param <T> Region Type. Currently it should be Valuation or Expressiation
 */
public abstract class Region<T> extends AbstractPrintable{
  
  public abstract void add(T toAdd);
  
  public abstract List<T> getRegionEntries();
  
  public abstract Set<T> getValuesForEntry(Variable entry);
  
  public abstract boolean isEmpty();
  
  public abstract Expression toExpression();
  
}
