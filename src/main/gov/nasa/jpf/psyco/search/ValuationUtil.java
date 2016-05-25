/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search;

import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.api.ValuationEntry;
import gov.nasa.jpf.constraints.api.Variable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author mmuesly
 */
public class ValuationUtil {

  public static boolean isEmpty(Valuation valuation){
    return valuation.entries().isEmpty();
  }
  public static Valuation disjunction(Valuation regionA, Valuation regionB){
    Valuation regionToReturn = new Valuation();
    Collection<ValuationEntry<?>> statesOfRegionA = regionA.entries();
    Collection<ValuationEntry<?>> statesOfRegionB = regionB.entries();
    regionToReturn = checkPartialDisjunction(statesOfRegionA,
            statesOfRegionB, regionToReturn);
    regionToReturn = checkPartialDisjunction(statesOfRegionB,
            statesOfRegionA, regionToReturn);
    return regionToReturn;
  }
  
  public static Valuation conjunction(Valuation regionA, Valuation regionB){
    Valuation regionToReturn = new Valuation();
    Collection<ValuationEntry<?>> statesOfRegionA = regionA.entries();
    Collection<ValuationEntry<?>> statesOfRegionB = regionB.entries();
    for(ValuationEntry entry: statesOfRegionA){
      ValuationEntry correspondingState = checkForCorrespondingState(entry,
              statesOfRegionB);
      if(correspondingState != null){
        regionToReturn.addEntry(entry);
      }
    }
    return regionToReturn;
  }
  
  public static Valuation difference(Valuation outterRegion, Valuation excludedRegion){
    Valuation regionToReturn = new Valuation();
    Collection<ValuationEntry<?>> statesOfOutterRegion = 
            outterRegion.entries();
    Collection<ValuationEntry<?>> statesOfExcludedRegion = 
            excludedRegion.entries();
    regionToReturn = checkPartialDisjunction(statesOfOutterRegion,
            statesOfExcludedRegion, regionToReturn);
    return regionToReturn;
  }
  
  public static Set<Variable<?>> exists(Valuation aRegion, Set<Variable<?>> subsetOfVariables){
    Set<Variable<?>> variablesNotInTheSubset = new HashSet<Variable<?>>();
    for(ValuationEntry entry: aRegion){
      Variable anyRegionVariable = entry.getVariable();
      if(subsetOfVariables.contains(anyRegionVariable)){
        continue;
      }
      variablesNotInTheSubset.add(anyRegionVariable);
    }
    return variablesNotInTheSubset;
  }
  
  public static Set<Variable<?>> convertToVariableSet(Valuation region){
    Set<Variable<?>> variablesInRegion = new HashSet<Variable<?>>();
    for(ValuationEntry regionEntry: region){
      variablesInRegion.add(regionEntry.getVariable());
    }
    return variablesInRegion;
  }
  
  public static Valuation rename(Valuation region, List<Variable<?>> oldNames, List<Variable<?>> newNames){
    Valuation resultingRegion = new Valuation();
    Set<Variable<?>> variablesToBeConsidered = convertToVariableSet(region);
    for(int i = 0; i < oldNames.size(); i++){
      Variable oldName = oldNames.get(i);
      Variable newName = newNames.get(i);
      renameAllVariableEntrys(region, resultingRegion, oldName, newName);
      variablesToBeConsidered.remove(oldName);
    }
    for(Variable notYetConsidered: variablesToBeConsidered){
      renameAllVariableEntrys(region, resultingRegion,
              notYetConsidered, notYetConsidered);
    }
    return resultingRegion;
  }
  
  private static void renameAllVariableEntrys(Valuation oldRegion,
          Valuation newRegion, Variable oldName, Variable newName){
    for(ValuationEntry entry: oldRegion.entries()){
        if(entry.getVariable().equals(oldName)){
          newRegion.setValue(newName, oldRegion.getValue(oldName));
        }
      }
  }
  
  private static Valuation checkPartialDisjunction(
            Collection<ValuationEntry<?>> entriesToCheck,
            Collection<ValuationEntry<?>> possibleCollisionEntries,
            Valuation resultingStates){
    for (ValuationEntry entry: entriesToCheck){
      ValuationEntry correspondingState = checkForCorrespondingState(entry,
              possibleCollisionEntries);
      if(correspondingState == null){
        resultingStates.addEntry(entry);
      }
    }
    return resultingStates;
  }
  private static ValuationEntry checkForCorrespondingState(ValuationEntry entryA, Collection<ValuationEntry<?>> entriesB){
    for(ValuationEntry entryB: entriesB){
      Variable variableA = entryA.getVariable();
      Variable variableB = entryB.getVariable();
      if(variableA.equals(variableB)){
        if(entryA.getValue() == entryB.getValue()){
          return entryB;
        }
      }
    }
    return null;
  }
}
