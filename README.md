# README #

This aim of this project is to construct a compiler for  [Extended MiniJava](https://cs.rit.edu/~hh/teaching/doku.php?id=cc17:eminijava) language. The compiler is being constructed in various logical phases. The phases are defined as follows: 

1. [Lexical Analysis](https://cs.rit.edu/~hh/teaching/cc17/assignment_2): Separate a stream of characters into different tokens of the language which are described by using regular expressions. I constructed regular expressions describe [token types of eMiniJava](https://cs.rit.edu/~hh/teaching/cc17/token_types) and used [JFlex](http://wwww.jflex.de/) to generate a Lexer.  

2. [Syntax Analysis](https://cs.rit.edu/~hh/teaching/cc17/assignment_3): We define a class hierarchy to represent nodes in the Abstract Syntax Tree (AST) for eMiniJava programs. This is used to create LL(1) version of context free grammar for eMiniJava. A recursive-descent Parser can parse LL(1) gramar in linear time to create an Abstract Syntax Tree.

3. [Name Analysis](https://cs.rit.edu/~hh/teaching/cc17/assignment_4): In this phase, we map (multiple) occurrences of class, method and variable names in a program, to their (unique) definition.

4. [Type checking](https://cs.rit.edu/~hh/teaching/cc17/assignment_5):
5. Byte Code Generation
6. Optimization


### Interface ###
Command-line is the primary interface for users to interact with this compiler. The format of command-line interface is as follows:

emjc [options] <source file>

As of now, there are six possible options. 

* ––help: Prints a synopsis of options
* ––pp: Pretty-prints the input file to the standard output
* ––lex: Generates output from lexical analysis as described in Assignment 2.
* ––ast: Generates output from syntactic analysis as described in Assignment 3.
* ––name: Generates output from name analysis as described in Assignment 4
* ––type: Generates output from type analysis

After executing the compiler with the option ––type for the source file filename.emj the compiler either accepts the program as a good eMiniJava program with printing out the following line:

Valid eMiniJava Program

Or, it prints out a set of errors (such as unknown identifier). Like the previous phases the format of an error message is the following:

<line>:<column> error:<description>
where <line> and <column> indicate the beginning position of the error, and <description> details the error.
