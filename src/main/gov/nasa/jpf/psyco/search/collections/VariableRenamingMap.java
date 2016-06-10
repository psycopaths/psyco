/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.collections;

import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.psyco.exceptions.RenamingException;
import gov.nasa.jpf.psyco.search.Transition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mmuesly
 */
public class VariableRenamingMap extends HashMap{
  List<Variable<?>> oldNames;
  List<Variable<?>> newNames;
  boolean updated = false;
  public VariableRenamingMap(){
    super();
    oldNames = new ArrayList<>();
    newNames = new ArrayList<>();
  }

  public void putValuePair(Variable oldName, Variable primeName) throws RenamingException{
    updated = true;
    if(this.containsKey(oldName)){
      Variable replacementName = (Variable) this.get(oldName);
      if(replacementName.equals(primeName)){
        return;
      }
      throw new RenamingException("Cannot rename the same variable to two names!");
    }
    put(oldName, primeName);
  }

  public List<Variable<?>> getOldNames(){
    convertMapToLists();
    return oldNames;
  }

  public List<Variable<?>> getPrimeNames(){
    convertMapToLists();
    return newNames;
  }

  public void convertMapToLists(){
    if(updated = true){
      oldNames = new ArrayList<>();
      newNames = new ArrayList<>();
      for(Object key: keySet()){
        oldNames.add((Variable) key);
        newNames.add((Variable) get(key));
      }
      updated = false;
    }
  }

  public void addRenamingsOfTransition(Transition transition){
    List<Variable<?>> oldNames = transition.getOldNames();
    List<Variable<?>> primeNames = transition.getPrimeNames();
    for(int i = 0; i < oldNames.size(); i++){
      Variable old = oldNames.get(i);
      Variable prime = primeNames.get(i);
      try {
        putValuePair(old, prime);
      } catch (RenamingException ex) {
        Logger.getLogger(VariableRenamingMap.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }
}
