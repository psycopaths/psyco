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

import gov.nasa.jpf.util.JPFLogger;
import gov.nasa.jpf.util.SimpleProfiler;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
/**
 * An adadpted version of the SimpleProfiler to collect execution 
 * insights on the search behavior.
 * Calls to SimpleProfiler are also included in the PsycoProfiler result.
 */
public class PsycoProfiler extends SimpleProfiler {

  private static HashMap<Integer, Long> cumulatedGuardTime =
          new HashMap<>();
  private static HashMap<Integer, Long> cumulatedDiffTime =
          new HashMap<>();
  private static HashMap<Integer, Long> cumulatedTransitionTime =
          new HashMap<>();
  private static HashMap<Integer, Long> cumulatedRenamingTime =
          new HashMap<>();
  private static HashMap<Integer, Long> transitionProfiler =
          new HashMap<>();
  private static HashMap<Integer, Long> guardProfiler =
          new HashMap<>();
  private static HashMap<Integer, Long> differenceProfiler =
          new HashMap<>();
  private static HashMap<Integer, Long> renamingTimer =
          new HashMap<>();
  private static HashMap<Integer, Integer> newStates =
          new HashMap<>();

  public static void startTransitionProfiler(int depth) {
    if (!SimpleProfiler.PROFILE) {
      return;
    }
    long currentTimeStamp = System.currentTimeMillis();
    transitionProfiler.put(depth, currentTimeStamp);
  }

  public static void stopTransitionProfiler(int depth) {
    if (transitionProfiler.containsKey(depth)) {
      long currentTimeStamp = System.currentTimeMillis();
      long startTimeStamp = transitionProfiler.get(depth);
      long diff = currentTimeStamp - startTimeStamp;
      if (cumulatedTransitionTime.containsKey(depth)) {
        diff += cumulatedTransitionTime.get(depth);
      }
      cumulatedTransitionTime.put(depth, diff);
    }
  }

  public static void startRenamingProfiler(int depth) {
    if (!SimpleProfiler.PROFILE) {
      return;
    }
    long currentTimeStamp = System.currentTimeMillis();
    renamingTimer.put(depth, currentTimeStamp);
  }

  public static void stopRenamingProfiler(int depth) {
    if (renamingTimer.containsKey(depth)) {
      long currentTimeStamp = System.currentTimeMillis();
      long startTimeStamp = renamingTimer.get(depth);
      long diff = currentTimeStamp - startTimeStamp;
      if (cumulatedRenamingTime.containsKey(depth)) {
        diff += cumulatedRenamingTime.get(depth);
      }
      cumulatedRenamingTime.put(depth, diff);
    }
  }

  public static void startGuardProfiler(int depth) {
    if (!SimpleProfiler.PROFILE) {
      return;
    }
    long currentTimeStamp = System.currentTimeMillis();
    guardProfiler.put(depth, currentTimeStamp);
  }

  public static void stopGuardProfiler(int depth) {
    if (guardProfiler.containsKey(depth)) {
      long currentTimeStamp = System.currentTimeMillis();
      long startTimeStamp = guardProfiler.get(depth);
      long diff = currentTimeStamp - startTimeStamp;
      if (cumulatedGuardTime.containsKey(depth)) {
        diff += cumulatedGuardTime.get(depth);
      }
      cumulatedGuardTime.put(depth, diff);
    }
  }

  public static void startDiffProfiler(int depth) {
    if (!SimpleProfiler.PROFILE) {
      return;
    }
    long currentTimeStamp = System.currentTimeMillis();
    differenceProfiler.put(depth, currentTimeStamp);
  }

  public static void stopDiffProfieler(int depth) {
    if (differenceProfiler.containsKey(depth)) {
      long currentTimeStamp = System.currentTimeMillis();
      long startTimeStamp = differenceProfiler.get(depth);
      long diff = currentTimeStamp - startTimeStamp;
      if (cumulatedDiffTime.containsKey(depth)) {
        diff += cumulatedDiffTime.get(depth);
      }
      cumulatedDiffTime.put(depth, diff);
    }
  }

  public static void newStates(int depth, int stateAmount) {
    newStates.put(depth, stateAmount);
  }

