package com.salesforce.zkieda.qcode.server;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.nio.file.Path;
import java.util.Arrays;

import com.salesforce.zkieda.qcode.drivers.QMain;

/**
 * used to run a job
 *
 * todo - use an encoding for our messages. Have "(RESULT=<int>)" 
 * in a block of output (key/value pairs)
 * 
 * this will allow us to send information like whether or not the compilation was successful, 
 * or even layout for a particular portion of text
 * 
 * all other output : '\' -> '\\', '(' -> '\(', ')' -> '\)'
 * 
 * 
 * @author zkieda
 * @since 190
 * @version 0.8
 */
public class RunJob implements Runnable{
    private Path p;
//    private PrintStream qCodeOut, qCodeErr;
    
    /** run the qcode class at the given path */
    public RunJob(Path p, PrintStream qCodeOut, PrintStream qCodeErr) {
        this.p = p;
//        this.qCodeErr = qCodeErr;
//        this.qCodeOut = qCodeOut;
    }
    
    @Override
    public void run(){
        try {
          URLClassLoader classLoader = URLClassLoader.newInstance(
                  new URL[] {p.getParent().toUri().toURL()}
                  );
          Class<?> cls = Class.forName(QMain.CLASS_NAME + QMain.VERSION, true, classLoader);
          Object instance;
          instance = cls.newInstance();
          Method m = cls.getMethod("main");
          if(m != null){
              Object o = m.invoke(instance);//, new String[]{});
              if(o != null){
                  System.out.println("RESULT:");
                  String re;
                  if(o instanceof Object[])re = (Arrays.deepToString((Object[])o));
                  else if(o instanceof boolean[])re = (Arrays.toString((boolean[])o));
                  else if(o instanceof byte[])re = (Arrays.toString((byte[])o));
                  else if(o instanceof char[])re = (Arrays.toString((char[])o));
                  else if(o instanceof short[])re = (Arrays.toString((short[])o));
                  else if(o instanceof short[])re = (Arrays.toString((float[])o));
                  else if(o instanceof int[])re = (Arrays.toString((int[])o));
                  else if(o instanceof double[])re = (Arrays.toString((double[])o));
                  else if(o instanceof long[])re = (Arrays.toString((long[])o));
                  else re = (o.toString());
                  System.out.println(re);
              }//else System.out.println("CLEAR:");
          }
      } catch (ClassNotFoundException | 
              InstantiationException | 
              IllegalAccessException | 
              InvocationTargetException | 
              MalformedURLException  |
              NoSuchMethodException ex) {
          ex.printStackTrace();
      }
    }
}
