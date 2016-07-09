/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.region;

import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.constraints.api.ConstraintSolver.Result;
import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.api.ValuationEntry;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.constraints.expressions.Constant;
import gov.nasa.jpf.constraints.expressions.NumericBooleanExpression;
import gov.nasa.jpf.constraints.expressions.NumericComparator;
import gov.nasa.jpf.constraints.expressions.NumericCompound;
import gov.nasa.jpf.constraints.types.BuiltinTypes;
import gov.nasa.jpf.constraints.util.ExpressionUtil;
import gov.nasa.jpf.psyco.search.SolverInstance;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mmuesly
 */
public class SymbolicEntry extends ValuationEntry<Expression<Boolean>>{

  public SymbolicEntry(Variable<Expression<Boolean>> variable, Expression<Boolean> value) {
    super(variable, value);
  }
  @Override
  public boolean equals(Object obj) {
    if(obj instanceof SymbolicEntry){
      SymbolicEntry testEntry = (SymbolicEntry) obj;
      if(testEntry.getVariable().equals(this.getVariable())){
        Expression test = new NumericBooleanExpression(
                testEntry.getValue() , NumericComparator.EQ, this.getValue());
        System.out.println("gov.nasa.jpf.psyco.search.region.SymbolicEntry.equals()");
        System.out.println("this Value: " + this.getValue());
        System.out.println("testEntryValue: " + testEntry.getValue());
        Result res = SolverInstance.getInstance().isSatisfiable(test);
        System.out.println("Solver Res: " + res);
        if (res == Result.SAT){
          return true;
        }else if(res == Result.DONT_KNOW){
          try {
            throw new Exception("Cannot decide Equality");
          } catch (Exception ex) {
            Logger.getLogger(SymbolicEntry.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(42);
          }
        }
      }
    }
    return false;
  }
  
  public static SymbolicEntry create(ValuationEntry entry){
    Variable entryVariable = entry.getVariable();
    Expression entryValue = 
            new Constant(entryVariable.getType(), entry.getValue());
    Expression<Boolean> value = 
            new NumericBooleanExpression(entryVariable,
                    NumericComparator.EQ, entryValue);
    return new SymbolicEntry(entryVariable, value);
  }
}
