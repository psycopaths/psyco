/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;


import gov.nasa.jpf.JPFShell;
import gov.nasa.jpf.Config;
import gov.nasa.jpf.psyco.compiler.Compiler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author falk
 */
public class CompilerExample implements JPFShell {
  
  private Config conf;  
  
  public CompilerExample(Config conf) {
    this.conf = conf;
  }
  
  public static class Param {
    private String name;
    private String type;

    public Param(String type,String name) {
      this.name = name;
      this.type = type;
    }

    public String getName() {
      return name;
    }

    public String getType() {
      return type;
    }
    
  }
  
  public void start(String[] strings) {
   
    Map<String,Object> attributes = new HashMap<String, Object>();
    attributes.put("testpackage", "compiler");
    attributes.put("sutpackage","exampleProtocolSteffen2011");
    attributes.put("sutclass","Protocol");
    attributes.put("methodName","msg");
    
    List<Param> params = new ArrayList<Param>();
    params.add(new Param("int", "P_1"));
    params.add(new Param("int", "P_2"));
    
    attributes.put("params",params);
    
    
    Compiler c = new Compiler(
            "MethodTestCase", 
            attributes, 
            conf, 
            "src/examples/compiler");
    
    c.compile();
    
  }  
  
}
