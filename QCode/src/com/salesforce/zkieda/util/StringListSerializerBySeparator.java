package com.salesforce.zkieda.util;

import java.util.*;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;


public class StringListSerializerBySeparator implements StringListSerializer {
    private final Joiner joiner;
    private final EscaperDescaper esd;
    private final char escape, splitterChar;
    
    private List<String> split(String stringToSplit){
        List<String> re = new ArrayList<>();
        int startPos = 0;
        for(int i = 0; i < stringToSplit.length();){
            char c = stringToSplit.charAt(i);
            if(c==escape){
                i+=2;
            }else if(c==splitterChar){
                String string = stringToSplit.substring(startPos, i);
                re.add(string);
                startPos = (++i);
            }else{
                i++;
            }
        }
        re.add(stringToSplit.substring(startPos));
        return re;
    }
    
    
    private StringListSerializerBySeparator(EscaperDescaper es, char escape, char splitterChar){
        this.esd = es;
        this.splitterChar = splitterChar;
        this.escape = escape;
        this.joiner = Joiner.on(splitterChar);
    }
    
    @Override
    public List<String> get(String stringToSplit){
        return Lists.transform(
                split(stringToSplit),
                esd.asReverseFunction());
    }
    
    @Override
    public String put(List<String> stringToSplit){
        return joiner.join(Lists.transform(stringToSplit, esd.asFunction()));
    }
    
    public static StringListSerializerBuilder on(char escapeChar, char splitterChar){
        if(escapeChar==splitterChar) throw new IllegalArgumentException("Cannot have splitter char and escape char be identical in list serialization!");
        return new StringListSerializerBuilder(escapeChar, splitterChar);
    }
    
    public static class StringListSerializerBuilder{
        private final char escapeChar, splitterChar;
        private final SingleCharEscaperDescaper factory;
        private boolean failOnUnrecognized = false;
        private StringListSerializerBuilder(char escapeChar, char splitterChar){
            this.escapeChar = escapeChar;
            this.splitterChar = splitterChar;
            factory = new SingleCharEscaperDescaper(escapeChar);
        }
        
        public StringListSerializerBySeparator build(String specialChars){
            return new StringListSerializerBySeparator(factory.apply(specialChars), escapeChar, splitterChar);
        }                                                                          
        public StringListSerializerBySeparator build(char... specialChars){                   
            return new StringListSerializerBySeparator(factory.apply(specialChars), escapeChar, splitterChar);
        }                                                                          
        public StringListSerializerBySeparator build(){                                       
            return new StringListSerializerBySeparator(factory.apply(splitterChar), escapeChar, splitterChar);
        }
        public StringListSerializerBuilder failOnUnrecognized(boolean failOnUnrecognized) {
            this.failOnUnrecognized = failOnUnrecognized;
            return this;
        }
    }
}

class StringListSerializerTest{
    public static void main(String[] args) {
        
        int i = 267452175;
        
        char lHalf = (char)(i&0xFFFF);
        char uHalf = (char)((i>>16)&0xFFFF);
        
        System.out.println(Integer.toHexString(lHalf));
        System.out.println(Integer.toHexString(uHalf));
        
        char c1 = (char)0xff0f;
        char c2 = (char)0x0ff0;
        int lowerHalf = c1;
        int upperHalf = c2;
        
        int length = (upperHalf<<16)|(lowerHalf&0xFFFF);
        System.out.println(length);
        
        
        StringListSerializer ser 
         = SimplePairStringListSerializer.INSTANCE;
//        = StringListSerializerBySeparator
//                .on('\\', '|')
//                .build();
        String[] tests = {
            ser.put(Arrays.asList("\\asdf", "sa\\df")), 
            ser.put(Arrays.asList("\\asdf\\", "\\sa\\df")),
            ser.put(Arrays.asList("\\asdf\\", "\\sa|\\df")),
            ser.put(Arrays.asList("\\asdf\\", "|sa\\df")),
            ser.put(Arrays.asList("\\asdf\\", "|sa\\df")),
            ser.put(Arrays.asList("\\asdf|", "|sa\\df")),
//            ser.put(Arrays.asList("\\asdf||sa\\df")),
//            ser.put(Arrays.asList("\\asdf|", "|sa\\df", "\\\\||\\"))
        };
//        
        for(String test : tests)
            System.out.println(ser.get(test));
    }
}