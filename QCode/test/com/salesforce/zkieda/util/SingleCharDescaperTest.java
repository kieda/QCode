package com.salesforce.zkieda.util;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.google.common.escape.Escaper;

/**
 * Test {@link SingleCharDescaper}
 *
 * @author zkieda
 * @since 180
 */
public class SingleCharDescaperTest {
    @Test
    public void testSingleCharDescaperConstructor() {
        new SingleCharDescaper('k');
    }
    
    @Test
    public void testGetEscapeChar() {
        for(char c = 'a'; c < 'z'; c++){
            assertEquals(new SingleCharDescaper(c).getEscapeChar(), c);
        }
    }
    
    
    static final char escapeChar = '\\';
    static final char[] testEscapeSingleChars = {'a', 'b', 'c', 'd'};
    static final char[][] testEscapeChars = {
            {},
            {'a'},
            {'b'},
            {'c'},
            {'a', 'b'},
            {'a', 'b', 'c'},
            {'a', 'b', 'c', 'd'},
    };
    static final String[] testEscapeStrings = {
            "",
            "a",
            "b",
            "c",
            "bd",
            "abd",
            "abcd",
            "bcda",
            "$te",
    };
    Escaper[] testCases; 
    SingleCharFunc scf;
    
    //test  
    static final String[][] TEST_EMPTY = {
        {"hello\\world", "hello\\\\world"},
        {"\\", "\\\\"},
        {"hello world", "hello world"},
        {"", ""},
    };
    static String D(char c){
        return format("%c%c", escapeChar, c);
    }
    static String D(String s){
        return format("%s%s", D(s.charAt(0)), D(s.charAt(1)));
    }
    static String[][] testSingleChar(final char val){
        return new String[][]{
            {"1836" + val + "494", "1836" + D(val) + "494"},
            {D(val), D(D(val))},
            {val + "2"+val, D(val)+"2"+D(val)},
            {val+"", D(val)},
            {val+"1", D(val)+"1"},
            {"1"+val, "1"+D(val)},
            {"3276", "3276"},
            {"", ""}
        };
    }
    static String[][] combine(String[][] left, String[][] right){
        String[][] result = new String[left.length][2];
        int i = 0;
        for(String[] lTest : left){
            String[] rTest = right[i];
            
            String[] s = result[i];
            s[0] = lTest[0] + rTest[0];
            s[1] =  lTest[1] + rTest[1];
            i++;
        }
        return result;
    }
    static String[][] test2Chars(final char v1, final char v2){
        String[][] left = testSingleChar(v1);
        String[][] right = testSingleChar(v2);
        return combine(left, right);
    }
    static String[][] test3Chars(final char v1, final char v2, final char v3){
        return combine(test2Chars(v1, v2), testSingleChar(v3));
    }
    static String[][] test4Chars(final char v1, final char v2, final char v3, final char v4){
        return combine(test2Chars(v1, v2), test2Chars(v3, v4));
    }
    
    //terrible testing suite
    //note : do not use any char-numbers in here ('0' to '9').
    
    //each idx is a test for a descaper
    static final String[][][] descaperTests = {
        //each test for a descaper is an array of tests
        //each test is {string to descape, expected}
        
        //escape char : '\'
        
        /* test without any escape chars */
        //special : {}
        TEST_EMPTY,
        
        /* test single escape chars */
        //special : {'a'}
        testSingleChar('a'),
        //special : {'b'}
        testSingleChar('b'),
        //special : {'c'}
        testSingleChar('c'),
        //special : {'d'}
        testSingleChar('d'),
        
        /* test escape chars */
        //special : {}
        TEST_EMPTY,
        //special : {'a'}
        testSingleChar('a'),
        //special : {'b'}
        testSingleChar('b'),
        //special : {'c'}
        testSingleChar('c'),
        //special : {'a', 'b'}
        test2Chars('a', 'b'),
        //special : {'a', 'b', 'c'},
        test3Chars('a', 'b', 'c'),
        //special : {'a', 'b', 'c', 'd'}, 
        test4Chars('a', 'b', 'c', 'd'),
        
        /* test escape strings */
        //special : "",
        TEST_EMPTY,
        //special : "a",
        testSingleChar('a'),
        //special : "b",
        testSingleChar('b'),
        //special : "c",
        testSingleChar('c'),
        //special : "bd",
        test2Chars('b', 'd'),
        //special : "abd",
        test3Chars('a', 'b', 'd'),
        //special : "abcd",
        test4Chars('a', 'b', 'c', 'd'),
        //special : "bcda",
        test4Chars('b', 'c', 'd', 'a'),
        //special : "$te",
        test3Chars('$', 't', 'e'),
    };
    
    @Before
    public void init(){
       scf =  new SingleCharDescaper(escapeChar);
       
       testCases = new Escaper[
           testEscapeChars.length 
         + testEscapeStrings.length
         + testEscapeSingleChars.length
         + 1];
       
       testCases[0] = scf.apply();
       
       int i = 1;
       
       for(char escapeChar : testEscapeSingleChars)
           testCases[i++] = scf.apply(escapeChar);
       for(char[] escapeChars : testEscapeChars)
           testCases[i++] = scf.apply(escapeChars);
       for(String specialChars : testEscapeStrings)
           testCases[i++] = scf.apply(specialChars);
    }
    
    @Test
    public void testApply() {
        for(Escaper e : testCases){
            assertNotNull(e);
            assertNotNull(e.asFunction());
        }
    }

    @Test
    public void testDescaper() {
        int i = 0;
        for(Escaper e : testCases){
            for(String[] descaperTest : descaperTests[i]){
                //[0] : correct result
                //[1] : to descape
                assertEquals(descaperTest[0], e.escape(descaperTest[1]));
            }
            i++;
        }
    }
}
