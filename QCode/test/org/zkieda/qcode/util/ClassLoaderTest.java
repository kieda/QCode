package org.zkieda.qcode.util;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;


public class ClassLoaderTest {
    public static void main(String[] args) throws Exception {
        URLClassLoader ucl1 = new URLClassLoader(new URL[] {
                new File("./target/test-classes/").toURI().toURL()
            });
        Class<?> sc = Class.forName("org.zkieda.qcode.util.GCTester", false, ucl1);
        System.out.println(sc.getField("value").getInt(null));
//        sc.getField("value").setInt(null, 1);ru
//        System.out.println(sc.getField("value").getInt(null));
        
        URLClassLoader ucl = new URLClassLoader(new URL[] {
            new File("./target/test-classes/").toURI().toURL()
        });
        Class<?> c = Class.forName("org.zkieda.qcode.util.GCTester", true, ucl);
        System.out.println(c.getField("value").getInt(null));
        
        
        sc.getField("value").setInt(null, 4);
        System.out.println(c.getField("value").getInt(null));
        System.out.println(
                sc.getField("value").getInt(null)
            );
        
    }
}
