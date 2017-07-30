// file:    Packet.java
// author:  uma
// date:    17-06-17
package src;

import java.util.ArrayList;


class Packet
{
    private Header header;
    public int getId() { return this.header.m_id; }
    public int getKind() { return this.header.m_kind; }
    public void setKind(int _kind) { this.header.m_kind = _kind; }
    public byte[] getPass() { return this.header.m_passphrase; }
    private byte[] contents;
    public byte[] getContents() { return this.contents; }
    protected void setContents(byte[] _contents) { this.contents = _contents; }


    private long processed_time;
    public long getProcessedTime() { return this.processed_time; }
    protected void setProcessedTime(long _pt) { this.processed_time = _pt; }

    public String getProcessedCommand() { return "receive"; }

    protected void init(int _id, int _kind, byte[] _pass, byte[] _contents)
    {
        this.header = new Header(_id, _kind, _pass);
        this.contents = _contents;
    }

    public static byte[] convertPacket2Bytes(Packet _packet)
    {
        ArrayList<Byte> result = new ArrayList<Byte>();
        result.add((byte)_packet.getId());
        result.add((byte)_packet.getKind());
        for (int i = 0; i < 8; i++) { result.add(_packet.header.m_passphrase[i]); }
        for (int i = 0; i < _packet.contents.length; i++) { result.add(_packet.contents[i]); }
        byte[] result_array = new byte[result.size()];
        for (int i = 0; i < result_array.length; i++) { result_array[i]= (byte)result.get(i); }
        return result_array;
    }

} // Packet

