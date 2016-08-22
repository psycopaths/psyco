
/*
 * Copyright (C) 2015, United States Government, as represented by the 
 * Administrator of the National Aeronautics and Space Administration.
 * All rights reserved.
 *
 * The PSYCO: A Predicate-based Symbolic Compositional Reasoning environment 
 * platform is licensed under the Apache License, Version 2.0 (the "License"); you 
 * may not use this file except in compliance with the License. You may obtain a 
 * copy of the License at http://www.apache.org/licenses/LICENSE-2.0. 
 *
 * Unless required by applicable law or agreed to in writing, software distributed 
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the 
 * specific language governing permissions and limitations under the License.
 */
package gsoc.cev_esas;

import gov.nasa.jpf.jdart.Symbolic;
import java.util.EnumSet;

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