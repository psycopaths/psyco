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
package gov.nasa.jpf.psyco.path.learnlib;

import gov.nasa.jpf.jdart.constraints.Path;
import gov.nasa.jpf.psyco.alphabet.SymbolicMethodSymbol;
import java.util.Objects;

/**
 *
 * @author falk
 */
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
