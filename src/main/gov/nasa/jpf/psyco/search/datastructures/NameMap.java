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
package gov.nasa.jpf.psyco.search.datastructures;

import com.google.common.base.Function;
import java.util.HashMap;

public class NameMap implements Function<String, String> {

  HashMap<String, String> map;

  public NameMap() {
    map = new HashMap<String, String>();
  }

  public void mapNames(String oldName, String newName) {
    map.put(oldName, newName);
  }

  @Override
  public String apply(String f) {
    return map.getOrDefault(f, f);
  }

  @Override
  public String toString() {
    StringBuilder resultingString = new StringBuilder();
    resultingString.append("[ ");
    for (String key : map.keySet()) {
      String entry = key;
      entry += ": ";
      entry += map.get(key);
      entry += ",";
      resultingString.append(entry);
    }
    resultingString.append("]");
    return resultingString.toString();
  }
}