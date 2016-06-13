/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.region.util;

import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.constraints.api.ConstraintSolver.Result;
import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.expressions.LogicalOperator;
import gov.nasa.jpf.constraints.expressions.Negation;
import gov.nasa.jpf.constraints.expressions.functions.Function;
import gov.nasa.jpf.constraints.util.ExpressionUtil;
import gov.nasa.jpf.psyco.search.collections.NameMap;
import gov.nasa.jpf.psyco.search.region.ExpressionRegion;
import gov.nasa.jpf.psyco.search.region.SymbolicEntry;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author mmuesly
 */
public class ExpressionRegionUtil implements RegionUtil<ExpressionRegion>{

  @Override
  public ExpressionRegion disjunction(ExpressionRegion regionA, ExpressionRegion regionB) {
    ExpressionRegion disjunctedRegion = new ExpressionRegion();
    disjunctedRegion.add(regionA);
    disjunctedRegion.add(regionB);
    return disjunctedRegion;
    //return connectRegionsLogical(LogicalOperator.OR, regionA, regionB);
  }

  private ExpressionRegion connectRegionsLogical(LogicalOperator op,
          ExpressionRegion regionA, ExpressionRegion regionB){
    ExpressionRegion connectedRegion = new ExpressionRegion();
    Set<Variable<?>> stateVariables = convertToVariableSet(regionA);
    stateVariables.addAll(convertToVariableSet(regionB));
    for(Variable aStateVariable: stateVariables){
      List<Expression<Boolean>> expressions = new ArrayList<>();
      expressions = 
              addRegionExpressionToList(aStateVariable, regionA, expressions);
      expressions = 
              addRegionExpressionToList(aStateVariable, regionB, expressions);
      Expression disjunctedValue = ExpressionUtil.combine(op,
              ExpressionUtil.TRUE, expressions);
      connectedRegion.add(new SymbolicEntry(aStateVariable, disjunctedValue));
    }
    return connectedRegion;
  }
  
  private List<Expression<Boolean>> addRegionExpressionToList(Variable var,
          ExpressionRegion region, List<Expression<Boolean>> list){
    Expression valueInRegionA = 
              generateRegionExpressionsForVariable(var, region);
    if(valueInRegionA != null){
      list.add(valueInRegionA);
    }
    return list;
  }
  private Expression<Boolean> generateRegionExpressionsForVariable(Variable var,
          ExpressionRegion region){
    List<Expression<Boolean>> valuesForVariable = 
              region.getValuesForEntryAsList(var);
    return ExpressionUtil.combine(LogicalOperator.OR, null, valuesForVariable);
  }
  @Override
  public ExpressionRegion conjunction(ExpressionRegion regionA, ExpressionRegion regionB) {
    return connectRegionsLogical(LogicalOperator.AND, regionA, regionB);
  }

  @Override
  public ExpressionRegion difference(ExpressionRegion outterRegion,
          ExpressionRegion excludedRegion, ConstraintSolver solver) {
//    for(SymbolicEntry entry: excludedRegion.getRegionEntries()){
//      Expression value = entry.getValue();
//      value = new Negation(value);
//      entry.setValue(value);
//    }
//    return connectRegionsLogical(LogicalOperator.AND,
//            outterRegion, excludedRegion);
    ExpressionRegion result = new ExpressionRegion();
    Expression notRegion = new Negation(excludedRegion.toExpression());
    for(SymbolicEntry entry: outterRegion.getRegionEntries()){
      Expression testDiffState = ExpressionUtil.and(entry.getValue(), notRegion);
      Result rs = solver.isSatisfiable(testDiffState);
      if(rs == Result.SAT){
        result.add(entry);
      }
    }
    return result;
  }

  @Override
  public ExpressionRegion exists(ExpressionRegion aRegion, Set<Variable<?>> subsetOfVariables) {
    ExpressionRegion resultingRegion = new ExpressionRegion();
    for(SymbolicEntry entry: aRegion.getRegionEntries()){
      if(entry != null && entry.getVariable() != null && subsetOfVariables != null && subsetOfVariables.contains(entry.getVariable())){
        continue;
      }
      resultingRegion.add(entry);
    }
    return resultingRegion;
  }

  @Override
  public Set<Variable<?>> convertToVariableSet(ExpressionRegion region) {
    Set<Variable<?>> convertedVariables = new HashSet<>();
    for(SymbolicEntry entry: region.getRegionEntries()){
      convertedVariables.add(entry.getVariable());
    }
    return convertedVariables;
  }

  @Override
  public ExpressionRegion rename(ExpressionRegion region,
          List<Variable<?>> oldNames, List<Variable<?>> newNames) {
    ExpressionRegion resultingRegion = new ExpressionRegion();
    Set<Variable<?>> variablesToBeConsidered = convertToVariableSet(region);
    
    for(int i = 0; i < oldNames.size(); i++){
      Variable oldName = oldNames.get(i);
      Variable newName = newNames.get(i);
      resultingRegion = 
              renameAllVariableEntrys(region,
                      resultingRegion, oldName, newName);
      variablesToBeConsidered.remove(oldName);
    }
    for(Variable notYetConsidered: variablesToBeConsidered){
      resultingRegion = 
              renameAllVariableEntrys(region, resultingRegion,
                      notYetConsidered, notYetConsidered);
    }
    return resultingRegion;
  }

  private static ExpressionRegion renameAllVariableEntrys(
          ExpressionRegion oldRegion,
          ExpressionRegion newRegion, Variable oldName, Variable newName){
    for(SymbolicEntry entry: oldRegion.getRegionEntries()){
        if(entry.getVariable().equals(oldName)){
          Expression value = entry.getValue();
          NameMap rename = new NameMap();
          rename.mapNames(oldName.getName(), newName.getName());
          value = ExpressionUtil.renameVars(value, rename);
          SymbolicEntry newEntry = new SymbolicEntry(newName, value);
          newRegion.add(newEntry);
        }
      }
    return newRegion;
  }
  
  public ExpressionRegion createRegion(){
    return new ExpressionRegion();
  }

  @Override
  public ExpressionRegion difference(ExpressionRegion outterRegion, ExpressionRegion excludedRegion) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}
