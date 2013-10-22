/*******************************************************************************
 * Copyright (C) 2008 United States Government as represented by the
 * Administrator of the National Aeronautics and Space Administration
 * (NASA).  All Rights Reserved.
 * 
 * This software is distributed under the NASA Open Source Agreement
 * (NOSA), version 1.3.  The NOSA has been approved by the Open Source
 * Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
 * directory tree for the complete NOSA document.
 * 
 * THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
 * KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
 * LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
 * SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
 * A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
 * THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
 * DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
 ******************************************************************************/
package gov.nasa.jpf.psyco.alphabet;

import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.util.ExpressionUtil;
import gov.nasa.jpf.jdart.config.ConcolicMethodConfig;
import java.util.Objects;

/**
 *
 * @author falkhowar
 */
public class SymbolicMethodSymbol {
  
  private final String id;
  
  private final ConcolicMethodConfig concolicMethodConfig;
  
  private final Expression<Boolean> precondition;

  private final boolean constructor;
  
  private final boolean isStatic;
  
  public SymbolicMethodSymbol(ConcolicMethodConfig cmc, 
          boolean constructor, boolean isStatic) {
    this(cmc.getId(), cmc, constructor, isStatic);
  }
  
  public SymbolicMethodSymbol(String id, ConcolicMethodConfig cmc, 
          boolean constructor, boolean isStatic) {
    this(id, cmc, ExpressionUtil.TRUE, constructor, isStatic);
  }

  public SymbolicMethodSymbol(String id, ConcolicMethodConfig concolicMethodConfig, 
          Expression<Boolean> precondition, boolean constructor, boolean isStatic) {
    this.id = id;
    this.concolicMethodConfig = concolicMethodConfig;
    this.precondition = precondition;
    this.constructor = constructor;
    this.isStatic = isStatic;
  }

  /**
   * @return the id
   */
  public String getId() {
    return this.id;
  }

  /**
   * @return the concolicMethodConfig
   */
  public ConcolicMethodConfig getConcolicMethodConfig() {
    return this.concolicMethodConfig;
  }  

  /**
   * @return the precondition
   */
  public Expression<Boolean> getPrecondition() {
    return precondition;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final SymbolicMethodSymbol other = (SymbolicMethodSymbol) obj;
    if (!Objects.equals(this.id, other.id)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 59 * hash + Objects.hashCode(this.id);
    return hash;
  }

  @Override
  public String toString() {
    return this.id + ":" + this.concolicMethodConfig.getId() + "[" + this.precondition +  "]"; 
  }

  public boolean isConstructor() {
    return constructor;
  }
 
  public boolean isStatic() {
    return isStatic;
  }
  
  public int getArity() {
    return this.concolicMethodConfig.getParams().size();
  }
}
