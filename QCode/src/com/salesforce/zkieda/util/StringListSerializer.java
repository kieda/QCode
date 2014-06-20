package com.salesforce.zkieda.util;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.*;



public class StringListSerializer {
    private final Splitter splitter;
    private final Joiner joiner;
    private final EscaperDescaper esd;
    private static String regexEscape(char c){
        return SingleCharEscaper.POSIX_REGEX_ESCAPER.escape(String.valueOf(c));
    }
    
    private StringListSerializer(EscaperDescaper es, char escape, char splitterChar){
        this(es, Joiner.on(splitterChar), regexEscape(escape), regexEscape(splitterChar));
    }
    
    private StringListSerializer(EscaperDescaper es, Joiner j, String escapedEscapeChar, String escapedSplitterChar){
        //example : '|' is separator, '\' is escape
        //"asd|" "|" "|sda"  ->  "asd\||\|sda"
        
        //our pattern should split on "|" but not on "\|"
        //pattern (POSIX) : [^\\]\\|
        
        
        this.esd = es;
        this.joiner = j;
        this.splitter = Splitter.onPattern(
                String.format(
                    "(?=[^%s])%s",
                    escapedEscapeChar,
                    escapedSplitterChar
                ));
    }
    
    
    
    public ImmutableList<String> get(String stringToSplit){
        return ImmutableList.copyOf(
                Iterables.transform(
                splitter.split(stringToSplit),
                esd.asReverseFunction())
            );
    }
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
        
        private StringListSerializerBuilder(char escapeChar, char splitterChar){
            this.escapeChar = escapeChar;
            this.splitterChar = splitterChar;
            factory = new SingleCharEscaperDescaper(escapeChar);
        }
        
        public StringListSerializer build(String specialChars){
            return new StringListSerializer(factory.apply(specialChars), escapeChar, splitterChar);
        }                                                                          
        public StringListSerializer build(char... specialChars){                   
            return new StringListSerializer(factory.apply(specialChars), escapeChar, splitterChar);
        }                                                                          
        public StringListSerializer build(){                                       
            return new StringListSerializer(factory.apply(splitterChar), escapeChar, splitterChar);
        }
    }
}
class StringListSerializerTest{
    public static void main(String[] args) {
        StringListSerializer ser = StringListSerializer
                .on('\\', '|')
                .build();
        String test = ser.put(Arrays.asList("asdf", "sadf"));
        System.out.println(test);
        
        System.out.println(ser.get(test));
        
    }
}