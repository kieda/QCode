package com.salesforce.zkieda.util;

import com.google.common.base.Preconditions;
import com.google.common.escape.CharEscaperBuilder;
import com.google.common.escape.Escaper;

/**
 * Class for escaping using a single char
 * 
 * @see SingleCharEscaperDescaper
 * 
 * @author zkieda
 * @since 180
 */
public class SingleCharEscaper extends SingleCharFunc{
    
    //used for escaping with patterns that use '\' as an escape character
    static final SingleCharFunc BACKSLASH_FUNC = new SingleCharEscaper('\\'); 
    static final Escaper POSIX_REGEX_ESCAPER      = BACKSLASH_FUNC.apply(".^$*+?()[{\\|");  //escaping POSIX regex special chars
    static final Escaper BACKSLASH_DOLLAR_ESCAPER = BACKSLASH_FUNC.apply("$\\");            //escaping on java.util.regex.Matcher#appendReplacement
    
    public SingleCharEscaper(char escapeChar){
        super(escapeChar);
    }
    
    @Override
    public Escaper apply(char... chars){
        Preconditions.checkNotNull(chars);
        
        CharEscaperBuilder charEscapeBuilder = make();
        for (char c : chars) {
            addCharEscape(charEscapeBuilder, c);
        }
        return charEscapeBuilder.toEscaper();
    }
    @Override
    public Escaper apply(char c){
        return make()
           .addEscape(c, charEscape(c))
           .toEscaper();
    }
    
    @Override
    public Escaper apply(){
        return make().toEscaper();
    }
    
    private CharEscaperBuilder make(){
        return addCharEscape(new CharEscaperBuilder(), getEscapeChar());
    }
    
    private CharEscaperBuilder addCharEscape(CharEscaperBuilder charEscapeBuilder, char escapedVal){
        return charEscapeBuilder.addEscape(escapedVal, charEscape(escapedVal));
    }
    
    private String charEscape(char escapedVal){
        char[] chars = {getEscapeChar(), escapedVal};
        return new String(chars);
    }
}