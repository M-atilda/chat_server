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
                System.out.println("[Debug]store icon of ID" + this.m_rp.getId() + "\n");
            } else {
                // get the image name and its contents
                byte[] divide_token = new byte[8];
                String divide_token_str = "roliroli";
                for (int i = 0; i < 8; i++) { divide_token[i] = (byte)divide_token_str.toCharArray()[i]; }
                int file_name_end = 0;
                byte[] _contents = this.m_rp.getContents();
                try {
                    //TODO: use method (too much nesting)
                    while (true) {
                        file_name_end++;
                        if (_contents[file_name_end] == divide_token[0] && _contents[file_name_end + 7] == divide_token[7]) { // search last byte for efficiency
                            boolean is_end = true;
                            for (int j = 1; j < 7; j++) {
                                if (_contents[file_name_end + j] != divide_token[j]) {
                                    is_end = false;
                                    break;
                                }
                            }
                            if (is_end) { break; }
                        }
                    }
                } catch (Exception e) { // maybe out of index exception occured
                    DataManager.logging("[Error]failed to get filename from the receive packet contents(ImageDumpCmd:ImageDumpCmd.java)");
                    return;
                }

                byte[] image_name_bytes = new byte[file_name_end];
                for (int i = 0; i < file_name_end; i++) { image_name_bytes[i] = _contents[i]; }
                String image_name = new String(image_name_bytes, "UTF-8");
                this.m_dm.pushSupplyData(this.m_rp.getId(), "Image", this.m_rp.getContents(), image_name);
                System.out.println("[Debug]store image\n");            
            }
            this.is_success_to_preserve = true;
        } catch (Exception e) {
            DataManager.logging("[Error]failed to preserve an image data(ImageCmd:ImageCmd.java)");
        }
    }

    @Override
    String getName() { return "ImageDumpCmd"; }

    @Override
    protected void make_header()
    {
        this.m_sp.init(this.m_rp.getId(), 4, this.m_rp.getPass(), new byte[0]);
    }

    @Override
    protected void make_contents()
    {
        System.out.println("[Debug]make image_dump response contents(make_contents:ImageDumpCmd.java)");
        byte[] result = new byte[1];
        if (this.is_success_to_preserve) {
            result[0] = (byte)'T';
        } else {
            result[0] = (byte)'F';
        }
        this.m_sp.setContents(result);
    }
} // TalkCmd


