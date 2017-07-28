// file:    AbstractCommand.java
// author:  uma
// date:    17-06-17
package src;


/*TODO: I have wanted to treat this not as abstract class but as interface*/
public abstract class AbstractCommand
{
    String getName() { return ""; }

    protected ReceivePacket m_rp;
    protected SendPacket m_sp;
    protected DataManager m_dm;

    public void execute(ReceivePacket rp, SendPacket sp, DataManager dm) throws NoneLoginAccessException
    {
        this.m_rp = rp;
        this.m_sp = sp;
        this.m_dm = dm;

        if (rp == null) { System.out.println("rp"); }
        if (sp == null) { System.out.println("sp"); }
        if (dm == null) { System.out.println("dm"); }
        
        // template method patern? (more subdivision)
        if (validation_check())
            {
                make_header();
                make_contents();
            }
        else // invalid access
            {
                throw new NoneLoginAccessException(this.m_rp.getId());
            }
    }

    boolean validation_check()
    {
        return this.m_dm.passCheck(this.m_rp.getId(), this.m_rp.getPass());
    }

    protected abstract void make_header();
    /*
    {
        // NOTE: default packet's kind is "talk", contents is empty byte array
        this.m_sp.init(ParamsProvider.getServerId(), 0, this.m_rp.getPass(), new byte[0]);
    }
    */

    protected abstract void make_contents();
}


