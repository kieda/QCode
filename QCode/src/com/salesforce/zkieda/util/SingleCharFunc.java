package com.salesforce.zkieda.util;

import java.util.Objects;

import com.google.common.base.Function;
import com.google.common.escape.Escaper;

/**
 * Takes a single char as an escape character, then some char[] for the escape 
 * vals and builds an {@link Escaper}, which is a {@code String} to {@code String}
 * function.<br/><br/>
 * 
 * Typically, or at least in this package, we use this class to escape or un-escape
 * strings with single character escape sequences. Most notably, in java strings we have
 * things like <br/>
 * <pre>
 * {@code
 *    NEW LINE -> \n
 *    "        -> \"
 *    \        -> \\
 * }
 * </pre>
 * 
 * Notice how all of the escape sequences begin with the char '\'. In this use case, we 
 * just want to have one single "{@code escapeChar}" that is placed before some set of 
 * escape sequences.<br/><br/> 
 * 
 * A more generic use case : this class can be used to express a curried function of the 
 * type {@code char -> char[] -> (String -> String)}<br/><br/>
 * 
 * More specifically, after we give a {@code char} argument from the construcor, then 
 * after we give a {@code char[]} argument from our {@code apply} method, we create when 
 * {@code String -> String} function (an {@link Escaper}). 
 *
 * @author zkieda
 * @since 180
 * @see SingleCharEscaper
 * @see SingleCharDescaper
 * @see SingleCharEscaperDescaper
 */
public abstract class SingleCharFunc implements Function<char[], Escaper>{
    //a single char - usually the escape char
    private final char escapeChar;
    
    /**
     * @param escapeChar the char we want to use as an escape character 
     */
    protected SingleCharFunc(char escapeChar) {
        this.escapeChar = escapeChar;
    }
    
    /**
     * @return the char we will use as an escape character
     */
    public char getEscapeChar() {
        return escapeChar;
    }
    
    /* utility methods that can be overridden */
    
    public Escaper apply(){return apply();}
    public Escaper apply(char c){return apply(c);}
    public Escaper apply(String chars){
        return apply(Objects.requireNonNull(chars).toCharArray());
    }

    /**
     * @param chars the special chars
     * @return a {@code (String -> String)} function 
     */
    @Override
    public abstract Escaper apply(char... chars);
}
