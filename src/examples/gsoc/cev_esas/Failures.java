/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gsoc.cev_esas;

import gov.nasa.jpf.jdart.Symbolic;
import java.util.EnumSet;

/**
 *
 * @author mmuesly
 */
public class Failures {
  enum Type { EARTH_SENSOR, LAS_CNTRL, CM_MASS, CM_RCS };
  
  @Symbolic("true")
  EnumSet<Type> pending;
  ErrorLog errors;
  
  
  public Failures(ErrorLog errors) {
    pending = EnumSet.noneOf(Type.class);
    this.errors = errors;
  }
  
  //--- actions
  
  public void setLAS_CNTRLfailure() {
    pending.add(Type.LAS_CNTRL);
  }
  
  public void setCM_RCSfailure() {
    pending.add(Type.CM_RCS);
  }
  
  //--- assertions
  
  public boolean noLAS_CNTRLfailure () {
    return !pending.contains(Type.LAS_CNTRL);
  }
  
  public boolean noEARTH_SENSORfailure() {
    return !pending.contains(Type.EARTH_SENSOR);
  }
}