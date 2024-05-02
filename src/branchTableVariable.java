/*
    Branch table variable file
    Creates objects for the code generation branch table
*/

//The branch table variable class!
public class branchTableVariable {
    String temp;    //jump to variable
    int distance;    //how many bytes to jump

    //Static table variable constructor -- initializes all variables
    public branchTableVariable(String temp, int distance){
        this.temp = temp;
        this.distance = distance;
    }
}