#--
	import org.zkieda.qcode.util.*;
	import java.util.regex.*;
--#


Pattern.compile("(?<!a)b");


{
	StringListSerializer ser = StringListSerializer
	        .on('\\', '|')
	        .build();
	
	String[] tests = {
		ser.put(Arrays.asList("\\asdf", "sa\\df")), 
		ser.put(Arrays.asList("\\asdf\\", "\\sa\\df")),
		ser.put(Arrays.asList("\\asdf\\", "\\sa|\\df")),
		ser.put(Arrays.asList("\\asdf\\", "|sa\\df")),
		ser.put(Arrays.asList("\\asdf\\", "|sa\\df")),
		ser.put(Arrays.asList("\\asdf|", "|sa\\df"))
	};
	
	for(String test : tests)
		System.out.println(ser.get(test));
}