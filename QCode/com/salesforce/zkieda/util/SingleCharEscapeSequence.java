package com.salesforce.zkieda.util;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.*;
import com.google.common.escape.CharEscaperBuilder;
import com.google.common.escape.Escaper;

/**
 * Used to escape a sequence on a single character. Made to be a bit more versatile than 
 * other string utility options<br/><br/>
 * 
 * This class is used for : you want to escape some number of characters using a single 
 * escape sequence. This is a {@code SingleCharEscapeSequence} since we only escape 
 * individual chars, and we only escape them using a single char.    
 * 
 * Example usage : 
 * <pre>
 * {@code
 *  
 * Escaper es = SingleCharEscapeSequence
 *      .on('\\').escapeOn('\n', '-');
 * 
 * String result = es.escape("hello \\ world \n-okra\\ delight");
 *      //result   : "hello \\\\ world \\\n\\-okra\\\\ delight"
 * 
 * String decoded = es.unescape(result);
 *      //decoded  : "hello \\ world \n-okra\\ delight"
 * 
 * String leftSide = es.escape("left side\\ of\n clause");
 *      //leftSide : "left side\\\\ of\\\n clause");
 *       
 * 
 * }
 * </pre>
 * 
 *
 * @author zkieda
 * @since 180
 */
class SingleCharEscaper {
    //an escaper that only escapes and does not descape.
    //this is used since we use regular expressions to descape strings 
    static final Escaper POSIX_REGEX_ESCAPER = on('\\').escape(".^$*+?()[{\\|");
    static final Escaper BACKSLASH_DOLLAR_ESCAPER = on('\\').escape("$\\");
    
    private final char escapeChar;
    
    /**
     * Currently the only entry point.
     * 
     * @param escapeChar
     * @return
     */
    public static SingleCharEscaper on(char escapeChar){
        return new SingleCharEscaper(escapeChar);
    }
    
    private SingleCharEscaper(char escapeChar) {
        this.escapeChar = escapeChar;
    }

    
    public Escaper escape(String chars){
        return escape(Objects.requireNonNull(chars).toCharArray());
    }
    public Escaper escape(char... chars){
        Preconditions.checkNotNull(chars);
        
        CharEscaperBuilder charEscapeBuilder = make();
        for (char c : chars) {
            addCharEscape(charEscapeBuilder, c);
        }
        return charEscapeBuilder.toEscaper();
    }
    public Escaper escape(char c){
        return make()
           .addEscape(c, charEscape(c))
           .toEscaper();
    }
    
    public Escaper escapeSelf(){
        return make().toEscaper();
    }
    
    private CharEscaperBuilder make(){
        return addCharEscape(new CharEscaperBuilder(), escapeChar);
    }
    
    private CharEscaperBuilder addCharEscape(CharEscaperBuilder charEscapeBuilder, char escapedVal){
        return charEscapeBuilder.addEscape(escapedVal, charEscape(escapedVal));
    }
    
    private String charEscape(char escapedVal){
        char[] chars = {escapeChar, escapedVal};
        return new String(chars);
    }
}

class SingleCharEscapeSequenceTest{
    public static void main(String[] args) {
        EscaperDescaper es = SingleCharEscaperDescaper
                .on('\\')
                .escapeSelf();
        
        
        
        
        String result = es.escape("hell\\o w\\orld");
        
        System.out.println(
            result
        );
        System.out.println(
            es.descape(result)
        );
    }
}
class SingleCharEscapeDescapeTest{
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
class SingleCharEscaperDescaper{
    private final char escapeChar;
    private final SingleCharEscaper es;
    private final SingleCharDescaper ds;
    private SingleCharEscaperDescaper(char escapeChar){
        this.escapeChar = escapeChar;
        this.es = SingleCharEscaper.on(escapeChar);
        this.ds = SingleCharDescaper.on(escapeChar);
    }
    
    public static SingleCharEscaperDescaper on(char escapeChar){
        return new SingleCharEscaperDescaper(escapeChar);
    }
    
    public EscaperDescaper escape(String chars){
        return escape(Objects.requireNonNull(chars).toCharArray());
    }
    public EscaperDescaper escape(char... chars){
        return new EscaperDescaperImpl(
                es.escape(chars), ds.escape(chars)
            );
    }
    public EscaperDescaper escape(char c){
        return new EscaperDescaperImpl(
                es.escape(c), ds.escape(c)
            );
    }
    
