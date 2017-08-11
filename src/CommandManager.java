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
                //System.out.println("[Debug]LoginCmd generated (commandFactory:CommandManager.java)");
                result = new LoginCmd(rp, dm);
                break;
            case 1:
                //System.out.println("[Debug]TalkCmd generated (commandFactory:CommandManager.java)");
                result = new TalkCmd(rp, dm);
                break;
            case 5:
                //System.out.println("[Debug]ImageDumpCmd(icon) generated (commandFactory:CommandManager.java)");
                result = new ImageDumpCmd(rp, dm, true);
                break;
            case 7:
                //System.out.println("[Debug]ImageDumpCmd generated (commandFactory:CommandManager.java)");
                result = new ImageDumpCmd(rp, dm, false);
                break;
            case 3:
                //System.out.println("[Debug]ImageReadCmd generated (commandFactory:CommandManager.java)");
                result = new ImageReadCmd(rp, dm);
                break;
            default:
                //System.out.println("[Debug]EmptyCmd generated (commandFactory:CommandManager.java)");
                result = new EmptyCmd(rp, dm);
            }
        return result;
    }

    // provide Command classes' common method templates
}

