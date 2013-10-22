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
package gov.nasa.jpf.psyco.learnlib;

import gov.nasa.jpf.jdart.constraints.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
