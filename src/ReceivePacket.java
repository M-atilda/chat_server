// file:    ReceivePacket.java
// author:  uma
// date:    17-07-28
package src;


public class ReceivePacket extends Packet
{
    public static ReceivePacket rpFactory(int _id, int _kind, byte[] _pass, byte[] _contents)
    {
        ReceivePacket rp = new ReceivePacket();
        rp.init(_id, _kind, _pass, _contents);
        rp.setProcessedTime(System.currentTimeMillis());
        return rp;
    }
} // Receivepacket
