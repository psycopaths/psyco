/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *The class first Exampel is used to demonstrate the symbolic search setup.
 * Further it is a first Example to demonstate the search's capability.
 * @author mmuesly
 */
package symbolicsearch.twoTransitions;
import symbolicsearch.TwoTransitions.*;
import gov.nasa.jpf.jdart.Symbolic;

public class TwoTransitionExample {
  @Symbolic("true")
  private int x = 0;
  
//  public FirstExample(){
//  }
  
  public void m1(){
    if(x<5){
      x = x + 2;
    }else{
      assert false;
    }
    if(x > 0 && x < 2){
      x = x - 1;
    }else{
      x = x-3;
    }
  }
}
