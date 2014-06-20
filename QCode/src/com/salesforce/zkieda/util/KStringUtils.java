package com.salesforce.zkieda.util;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Chars;

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
    
    private static final char[] generateUnique(char start, final int length){
        char[] result = new char[length];
        for (int i = 0; i < length; i++) {
            result[i] = start++;
        }
        return result;
    }
    
    public static final boolean GOOG = true;
    
    public static void main(String[] args) throws Exception{
        final int TRIALS = 100;
        //jvm warm up
        Thread.sleep(10000);
        
        //see how me vs the googs stack up
        char[] chars10_000 = generateUnique((char)0, 10_000);
        
        long total = 0;
        for (int i = 0; i < TRIALS; i++) {
            long time = System.nanoTime();
            
            if(GOOG){
                Chars.join("|", chars10_000);
            }else{
                join('|', chars10_000);
            }
            
            time = System.nanoTime() - time;
            total += time;
            System.out.println("trial "+i+" time "+time);
        }
        System.out.println("total : "+total);
        System.out.println("avg : "+(double)total/(double)TRIALS);
        
        //          total,       avg,  trial 0,  trial 1, trial 99
        //ME   : 17575029, 175750.29,  6399127,   259914,    41925
        //GOOG : 33495093, 334950.93, 13437859,  2456392,   105824
    }
}