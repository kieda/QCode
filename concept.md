## Background

Typically in the JVM class definitions are held in a separate location in memory (the permagen pre-java8, and the metaspace as of java8).  A user can't really load a new version of a class easily, since we have a bunch of objects that adhere to its specification. 

For example, suppose we have a class called `Point` with methods `getX()` and `getY()`. Suppose have a series `Point` instances already in the JVM. Let's say we want to load a new version of the class that has a new field `int weight`. Then we have to somehow update all of the previous instances. Unfortunately, it's very hard to update all of the instances and keep new class invariants, especially in the case where the class hierarchy (inheritance) changes. It's hard to do this for a good reason - if we could we could easily break external invariants - for example, let's say that `Point instanceof AbstractPoint`, and we had an `ArrayList` of `AbstractPoints`. We then changed `Point` to no longer be instanceof `AbstractPoint`.

The JVM has a bit of a workaround, though, which allows us to do limited dynamic programming. Each class, is loaded by a ClassLoader, and the class 'belongs' to the classloader. We cannot load up a new class definition of `Point` under the same classloader, but we can do it under a **different** classloader - the old instances of `Point` will still remain valid in the context of the old classloader, and all new instances of the new version of `Point` will remain valid under the new classloader. 

This is the way that QCode currently works - whenever we reload a definition of a class we just do it under a new classloader, which allows us to 'simulate' reloading a new class, when it's just under a new context. We create a new UrlClassloader, then using this classloader to load and run a target class. 

## Problem

Currently QCode works well when reloading one or a few classes at the same time. Unfortunately, it's not so good for building a chunk of the program, and it's even harder for the rest of the program to reach the dynamically loaded classes and find the most recently used version of a class (currently we just make a new ClassLoader then dispose of it after running)

Suppose we dynamically loaded class `edu.cmu.ballers.Pfenning`. Then, in a separate instance we dynamically load `edu.cmu.ballers.RJ`. However, `RJ` needs `Pfenning` and attempts to import it. In the current implementation it will fail, since they are loaded under separate classloaders, and so `RJ` will not be able to find `Pfenning`.

## The Concept

