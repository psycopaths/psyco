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
package gov.nasa.jpf.psyco.search.datastructures.searchImage;

import gov.nasa.jpf.psyco.search.datastructures.region.Region;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A state image is the current search state at a given point of time.
 * At the end of the search, an SearchIterationImage object is returned as search result.
 * @param <T> Any type implementing the abstract Region class.
 */
public abstract class SearchIterationImage<T extends Region> {

  protected int depth = 0;
  protected StringBuilder errors = new StringBuilder();
  protected T reachableStates, newStates, previousNewStates;

  public SearchIterationImage(T reachableStates) {
    this.reachableStates = reachableStates;
  }

  public SearchIterationImage(T reachableStates, StringBuilder errors, int depth) {
    this.reachableStates = reachableStates;
    this.errors = errors;
    this.depth = depth;
  }

  public int getDepth() {
    return depth;
  }

  public void setDepth(int depth) {
    this.depth = depth;
  }

  public void increaseDepth(int amount) {
    this.depth += amount;
    errors.append("\n");
  }

  public String reachableErrorsAsString() {
    return errors.toString();
  }

  public StringBuilder getErrors() {
    return errors;
  }

  public void setErrors(StringBuilder errors) {
    this.errors = errors;
  }

  public void addError(String error, int depth) {
    String errorString = "In: " + depth 
            + " reached the error: " + error + "\n";
    errors.append(errorString);
  }

  public void addErrorInCurrentDepth(String error) {
    addError(error, depth);
  }

  public T getReachableStates() {
    return (T) reachableStates;
  }

  public void setReachableStates(T reachableStates) {
    this.reachableStates = reachableStates;
  }

  public void setNewStates(T newStates) {
    this.newStates = newStates;
  }

  public T getNewStates() {
    return newStates;
  }

  public void addNewStates(T newStates) {
    if (this.newStates == null) {
      this.newStates = newStates;
    } else {
      this.newStates.putAll(newStates);
    }
  }

  public abstract T getPreviousNewStates();

  public void setPreviousNewStates(T previousNewStates) {
    this.previousNewStates = previousNewStates;
  }

  public void print(Appendable searchResultString) throws IOException {
    searchResultString.append("This is the iteration image of depth: ");
    searchResultString.append(Integer.toString(depth));
    searchResultString.append("\n");
    searchResultString.append(
            "In case this iteration image is the search result:\n");
    searchResultString.append("A good choice for k in Psyco would be k = ");
    searchResultString.append(Integer.toString(depth));
    searchResultString.append("\n");
    searchResultString.append("There are: " + reachableStates.size()
            + " states reachable\n");
    searchResultString.append("The reachable states are:\n");
    try {
      reachableStates.print(searchResultString);
    } catch (IOException ex) {
      Logger.getLogger("psyco")
              .log(Level.SEVERE, null, ex);
    }
    searchResultString.append("Further the following errors are reached:\n");
    searchResultString.append(errors);
    searchResultString.append("\niteration image end\n");
  }
}