#--
	import java.util.regex.*;
	import java.util.function.*;
--#
f2 f = new f2(2);
List l = new ArrayList();

for(int i = 0; i<10; i++){
    l.add(f.add(i));
    f.dec();
}
Runnable r = (() -> {System.out.println("sadf");});
for(int i = 0; i <1000; i++)
    r.run();

return "asdf";
<--
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