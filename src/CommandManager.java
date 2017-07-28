// file:    CommandManager.java
// author:  uma
// date:    17-07-28
package src;


public class CommandManager
{
    public static AbstractCommand commandFactory(int kind)
    {
        AbstractCommand result;
        switch(kind)
            {
            case 255:
                result = new LoginCmd();
                break;
            default:
                result = new EmptyCmd();
                break;
            }
        return result;
    }

    // provide Command classes' common method templates
}

