#-- 
	import java.io.*;
	import static com.salesforce.zkieda.util.ThreadIO.*;
	import com.salesforce.zkieda.util.*;
--#


File f = new File("./test-out");
sout(f.exists()); // should print "true" to sys err

sout("hello world!");

//output on this thread redirected to file
dup2(System.out, new PrintStream(new FileOutputStream(f)));

new PrintStreamThread(
   new Runnable(){
       public void run(){
           sout("############");
       }
   }
).dup2(System.out, ThreadIO.OUT)
 .start();
 


List k = new ArrayList();
for(char c = 'a'; c < 'z'; c++)
	k.add(c);
	
List l = new ArrayList();

for(int i = 0; i < 10; i++){
	if(i == 5) l.add(k);
	else l.add(i);
}

l.add(fib(5));

C t = new C(4, fib(20));
System.out.println("test err");
System.err.println("test out");

//should be swapped.
sout(
	mult("$", 100)
);
return "asdfasdf";

<-- 
int fib(int i){
	if(i == 0 || i == 1) return i;
	else return fib(i-1) + fib(i-2);
}


int asdf(int k){
	if(k == 0) return 1;
	return k * asdf(k-1);
}

String mult(String a, int times){
	StringBuilder s = new StringBuilder(a.length() * times);
	for(int i = 0; i < times; i++)
		s.append(a);
	return s.toString();
}
--> 


<--
class C {
	private int i1, i2;
	public C(int i1, int i2){
		this.i1 = i1;
		this.i2 = i2;
	}
	public int getSum(){
		return i1 + i2;
	}
}
-->

@--
class Main{
	public Main(){
		$Pool$.sout("nope");
	}
}
--@