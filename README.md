# THE-Compiler

THECompiler is a Java based compiler built off of [this](https://www.labouseur.com/commondocs/6502alan-instruction-set.pdf) 6502 microprocessor instruction set. This compiler build will be separated into four sections denoted by Project 1, Project 2, and so on. Like a standard compiler this compiler will contain both a front end and back end. The front end will contain the lexer, parser, and semantic analysis in order to translate the high level input code into an intermediate representation, which for us will be an abstract syntax tree. Our back end, however, will simply translate our intermediate representation into 6502 machine instruction!

# Sections
- Project 1
- Project 2
- Project 3
- Project 4

# What's Needed?
To create an environment for THECompiler we will need the following:

1. Make sure you are running a working version of Java. If you need help installing or updating it, look at this [link](https://www.java.com/en/download/help/download_options.html).
2. A sufficient IDE. That WORKS! I prefer using [VS Code](https://code.visualstudio.com/download).

# Building and Running
  - Navigate to the src folder within THECompiler and compile the Java source code into byte code using the command: "javac THECompiler.java"
  - This should create another file or update the current file, named: "THECompiler.class".
  - To run the compiled code you can use: "java THECompiler.class" or if you would like to use a parameter: "java THECompiler.class example.txt"
