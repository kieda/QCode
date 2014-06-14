package com.salesforce.zkieda.qcode.lang;

import java.io.*;

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
    * @throws IllegalFormatException if the string is not in the desired format, or is not a valid file name, package name, or class name
    */
   public JavaClassParser(String outClassPath){
       
   }
   /** 
    * returns the path we parsed
    */
   public String getPath(){}

   /**
    * returns the package we parsed
    */
   public String getPackage(){}

   /**
    * returns the java class we parsed
    */
   public String getJavaClass(){}
}
