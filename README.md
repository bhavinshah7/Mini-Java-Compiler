# README #

This aim of this project is to construct a compiler for  [Extended MiniJava](https://www.cs.rit.edu/~hh/teaching/doku.php?id=cc17:eminijava) language. The project is divided into different phases.

1. Lexical Analysis
2. Syntax Analysis
3. Name Analysis
4. Type checking
5. Byte Code Generation
6. Optimization

* Quick summary
* Version
* [Learn Markdown](https://bitbucket.org/tutorials/markdowndemo)

### How do I get set up? ###

* Summary of set up
* Configuration
* Dependencies
* Database configuration
* How to run tests
* Deployment instructions

### Interface ###
Command-line is the primary interface for users to interact with this compiler. The format of command-line interface is as follows:

emjc [options] <source file>

As of now, there are six possible options. 

––help: Prints a synopsis of options
––pp: Pretty-prints the input file to the standard output
––lex: Generates output from lexical analysis as described in Assignment 2.
––ast: Generates output from syntactic analysis as described in Assignment 3.
––name: Generates output from name analysis as described in Assignment 4
––type: Generates output from type analysis

After executing the compiler with the option ––type for the source file filename.emj the compiler either accepts the program as a good eMiniJava program with printing out the following line:

Valid eMiniJava Program

Or, it prints out a set of errors (such as unknown identifier). Like the previous phases the format of an error message is the following:

<line>:<column> error:<description>
where <line> and <column> indicate the beginning position of the error, and <description> details the error.


### Contribution guidelines ###

* Writing tests
* Code review
* Other guidelines

### Who do I talk to? ###

* Repo owner or admin
* Other community or team contact