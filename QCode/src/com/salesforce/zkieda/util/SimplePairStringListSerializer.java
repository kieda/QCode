package com.salesforce.zkieda.util;

import java.util.Arrays;
import java.util.List;

public class SimplePairStringListSerializer implements StringListSerializer{    
    public static final SimplePairStringListSerializer INSTANCE
        = new SimplePairStringListSerializer();
    @Override
    public List<String> get(String stringToSplit) {
        if(stringToSplit.length()<2) 
            throw new IndexOutOfBoundsException("Cannot split a string less than length 2!");
        
        int lowerHalf = stringToSplit.charAt(0);
        int upperHalf = stringToSplit.charAt(1);
        
        int length = (upperHalf<<16)|(lowerHalf&0xFFFF) + 2;
        if(length > stringToSplit.length())
            throw new IndexOutOfBoundsException("Expected longer string!");
        
        
        String s1 = stringToSplit.substring(2, length);
        String s2 = stringToSplit.substring(length);
        return Arrays.asList(s1, s2);
    }

    @Override
    public String put(List<String> strings) {
        if(strings.size()!=2){
            throw new IllegalArgumentException("This class can only serialize lists of length 2!");
        }
        return put(strings.get(0), strings.get(1));
    }
    
    
    public String put(String val1, String val2) {
        int length = val1.length();
        
        char lowerHalf = (char)(length&0xFFFF);
        char upperHalf = (char)((length>>16)&0xFFFF);
        
        return new StringBuilder(val1.length()+val2.length()+2)
            .append(lowerHalf).append(upperHalf)
            .append(val1)
            .append(val2)
            .toString();
    }
}
