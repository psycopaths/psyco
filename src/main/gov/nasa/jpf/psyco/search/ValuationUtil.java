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
