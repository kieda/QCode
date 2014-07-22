package org.zkieda.qcode.serverlistener;

import java.io.*;
import java.nio.file.*;

import org.zkieda.qcode.util.DtHandler;

/**
 * an implementation for the InputStreamWatcher that watches on a file. 
 *
 * uses java.nio.file to watch for changes in a file itself
 *  
 * todo - improvement - be able to register multiple paths, and have the server 
 * watch on multiple files at a time
 * 
 * todo - improvement - have the server listen on a socket, and accept jobs. This 
 * watcher can just send multiple jobs to the server
 * 
 * todo - improvement - use random access file and seekable byte channel to seek 
 * to the beginning of the stream at each iteration, rather than creating a new 
 * object 
 * "sadf"
 * @author zkieda
 * @since 192
 * @version 1.0
 */
public class PathInputStreamWatcher extends InputStreamWatcher implements Closeable{
//    private final Path path;
    
    //the path for the name of the file we're watching
    private final Path filePath;    
    
    //the file we are watching
    private final File file;
    
    //the watch service we are using
    private final WatchService pathWatchService;
    
    //key listening for modifications at path
    private WatchKey key;
    
    //handles spurious requests
    private final DtHandler dtH = new DtHandler();
    
    /**
     * creates a input stream watcher that watches a file at a path
     * @param path the path of the file. Cannot be a directory
     * @throws IOException if there is an error loading the file or something
     * @throws IllegalArgumentException if <code>path</code> is a directory (and not a file)  
     */
    public PathInputStreamWatcher(Path path) throws IOException{
//        this.path = path;
        this.filePath = path.getFileName();
        this.file = path.toFile();
        
        if(file.isDirectory())
            throw new IllegalArgumentException("Expected a file, not a directory");
        pathWatchService = FileSystems.getDefault().newWatchService();
        
        key = path.getParent().register(pathWatchService, StandardWatchEventKinds.ENTRY_MODIFY);
        
    }
    
    /**
     * returns true if a change occured on the file at the path
     * 
     * This appears to work fine when editing a file in eclipse
     */
    @Override
    public boolean changeOccurred() {
        WatchKey wkey;
        boolean result = false;
        while((wkey = pathWatchService.poll()) != null){
            //poll events
            for(WatchEvent<?> evt : wkey.pollEvents()){
                WatchEvent.Kind kind = evt.kind();
                if(kind == StandardWatchEventKinds.ENTRY_MODIFY && filePath.equals((evt.context()))){
                    result = true;
                }
            }
            wkey.reset();
        }
        
        if(result) return dtH.poll();
        return result;
    }

    /**
     * returns the stream, reloaded
     */
    @Override
    public InputStream reloadStream() throws IOException {
        return new FileInputStream(file);
    }

    /**
     * close this watcher
     */
    @Override
    public void close() throws IOException {
        pathWatchService.close();
    }
}
