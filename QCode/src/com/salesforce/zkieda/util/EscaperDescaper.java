package com.salesforce.zkieda.util;

import com.google.common.base.Function;
import com.google.common.escape.Escaper;

/**
 * An extension of escaper that also provides functionality for un-escaping strings<br/><br/>
 * 
 * Note that implementing classes should hold the following contract: 
 * <pre>
 * {@code
 *      1) {@link EscaperDescaper#escape(String)} and {@link EscaperDescaper#descape(String)} are pure
 *      2) For any non-null String s :  descape(escape(s)) is equal to s
 * }
 * </pre>
 * 
 * <p>Known implementing classes {@link SingleCharEscaperDescaper}
 *
 * @author zkieda
 * @since 180
 * @see SingleCharEscaperDescaper
 */
public abstract class EscaperDescaper extends Escaper{
    /** Constructor for use by subclasses. */
    protected EscaperDescaper() {}
  
    private final Function<String, String> asReverseFunction =
        new Function<String, String>() {
              @Override
              public String apply(String from) {
                  return descape(from);
              }
        };
    
    /**
     * Returns a {@link Function} that invokes {@link #escape(String)} on this escaper.
     */
    public final Function<String, String> asReverseFunction(){
        return asReverseFunction;
    }
    
    /**
     * Returns the unescaped form of a given escaped string.
     * 
     * <p> If there are unescaped literals in {@code string} that would normally be escaped 
     * in an escaped string, we do nothing about them
     * 
     * <p> Example, escape chars in POSIX strings
     * <pre>
     * {@code
     *     descape("\\\\") -> "\\"
     *     descape("\\|") -> "|"
     *     descape("|") -> "|"
     *     descape("\\\\hello\\|world|as") -> "\\hello|world|as"
     * }
     * </pre>
     * 
     * @param string the literal string to be escaped
     * @return the escaped form of {@code string}
     * @throws NullPointerException if {@code string} is null
     */
    public abstract String descape(String escapedString);
}