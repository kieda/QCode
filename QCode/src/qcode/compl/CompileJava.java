package qcode.compl;

import qcode.frame.DiagnosticFrame;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import javax.tools.*;
import qcode.QCode;
import qcode.drivers.QMain;

/**
 *
 * @author NOT kieda
 */
public class CompileJava {
    /**
     * 
     * Not by me, by http://www.accordess.com/wpblog/an-overview-of-java-compilation-api-jsr-199/
     * 
     * I believe this (might) be fine, as the intended use is for educational 
     * purposes, and the code source was posted online in a public fashion...
     */
    public static boolean doCompilation (String source_code, QCode env){
        
//        File root = new File(".");
        /*Creating dynamic java source code file object*/
        SimpleJavaFileObject fileObject = new DynamicJavaSourceCodeObject (env.CLASS_NAME, source_code) ;
        JavaFileObject javaFileObjects[] = new JavaFileObject[]{fileObject} ;
        
        /*Instantiating the java compiler*/
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        /*Create a diagnostic controller, which holds the compilation problems*/
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        /**
         * Retrieving the standard file manager from compiler object, which is used to provide
         * basic building block for customizing how a compiler reads and writes to files.
         *
         * The same file manager can be reopened for another compiler task.
         * Thus we reduce the overhead of scanning through file system and jar files each time
         */
        StandardJavaFileManager stdFileManager = compiler.getStandardFileManager(diagnostics, null, null);
 
        /* Prepare a list of compilation units (java source code file objects) to input to compilation task*/
        Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(javaFileObjects);
 
//        /*Prepare any compilation options to be used during compilation*/
//        //In this example, we are asking the compiler to place the output files under bin folder.
////        String[] compileOptions;
////        if(output_dir == null) compileOptions = new String[]{};
////        else compileOptions = new String[]{"-d", output_dir.getPath()};//"-d", "bin"} ;
//        String[] compileOptions = new String[]{};
//        //NOTE : this program will not work if the directory output_dir does not exist.
//        //We are relying on the fact that we "created" the file while parsing the 
//        //args that the folder was also created.
//        //we also are relying on that we are inputting a folder that is created.
//        Iterable<String> compilationOptionss = Arrays.asList(compileOptions);
 
        
        /*Create a compilation task from compiler by passing in the required input objects prepared above*/
        JavaCompiler.CompilationTask compilerTask = compiler.getTask(null, stdFileManager, diagnostics, null, null, compilationUnits) ;
        
        //Perform the compilation by calling the call method on compilerTask object.
        boolean status = compilerTask.call();

        if (!status){//If compilation error occurs
            StringBuffer message = new StringBuffer();
            /*Iterate through each compilation problem and print it*/
            for (Diagnostic diagnostic : diagnostics.getDiagnostics()){
//                System.out.format("Error on line %s\n", diagnostic.toString().substring(11));
                message.append("Error on line ").append(diagnostic.toString().substring(11)).append("\n");
            }
            env.df.setText(message.toString());
        } else env.df.success();
        try {
            stdFileManager.close() ;//Close the file manager
        } catch (IOException e) {
            e.printStackTrace();
        } 
         return status;
    }
    public static void run(QCode env){
        p:{for(String s : new File(".").list()){
            if(s.equals(env.CLASS_NAME + ".class")) break p;
        } return;
        }
            try {
                URLClassLoader classLoader = URLClassLoader.newInstance(
                        new URL[] { new File(".").toURI().toURL() }
                        );
                Class<?> cls = Class.forName(env.CLASS_NAME, true, classLoader); 
                Object instance;
                instance = cls.newInstance();
                for(Method m : cls.getMethods()){
                    if(m.getName().equals("main")){
                        Object o = m.invoke(instance);
                        if(o != null){
                            if(o instanceof Object[])env.df.setResult(Arrays.toString((Object[])o));
                            else if(o instanceof boolean[])env.df.setResult(Arrays.toString((boolean[])o));
                            else if(o instanceof byte[])env.df.setResult(Arrays.toString((byte[])o));
                            else if(o instanceof char[])env.df.setResult(Arrays.toString((char[])o));
                            else if(o instanceof short[])env.df.setResult(Arrays.toString((short[])o));
                            else if(o instanceof short[])env.df.setResult(Arrays.toString((float[])o));
                            else if(o instanceof int[])env.df.setResult(Arrays.toString((int[])o));
                            else if(o instanceof long[])env.df.setResult(Arrays.toString((double[])o));
                            else if(o instanceof long[])env.df.setResult(Arrays.toString((long[])o));
                            else env.df.setResult(o.toString());
                        }else env.df.resetText();
                        
                    }
                }
//                System.out.println(instance); // Should print "test.Test@hashcode".
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException | MalformedURLException ex) {ex.printStackTrace();}
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