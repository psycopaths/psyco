/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.region.util;

import gov.nasa.jpf.constraints.api.ValuationEntry;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.psyco.search.region.Region;
import gov.nasa.jpf.psyco.search.region.ValuationRegion;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author mmuesly
 */
public class ValuationRegionUtil implements RegionUtil<ValuationRegion>{
  
  @Override
  public ValuationRegion disjunction(ValuationRegion regionA,
          ValuationRegion regionB) {
    ValuationRegion resultDisjuncted = new ValuationRegion();
    resultDisjuncted.add(regionA);
    resultDisjuncted.add(regionB);
    return resultDisjuncted;
  }
  
  public ValuationRegion exklusiveDisjunction(ValuationRegion regionA,
          ValuationRegion regionB) {
    ValuationRegion resultDisjuncted;
    if(regionA.isEmpty()){
      return regionB;
    }
    if(regionB.isEmpty()){
      return regionA;
    }
    resultDisjuncted = new ValuationRegion();
    resultDisjuncted= checkDisjunctionOneDirection(regionA, regionB,
            resultDisjuncted);
    resultDisjuncted= checkDisjunctionOneDirection(regionB, regionA,
            resultDisjuncted);
    return resultDisjuncted;
  }

  @Override
  public ValuationRegion conjunction(ValuationRegion regionA,
          ValuationRegion regionB) {
    ValuationRegion resultConjuncted = new ValuationRegion();
    for(ValuationEntry entry: regionA.getRegionEntries()){
      Variable entryVar = entry.getVariable();
      Set<ValuationEntry> counterParts = regionB.getValuesForEntry(entryVar);
      if(counterParts == null){
        continue;
      }
      for(ValuationEntry counterPart: counterParts){
        if(counterPart.equals(entry)){
          resultConjuncted.add(entry);
        }
      }
    }
    return resultConjuncted;
  }

  @Override
  public ValuationRegion difference(ValuationRegion outterRegion,
          ValuationRegion excludedRegion) {
    ValuationRegion difference = new ValuationRegion();
    for(ValuationEntry entry: outterRegion.getRegionEntries()){
      Variable entryVar = entry.getVariable();
      Set<ValuationEntry> counterParts = excludedRegion.
              getValuesForEntry(entryVar);
      if(counterParts == null){
        difference.add(entry);
      }
      boolean foundValue = false;
      for(ValuationEntry counterPart: counterParts){
        if(counterPart.equals(entry)){
          foundValue = true;
        }
      }
      if(!foundValue){
        difference.add(entry);
      }
    }
    return difference;
  }

  @Override
  public ValuationRegion exists(ValuationRegion aRegion,
          Set<Variable<?>> subsetOfVariables) {
    ValuationRegion result = new ValuationRegion();
    for(ValuationEntry entry: aRegion.getRegionEntries()){
      if(subsetOfVariables.contains(entry.getVariable())){
        continue;
      }
      result.add(entry);
    }
    return result;
  }

  @Override
  public Set<Variable<?>> convertToVariableSet(ValuationRegion region) {
    Set<Variable<?>> result = new HashSet<Variable<?>>();
    for(ValuationEntry entry: region.getRegionEntries()){
      result.add(entry.getVariable());
    }
    return result;
  }

  @Override
  public ValuationRegion rename(ValuationRegion region,
          List<Variable<?>> oldNames, List<Variable<?>> newNames) {
    ValuationRegion result = new ValuationRegion();
    for(ValuationEntry entry: region.getRegionEntries()){
      Variable potentialCandidateToRename = entry.getVariable();
      if(oldNames.contains(potentialCandidateToRename)){
        int index = oldNames.indexOf(potentialCandidateToRename);
        ValuationEntry renamedEntry = 
                new ValuationEntry(newNames.get(index), entry.getValue());
        result.add(renamedEntry);
      }
    }
    return result;
  }
  
  private ValuationRegion checkDisjunctionOneDirection(ValuationRegion regionA,
        ValuationRegion regionB, ValuationRegion resultDisjuncted){
    outterLoop:
    for(ValuationEntry entry: regionA.getRegionEntries()){
      Variable entryVar = entry.getVariable();
      Set<ValuationEntry> counterParts = regionB.getValuesForEntry(entryVar);
      if(counterParts == null){
        resultDisjuncted.add(entry);
      }
      for(ValuationEntry counterPart: counterParts){
//        System.out.println("gov.nasa.jpf.psyco.search.util.ValuationRegionUtil.checkDisjunctionOneDirection()");
//        System.out.println(counterPart.getVariable().getName() +": " + counterPart.getValue().toString());
//        System.out.println(entry.getVariable().getName() +": " + entry.getValue().toString());
        if(counterPart.equals(entry)){
          continue outterLoop;
        }
        resultDisjuncted.add(entry);
      }
    }
    return resultDisjuncted;
  }
  
  public ValuationRegion createRegion(){
    return new ValuationRegion();
  }
}
