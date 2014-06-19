package com.salesforce.zkieda.util;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.primitives.Chars;

public class KStringUtilsTest {
    /** 
     * after running some tests, my solution is ~2.5x faster after warmup. 
     * Cold, mine is 4x to 10x faster
     */
    
    /** test join against google's naive implementation */
    private static final char separator = '|';
    private static final String separatorString = String.valueOf(separator);
    
    private static final String naiveGoogleCharJoin(char[] vals){
        return Chars.join(separatorString, vals);
    }
    
    private static final String mySuperiorCharJoin(char[] vals){
        return KStringUtils.join(separator, vals);
    }
    
    private static final char[] generateUnique(char start, final int length){
        char[] result = new char[length];
        for (int i = 0; i < length; i++) {
            result[i] = start++;
        }
        return result;
    }
    
    private static final char[][] testGenerateUnique_results = 
        {
            {}, 
            {'a'}, 
            {'a', 'b'}, 
            {'a', 'b', 'c'}, 
            {'a', 'b', 'c', 'd'}, 
            {'a', 'b', 'c', 'd', 'e'}, 
            {'a', 'b', 'c', 'd', 'e', 'f'}, 
        };
    //test our internal utility method
    @Test
    public void testGenerateUnique_internal(){
        for(int i = 0; i < testGenerateUnique_results.length; i++){
            Assert.assertArrayEquals(
                    testGenerateUnique_results[i], 
                    generateUnique('a', i)
                );
        }
    }
    
    
    @Test
    public void testCharJoin() {
        final int testLength = 100;
        
        for(int i = 0; i < testLength; i++){
            char[] uniqueChars = generateUnique('a', i);
            
            Assert.assertEquals(
                    naiveGoogleCharJoin(uniqueChars),
                    mySuperiorCharJoin(uniqueChars));
        }
    }
}
