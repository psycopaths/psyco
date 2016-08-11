/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search;

import gov.nasa.jpf.constraints.api.ConstraintSolver;
import gov.nasa.jpf.constraints.api.ConstraintSolver.Result;
import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Valuation;

/**
 *
 * @author mmuesly
 */
public class SolverInstance {
  
  private static SolverInstance instance = null;
  
  private ConstraintSolver solver = null;
  public static SolverInstance getInstance(){
    if(instance == null){
      instance = new SolverInstance();
    }
    return instance;
  }
  
  public void setSolver(ConstraintSolver solver){
    this.solver = solver;
  }
  
  public Result isSatisfiable(Expression expr){
    return solver.isSatisfiable(expr);
  }
  
  public Result solve(Expression expr, Valuation res){
    return solver.solve(expr, res);
  }
}
