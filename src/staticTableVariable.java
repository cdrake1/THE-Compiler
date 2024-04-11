/*
    Static table variable file
    Creates objects for the code generation variable table
*/

//The static table variable class!
public class staticTableVariable {
    String temp;    //store the accumulator in a temp location
    String var; //what variable is this?
    String address; //unsure?

    //Static table variable constructor -- initializes all variables
    public staticTableVariable(String temp, String var, String address){
        this.temp = temp;
        this.var = var;
        this.address = address;
    }
}