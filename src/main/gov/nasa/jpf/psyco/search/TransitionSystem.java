/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search;

import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.jdart.constraints.Path;
import gov.nasa.jpf.jdart.constraints.PathState;
import gov.nasa.jpf.jdart.constraints.PostCondition;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mmuesly
 */
public class TransitionSystem {
  private ArrayList<Path> transitions;
  private Valuation initValuation;
  private boolean modified;
  private ArrayList<Integer> indexForTransitionWithEffekt;
  
  public TransitionSystem(){
    this.modified = true;
    transitions = new ArrayList<>();
    initValuation = new Valuation();
  }
  
  public TransitionSystem(Valuation initValuation){
    this();
    this.initValuation = initValuation;
  }

  public TransitionSystem(Valuation initValuation, List<Path> transtions){
    this.modified = true;
    this.transitions = new ArrayList<>(transtions);
    this.initValuation = initValuation;
  }
  
  public void add(Path p){
    if(p != null){
      transitions.add(p);
    }
  }

  public List<Path> getTransitions() {
    checkTransitionsForEffekt();
    return transitions;
  }

  public List<Path> getConsideredOKPaths(){
    ArrayList<Path> returnList = new ArrayList<>();
    checkTransitionsForEffekt();
    for(Integer i : indexForTransitionWithEffekt){
      returnList.add(transitions.get(i));
    }
    return returnList;
  }
  public void setTransitions(List<Path> transitions) {
    this.transitions = new ArrayList<>(transitions);
    this.modified = true;
  }

  public Valuation getInitValuation() {
    return initValuation;
  }

  public void setInitValuation(Valuation initValuation) {
    this.initValuation = initValuation;
  }
  
  private void checkTransitionsForEffekt(){
    if(modified){
      this.indexForTransitionWithEffekt = new ArrayList<>();
      for(int i = 0; i < transitions.size(); i++){
        Path p = transitions.get(i);
        if(checkPathForEffekt(p)){
          indexForTransitionWithEffekt.add(i);
        }
      }
      modified = false;
    }
  }

  private boolean checkPathForEffekt(Path p) {
    if(p.getState() == PathState.OK){
      PostCondition transitionEffekt = p.getPostCondition();
      for(Variable key : transitionEffekt.getConditions().keySet()){
        Expression value = transitionEffekt.getConditions().get(key);
        if(value instanceof Variable && value.equals(key)){
          continue;
        }
        return true;
      }
    }
    return false;
  }

  public ArrayList<Path> getConsideredErrorPaths() {
    ArrayList<Path> returnList = new ArrayList<>();
    for(Path p : transitions){
      if(p.getState() ==PathState.ERROR){
        returnList.add(p);
      }
    }
    return returnList;
  }

  @Override
  public String toString() {
    checkTransitionsForEffekt();
    String transitionSystem ="Transition system:\n";
    transitionSystem += "transitions: " + transitions.size() + "\n";
    transitionSystem += "consideredTransitions: " 
            + indexForTransitionWithEffekt.size() + "\n";
//    StringBuilder builder = new StringBuilder();
//    for(Path p: transitions){
//      try {
//        p.print(builder);
//        builder.append("\n");
//      } catch (IOException ex) {
//        Logger.getLogger(TransitionSystem.class.getName()).log(Level.SEVERE, null, ex);
//      }
//    }
//    transitionSystem += builder.toString();
    transitionSystem += "Init Variables: ";
    for(Variable var:  initValuation.getVariables()){
      transitionSystem += var.getName().toString() + ",";
    }
    transitionSystem += "\n\n";
    return transitionSystem;
  }
}
