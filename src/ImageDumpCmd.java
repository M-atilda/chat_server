// file:    ImageDumpCmd.java
// author:  uma
// date:    17-07-31
package src;

import java.util.ArrayList;


public class ImageDumpCmd extends AbstractCommand
{
    boolean is_success_to_preserve = false;
    
    ImageDumpCmd(ReceivePacket rp, DataManager dm, boolean is_icon)
    {
        this.m_rp = rp;
        this.m_dm = dm;

        //FIXME: get image's name
        try {
            if (is_icon) {
                this.m_dm.pushSupplyData(this.m_rp.getId(), "Icon", this.m_rp.getContents());
                System.out.println("[Debug]store icon\n");
            } else {
                this.m_dm.pushSupplyData(this.m_rp.getId(), "Image", this.m_rp.getContents(), );
                System.out.println("[Debug]store image\n");            
            }
            is_success_to_preserve = true;
        } catch (Exception e) {
            DataManager.logging("[Error]failed to preserve an image data(ImageCmd:ImageCmd.java)");
        }
    }

    @Override
    String getName() { return "ImageDumpCmd"; }

    @Override
    protected void make_header()
    {
        this.m_sp.init(this.m_rp.getId(), 6, this.m_rp.getPass(), new byte[0]);
    }

    @Override
    protected void make_contents()
    {
        System.out.println("[Debug]make image_dump response contents(make_contents:ImageCmd.java)");
        byte[] result = new byte[1];
        if (this.is_success_to_preserve) { result[0] = (byte)'T'; } else { result[0] = (byte)'F'; }
        this.m_sp.setContents(result);
    }
} // TalkCmd


