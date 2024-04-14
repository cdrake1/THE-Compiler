/*
    Static table variable file
    Creates objects for the code generation variable table
*/

//The static table variable class!
public class staticTableVariable {
    String temp;    //store the accumulator in a temp location
    String var; //what variable is this?
    int scope;   //keeps track of the scope the variable is located
    int offset; //number of offset following code section of the array (location of variable stored)

    //Static table variable constructor -- initializes all variables
    public staticTableVariable(String temp, String var, int scope, int address){
        this.temp = temp;
        this.var = var;
        this.scope = scope;
        this.offset = address;
    }
}