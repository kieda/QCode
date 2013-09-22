package qcode.lib;

import java.util.Arrays;
import java.util.List;

public class Lists {
    /**
     * makes a list from variable arguments
     */
    public static List list(Object... t){
	if(t == null) return null;
	return Arrays.asList(t);
    }
}
