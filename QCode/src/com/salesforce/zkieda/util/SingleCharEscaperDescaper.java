package com.salesforce.zkieda.util;

import java.util.regex.Pattern;

import com.google.common.escape.Escaper;

/**
 * Used to escape a sequence on a single character, and make it go back again. This class produces a 
 * bijective function for escaping and un-escaping (descaping) strings that use a single character 
 * for its escape sequence. Made to be a bit more versatile than other string utility options
 * 
 * <p>Use case: you want to escape some number of characters using a single escape sequence, and be 
 * able to put the characters back again.  This is a {@code SingleCharEscaperDescaper} since we only 
 * escape individual chars, and we only escape them using a single char.    
 * 
 * <p>Note that this class will escape the escape character in a string.
 * 
 * <p>Example usage : 
 * <pre>
 * {@code
 *  
 * EscaperDescaper es = new SingleCharEscaperDescaper('\\').apply('\n', '-');
 * 
 * String result = es.escape("hello \\ world \n-okra\\ delight");
 *      //result   : "hello \\\\ world \\\n\\-okra\\\\ delight"
 * 
 * String decoded = es.descape(result);
 *      //decoded  : "hello \\ world \n-okra\\ delight"
 * 
 * String leftSide = es.escape("left side\\ of\n clause");
 *      //leftSide : "left side\\\\ of\\\n clause");
 *       
 * String descaped = es.descape(leftSide + result);
 *      //descaped : "left side\\ of\n clausehello \\ world \n-okra\\ delight"
 * }
 * </pre>
 * @author zkieda
 * @since 180
 */
public class SingleCharEscaperDescaper extends SingleCharFunc{
    //function for escaping the string, function for descaping the string
    private final SingleCharFunc singleCharEscaper;
    private final SingleCharFunc singleCharDescaper;
    
    /** used to create EscaperDescapers that have '\' as the escape character */
    public static final SingleCharEscaperDescaper BACKSLASH_FUNC 
        = new SingleCharEscaperDescaper(SingleCharEscaper.BACKSLASH_FUNC);
    
    /** 
     * an escaper/descaper that has '\' and '$' as special characters. The escape character is '\'.
     * 
     * <p> this is useful when escaping on the string argument of 
     * {@link java.util.regex.Matcher#appendReplacement(StringBuffer, String)}
     */
    public static final EscaperDescaper BACKSLASH_DOLLAR_CONVERTER
        = BACKSLASH_FUNC.new EscaperDescaperImpl(SingleCharEscaper.BACKSLASH_DOLLAR_ESCAPER);
    
    /**
     * an escaper/descaper that escapes/descapes the standard POSIX special regex characters. 
     * The escape character is '\'.
     * 
     * @see Pattern
     */
    public static final EscaperDescaper POSIX_REGEX_CONVERTER
        = BACKSLASH_FUNC.new EscaperDescaperImpl(SingleCharEscaper.POSIX_REGEX_ESCAPER);
    
    /**
     * internal constructor. Not to be exposed externally.
     */
    private SingleCharEscaperDescaper(SingleCharFunc escaper){
        super(escaper.getEscapeChar());
        this.singleCharEscaper = escaper;
        this.singleCharDescaper = new SingleCharDescaper(getEscapeChar());
    }
    
    /**
     * @param escapeChar the escape character we will use for escaping the string. The escape character 
     * itself is treated as a special character, and is escaped by itself
     */
    public SingleCharEscaperDescaper(char escapeChar){
        super(escapeChar);
        this.singleCharEscaper = new SingleCharEscaper(escapeChar);
        this.singleCharDescaper = new SingleCharDescaper(escapeChar);
    }
    
    
    /**{@inheritDoc}*/
    @Override
    public EscaperDescaper apply(String chars) {
        return (EscaperDescaper) super.apply(chars);
    }
    
    /**
     * @param chars our special characters we will escape
     * @return a bijective function that will escape on our escape char, and will escape all 
     * instances of {@code chars} 
     */
    @Override
    public EscaperDescaper apply(char... chars){
        return new EscaperDescaperImpl(chars);
    }
    
    
    /**
     * @param c the special character we will escape 
     * @return a bijective function for escaping and descaping when we have a single char to escape 
     */
    @Override
    public EscaperDescaper apply(char c){
        return new EscaperDescaperImpl(c);
    }
    
    /**
     * Note that we do not have any special characters that we are escaping here. Thus, the only 
     * special character is the escape char itself, so we escape that.
     * 
     * <p>Usage:
     * <pre>{@code
     *     EscaperDescaper ed = new SingleCharEscaperDescaper('$').apply();
     *     String s = ed.escape("hello$world");         //hello$$world
     *     String r = ed.descape(s);                    //hello$world
     * }</pre>
     * 
     * @return a bijective function for escaping and descaping on a single char  
     */
    @Override
    public EscaperDescaper apply(){
        return new EscaperDescaperImpl();
    }
    
    /** implementation class. */
    private class EscaperDescaperImpl extends EscaperDescaper{
        private final Escaper escaper, descaper;
        
        /** internal constructor. Not to be exposed externally. */
        private EscaperDescaperImpl(Escaper escaper) {
            this.escaper = escaper;
            this.descaper = singleCharDescaper.apply();
        }
        
        private EscaperDescaperImpl() {
            this.escaper = singleCharEscaper.apply();
            this.descaper = singleCharDescaper.apply();
        }
        private EscaperDescaperImpl(char escapeChar) {
            this.escaper = singleCharEscaper.apply(escapeChar);
            this.descaper = singleCharDescaper.apply(escapeChar);
        }
        private EscaperDescaperImpl(char[] chars) {
            this.escaper = singleCharEscaper.apply(chars);
            this.descaper = singleCharDescaper.apply(chars);
        }
        
        @Override
        public String escape(String stringToEscape) {
            return escaper.escape(stringToEscape);
        }
        
        @Override
        public String descape(String escapedString) {
            return descaper.escape(escapedString);
        }
    }
}
