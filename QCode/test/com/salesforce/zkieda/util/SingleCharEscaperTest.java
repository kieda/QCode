package com.salesforce.zkieda.util;

import static com.salesforce.zkieda.util.SingleCharDescaperTest.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.google.common.escape.Escaper;

/**
 * Test {@link SingleCharEscaper}
 *
 * @author zkieda
 * @since 180
 */
public class SingleCharEscaperTest {
    //we import the tests from descaper, since those 
    //have shown to work
    
    Escaper[] testCases; 
    SingleCharFunc scf;
    

    
    @Before
    public void init(){
       scf =  new SingleCharEscaper(escapeChar);
       
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
    public void testEscaper() {
        int i = 0;
        for(Escaper e : testCases){
            for(String[] descaperTest : descaperTests[i]){
                //[1] : correct result
                //[0] : to escape
                assertEquals(descaperTest[1], e.escape(descaperTest[0]));
            }
            i++;
        }
    }

}
