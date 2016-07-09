/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.transitionSystem;

import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.Valuation;
import gov.nasa.jpf.constraints.api.Variable;
import gov.nasa.jpf.jdart.constraints.Path;
import gov.nasa.jpf.jdart.constraints.PathState;
import gov.nasa.jpf.jdart.constraints.PostCondition;
import gov.nasa.jpf.psyco.search.collections.StateImage;
import gov.nasa.jpf.psyco.search.collections.SymbolicImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mmuesly
 */
public class TransitionSystem<T extends TransitionHelper> {
  private List<Transition> transitions;
  private Valuation initValuation;
  private T helper;
  public TransitionSystem(T helper){
    transitions = new ArrayList<>();
    initValuation = new Valuation();
    this.helper = helper;
  }
  
  public TransitionSystem(Valuation initValuation, T helper){
    this(helper);
    this.initValuation = initValuation;
  }

  public TransitionSystem(Valuation initValuation, List<Path> paths, T helper){
    this(helper);
    this.transitions = convertPathsToTransitions(paths);
    this.initValuation = initValuation;
  }
  
  public void add(Path p){
    if(p != null){
      Transition t = new Transition(p);
      transitions.add(t);
    }
  }

  public List<Path> getTransitions() {
    ArrayList<Path> paths = new ArrayList<>();
    for (Transition t: transitions){
      paths.add(t.getPath());
    }
    return paths;
  }

  public List<Path> getConsideredOKPaths(){
    ArrayList<Path> returnList = new ArrayList<>();
    for(Transition t : transitions){
      if(t.isOK()){
        if(t.isStutterTransition()){
          continue;
        }
        returnList.add(t.getPath());
      }
    }
    return returnList;
  }
  public void setTransitions(List<Path> transitions) {
    this.transitions = convertPathsToTransitions(transitions);
  }

  public Valuation getInitValuation() {
    return initValuation;
  }

  public void setInitValuation(Valuation initValuation) {
    this.initValuation = initValuation;
  }

  public ArrayList<Path> getConsideredErrorPaths() {
    ArrayList<Path> returnList = new ArrayList<>();
    for(Transition t : transitions){
      Path p = t.getPath();
      if(p.getState() ==PathState.ERROR){
        returnList.add(p);
      }
    }
    return returnList;
  }

  public boolean isLimited(){
    boolean limited = true;
    for(Transition t: transitions){
      if(!t.isLimitedTransition()){
        System.out.println("gov.nasa.jpf.psyco.search.transitionSystem.TransitionSystem.isLimited()");
        System.out.println("unlimitedTransition: " + t.getPath().toString());
        limited = false;
      }
    }
    return limited;
  }
  private List<Transition> convertPathsToTransitions(List<Path> paths){
    ArrayList<Transition> tmpTransitions = new ArrayList();
    for(Path p: paths){
      tmpTransitions.add(new Transition(p));
    }
    return tmpTransitions;
  }
  
  private List<Path> getStutterPaths(){
    ArrayList<Path> returnList = new ArrayList<>();
    for(Transition t : transitions){
      if(t.isOK()){
        if(t.isStutterTransition()){
          returnList.add(t.getPath());
        }
      }
    }
    return returnList;
  }
  @Override
  public String toString() {
    String transitionSystem ="Transition system:\n";
    transitionSystem += "transitions: " + transitions.size() + "\n";
    List<Path> okPaths = getConsideredOKPaths();
    List<Path> stutterPath = getStutterPaths();
    List<Path> errors = getConsideredErrorPaths();
    transitionSystem += "consideredTransitions: " 
            + getConsideredOKPaths().size() + "\n";
    transitionSystem += "stutterTransitions: " 
            + stutterPath.size() + "\n";
    transitionSystem += "errorTransitions: " 
            + errors.size() + "\n";
    transitionSystem += "OkPaths:\n";
    transitionSystem += convertPathListToString(okPaths);
    transitionSystem += "stutterPaths:\n";
    transitionSystem += convertPathListToString(stutterPath);
    transitionSystem += "ErrorPaths:\n";
    transitionSystem += convertPathListToString(errors);
    transitionSystem += "Init Variables: ";
    for(Variable var:  initValuation.getVariables()){
      transitionSystem += var.getName() + ",";
    }
    transitionSystem += "\n\n";
    return transitionSystem;
  }
  private String convertPathListToString(List<Path> paths){
    StringBuilder builder = new StringBuilder();
    for(Path p: paths){
      try {
        p.print(builder);
        builder.append("\n");
      } catch (IOException ex) {
        Logger.getLogger(TransitionSystem.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    return builder.toString();
  }

  public StateImage applyOn(StateImage alreadyReachedStates) {
    for(Transition t: transitions){
      alreadyReachedStates = t.applyOn(alreadyReachedStates, helper);
    }
    alreadyReachedStates.increaseDepth(1);
    return alreadyReachedStates;
  }
}
