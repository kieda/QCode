QCode
=====

QCode is a dynamic programming environment for java, allowing for faster development and bug testing. QCode detects changes within a text file, and complies and runs it on the fly. We allow additional instances of QCode to be opened within QCode, so we may dynamically build different classes upon another. In addition to running code in methods, a user may dynamically make methods and classes.

QCode, when opened in an existing project, all loaded classes are available for QCode to access. This means that a programmer can dynamically change variables, and even insert code into an application that is already open. 

We allow the programmer to save the QCode file, which saves the full QCode text, or the programmer can save the generated java file itself.

QCode is fantastic for a user whi wants to test code on the fly, or wants to build a gui environment quickly. 

Run and open QCode in Netbeans.

Currently:
    The default class a QCode environment is "Pool".
    We can enter an environment out of the default method by the tags "<--" and "-->"
    We can enter an environment out of the default class by the tags "@--" and "--@"
    We can enter an environment at the start of this class (the preamble) by the tags "#--" and "--#"

    One can save a QCode file through the hotkey "CTRL+S". One can save a java file through the hotkey "CTRL+ALT+S". One can open a .qcode file using the hotkey "CTRL+O". We cannot open a java file as a qcode file (we would have to do some crazy parsing to handle all those cases yo). 

Note that these are not final, and will probably change.

Soon, we will add in... 
    Additional functionality for the editor (beyond a JTextPane)
    Provide additional QCode features that will aide in macros for faster development (i.e. something similar to #define, and something that will aide in imports (automatically import everything in a certain list)
    Provide better error hints, and provide a "source tree" in the diagnostic pane. Allow the user to make calls IN QCode itself that will give additional reports in the Diagnostic Frame. Ideally, we will have a mix of QCode and java syntax that will be very useful for programming, but will compile to java so a programmer could easily read it.
