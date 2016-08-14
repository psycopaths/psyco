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
package gov.nasa.jpf.psyco.path.learnlib;

import gov.nasa.jpf.jdart.constraints.Path;
import gov.nasa.jpf.psyco.alphabet.SymbolicMethodSymbol;
import java.util.Objects;

public class PathSymbol {
  
  private final Path path;
  
  private final SymbolicMethodSymbol method;

  public PathSymbol(Path path, SymbolicMethodSymbol method) {
    this.path = path;
    this.method = method;
  }
  
  /**
   * @return the path
   */
  public Path getPath() {
    return path;
  }

  /**
   * @return the method
   */
  public SymbolicMethodSymbol getMethod() {
    return method;
  }

  @Override
  public String toString() {
    return this.method.getId() + "[" + this.path +  "]"; 
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final PathSymbol other = (PathSymbol) obj;
    if (!Objects.equals(this.path, other.path)) {
      return false;
    }
    if (!Objects.equals(this.method, other.method)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 43 * hash + Objects.hashCode(this.path);
    hash = 43 * hash + Objects.hashCode(this.method);
    return hash;
  }

  
}
