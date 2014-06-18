package com.salesforce.zkieda.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

/**
 * utilities used to clean directories of certain files
 *
 * @author zkieda
 * @since 180
 */
public class CleanDir {
    private CleanDir(){}
    
    /**
     * Cleans a directory of the files that ascribe to a name patten and type pattern in a directory
     * @param namePattern the pattern that will be used for the name of the file
     * @param typePattern the type of the file that will be used to match the file
     * @param filePath the path we are cleaning
     */
    public static void cleanDir(String namePattern, String typePattern, String filePath){
        cleanDir(namePattern, typePattern, new File(filePath));
    }
    
    /**
     * Cleans a directory of the files that ascribe to a name patten and type pattern in a directory
     * @param namePattern the pattern that will be used for the name of the file
     * @param typePattern the type of the file that will be used to match the file
     * @param filePath the path we are cleaning
     */
    public static void cleanDir(String namePattern, String typePattern, File filePath){
        cleanDir(
            String.format("%s\\.%s",
                namePattern,
                typePattern),
            filePath);
    }
    
    /**
     * Cleans a directory of the files that ascribe to a name patten in a directory
     * @param pattern the pattern used to describe the name of a file
     * @param filePath the path we are cleaning
     */
    public static void cleanDir(String pattern, String path){
        cleanDir(Pattern.compile(pattern), new File(path));
    }
    
    /**
     * Cleans a directory of the files that ascribe to a name patten in a directory
     * @param pattern the pattern used to describe the name of a file
     * @param filePath the path we are cleaning
     */
    public static void cleanDir(String pattern, File path){
        cleanDir(Pattern.compile(pattern), path);
    }
    
    /**
     * Cleans a directory of the files that ascribe to a name patten in a directory
     * @param pattern the pattern used to describe the name of a file
     * @param filePath the path we are cleaning
     */
    public static void cleanDir(final Pattern pattern, File path){
       for(File f : path.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return pattern.matcher(name).matches();
            }
        })){
           f.delete();
       }
    }
}
/* cleanup  cases ;
 *   when the user forcefully exits and a class is left behind
 *      :: method to clean up on start (clean a directory of CLASS_NAME (int) ".class" ) 
 *   when the task does not finish and the task is evicted
 *   when a task exits successfully
 */