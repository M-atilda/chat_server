// file:    EmptyCmd.java
// author:  uma
// date:    17-07-28
package src;

public class EmptyCmd extends AbstractCommand
{
    EmptyCmd(ReceivePacket rp, DataManager dm) {
        this.m_rp = rp;
        this.m_dm = dm;
    }
    
    @Override
    String getName() { return "EmptyCmd"; }

    @Override
    protected void make_contents() {}
} // EmptyCmd

