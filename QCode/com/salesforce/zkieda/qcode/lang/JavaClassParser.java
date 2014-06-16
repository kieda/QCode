package com.salesforce.zkieda.qcode.lang;

import java.io.*;
import java.nio.file.Files;

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
 */
public class JavaClassParser {
   private String filePath, javaPackage, javaClass;

   /** 
    * immediately parses the string
    * @param outClassPath the string we are parsing
    * @throws NullPointerException if the string is {@code null}
    * @throws IllegalArgumentException if the string is not in the desired 
    * format. Note that this class does not validate if the java class or 
    * files are properly named.
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
	   
	   //if lastSlash==classNamePos
	   //	keep it
	   //else classNamePos+1
	   
	   //(~(lastSlash^classNamePos))>>31 + 1
	   //0 if equal, 1 otherwise
	   
	   //both will be 0 when they eq
	   //one will be negative when they do not.
	   int i = (lastSlash - classNamePos) | (classNamePos - lastSlash);
	   i = i >> 31;
	   i = i + 1;
//	   int i = (lastSlash^classNamePos) | (~(lastSlash ^ classNamePos));
//	   i = i>>31;
//	   i = i+1;
	   
	   
	   javaPackage = outClassPath.substring(lastSlash+1, i + classNamePos);
	   
	   System.out.println(
		   filePath + " " + javaPackage + " " + javaClass
	   );
   }
   public static void main(String[] as){
	   new JavaClassParser("path/to/file/java.package.path.JavaClassName");
	   new JavaClassParser("path/to/fi.le/java.package.path.JavaClassName");
	   new JavaClassParser("java.package.path.JavaClassName");
	   new JavaClassParser("JavaClassName");
	   new JavaClassParser("path/to/file/JavaClassName");
	   
	   try{
		   new JavaClassParser("");
		   System.err.println("test failed");
	   }catch(IllegalArgumentException e){
		   System.out.println("test passed");
	   }
	   
	   try{
		   new JavaClassParser("package.to.nothing.");
		   System.err.println("test failed");
	   }catch(IllegalArgumentException e){
		   System.out.println("test passed");
	   }
	   
	   try{
		   new JavaClassParser("path/to/nothing/");
		   System.err.println("test failed");
	   }catch(IllegalArgumentException e){
		   System.out.println("test passed");
	   }
	   
	   try{
		   new JavaClassParser("path/to/nothing/path.to.nothing.");
		   System.err.println("test failed");
	   }catch(IllegalArgumentException e){
		   System.out.println("test passed");
	   }
   }
   private String msg(String query, String msg){
	   return String.format("Invalid query syntax \"%s\". %s", query,  msg);
   }
   
//   /** 
//    * returns the path we parsed
//    */
//   public String getPath(){}
//
//   /**
//    * returns the package we parsed
//    */
//   public String getPackage(){}
//
//   /**
//    * returns the java class we parsed
//    */
//   public String getJavaClass(){}
}