    public EscaperDescaper escapeSelf(){
        return new EscaperDescaperImpl(
                es.escapeSelf(), ds.escapeSelf()
            );
    }
}
class EscaperDescaperImpl implements EscaperDescaper{
    private final Escaper es, ds;
    EscaperDescaperImpl(Escaper es, Escaper ds) {
        this.es = es;
        this.ds = ds;
    }
    
    @Override
    public String escape(String stringToEscape) {
        return es.escape(stringToEscape);
    }
    
    @Override
    public String descape(String escapedString) {
        return ds.escape(escapedString);
    }
    @Override
    public Function<String, String> asEscapeFunction() {
        return es.asFunction();
    }
    @Override
    public Function<String, String> asDescapeFunction() {
        return ds.asFunction();
    }
}

class SingleCharDescaper {
    private final char escapeChar;
    
    private SingleCharDescaper(char escapeChar){
        this.escapeChar = escapeChar;
    }
    
    public static SingleCharDescaper on(char escapeChar){
        return new SingleCharDescaper(escapeChar);
    }
    
    public Escaper escape(String chars){
        return escape(Objects.requireNonNull(chars).toCharArray());
    }
    public Escaper escape(char... chars){
        return new CharDescaper(chars);
    }
    public Escaper escape(char c){
        return new CharDescaper(c);
    }
    
    public Escaper escapeSelf(){
        return new CharDescaper();
    }
    
    private class CharDescaper extends Escaper {
        private final Pattern pattern;
        public CharDescaper(char... escapeChars){
            final String escapedEscapeChar = SingleCharEscaper.POSIX_REGEX_ESCAPER.escape(String.valueOf(escapeChar));
            final String[] escapedEscapedChars = new String[escapeChars.length+1];
            escapedEscapedChars[0] = SingleCharEscaper.POSIX_REGEX_ESCAPER.escape(String.valueOf(escapeChar));
            int i = 1;
            for (char s : escapeChars) {
                escapedEscapedChars[i++] = SingleCharEscaper.POSIX_REGEX_ESCAPER.escape(String.valueOf(s));
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
            
            //we allocate a buffer of size string.length() since  
            //it's a good approximation for the number of chars in the 
            //descaped string
            StringBuffer buffer = new StringBuffer(string.length());
            while(m.find()){
                m.appendReplacement(buffer,
                        SingleCharEscaper.BACKSLASH_DOLLAR_ESCAPER.escape(String.valueOf(string.charAt(m.end()-1)))
                    );
            }
            
            m.appendTail(buffer);
            
            return buffer.toString();
        }
    }
}

interface EscaperDescaper {
    public String escape(String stringToEscape);
    public String descape(String escapedString);
    public Function<String, String> asEscapeFunction();
    public Function<String, String> asDescapeFunction();
}

//
///**
// * Used in {@link SingleCharEscapeSequence} when we make a multi char escaper with low 
// * sparseness. 
// * 
// * @author zkieda
// * @since 180
// */
//class ArrayBackedMultiEscaper implements Escaper{
//    private static final Esc
//}
//
///**
// * Used in {@link SingleCharEscapeSequence} when we make a multi char escaper with higher
// * sparseness. 
// *
// * @author zkieda
// * @since 180
// */
//class MapBackedMultiEscaper implements Escaper{
//    
//}
//
//class SingleCharEscaper  implements Escaper{
//    private final char escape;
//    private final CharMatcher matcher;
//    private final String replacement;
//    public SingleCharEscaper(char escape){
//        this.replacement = new String(new char[]{escape, escape});
//        this.escape = escape;
//        this.matcher = CharMatcher.is(escape);
//    }
//    @Override
//    public String escape(CharSequence cs) {
//        return matcher.replaceFrom(cs, replacement);
//    }
//
//    @Override
//    public String unescape(CharSequence cs) {
//        
//        return null;
//    }
//
//    @Override
//    public List<CharSequence> split(CharSequence cs) {
//        // TODO Auto-generated method stub
//        return null;
//    }
//}
//
//class SingleCharEscapeSequenceTest{
//    
//}
