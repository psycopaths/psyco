//
// Copyright (C) 2008 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA).  All Rights Reserved.
//
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3.  The NOSA has been approved by the Open Source
// Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
// directory tree for the complete NOSA document.
//
// THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
// KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
// LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
// SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
// A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
// THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
// DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//
package gov.nasa.jpf.psyco.compiler;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.util.JPFLogger;
import gov.nasa.jpf.util.SimpleProfiler;
import java.io.*;
import java.util.Map;
import java.util.Map.Entry;
import org.stringtemplate.v4.ST;

/**
 * Compiles source generated from a string template
 */
public class Compiler {
  
  private String tplName;
  private String targetName;
  private Map<String,Object> attributes;  
  private String tplDir;
  
  private JPFLogger logger = JPF.getLogger("psyco");
  

  public Compiler(String tplName, Map<String, Object> attributes, Config conf, String targetDir) {
    this.tplName = tplName;
    this.attributes = attributes;
    this.targetName = targetDir + "/" +  tplName + ".java";
    this.tplDir = conf.getProperty("jpf-psyco") + "/src/resources/gov/nasa/jpf/psyco/compiler";
  }
  
  public void compile() {
    compile(true);
  }
  
  public void compile(boolean removeSource) {
    
    SimpleProfiler.start("PSYCO-compiler");
    File f = new File(tplDir + "/" + tplName + ".st");
    StringBuilder sb = new StringBuilder();
    BufferedReader r = null;
    try {
      r = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
      String l;
      while ((l = r.readLine()) != null) {
        sb.append(l).append("\n");    
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      try {
        if (r != null) {
          r.close( );
        }
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    ST tpl = new ST(sb.toString());
    
    for (Entry<String,Object> e : attributes.entrySet())
      tpl.add(e.getKey(), e.getValue());
    
    BufferedWriter writer = null;
    try {
      writer = new BufferedWriter(new FileWriter(targetName));
      writer.write(tpl.render());
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      try {
        if (writer != null) {
          writer.close( );
        }
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }      
    }
    try {
      String s = null;
      Process p = Runtime.getRuntime().exec("ant compile-examples");

      BufferedReader stdInput = new BufferedReader(new InputStreamReader(
          p.getInputStream()));

      BufferedReader stdError = new BufferedReader(new InputStreamReader(
          p.getErrorStream()));

      // read the output from the command
      logger.finest("Here is the standard output of the compile command:\n");
      while ((s = stdInput.readLine()) != null) {
        logger.finest(s);
      }

      // read any errors from the attempted command
      logger.finest("Here is the standard error of the compile command (if any):\n");
      while ((s = stdError.readLine()) != null) {
        logger.finest(s);
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } 
    
    if (removeSource) {
      File d = new File(targetName);
      d.delete();
    }
    
    SimpleProfiler.stop("PSYCO-compiler");    
  }      
  
}
