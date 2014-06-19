package com.salesforce.zkieda.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;
import com.google.common.escape.Escaper;

/** 
 * class for de-escaping sequences of chars 
 * @see SingleCharEscaperDescaper
 */
public class SingleCharDescaper extends SingleCharFunc{
    public SingleCharDescaper(char escapeChar){
        super(escapeChar);
    }
    
    @Override
    public Escaper apply(char... chars){
        return new CharDescaper(chars);
    }
    
    /** implementation for descaping the single char */
    private class CharDescaper extends Escaper {
        private final Pattern pattern;
        
        public CharDescaper(char... escapeChars){
            final String escapeCharAsString = String.valueOf(getEscapeChar());
            //escape the escape char in case it is a unicode special char
            final String escapedEscapeChar = SingleCharEscaper
                    .POSIX_REGEX_ESCAPER
                    .escape(escapeCharAsString);
            
            //our other chars that we want to escape
            final String[] escapedEscapedChars = new String[escapeChars.length+1];
            
            //the first should be our escape char
            escapedEscapedChars[0] = SingleCharEscaper
                    .POSIX_REGEX_ESCAPER
                    .escape(escapeCharAsString);
            
            //the rest should be in the escape chars
            int i = 1;
            for (char s : escapeChars) {
                escapedEscapedChars[i++] = SingleCharEscaper
                        .POSIX_REGEX_ESCAPER
                        .escape(String.valueOf(s));
            }
            
            String compiledPattern = String.format("%s(%s)", 
                escapedEscapeChar,
                Joiner.on('|')
                    .join(
                        escapedEscapedChars
                    )
            );
            pattern = Pattern.compile(compiledPattern);
        }
        
        @Override
        public String escape(String string) {
            Matcher m = pattern.matcher(string);
            //match our escaped string. We replace each instance of an 
            //escaped char with its value
            
            //we allocate a buffer of size string.length() since  
            //it's a good approximation for the number of chars in the 
            //descaped string
            StringBuffer buffer = new StringBuffer(string.length());
            while(m.find()){
                //append replacement treats '\' and '$' as special chars.
                //we have to escape them with '\' to make them show up 
                //literally
                m.appendReplacement(buffer,
                        SingleCharEscaper
                            .BACKSLASH_DOLLAR_ESCAPER
                            .escape(String.valueOf(string.charAt(m.end()-1)))
                    );
            }
            
            m.appendTail(buffer);
            
            return buffer.toString();
        }
    }
}
