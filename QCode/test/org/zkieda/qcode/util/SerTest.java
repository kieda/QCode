package org.zkieda.qcode.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SerTest {
    public static void main(String[] args) throws Exception{
        write();
        read();
    }
    private static void write() throws Exception{
        ObjectOutputStream oo = new ObjectOutputStream(
                new FileOutputStream("./test-ser")
            );
        oo.writeObject(new C("adf"));
    }
    private static void read() throws Exception{
        ObjectInputStream is = new ObjectInputStream(new FileInputStream("./test-ser"));
        System.out.println(is.readObject());
    }
}
class N {
    static int instances = 0;
}
class A extends N implements Serializable{
    public A() {
        System.out.println(++instances);
    }
}
abstract class B extends A implements Runnable, Serializable{
    private final String s;
    protected String st = "";
    public B(String s) {
        this.s = s;
        run();
    }
    @Override
    public String toString() {
        return s + st;
    }
}
class C extends B {
    public C(String s) {
        super(s);
    }
    @Override
    public void run() {
        st = " end";
    }
}
