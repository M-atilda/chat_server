// file:    CommandManager.java
// author:  uma
// date:    17-07-28
package src;


public class CommandManager
{
    public static AbstractCommand commandFactory(ReceivePacket rp, DataManager dm)
    {
        AbstractCommand result;
        switch(rp.getKind())
            {
            case 255:
                System.out.println("[Debug]LoginCmd generated (commandFactory:CommandManager.java)");
                result = new LoginCmd(rp, dm);
                break;
            case 1:
                System.out.println("[Debug]TalkCmd generated (commandFactory:CommandManager.java)");
                result = new TalkCmd(rp, dm);
                break;
            default:
                System.out.println("[Debug]EmptyCmd generated (commandFactory:CommandManager.java)");
                result = new EmptyCmd(rp, dm);
            }
        return result;
    }

    // provide Command classes' common method templates
}

