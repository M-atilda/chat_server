// file:    SendPacket.java
// author:  uma
// date:    17-07-28
package src;


public class SendPacket extends Packet
{
    private AbstractCommand processed_with;
    @Override
    public String getProcessedCommand() { return this.processed_with.getName(); }

    public static SendPacket spFactory(ReceivePacket rp, DataManager dm) throws NoneLoginAccessException
    {
        AbstractCommand cmd = CommandManager.commandFactory(rp.getKind());
        SendPacket sp = new SendPacket();
        sp.processed_with = cmd;

        if (cmd.getName() == "LoginCmd")
            {
                LoginCmd lc = (LoginCmd)cmd;
                lc.execute(rp, sp, dm);
            }
        else
            {
                cmd.execute(rp, sp, dm);
            }
        return sp;
    }
} // SendPacket
