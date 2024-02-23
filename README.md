# THE-Compiler

THECompiler is a Java-based compiler built from [this 6502 microprocessor instruction set.](https://www.labouseur.com/commondocs/6502alan-instruction-set.pdf) This compiler build will be separated into four sections denoted by Project 1, Project 2, and so on. If you are unsure exactly what a compiler is, a brief definition from the [dragon textbook](https://www.amazon.com/Compilers-Principles-Techniques-Tools-Edition/dp/0321486811) describes it as "a program that can read a program in one lan­guage, the source language and translate it into an equivalent program in another language, the target language." Like a standard compiler, this compiler will contain both a front and back end. The front end will contain the lexer, parser, and semantic analysis to translate the high-level input code into an intermediate representation, which for us will be an abstract syntax tree. Our back end, however, will translate our intermediate representation into 6502 machine instruction!

# Sections
- Project 1 -- Lexer
- Project 2 -- Parser
- Project 3 -- Semantic Analysis
- Project 4 -- Code Generation

# What's Needed?
To create an environment for THECompiler we will need the following:

1. A working version of Java. If you need help installing or updating it, [look at this link](https://www.java.com/en/download/help/download_options.html).

# Building and Running THECompiler
To build and run THECompiler follow these steps:

1. Open a command line interface on your machine (terminal, command prompt, etc).
2. Navigate to the **"src"** folder of THECompiler using the **"cd"** command
3. Build the compiler by executing the following command: **"javac \*.java"**
4. Run THECompiler with the following command: **"java Compiler"**
5. Now you should see a welcome message indicating that the compiler is running!
6. If you are attempting to compile code, ensure it is in a text file located within the same directory as the compiler's entry point: **"Compiler.java"**
7. To run this code, execute the following command, replacing **"example"** with the name of your code text file: **"java Compiler example.txt"**
