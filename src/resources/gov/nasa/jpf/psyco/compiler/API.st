package <package>;

public class <name>_API {

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
  
\}