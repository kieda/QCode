package com.salesforce.zkieda.util;

import static org.junit.Assert.fail;

import org.junit.Test;

public class SingleCharEscapeSequenceTest {

    @Test
    public void test() {
        fail("Not yet implemented");
    }
    public static void main(String[] args) {
        EscaperDescaper es = SingleCharEscaperDescaper
                .on('\\')
                .escape();
        
        
        
        
        String result = es.escape("hell\\o w\\orld");
        
        System.out.println(
            result
        );
        System.out.println(
            es.descape(result)
        );
    }
}
