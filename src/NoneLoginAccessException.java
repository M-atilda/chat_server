// file:    NoneLoginAccessException.java
// author:  uma
// date:    17-06-17
package src;


public class NoneLoginAccessException extends Exception
{
    public NoneLoginAccessException(int _id)
    {
        super("The pass_phrase wasn't found\nUser ID : " + String.valueOf(_id) + "]");
    }
}
