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
package gov.nasa.jpf.psyco.alphabet;

import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.util.ExpressionUtil;
import gov.nasa.jpf.jdart.config.ConcolicMethodConfig;
import java.util.Objects;

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

  public SymbolicMethodSymbol(String id,
          ConcolicMethodConfig concolicMethodConfig,
          Expression<Boolean> precondition,
          boolean constructor, boolean isStatic) {
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
    return this.id + ":" + this.concolicMethodConfig.getId() + "[" + this.precondition + "]";
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
