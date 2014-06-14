/**
 * TRADEMARK BUTTFORCE TM 2014 DO NOT COPYRIGHT NO LOOKING
 */
package com.salesforce.zkieda.qcode.server;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Arrays;

import javax.tools.*;

import com.salesforce.zkieda.qcode.drivers.QMain;

/**
 * represents a compilation job, given some input stream for 
 *
 * @author zkieda
 * @since 190
 * @version 0.4
 */
public class CompilationJob {
    /**
     * Based on the article at http://www.accordess.com/wpblog/an-overview-of-java-compilation-api-jsr-199/
     */
    public static Path doCompilation (
            String sourceCode
            ){
        /*Creating dynamic java source code file object*/
        SimpleJavaFileObject fileObject = new DynamicJavaSourceCodeObject (QMain.CLASS_NAME + QMain.VERSION, sourceCode) ;
        JavaFileObject javaFileObjects[] = new JavaFileObject[]{fileObject} ;
        
        //Instantiate java compiler
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        //diagnostics holds information about our compilation problems
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        
        StandardJavaFileManager stdFileManager = compiler.getStandardFileManager(diagnostics, null, null);
 
        /* Prepare a list of compilation units (java source code file objects) to input to compilation task*/
        Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(javaFileObjects);
 
        /*Prepare any compilation options to be used during compilation*/
        
//        String[] compileOptions;
//        if(output_dir == null) compileOptions = new String[]{};
//        else compileOptions = new String[]{"-d", output_dir.getPath()};//"-d", "bin"} ;
//        String[] compileOptions = new String[]{};
//        Iterable<String> compilationOptionss = Arrays.asList(compileOptions);
 
        /*Create a compilation task from compiler by passing in the required input objects prepared above*/
        JavaCompiler.CompilationTask compilerTask = compiler.getTask(null, stdFileManager, diagnostics, null, null, compilationUnits) ;
        
        //Perform the compilation by calling the call method on compilerTask object.
        boolean status = compilerTask.call();

        if (!status){//If compilation error occurs
            System.out.println("FAIL:");
            /*Iterate through each compilation problem and print it*/
            for (Diagnostic diagnostic : diagnostics.getDiagnostics()){
                System.out.append("Error on line ").append(diagnostic.toString().substring(11)).append("\n");
            }
        } else{
            System.out.println("SUCCESS. ");
        }
        
        try {
            stdFileManager.close() ;//Close the file manager
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        if(status){
            //return path to compiled object
            File f = new File("./" + QMain.CLASS_NAME + QMain.VERSION + ".class");
            if(!f.exists()) return null;
            
            return f.toPath(); 
        } else
            return null;
    }
}


/**
 * Creates a dynamic source code file object
 *
 * This is an example of how we can prepare a dynamic java source code for compilation.
 * This class reads the java code from a string and prepares a JavaFileObject
 *
 */
class DynamicJavaSourceCodeObject extends SimpleJavaFileObject{
    private String qualifiedName ;
    private String sourceCode ;
 
    /**
     * Converts the name to an URI, as that is the format expected by JavaFileObject
     *
     *
     * @param fully qualified name given to the class file
     * @param code the source code string
     */
    protected DynamicJavaSourceCodeObject(String name, String code) {
        super(URI.create("string:///" +name.replaceAll("\\.", "/") + JavaFileObject.Kind.SOURCE.extension), JavaFileObject.Kind.SOURCE);
        this.qualifiedName = name ;
        this.sourceCode = code ;
    }
 
    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors)
            throws IOException {
        return sourceCode ;
    }
 
    public String getQualifiedName() {
        return qualifiedName;
    }
 
    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }
 
    public String getSourceCode() {
        return sourceCode;
    }
 
    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }
}