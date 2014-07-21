## Background

Typically in the JVM class definitions are held in a separate location in memory (the permagen pre-java8, and the metaspace as of java8).  A user can't really load a new version of a class easily, since we have a bunch of objects that adhere to its specification. 

For example, suppose we have a class called `Point` with methods `getX()` and `getY()`. Suppose have a series `Point` instances already in the JVM. Let's say we want to load a new version of the class that has a new field `int weight`. Then we have to somehow update all of the previous instances. Unfortunately, it's very hard to update all of the instances and keep new class invariants, especially in the case where the class hierarchy (inheritance) changes. It's hard to do this for a good reason - if we could we could easily break external invariants - for example, let's say that `Point instanceof AbstractPoint`, and we had an `ArrayList` of `AbstractPoints`. We then changed `Point` to no longer be instanceof `AbstractPoint`.

The JVM has a bit of a workaround, though, which allows us to do limited dynamic programming. Each class, is loaded by a ClassLoader, and the class 'belongs' to the classloader. We cannot load up a new class definition of `Point` under the same classloader, but we can do it under a **different** classloader - the old instances of `Point` will still remain valid in the context of the old classloader, and all new instances of the new version of `Point` will remain valid under the new classloader. 

This is the way that QCode currently works - whenever we reload a definition of a class we just do it under a new classloader, which allows us to 'simulate' reloading a new class, when it's just under a new context. We create a new UrlClassloader, then using this classloader to load and run a target class. 

## Problem

Currently QCode works well when reloading one or a few classes at the same time. Unfortunately, it's not so good for building a chunk of the program, and it's even harder for the rest of the program to reach the dynamically loaded classes and find the most recently used version of a class (currently we just make a new ClassLoader then dispose of it after running)

Suppose we dynamically loaded class `edu.cmu.ballers.Pfenning`. Then, in a separate instance we dynamically load `edu.cmu.ballers.RJ`. However, `RJ` needs `Pfenning` and attempts to import it. In the current implementation it will fail, since they are loaded under separate classloaders, and so `RJ` will not be able to find `Pfenning`.

## Solution (attempt)

We will make a 'delegating' classloader  (let's call this `D`) that manages and organizes a series of other classloaders. Internally, `D` has some set of internal classloaders that it manages. When a newer version of a class is loaded, we evict the previous version. As the code is executing, we link the classes through `D`. 

For example, when loading `edu.cmu.ballers.*`, we create two new classloaders inside `D`. Then, as `RJ` is calling some block of code which requires `Pfenning`, we find the class by searching through `D`. Then we can access it via reflection from `D.getClass().getMethod ...`

In the end, we want to achieve dynamic linking of dynamic classes via reflection.

This is the important part - we know how to make a solution, but we don't know an efficient implementation for the job. The solution involves organizing java classes in some sort of efficient in-memory database that's optimized for java names. We must somehow (1) create an efficient lookup system to evict older classloaders, and (2) find a way to quickly and efficiently find and load classes in its scope. Not only that, we also need to find out which classloader has which objects, and which classloader we should use. 

In the future, we would also want to have a single classloader load a module of classes (for efficiency). For example, lets say that classloader `C`  has loaded classes `P`, `Q`, and `R`. Then, we modify `R`. We must know that `C` is affected, and that we should create a new classloader, `C'`, consisting of `P`, `Q`, and the updated `C'`. Then, we need to evict the old classloader `C` from the delegating classloader `D`. Finally, we need to update `D` such that it knows where to find `P`, `Q`, and `R`.

Ideally, all of these operations will have a good time complexity and will be efficient at a system level (so it can scale well).

Ideas?
