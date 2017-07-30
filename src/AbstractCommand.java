// file:    AbstractCommand.java
// author:  uma
// date:    17-06-17
package src;

import java.util.ArrayList;


/*TODO: I have wanted to treat this not as abstract class but as interface*/
public abstract class AbstractCommand
{
    abstract String getName();

    protected ReceivePacket m_rp;
    protected SendPacket m_sp;
    protected DataManager m_dm;

    public void execute(SendPacket sp) throws NoneLoginAccessException
    {
        this.m_sp = sp;

        if (this.m_rp == null) { System.out.println("rp is null\n"); }
        if (this.m_sp == null) { System.out.println("sp is null\n"); }
        if (this.m_dm == null) { System.out.println("dm is null\n"); }
        
        // template method patern? (more subdivision)
        if (validation_check()) {
            make_header();
            make_contents();
        } else { // invalid access
            throw new NoneLoginAccessException(this.m_rp.getId());
        }
    }

    boolean validation_check()
    {
        boolean result = this.m_dm.passCheck(this.m_rp.getId(), this.m_rp.getPass());
        //if (result) System.out.println("validation check completed");
        return result;
    }

    protected void make_header()
    {
        this.m_sp.init(this.m_rp.getId(), 0, this.m_rp.getPass(), new byte[0]);
    }

    protected abstract void make_contents();




    //helper
    static void appendArrayToList(byte[] giver, ArrayList<Byte> recepter) {
        for (byte b : giver) {
            recepter.add((Byte)b);
        }
    }

}


