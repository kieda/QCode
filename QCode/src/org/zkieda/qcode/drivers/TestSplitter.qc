#--
import drivers.*;
--#

sout("asd");
Class c = Class.forName("drivers.$Pool$");
sout(c);
        System.out.println(
            Arrays.asList(
            Class.forName("drivers.$Pool$").getDeclaredMethods()
        ));
@--
class C{
    $Pool2$ p = null;
    static {
        System.out.println(
            Arrays.asList(
            $Pool2$.class.getDeclaredMethods()
        ));
    }
}
--@