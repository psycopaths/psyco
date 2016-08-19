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
package gov.nasa.jpf.psyco.search.datastructures.state;

import gov.nasa.jpf.constraints.api.Expression;
import gov.nasa.jpf.constraints.api.ValuationEntry;
import gov.nasa.jpf.constraints.api.Variable;
import java.util.HashSet;
import java.util.Set;

public abstract class State<T extends ValuationEntry> extends HashSet<T> {

  public Set<T> getEntriesForVariable(Variable var) {
    Set<T> entriesForVariable = new HashSet<>();
    for (T entry : this) {
      if (entry.getVariable().equals(var)) {
        entriesForVariable.add(entry);
      }
    }
    return entriesForVariable;
  }

  public abstract Expression<Boolean> toExpression();

  public abstract State<T> createEmptyState();
}