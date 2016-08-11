/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.util.region;

import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.util.ExpressionUtil;
import gov.nasa.jpf.psyco.search.datastructures.NameMap;
import gov.nasa.jpf.psyco.search.region.Region;
import gov.nasa.jpf.psyco.search.region.SymbolicEntry;
import gov.nasa.jpf.psyco.search.region.SymbolicRegion;
import gov.nasa.jpf.psyco.search.region.SymbolicState;
import java.util.List;
import java.util.Set;

/**
 *
 * @author mmuesly
 */
public class SymbolicRegionUtil extends RegionUtil<SymbolicState, SymbolicRegion>{
  public SymbolicRegionUtil(ConstraintSolver solver){
    super(solver);
  }

   public SymbolicRegion rename(SymbolicRegion region,
          List<Variable<?>> primeNames, List<Variable<?>> variableNames) {
    SymbolicRegion resultingRegion = region.createNewRegion();
     for(String key: region.keySet()){
      SymbolicState state = region.get(key);
      SymbolicState renamedState = renameState(state, primeNames, variableNames);
      resultingRegion.put(key, renamedState);
    }
    return resultingRegion;
  }
  
  private SymbolicState renameState(SymbolicState state, 
          List<Variable<?>> primeNames, List<Variable<?>> variableNames ){
    logger.finest("gov.nasa.jpf.psyco.search.region.util.SymbolicRegionUtil.renameState()");
    logger.finest("stateToRename: " );
    for(SymbolicEntry entry: state){
      logger.finest("Var: " + entry.getVariable() + " : " + entry.getValue());
    }
    Set<Variable<?>> variablesInTheState = 
            ExpressionUtil.freeVariables(state.toExpression());
    logger.finest("gov.nasa.jpf.psyco.search.region"
            + ".util.SymbolicRegionUtil.renameState()");
    logger.finest("State bevor rename: " + state.toExpression().toString());
    for(int i = 0; i < primeNames.size(); i++){
        Variable primeName = primeNames.get(i);
        Variable variableName = variableNames.get(i);
        state = 
                renameAllVariableEntrys(state, primeName, variableName);
        variablesInTheState.remove(variableName);
        variablesInTheState.remove(primeName);
    }
    for(Variable var: variablesInTheState){
      if(!var.getName().startsWith("uVarReplacement")){
        String newParameterName = getUniqueParameterName(var);
        Variable newParameter = 
                new Variable(var.getType(), newParameterName);
        //logger.finest("gov.nasa.jpf.psyco.search.region"
        //        + ".util.SymbolicRegionUtil.renameState()");
        //logger.finest(newParameterName);
        state = 
                renameParameterInEntrys(state, var, newParameter);
      }
    }
    
    logger.finest(" renamed State: " + state);
    return state;
  }
  private SymbolicState renameAllVariableEntrys(
          SymbolicState oldState,  Variable primeName, Variable varibaleName){
    SymbolicState renamedState = new SymbolicState();
    NameMap renameVariable = new NameMap();
    renameVariable.mapNames(varibaleName.getName(), getUniqueName());
    logger.finest("renameMap: " + renameVariable.toString());
    for(SymbolicEntry entry: oldState){
      Expression value = (Expression) entry.getValue();
      value = ExpressionUtil.renameVars(value, renameVariable);
      if(entry.getVariable().equals(primeName)){
        NameMap rename = new NameMap();
        rename.mapNames(primeName.getName(), varibaleName.getName());
        logger.finest("renameMap: " + rename.toString());
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
  private SymbolicState renameParameterInEntrys(SymbolicState renamedState,
          Variable var, Variable newParameter) {
    SymbolicState resultState = new SymbolicState();
    NameMap rename = new NameMap();
    rename.mapNames(var.getName(), newParameter.getName());
    for(SymbolicEntry entry: renamedState){
      Expression valueExpression = entry.getValue();
      valueExpression = ExpressionUtil.renameVars(valueExpression, rename);
      resultState.add(new SymbolicEntry(entry.getVariable(), valueExpression));
    }
    return resultState;
  }

  @Override
  public SymbolicRegion rename(Region<?, SymbolicState> region, List<Variable<?>> primeNames, List<Variable<?>> variableNames) {
    return rename((SymbolicRegion)region, primeNames, variableNames);
  }
}
