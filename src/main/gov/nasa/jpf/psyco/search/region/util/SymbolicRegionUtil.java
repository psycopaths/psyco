/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.region.util;

import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.expressions.Negation;
import gov.nasa.jpf.constraints.util.ExpressionUtil;
import gov.nasa.jpf.psyco.search.collections.NameMap;
import gov.nasa.jpf.psyco.search.region.ExpressionRegion;
import gov.nasa.jpf.psyco.search.region.SymbolicEntry;
import gov.nasa.jpf.psyco.search.region.SymbolicRegion;
import gov.nasa.jpf.psyco.search.region.SymbolicState;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mmuesly
 */
public class SymbolicRegionUtil implements RegionUtil<SymbolicRegion>{
  private long unique;
  ConstraintSolver solver;
  public SymbolicRegionUtil(ConstraintSolver solver){
    this.solver = solver;
    this.unique = 1L;
  }
  @Override
  public SymbolicRegion disjunction(SymbolicRegion regionA, SymbolicRegion regionB) {
    SymbolicRegion disjunctedRegion = new SymbolicRegion();
    disjunctedRegion.putAll(regionA);
    for(String key: regionB.keySet()){
      //This assumes, that state names are unique to work!
      if(disjunctedRegion.containsKey(key)){
        continue;
      }
      disjunctedRegion.put(key, regionB.get(key));
    }
    return disjunctedRegion;
  }

  @Override
  public SymbolicRegion conjunction(SymbolicRegion regionA,
          SymbolicRegion regionB) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public SymbolicRegion difference(SymbolicRegion outterRegion, SymbolicRegion excludedRegion) {
    return difference(outterRegion, excludedRegion, this.solver);
  }

  @Override
  public SymbolicRegion difference(SymbolicRegion outterRegion, SymbolicRegion excludedRegion, ConstraintSolver solver) {
    SymbolicRegion result = new SymbolicRegion();
    Expression notRegion = new Negation(excludedRegion.toExpression());
    for(String key : outterRegion.keySet()){
        SymbolicState state = outterRegion.get(key);
        Expression testDiffState = ExpressionUtil.and(state.toExpression(), notRegion);
        ConstraintSolver.Result rs = solver.isSatisfiable(testDiffState);
        if(rs == ConstraintSolver.Result.SAT){
          result.put(key, state);
        }
    }
    return result;
  }

  @Override
  public SymbolicRegion exists(SymbolicRegion aRegion, Set<Variable<?>> subsetOfVariables) {
    SymbolicRegion existingRegion = new SymbolicRegion();
    for(String key: aRegion.keySet()){
      SymbolicState state = aRegion.get(key);
      SymbolicState newState = new SymbolicState();
      for(SymbolicEntry entry: state){
        if(subsetOfVariables.contains(entry.getVariable())){
          continue;
        }
        newState.add(entry);
      }
      if(!newState.isEmpty()){
        existingRegion.put(key, newState);
      }
    }
    return existingRegion;
  }

  @Override
  public Set<Variable<?>> convertToVariableSet(SymbolicRegion region) {
    Set<Variable<?>> resultingSet = new HashSet<>();
    for(SymbolicState state: region.values()){
      for(SymbolicEntry entry: state){
        resultingSet.add(entry.getVariable());
      }
    }
    return resultingSet;
  }

  public Set<Variable<?>> convertToVariableSet(SymbolicState state) {
    Set<Variable<?>> resultingSet = new HashSet<>();
    for(SymbolicEntry entry: state){
      resultingSet.add(entry.getVariable());
    }
    return resultingSet;
  }
  
  @Override
  public SymbolicRegion rename(SymbolicRegion region,
          List<Variable<?>> primeNames, List<Variable<?>> variableNames) {
      SymbolicRegion resultingRegion = new SymbolicRegion();
    for(String key: region.keySet()){
      SymbolicState state = region.get(key);
      SymbolicState renamedState = renameState(state, primeNames, variableNames);
      resultingRegion.put(key, renamedState);
    }
    return resultingRegion;
  }
  
  private SymbolicState renameState(SymbolicState state, 
          List<Variable<?>> primeNames, List<Variable<?>> variableNames ){
    SymbolicState renamedState = state;
    for(int i = 0; i < primeNames.size(); i++){
        Variable primeName = primeNames.get(i);
        Variable variableName = variableNames.get(i);
        renamedState = 
                renameAllVariableEntrys(renamedState, primeName, variableName);
      }
    return renamedState;
  }
  private SymbolicState renameAllVariableEntrys(
          SymbolicState oldState, Variable primeName, Variable varibaleName){
    SymbolicState renamedState = new SymbolicState();
    NameMap renameVariable = new NameMap();
    renameVariable.mapNames(varibaleName.getName(), getUniqueName());
    for(SymbolicEntry entry: oldState){
      Expression value = entry.getValue();
      value = ExpressionUtil.renameVars(value, renameVariable);
      if(entry.getVariable().equals(primeName)){
        NameMap rename = new NameMap();
        rename.mapNames(primeName.getName(), varibaleName.getName());
        value = ExpressionUtil.renameVars(value, rename);
        SymbolicEntry newEntry = new SymbolicEntry(varibaleName, value);
        renamedState.add(newEntry);
      }else{
        entry.setValue(value);
        renamedState.add(entry);
      }
    }
    return renamedState;
  }

  @Override
  public SymbolicRegion createRegion() {
    return new SymbolicRegion();
  }
  
  private String getUniqueName(){
    String uniqueName = "uVarReplacement_" + unique;
    ++unique;
    return uniqueName;
  }
}
