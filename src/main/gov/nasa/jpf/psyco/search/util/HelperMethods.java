/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.search.util;

/**
 *
 * @author mmuesly
 */
public class HelperMethods {
  private static long currentCounter = 0;
  private static long stateCounter = 0;
  
  public static String getUniqueStateName(){
    String stateName = "state_" + stateCounter;
    ++stateCounter;
    return stateName;
  }
}
