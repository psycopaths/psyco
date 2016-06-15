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
package symbolicsearch.twoStateVariables;
import symbolicsearch.twoStateVariables.*;
import gov.nasa.jpf.jdart.Symbolic;

public class TwoStateVariables {
  @Symbolic("true")
  private int x = 0;
  @Symbolic("true")
  private int y = 5;
  
//  public FirstExample(){
//  }
  
  public void m1(){
    if(x<5){
      x = x + 2;
      y = y + 1;
    }else{
      assert false;
    }
    if(y == 3){
      assert false;
    }else{
      y = y - 3;
    }
  }
}
