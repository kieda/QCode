QCode 2.0
=====

QCode is a dynamic programming environment for java, allowing for faster development and bug testing. QCode detects changes within a text file, and complies and runs it on the fly. This allows the user to dynamically add and change methods, classes, and fields. The QCode application is very modular, allowing many different compilation options depending how you set it up.

Check out the code! You can set it up for different builds to do vastly different things!

Note that if one opens a QCode instance on the same jvm and classloader, the QCode application should have access to the classes of the project that opened it. This allows us to 'scaffold' a project by building off an existing one, and allows us to test existing code quickly.

Some QCode builds use QLang, a bit of a faster way to write and test basic java code. QLang is typically in a '.qc' file. In QLang, the outermost block of syntax is a no-arg, non-static main method. We run the main method by default. The returned value of this method is displayed as the program's result, where null means no result. We can add methods, fields, and internal classes by the tags
```
    "<--" and "-->"
```
We can add classes by the tags
```
    "@--" and "--@"
```
We can add imports and set the package by the tags
```
    "#--" and "--#"`
```
Currently, the default ServerInputStreamListener assumes the file we are watching is in QLang 

Example QLang program : 
=====
```java
#-- import java.util.* --#

List k = new DoubleList();
for(int i = 0; i < 5; i++)
    k.add(i);

return doubleString(k.toString());

<-- 
String sep = " : "
public String doubleString(String s){
        return s + sep +  s;
} -->
@-- class DoubleList<T> extends ArrayList<T>{
    @Override public void add(T e){
        super.add(e); super.add(e);
    }
} --@
```
This should give the output :
```java "[0, 1, 2, 3, 4] : [0, 1, 2, 3, 4]" ```

QCode 2.0 vs 1.0
=====
This section refers to QCode 1.0, and the differences from 1.0 to 2.0.

In 1.0 we set up two JFrames, and used one to type into a text area, and another to display the output. The text area would detect change, recompile, and rerun. Though this was simple and nice, it was unfortunately not easily expandable, since a text area lacks most of the functionality people would like to see in a full development environment, and making a decent one would take a significant amount of time. In addition, the two components were very tied together. We decided to go with a bit more modular approach for 2.0.

As of Java 7.0, new libraries were introduced which allows the JVM to moniter file changes that have occurred in a directory of a file system. We decided to use this to detect file changes, and when the changes occur we recompile and rerun them. We then route the output to local sockets, and listeners can read  the output. 

In addition, we made some significant refactoring to make QCode as modular and expansive as possible. The input and output systems are all abstract layers, so there are some significant implications for future builds - we can change just individual components, have the whole system still work, and have vastly different functionality. 

Here's what's in store for QCode 2.2: 

Watching on plain java: We will watch on a full directory for changes and autoloading java from the directory. This will be powerful in Aura development - not only will we be able to dynamically create aura components and javascript controllers, we will also be able to dynamically create java Models and Components for the aura components. 

Watching with a ScriptEngineManager. We don't have to just use java! We cam use the jvm to listen on javascript (or even haskell, or more) files, then use Rhino (or Jaskell) to run the files and produce the output.

A graphical tool for the QCode output. This tool will listen to the sockets, and provide the user with streamed output from the server.

Here's what's planned for future versions of QCode : 

A markup system for the QCode output. We will expose a library in QCode which will allow the users to send specific markup tags while the program is running. For example, we could have QCode output be represented as an html file. The listening program would be a web browser, and we can send an html file and dynamically send the output stream and error stream to any connected browser. 

In these future versions, a user just creates a customized 'qcode build' by composing a QCodeServer from a bunch of independent components. 

Bugs
=====

Eclipse occasionally gives a hotswap code error. This seems to happen after the jvm classloader disposes of a class, but the class no longer exists since we created it virtually. 

From the tests I have run, QCode still works and runs after we receive the error message.
