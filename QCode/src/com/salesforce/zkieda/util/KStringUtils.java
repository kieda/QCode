package com.salesforce.zkieda.util;

import org.junit.Assert;

import com.google.common.base.Preconditions;

public class KStringUtils {
    public static String join(char separator, char... vals){
        int needed = Preconditions.checkNotNull(vals).length - 1;
                                //if length = 0, -1. 
        
        if(needed<0) return "";
                                
        char[] result = new char[needed + vals.length];
        
        //if vals.length is even, want vals.length-1
        //otherwise we want vals.length
        for (int i = 0; i < needed; i++) {
            int idx = i*2;
            result[idx] = vals[i];       
            result[idx+1] = separator;
        }
        
        //set the last one. If we already set it, the array does not change
        result[result.length-1]=vals[needed];
        
        return new String(result);
    }
}
class KStringUtilsTest{
    public static void main(String[] args) {
        Assert.assertEquals("",  KStringUtils.join('|'));
        Assert.assertEquals("a", KStringUtils.join('|', 'a'));
        Assert.assertEquals("a|b",KStringUtils.join('|', 'a','b'));
        Assert.assertEquals("a|b|c",KStringUtils.join('|', 'a','b','c'));
        Assert.assertEquals("a|b|c|d|e|f",KStringUtils.join('|', 'a','b','c','d','e','f'));
    }
}