We will make a 'delegating' classloader  (let's call this `D`) that manages and organizes a series of other classloaders. Internally, `D` has some set of internal classloaders that it manages. When a newer version of a class is loaded, we evict the previous version. As the code is executing, we link the classes through `D`. 

For example, when loading `edu.cmu.ballers.*`, we create two new classloaders inside `D`. Then, as `RJ` is calling some block of code which requires `Pfenning`, we find the class by searching through `D`. Then we can access it via reflection from `D.getClass().getMethod ...`

In the end, we want to achieve dynamic linking of dynamic classes via reflection.

This is the important part - we know how to make a solution, but we don't know an efficient implementation for the job. The solution involves organizing java classes in some sort of efficient in-memory database that's optimized for java names. We must somehow (1) create an efficient lookup system to evict older classloaders, and (2) find a way to quickly and efficiently find and load classes in its scope. Not only that, we also need to find out which classloader has which objects, and which classloader we should use. 

In the future, we would also want to have a single classloader load a module of classes (for efficiency). For example, lets say that classloader `C`  has loaded classes `P`, `Q`, and `R`. Then, we modify `R`. We must know that `C` is affected, and that we should create a new classloader, `C'`, consisting of `P`, `Q`, and the updated `C'`. Then, we need to evict the old classloader `C` from the delegating classloader `D`. Finally, we need to update `D` such that it knows where to find `P`, `Q`, and `R`.

## Data Structure

As an initial naive approch, I've decided to use a hashmap to link the java class names to the respective class loaders.

Looking at some micro-benchmarking, I've noticed that invoking a method via reflection is roughly 17x slower than regular method invocation. Furthermore, a lookup operation in the (new and improved) java 8 hashmap is about 20x slower than regular method invocation.

This is not ideal for all method invocations - especially since we only need a small portion of the jvm to be dynamically reloadable. 

Before, I discussed that we could load a series of classes into a single classloader. If we do this, the classloader does not need to (1) go through the delegate classloader, and (2) does not need to use reflection. This will allow everything under this classloader to run smoothly as long as the classes under it are mainly interacting with each other. 

Because of this I introduce **a new operation** that is possible (and we should use). 

  * We may group a series of classes from different classloaders. This operation takes a non-empty susbset of classes from some number of classloaders, removes them, and forms a new classloader with the new set of classes. 
  * This operation entails merging two old classloaders into a new one
  * This operation entails splitting an old classloader into two new ones, or extracting a subset of classes.
  * RESTRICTION : classloaders cannot explicitly remove a class - **if we want to extract a single class `c` from classloader `A`, we have to create two new classloaders, `A' = A \setminus c` and `B = {c}`**. Only then will we be able to dispose of the old classloader `A`. **The same is true for reloading classes - if we want to reload one we will have to create a whole new classloader**

In light of this new data structure, and for making the program optimized, we will need to implement a data structure that
   1. for a series of classes, when there is a lot of class loading and reloading, we dynamically break it down into smaller classloaders. This will allow us to do less work - reloading a classloader consisting of one class is faster than reloading a classloader that has many classes in it!
   2. classloader that are not being dynamically reloaded - we group into newer, larger classloaders. Optimally, we will group them into MRU modules. (i.e. if classloader `A` is using a lot of resources from classloader `B`, we should merge them into a combined classloader)

If this is acheived, it will have significant implications for dynamic programming in java. Not only will it enable quick dynamic class loading, but it will also be nearly as efficient as the standard JVM

## Findings 

  * Look at Apache Tapestry 5 - https://cwiki.apache.org/confluence/display/TAPESTRY/Persistent+Page+Data . It seems to use a form of data persistance specifically for POJOs and requires specific annotations. This is limited however, and we might be able to make a method for more persistant objects
   * http://tapestryjava.blogspot.com/2006/05/tapestry-5-class-reloading.html 
  * This method (likely) does not allow persistance of static fields, as they will be disposed with the classloader as well
  * apache jci - looks like they have a relatively simple approach. They have a single (delegating) classloader which has one internal "Delegate" classloader. This internal classloader is exchanged each time a file they are watching changes. This is non-optimal for larger projects, and does not allow good persistance
  * Classloader prevention leaks - (common leaks prevented) - https://github.com/mjiderhamn/classloader-leak-prevention/blob/master/src/main/java/se/jiderhamn/classloader/leak/prevention/ClassLoaderLeakPreventor.java
   * This prevents classloader leaks for some things that will commonly leak them
  * New design idea - 
   * have one single classloader
   * use bcel to take .class and make it into individual components. (possibly) Have a space for static data, space for fields, class heirarchy info, etc.
   * use bcel to make these queries instead of standard object references. Use a proxy to access objects.
   * This design will have persistance in static members - (possibly) not possible with multiple classloaders
   * This design will help with object persistence
  * Alternative idea
   * Have delegating classloader + bcel. Extract static vars from classes, put them into delegating classloader. Then persistance of static fields.
  * Looked at http://felix.apache.org/site/dive-into-the-ipojo-manipulation-depths.html
   * Important lesson here - we can use bytecode manipulation to create constructors for persistance. We could use this as an alternative for proxies, but we can run into issues when dealing with non-POJO objects.
   * We could run this on POJOs, then switch to proxies when class hierarchy has changed
   * More advanced idea - use java annotations to indicate an individual java object or instance (on instantiation.) We can then access this later and dynamically change it
   * Since we can keep track of objects by classloader grouping, we can use sql-like queries for objects, and update even a group of objects.
  * Note - we could build a system that is tied in with OSGi : restart a bundle using dynamic loading or even with class persistance
  * more info for class loading http://www2.sys-con.com/itsg/virtualcd/java/archives/0808/chaudhri/index.html
  * We can use java serialization to reload classes. However, we will need to write our own serializer, and it only works when the class that we serialized has a parent that has a no-arg constructor. Even then, the no-arg constructor will be called, which is problematic if non-pure.
  * We can use bytecode manipulation to get by most of these problems. Namely, we can reload in all situations except for when we have a non-dynamic class with a non-no arg constructor. 
   * Alternatively - we can make all top-level classes (classes and interfaces that are directly subclasses of Object) implement serializable on load-time. Since the Object() constructor is pure, we will be able to call serialize without worry. This works for all classes except for the core classes required to load before the main class starts. This solution can also be problematic due to inconsistencies with Serializable.
   * We could also just dispose of Proxy classes, or wrap them in an OSGi POJO (no way to go back to non-dynamic without disposing of instances)
  * Idea - look at module-like systems. Having a fully dynamic system can cause inconsistencies on restart. We might be able to have a "provider" module which provides classes for a dependent module to use. When this module is reloaded we grab classes from the provider again. 
   * Example : provider is .css file. Dependencies : dependent javafx nodes
