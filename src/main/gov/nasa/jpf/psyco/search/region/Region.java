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
import gov.nasa.jpf.constraints.util.AbstractPrintable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author mmuesly
 * @param <T> Region Type. Currently it should be Valuation or Expressiation
 */
public abstract class Region<T extends ValuationEntry> extends AbstractPrintable{
  protected Set<T> region;
  
  public Region(){
    region = new HashSet<T>();
  }
  public Region(Valuation initValue){
    this();
    setInitValuation(initValue);
  }

  protected abstract void setInitValuation(Valuation initValue);
  
  public void add(T toAdd){
    region.add(toAdd);
  }
  
  public List<T> getRegionEntries(){
    return new ArrayList<>(region);
  };
  
  public Set<T> getValuesForEntry(Variable entry){
    Set<T> result = new HashSet<>();
    for(T currentEntry: region){
      if(currentEntry.getVariable().equals(entry)){
        result.add(currentEntry);
      }
    }
    return result;
  }
  
  public boolean isEmpty(){
    return region.isEmpty();
  }
  
  public abstract Expression toExpression();
  
  @Override
  public void print(Appendable a) throws IOException {
    for(T entry: region){
      //a.append("This Region contains the following var: values :\n");
      a.append(entry.getVariable().getName());
      a.append(": ");
      a.append(entry.getValue().toString());
      a.append("\n");
    }
  }
}
