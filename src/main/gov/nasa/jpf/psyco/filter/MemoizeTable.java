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
package gov.nasa.jpf.psyco.filter;

import gov.nasa.jpf.psyco.learnlib.SymbolicQueryOutput;
import java.util.TreeMap;

/**
 * Stores results that have already been computed so they can be retrieved later
 */
public class MemoizeTable {
  // The MemoizeTable stores the data in a tree format.  This way, if
  // a sequence has a prefix that is known to violate, we can easily
  // detect that and say that the sequence will violate without doing
  // simulating the sequence on the LTS system.

  /**
   * The children of this node in the table
   */
  private TreeMap<String, MemoizeTable> children_;

  /**
   * The result for this node in the table, or null if it has not been computed
   * yet.
   */
  private SymbolicQueryOutput isViolating_;

  /**
   * Creates a new MemoizeTable
   */
  public MemoizeTable() {
    children_ = new TreeMap<>();
    isViolating_ = null;
  }

  /**
   * Stores a result in the table
   *
   * @param sequence the sequence
   * @param result the result
   */
  public void setResult(String[] sequence, SymbolicQueryOutput result) {
    this.setResult(sequence, result, 0);
  }

  /**
   * Stores a result in the table
   *
   * @param sequence the sequence
   * @param result the result
   * @param position the depth in the tree this node is at
   */
  private void setResult(String[] sequence, SymbolicQueryOutput result,
          int position) {
    if (sequence.length == position) {
      // We have reached the correct point
      isViolating_ = result;
    } else {
      // We need to go deeper
      String action = sequence[position];
      MemoizeTable child = children_.get(action);
      if (child == null) {
        child = new MemoizeTable();
        children_.put(action, child);
      }

      child.setResult(sequence, result, position + 1);
    }
  }

  /**
   * Retrieves a result from the table
   *
   * @param sequence the sequence
   *
   * @return the stored result, or null if the result has not been stored
   */
  public SymbolicQueryOutput getResult(String[] sequence) {
    return (this.getResult(sequence, 0));
  }

  public SymbolicQueryOutput getSimulatedResult(String[] sequence) {
    return (this.getSimulatedResult(sequence, 0));
  }

  /**
   * Retrieves a result from the table
   *
   * @param sequence the sequence
   * @param position the depth in the tree this node is at
   *
   * @return the stored result, or null if the result has not been stored
   */
  private SymbolicQueryOutput getResult(String[] sequence, int position) {
    if (sequence.length == position) {
      // We have reached the maximum depth
      return (isViolating_);
    } else // We _MAY_ need to go deeper.  If the sequence is violating,
    // we can stop here
    if ((isViolating_ != null)
            && ((isViolating_ == SymbolicQueryOutput.DONT_KNOW)
            || (isViolating_ == SymbolicQueryOutput.ERROR))) {
      return (isViolating_);
    } else {
      // Go deeper if we can
      String action = (String) sequence[position];
      MemoizeTable child = children_.get(action);
      if (child == null) {
        // We have no knowledge that can help
        return (null);
      } else {
        return (child.getResult(sequence, position + 1));
      }
    }
  }

  // This is in order to check also based on the alphabet 
  // refinement in order to reuse previous rounds of queries
  private SymbolicQueryOutput getSimulatedResult(String[] sequence, int position) {
    if (sequence.length == position) {
      // We have reached the maximum depth
      return (isViolating_);
    } else // We _MAY_ need to go deeper.  If the sequence is violating,
    // we can stop here
    if ((isViolating_ != null)
            && ((isViolating_ == SymbolicQueryOutput.DONT_KNOW)
            || (isViolating_ == SymbolicQueryOutput.ERROR))) {
      return (isViolating_);
    } else {
      // Go deeper if we can
      String action = (String) sequence[position];
      String[] parts = action.split("_");
      String root = "";

      MemoizeTable child = null;
      boolean check = false; // tracks first string

      int nextPos = position + 1;
      SymbolicQueryOutput result = null;

      for (String p : parts) {
        root = root.concat(p);
        if (check) {
          child = (MemoizeTable) children_.get(root);
          if (child != null) {
            result = child.getSimulatedResult(sequence, nextPos);
            if (result != null) {
              return (result); // otherwise see if other child has a stored sequence
            }
          }
        }
        root = root.concat("_");
        check = true;
      }
      return result; // at this point it will always be null

      // otherwise we have no knowledge that may help 
    }
  }
}