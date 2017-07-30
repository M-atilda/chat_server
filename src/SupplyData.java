// file:    SupplyData.java
// author:  uma
// date:    17-07-30
package src;


public class SupplyData
{
    private String m_data_kind;
    private boolean[] m_is_sent;
    private byte[] m_contents;


    public SupplyData(String _kind, byte[] _contents)
    {
        this.m_data_kind = _kind;
        this.m_is_send = new byte[ParamsProvider.getMaxClientNum()];
        this.m_contents = _contents;
    }


    public boolean[] getIsSent() { return this.m_is_sent; }
    public String getDataKind() { return this.m_data_kind; }
    public byte[] getContents() { return this.m_contents; }
}
