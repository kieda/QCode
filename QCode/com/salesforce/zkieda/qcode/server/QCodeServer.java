package com.salesforce.zkieda.qcode.server;

import java.io.*;
import java.nio.file.Path;

import com.salesforce.zkieda.qcode.lang.Parsers;
import com.salesforce.zkieda.util.PrintStreamThread;
import com.salesforce.zkieda.util.ThreadIO;

/**
 * central component to the qcode server
 * 
 * todo increase modularity -- we don't care if we will be running .qc files and running 
 * them, or if we will be running java files. Thus, we will break them down into a couple 
 * steps
 * 
 * 1) a preprocessing stage for the files that are modified
 * 2) a generic preprocessing stage (allows for #define)
 * 3) a stage that takes the processed set of data
 *      in some builds :
 *          a) compile the java class
 *          b) do something after with the java (run it, or just load it up)
 *      in other builds : 
 *          a) run the files (like using .js)
 *
 * @author zkieda
 * @since 190
 * @version 1.0
 */
public class QCodeServer {
    //policy for removing old threads
    private EvictionPolicy ep;
    //the class that manages removing old threads
    private ThreadEvictionManager tem;
    
    //how we output our compilation errors, and errors the programs encounter
    private QCodeServerOut qcso;
    
    private PrintStream 
        complOut,
        qCodeOut,
        qCodeErr;
    
    //listens for a change in the input stream. When this is notified of a 
    //change, we recompile and rerun the class.
    private ServerInputStreamListener sisw;
    
    //watcher for an input stream change. Notifies our listener
    private InputStreamWatcher qsi;
    
    private JavaOutputPath classPath;
    
    private static PrintStream makePrintStream(
            OutputStream os,
            PrintStream defaultIfNull
        ){
        return os == null ? defaultIfNull : new PrintStream(os);
    }
    
    public QCodeServer(
        //policy for removing jobs
        EvictionPolicy ep,
        
        //where we pipe our various output to
        QCodeServerOut qso,
        
        InputStreamWatcher qsi,
        
        JavaOutputPath classPath
            ){
        this.ep = ep;
        this.qcso = qso;
        this.qsi = qsi;
        this.classPath = classPath;
        
        sisw = new ServerInputStreamListener();
        qsi.setListener(sisw);
        
        tem = new ThreadEvictionManager(ep);
        
        ThreadIO.replaceSystemErr();
        ThreadIO.replaceSystemOut();
        
        complOut = makePrintStream(qcso.getCompilationOut(), ThreadIO.OUT);
        qCodeOut = makePrintStream(qcso.getQCodeOut(), ThreadIO.OUT);
        qCodeErr = makePrintStream(qcso.getQCodeErr(), ThreadIO.ERR);
    }
    
    public void start(){
        new PrintStreamThread(qsi)
            .dup2(System.out, complOut)
            .dup2(System.err, qCodeErr)
            .start();
    }
    
    public void stop(){
        qsi.stop();
    }
    
    
    private class ServerInputStreamListener implements InputStreamListener{
        @Override
        public void onChange(InputStream is) {
            //way too ghetto do not look
            
            try{
                //java class definition
                String javaClass = Parsers.getClassForCompile(
                        classPath, Parsers.inputStreamToString(is));
                if(javaClass == null){
                    //error on compilation. Error details should be sent via 
                    //complOut
                    return;
                }              
              Path res = CompilationJob.doCompilation(classPath, javaClass);
              if(res != null){
                  
                  tem.addThread(
                      new PrintStreamThread(new RunJob(classPath, qCodeOut, qCodeErr))
                          .dup2(System.out, qCodeErr)
                          .dup2(System.err, qCodeOut)
                      , 5000);
              }
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
