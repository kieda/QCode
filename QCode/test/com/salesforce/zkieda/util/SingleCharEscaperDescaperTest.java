package com.salesforce.zkieda.util;

import static org.junit.Assert.fail;

import org.junit.Test;

public class SingleCharEscaperDescaperTest {

    @Test
    public void testOn() {
        fail("Not yet implemented");
    }

    @Test
    public void testEscapeString() {
        fail("Not yet implemented");
    }

    @Test
    public void testEscapeCharArray() {
        fail("Not yet implemented");
    }

    @Test
    public void testEscapeChar() {
        fail("Not yet implemented");
    }

    @Test
    public void testEscapeSelf() {
        fail("Not yet implemented");
    }
    
    public static void main(String[] args) {
        EscaperDescaper es = SingleCharEscaperDescaper
                .on('\\')
                .escape("[]");
        
        
         
        String result = es.escape("hello \\ world []okra\\ delight");
             //result   : "hello \\\\ world \\[\\]okra\\\\ delight"
        
        System.out.println(result);
        System.out.println(es.descape(result));
    }
}
