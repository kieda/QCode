package com.salesforce.zkieda.util;

public class KPrimitiveUtils {
    private KPrimitiveUtils(){}
    public  static final char[] EMPTY_CHAR = {};
    public static final int[] EMPTY_INT = {};
    public  static final long[] EMPTY_LONG = {};
    public static final short[] EMPTY_SHORT = {};
    public static final boolean[] EMPTY_BOOL = {};
    public static final Object[] EMPTY_OBJ = {};
    
    public static <A> A retainIfSecondArgIsNull(A val, Object o){
        return o==null?val:null;
    }
}
