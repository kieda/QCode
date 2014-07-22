package org.zkieda.qcode.compl;


/**
 * contains a set of utility functions useful for using qcode
 * not important at the moment (remove and reinroduce)
 * @author zkieda
 */
public class PoolBackBone {
    private static StringBuilder out = new  StringBuilder();
    public static void qout(int i){out.append(i);}
    public static void qout(char i){out.append(i);}
    public static void qout(char[] i){out.append(i);}
    public static void qout(Object i){out.append(i);}
    public static void qout(StringBuffer i){out.append(i);}
    public static void qout(boolean i){out.append(i);}
    public static void qout(short i){out.append(i);}
    public static void qout(byte i){out.append(i);}
    public static void qout(double i){out.append(i);}
    public static void qout(float i){out.append(i);}
    public static void qout(long i){out.append(i);}
    public static void qout(char[] c, int offset, int length){ out.append(c, offset, length); }
    
    public static void qoutln(int i){out.append(i).append('\n');}
    public static void qoutln(char i){out.append(i).append('\n');}
    public static void qoutln(char[] i){out.append(i).append('\n');}
    public static void qoutln(Object i){out.append(i).append('\n');}
    public static void qoutln(StringBuffer i){out.append(i).append('\n');}
    public static void qoutln(boolean i){out.append(i).append('\n');}
    public static void qoutln(short i){out.append(i).append('\n');}
    public static void qoutln(byte i){out.append(i).append('\n');}
    public static void qoutln(double i){out.append(i).append('\n');}
    public static void qoutln(float i){out.append(i).append('\n');}
    public static void qoutln(long i){out.append(i).append('\n');}
    public static void qoutln(char[] c, int offset, int length){ out.append(c, offset, length).append('\n'); }

    static String get(){
        return out.toString();
    }

    static void clear(){
        out = new StringBuilder();
    }
}