Intro
=====

QLang is a format used for quickly testing and running java code. This format is supported by qcode, and is easy to pick up for any person that knows java.

In this file we run through the basics of QLang. Note that this is the languages first iteration, and is subject to change.

Folder info : 
    An example QCode build that runs a qcode file is located in "QLangExample.java"
    An example QLang run is located in "QLangExample.qc"

    QLangExample.java listens to the file QLangExample.qc, and runs it on any file modifications


QLang (1.0)
=====

In reality, QLang is just a file parsed into a java file. We parse a file, put it into a java class file. We then instantiate the object and run an entry point method. 

A QLang file is composed of several sections of code - the 'main block', the 'class block', the 'method block', and the 'header block'. 


All of the code in the main block run as if it were a `public`, non-`static` method `main()`.

All of the code in the class block are put outside of the main, public class. This should be used for non-`public` class definitions. 

All of the code in the method block is placed right outside, before the main method. This is typically used for declaring any fields, methods, or other class-level declarations.

All of the blocks in the header block are placed 

Note that we may have multiple blocks of the same type in different sections of code. QCode joins all of these blocks together sequentially.



QLang (2.0) - not yet implemented - not finalized - DO NOT READ
=====

We will support method calls that will be run when preprocessing the file. For example, we could have something like

```
	#{setClassName "HelloWorld"}
	#{setPackage "com.helloworld"}
```

This will allow us to just define java methods as part of the qcode system, then files processed by qcode will be able to use them automatically. For example, we could make our own definition

```java
package com.really.long.package.name;

@QModule(as="Basic")
class BasicModule{
	//no-arg constructor
	@QMethod public void test(){
		
	}
}
```

This can be used with
```
	#{@Basic.test}  //either
	#{com.really.long.package.name.BasicModule.test} //or
	
	//or even
	#{@Use com.really.long.package.name.*}
	#{BasicModule.test}
```

The "@Use" is an example usage of a module with the optional 'method' parameter.
We can set the default method that a user uses for convenience.

```java
@QModule(as="Use", method="preprocessorImport")
class PreprocessorImporter{
	//note that all overloadable methods can be used in the module
	@QMethod public void preprocessorImport(Package... imports){
		//...
	}
	@QMethod public void preprocessorImport(String... imports){
		//...
	}
}
```

Notice that this opens up the the option of C-like macro definitions. This will remain unimplemented in qcode specifically, since they can be used as an anti-pattern.

One thing I want to implement, however, is the classic "[[ ... ]]" pattern. This will allow a user to iterate within a specific scope.

```
	{@Use com.really.long.package.[[*, name.*, static name.BasicModule.*]]}
	System.out.println([["hello", "world"]]);
	
```

THIS IMPLIES THAT WE CAN LITERALLY BUILD DYNAMICALLY COMPILED LANGUAGES ON TOP OF THE JVM. If we have enough of an existing framework, we will be able to make enough definitions about how an arbitrary language is parsed and run.
