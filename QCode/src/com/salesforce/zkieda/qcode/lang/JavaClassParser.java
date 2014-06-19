package com.salesforce.zkieda.qcode.lang;

import com.salesforce.zkieda.qcode.server.JavaOutputPath;


/**
 * used to parse a string in the format<br/>
 * <pre>
 * @code{
 *    path/to/file/java.package.path.JavaClassName
 * }
 * </pre><br/>
 * into the parts {@code path/to/file/}, {@code java.package.path}, and {@code JavaClassName}. 
 * This class also verifies that each component is good.<br/><br/>
 *
 * With a bit more description without getting too formal, the path should follow the pattern 
 * <pre>
 * @code{
 *    (absolute or relative filepath that ends in "/") (series of packages ends in ".") (valid java class name)
 * }
 * </pre>
 * 
 */
public class JavaClassParser implements JavaOutputPath{
   private String filePath, javaPackage, javaClass;

   /** 
    * immediately parses the string
    * @param outClassPath the string we are parsing
    * @throws NullPointerException if the string is {@code null}
    * @throws IllegalArgumentException if the string is not in the desired 
    * format. Note that this class does not validate if the java class or 
    * files are properly named.
    * @note we do not check if the java class names or the file path names 
    * are actually valid. Check for invalid naming errors later!
    */
   public JavaClassParser(String outClassPath){
       
	   int lastSlash = -1;
	   int lastDot = -1;
	   int len = outClassPath.length();
	   for(int i = len-1; i >= 0; i--){
		   char c = outClassPath.charAt(i);
		   if(c=='/' && lastSlash < 0){
			   lastSlash = i;
		   } else if(c=='.' && (lastSlash&lastDot) < 0){
			   //lastSlash&lastDot since file could have a '.' in it
			   lastDot = i;
		   }
		   
		   if((lastDot|lastSlash)>=0){
			   break;
		   }
	   }
	   
	   //prefer to trim off at last '.'
	   //otherwise trim off at last '/'
	   //otherwise 0
	   int classNamePos = ((~(lastDot >> 31) | lastSlash) & lastDot); 
	   
	   // ((~(lastDot >> 31) | lastSlash) & lastDot)
	   //
	   // if lastDot = -1, lastSlash >= 0, we have lastSlash       
	   //                                  we want lastSlash
	   // if lastDot >= 0, lastSlash = -1, we have lastDot
	   //                                  we want lastDot
	   // if lastDot >= 0, lastSlash >= 0, we have lastDot
	   //		                           we want lastDot
	   // if lastDot = -1, lastSlash = -1, we have -1
	   //                                  we want -1
	   
	   if(classNamePos + 1 == len){
		   //no room for the class name
		   throw new IllegalArgumentException(msg(outClassPath, 
				   String.format("No class name; About idx %d", classNamePos)
			   ));
	   }
	   
	   //class name
	   javaClass = outClassPath.substring(classNamePos+1, len);
	   
	   
	   
	   //end is lastSlash if lastSlash >= 0
	   //is -1 otherwise
	   filePath = outClassPath.substring(0, lastSlash+1);
	   
	   /** 
	    * the next block : {@code i} is {@code 0} if 
	    * {@code lastSlash == classNamePos}. Otherwise, {@code i} is {@code 1}
	    */
	   //both will be 0 when they eq
	   //one will be negative when they do not.
	   int i = (lastSlash - classNamePos) | (classNamePos - lastSlash);
	   i = i >> 31; // if not eq, -1. Otherwise, 0
	   i = i + 1;   // if not eq, 0. Otherwise 1.
	   
	   
	   javaPackage = outClassPath.substring(lastSlash+1, i + classNamePos);
   }
   
   private String msg(String query, String msg){
	   return String.format("Invalid query syntax \"%s\". %s", query,  msg);
   }
   
   /** 
    * returns the path we parsed
    */
   @Override
   public String getPath(){
       return filePath;
   }

   /**
    * returns the package we parsed
    */
   @Override
   public String getJavaPackage(){
       return javaPackage;
   }

   /**
    * returns the java class we parsed
    */
   @Override
   public String getJavaClass(){
       return javaClass;
   }
}

/**
 * basic printing test 
 * 
 * TODO zak change to a unit test that actually asserts stuff
 * @author zkieda
 * @since 180
 */
class JavaClassParserTest{
    private static String putSpace(String s){
        return "".equals(s)? "" : s + " ";
    }
    private static void printClassParser(JavaClassParser instance) {
        System.out.println(
                putSpace(instance.getPath()) +
                putSpace(instance.getJavaPackage()) + 
                putSpace(instance.getJavaClass()) 
            );
    }
    private static void printClassParsers(JavaClassParser...classParsers) {
        for (JavaClassParser javaClassParser : classParsers) {
            printClassParser(javaClassParser);
        }
    }
    /** attempts to make a class parser that we know will fail */
    private static void failClassParser(String classParserArg) {
        try {
            new JavaClassParser(classParserArg);
            System.err.println("test failed");
        } catch (IllegalArgumentException e) {
            if(e.getMessage().startsWith("Invalid query syntax")){
                System.out.println("test passed");
            } else{
                System.err.println("test failed");
            }
        }
    }
    private static void failClassParsers(String... classParserArgs) {
        for (String string : classParserArgs) {
            failClassParser(string);
        }
    }
    public static void main(String[] as){
        
        printClassParsers(
            new JavaClassParser("path/to/file/java.package.path.JavaClassName"),
            new JavaClassParser("path/to/fi.le/java.package.path.JavaClassName"),
            new JavaClassParser("java.package.path.JavaClassName"),
            new JavaClassParser("JavaClassName"),
            new JavaClassParser("path/to/file/JavaClassName")
        );
        
        //note that if we don't have the trailing '.' or '/' we assume that the 
        //last word is the java class name, which will make it pass
        failClassParsers(
                "",
                "package.to.nothing.",
                "path/to/nothing/",
                "path/to/no.thing/",
                "path/to/nothing/path.to.nothing."
            );
    }
}