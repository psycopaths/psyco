
package <testpackage>;

import gov.nasa.jpf.jdart.Symbolic;
import <sutpackage>.<sutclass>;

public class MethodTestCase {

  <params:{p | 
  @Symbolic("true")
  public static <p.type> <p.name>;  
  }> 
          
  public static void main(String[] args) {
    // has to have default constructor ...
    <sutclass> testObject = new <sutclass>();
    testObject.<methodName>(
      <trunc(params):{p | <p.name>, 
      }> 
      <if(params)> <last(params).name> <endif>
    );
  }
} 
  
