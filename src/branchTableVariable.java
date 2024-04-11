/*
    Branch table variable file
    Creates objects for the code generation branch table
*/

//The branch table variable class!
public class branchTableVariable {
    String temp;
    String distance;

    //Static table variable constructor -- initializes all variables
    public branchTableVariable(String temp, String distance){
        this.temp = temp;
        this.distance = distance;
    }
}