/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.region;

import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.ValuationEntry;
import gov.nasa.jpf.constraints.api.Variable;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author mmuesly
 */
public abstract class State<T extends ValuationEntry> extends HashSet<T>{
  public Set<T> getEntriesForVariable(Variable var){
    Set<T> entriesForVariable = new HashSet<>();
    for(T entry: this){
      if(entry.getVariable().equals(var)){
        entriesForVariable.add(entry);
      }
    }
    return entriesForVariable;
  }
//  public void addEntry(T toAdd){
//    add(toAdd);
//  }
  
  public abstract Expression<Boolean> toExpression();
  public abstract State<T> createEmptyState();
}
