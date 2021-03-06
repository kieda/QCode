package org.zkieda.qcode.qcodeserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;

import org.zkieda.qcode.lang.Parsers;
import org.zkieda.qcode.server.CompilationJob;
import org.zkieda.qcode.server.CompilationServerOut;
import org.zkieda.qcode.server.JavaOutputPath;
import org.zkieda.qcode.server.RunJob;
import org.zkieda.qcode.serverlistener.InputStreamListener;
import org.zkieda.qcode.serverlistener.InputStreamWatcher;
import org.zkieda.qcode.util.EvictionPolicy;
import org.zkieda.qcode.util.PrintStreamThread;
import org.zkieda.qcode.util.ThreadEvictionManager;
import org.zkieda.qcode.util.ThreadIO;
import org.zkieda.qcode.util.ThreadInfo;

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
 * @version 1.0
 */
public class QCodeServer {
    //policy for removing old threads
    private final EvictionPolicy<ThreadInfo> ep;
    //the class that manages removing old threads
    private final ThreadEvictionManager tem;
    
    //how we output our compilation errors, and errors the programs encounter
    private final CompilationServerOut qcso;
    
    private final PrintStream 
        complOut,
        qCodeOut,
        qCodeErr;
    
    //listens for a change in the input stream. When this is notified of a 
    //change, we recompile and rerun the class.
    private final ServerInputStreamListener sisw;
    
    //watcher for an input stream change. Notifies our listener
    private final InputStreamWatcher qsi;
    
    private final JavaOutputPath classPath;
    
    private static PrintStream makePrintStream(
            OutputStream os,
            PrintStream defaultIfNull
        ){
        return os == null ? defaultIfNull : new PrintStream(os);
    }
    
    public QCodeServer(
        //policy for removing jobs
        EvictionPolicy<ThreadInfo> ep,
        
        //where we pipe our various output to
        CompilationServerOut qso,
        
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
    

    /**
     *  FIXME rare concurrency problem which causes the classfile to be truncated
     *  see : ClassFormatError: Truncated class file
     * @author zkieda
     */
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
