/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.nasa.jpf.psyco.util;

import gov.nasa.jpf.psyco.search.datastructures.searchImage.SymbolicImage;
import gov.nasa.jpf.psyco.search.transitionSystem.TransitionSystem;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

/**
 *
 * @author mmuesly
 */
public class ResultSaver {
  public static void writeResultToFolder(SymbolicImage result,
          String folder){
    String currentDate = 
            new SimpleDateFormat("yyyyMMddhhmm'.txt'").format(new Date());
    String fileName = folder + "searchResult-" + currentDate;
    try(PrintWriter writer = new PrintWriter(fileName);){
      if(result.getDepth() == Integer.MAX_VALUE){
        writer.println("!!!!The search hit a predefined max search level."
                + " It was interupted!!!!");
      }else{
        writer.println("!!!!The search terminated reaching a fix point!!!!");
      }
      result.print(writer);
    } catch (IOException ex) {
      Logger.getLogger("psyco").severe(ex.getStackTrace().toString());
    }
  }

  public static void writeResultToFolder(SymbolicImage result,
          TransitionSystem transitionSystem, String folder){
    String currentDate = 
            new SimpleDateFormat("yyyyMMddhhmm'.txt'").format(new Date());
    String fileName = folder + "searchResult-" + currentDate;
    try(PrintWriter writer = new PrintWriter(fileName);){
      if(result.getDepth() == Integer.MAX_VALUE){
        writer.println("!!!!The search hit a predefined max search level."
                + " It was interupted!!!!");
      }else{
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