  public static void reset() {
    cumulatedGuardTime = new HashMap<>();
    cumulatedDiffTime = new HashMap<>();
    cumulatedTransitionTime = new HashMap<>();
    cumulatedRenamingTime = new HashMap<>();
    transitionProfiler = new HashMap<>();
    guardProfiler = new HashMap<>();
    differenceProfiler = new HashMap<>();
    renamingTimer = new HashMap<>();
    newStates = new HashMap<>();
  }

  public static String getResults() {
    String simpleProfilerResult = SimpleProfiler.getResults();
    StringBuilder thisResult = new StringBuilder(simpleProfilerResult);
    thisResult.append("Search specific timing\n");
    int sum = 0;
    for (Entry<Integer, Long> e : cumulatedGuardTime.entrySet()) {
      addEntryToString(e, thisResult, "guardTime-");
      sum += e.getValue();
    }
    thisResult.append("total time for guard checking: " + sum + " ms\n");
    sum = 0;
    for (Entry<Integer, Long> e : cumulatedDiffTime.entrySet()) {
      thisResult = addEntryToString(e, thisResult, "differnceTime-");
      sum += e.getValue();
    }
    thisResult.append("total time for difference: " + sum + " ms\n");
    sum = 0;
    for (Entry<Integer, Long> e : cumulatedTransitionTime.entrySet()) {
      thisResult = addEntryToString(e, thisResult, "transition-");
      sum += e.getValue();
    }
    thisResult.append("total time for transition enrollment: " 
            + sum + " ms\n");
    sum = 0;
    for (Entry<Integer, Long> e : cumulatedRenamingTime.entrySet()) {
      thisResult = addEntryToString(e, thisResult, "renaming-");
      sum += e.getValue();
    }
    thisResult.append("total time for renaming: " + sum + " ms\n");

    thisResult.append("\nStates enrollment over time\n");
    for (Entry<Integer, Integer> e : newStates.entrySet()) {
      thisResult.append("depth : " + e.getKey() + " has added: "
              + e.getValue() + " states\n");
    }
    return thisResult.toString();
  }

  private static StringBuilder addEntryToString(Entry<Integer, Long> e,
          StringBuilder builder, String prefix) {
    builder.append(prefix).append(e.getKey());
    builder.append(": ");
    builder.append(e.getValue());
    builder.append("\n");
    return builder;
  }

  public static void writeRunToFolder(String folderName) {
    writeRunToFolder(folderName, null);
  }

  public static void writeRunToFolder(String folderName, String prefix) {
    String currentDateSuffix
            = new SimpleDateFormat("yyyyMMddhhmm'.csv'").format(new Date());
    if(prefix == null){
      prefix = "";
    }
    String fileName = folderName + prefix + "guardTimes-" + currentDateSuffix;
    writeResultToFile(cumulatedGuardTime, fileName);
    fileName = folderName + prefix + "differenceTimes-" + currentDateSuffix;
    writeResultToFile(cumulatedDiffTime, fileName);
    fileName = folderName + prefix + "transitionTimes-" + currentDateSuffix;
    writeResultToFile(cumulatedTransitionTime, fileName);
    fileName = folderName + prefix + "renamingTimes-" + currentDateSuffix;
    writeResultToFile(cumulatedRenamingTime, fileName);
    fileName = folderName + prefix + "profilerOverview-" + currentDateSuffix;
    writeResultToFile(getResults(), fileName);
  }

  private static void writeResultToFile(
          HashMap<Integer, Long> map, String filename) {
    try (PrintWriter writer = new PrintWriter(filename)) {
      writer.println("#depth\ttime\tdimensionOfTime");
      for (Entry<Integer, Long> e : map.entrySet()) {
        writer.println(e.getKey() + "\t" + e.getValue() + "\tms");
      }
    } catch (IOException e) {
      JPFLogger.getLogger("psyco").severe(e.getStackTrace().toString());
    }

  }

  private static void writeResultToFile(String content, String filename) {
    try (PrintWriter writer = new PrintWriter(filename)) {
      writer.print(content);
    } catch (IOException e) {
      JPFLogger.getLogger("psyco").severe(e.getStackTrace().toString());
    }
  }
}