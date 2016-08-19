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
package gov.nasa.jpf.psyco.util;

import gov.nasa.jpf.psyco.search.datastructures.searchImage.StateImage;
import gov.nasa.jpf.psyco.search.transitionSystem.TransitionSystem;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Saves results to a file.
 */
public class ResultSaver {

  public static void writeResultToFolder(StateImage result,
          String folder, String prefix) {
    String currentDate
            = new SimpleDateFormat("yyyyMMddhhmm'.txt'").format(new Date());
    String fileName = folder + prefix + "searchResult-" + currentDate;
    try (PrintWriter writer = new PrintWriter(fileName);) {
      if (result.getDepth() == Integer.MAX_VALUE) {
        writer.println("!!!!The search hit a predefined max search level."
                + " It was interupted!!!!");
      } else {
        writer.println("!!!!The search terminated reaching a fix point!!!!");
      }
      result.print(writer);
    } catch (IOException ex) {
      Logger.getLogger("psyco").severe(ex.getStackTrace().toString());
    }
  }

  public static void writeResultToFolder(StateImage result,
          TransitionSystem transitionSystem, String folder, String prefix) {
    String currentDate
            = new SimpleDateFormat("yyyyMMddhhmm'.txt'").format(new Date());
    String fileName = folder + prefix + "searchResult-" + currentDate;
    try (PrintWriter writer = new PrintWriter(fileName);) {
      if (result.getDepth() == Integer.MAX_VALUE) {
        writer.println("!!!!The search hit a predefined max search level."
                + " It was interupted!!!!");
      } else {
        writer.println("!!!!The search terminated reaching a fix point!!!!");
      }
      result.print(writer);
      writer.println("\n");
      writer.println("The following statistic is provided"
              + "by the transition system:");
      writer.println(transitionSystem.getExecutionStatistics());
    } catch (IOException ex) {
      Logger.getLogger("psyco").severe(ex.getStackTrace().toString());
    }
  }
}