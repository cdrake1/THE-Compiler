/*
    Symbol file
    Creates symbols to be added to the symbol table
    To be used during Semantic Analysis
*/

//The symbol class!
public class Symbol{
    String name;    //the name of the symbol
    String type;    //the symbols type
    boolean isINIT; //is this symbol initialized?
    boolean isUsed; //is this symbol used?
    int scope;  //what is the symbols scope?

    // ---  remove if needed
    String line;   //what line is the symbol located within the source code?
    String position;   //the symbols position within the source code

    //Symbol constructor -- creates a symbol and initializes its variables
    public Symbol(String name, String type, int scope){
        this.name = name;
        this.type = type;
        this.isINIT = false;
        this.isUsed = false;
        this.scope = scope;
    }
}