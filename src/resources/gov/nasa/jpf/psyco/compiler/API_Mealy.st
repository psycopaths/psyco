package <package>;

import gov.nasa.jpf.jdart.Symbolic;

public class <name>_API {

  private static <target.type> <target.name> = null;

  //@Symbolic("true")
  private static boolean init = false;

<methods:{m |  
  public static <m.wrapper> {
    try {
      <m.call>;
    \}
    catch (java.lang.Throwable e) {
      throw new java.lang.RuntimeException(e.getClass().getName());
    \}
  \}  

}>
  
  public static void __reset() {
    init = false;
  \}

\}