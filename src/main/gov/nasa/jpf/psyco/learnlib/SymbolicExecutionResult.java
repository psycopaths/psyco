/**
 * *****************************************************************************
 * Copyright (C) 2008 United States Government as represented by the
 * Administrator of the National Aeronautics and Space Administration (NASA).
 * All Rights Reserved.
 *
 * This software is distributed under the NASA Open Source Agreement (NOSA),
 * version 1.3. The NOSA has been approved by the Open Source Initiative. See
 * the file NOSA-1.3-JPF at the top of the distribution directory tree for the
 * complete NOSA document.
 *
 * THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY KIND,
 * EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT LIMITED TO, ANY
 * WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO SPECIFICATIONS, ANY
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
 * FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL BE
 * ERROR FREE, OR ANY WARRANTY THAT DOCUMENTATION, IF PROVIDED, WILL CONFORM TO
 * THE SUBJECT SOFTWARE.
 *****************************************************************************
 */
package gov.nasa.jpf.psyco.learnlib;

import gov.nasa.jpf.jdart.constraints.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author falk
 */
public class SymbolicExecutionResult implements Iterable<Path> {

  private final Collection<Path> ok;
  private final Collection<Path> error;
  private final Collection<Path> dontKnow;

  public SymbolicExecutionResult(Collection<Path> ok, Collection<Path> error, Collection<Path> dontKnow) {
    this.ok = ok;
    this.error = error;
    this.dontKnow = dontKnow;
  }

  public Collection<Path> getOk() {
    return ok;
  }

  public Collection<Path> getError() {
    return error;
  }

  public Collection<Path> getDontKnow() {
    return dontKnow;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (Path p : getOk()) {
      sb.append(p).append("\n");
    }
    for (Path p : getError()) {
      sb.append(p).append("\n");
    }
    for (Path p : getDontKnow()) {
      sb.append(p).append("\n");
    }
    return sb.toString();
  }

  @Override
  public Iterator<Path> iterator() {
    List<Path> allpaths = new ArrayList<>();
    allpaths.addAll(this.ok);
    allpaths.addAll(this.error);
    allpaths.addAll(this.dontKnow);
    return allpaths.iterator();
  }  

}
