// file:    ImageReadCmd.java
// author:  uma
// date:    17-07-31
package src;

import java.util.ArrayList;


public class ImageReadCmd extends AbstractCommand
{
    ImageReadCmd(ReceivePacket rp, DataManager dm)
    {
        this.m_rp = rp;
        this.m_dm = dm;

        DataManager.logging(this.m_rp);
    }

    @Override
    String getName() { return "ImageReadCmd"; }


    @Override
    protected void make_header()
    {
        this.m_sp.init(this.m_rp.getId(), 2, this.m_rp.getPass(), new byte[0]);
    }

    
    @Override
    protected void make_contents()
    {
        System.out.println("[Debug]make image_read response contents(make_contents:ImageCmd.java)");
        try {
            String image_name = new String(this.m_rp.getContents(), "UTF-8");
            byte[] result = this.m_dm.getImageByBytes(image_name);
            this.m_sp.setContents(result);
        } catch (Exception e) {
            DataManager.logging("[Error]failed to encode the image file name(make_contents:ImageReadCmd.java)");
        }
    }
} // TalkCmd


