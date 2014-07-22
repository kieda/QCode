#--
	import java.util.regex.*;
	import java.util.function.*;
	
--#

/**
 * 
 * TODO : 
 *   1) fancy output - no one will care about special extensionality till later
 *   2) have interface for accessing these classes - 
 *              delegatingClassloader.loadClass("dynamic.class.name");
 *              (see : jci ReloadingClassLoader, but make it better)
 *   3) have method for linking dynamic classes to each-other
 *      i.e. have
 *      
 *      //#link drivers.Pool; // will tell the compiler to include in same compilation unit (earlier version)
 *      //#use drivers.Pool; // will tell the compiler to access drivers.Pool via reflection. 
 *      later ...
 *      references not found we load up via reflection. 
 *          - when we add/reload a class, we search the delegate for items in the same scope. Load imports, things in same package, etc.
 *            store via some efficient DB or representation?
 *          - when loading up classes, look through our dynamic classloader
 */
f2 f = new f2(2);
List l = new ArrayList();

for(int i = 0; i<10; i++){
    l.add(f.add(i));
    f.dec();
}

Runnable r = (() -> {System.out.println("sadf");});
A a = () -> {};

for(int i = 0; i <1000; i++)
    r.run();

l = new ArrayList();
l.add(2);
return l + " "; 
<--
    static interface A{
        void b();
    }
    int a(int a, int b){
        return a+b;
    }
-->

@--
    class f2{
        private final int a;
        private int k = 10;
        public f2(int a){
            this.a = a;
        } 
        
        public int add(int b){
            return a+b+k;
        }
        public void dec(){
            k-=5;
        }
    }
    
--@