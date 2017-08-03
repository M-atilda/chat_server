// file:    TalkCmd.java
// author:  uma
// date:    17-07-28
package src;

import java.util.ArrayList;


public class TalkCmd extends AbstractCommand
{
    TalkCmd(ReceivePacket rp, DataManager dm)
    {
        this.m_rp = rp;
        this.m_dm = dm;

        if (this.m_rp.getContents().length != 0) {
            try {
                this.m_dm.pushSupplyData(this.m_rp.getId(), "Talk", this.m_rp.getContents());
                DataManager.logging_talk(this.m_rp);
            } catch (Exception e) {
                DataManager.logging("[Error]storeing failed about talk message(TalkCmd:TalkCmd.java)");
            }
        }
    }

    @Override
    String getName() { return "TalkCmd"; }


    @Override
    protected void make_contents()
    {
        ArrayList<Byte> message = new ArrayList<Byte>();
        for (boolean b : this.m_dm.getLoginMember()) {
            if (b) {
                message.add((Byte)(byte)'T');
            } else {
                message.add((Byte)(byte)'F');
            }
        }

        byte[] divide_token = new byte[8];
        String divide_token_str = "roliroli";
        byte[] divide_token_img = new byte[8];
        String divide_token_img_str = "liroliro";
        for (int i = 0; i < 8; i++) {
            divide_token[i] = (byte)divide_token_str.toCharArray()[i];
            divide_token_img[i] = (byte)divide_token_img_str.toCharArray()[i];
        }

        AbstractCommand.appendArrayToList(divide_token, message);

        ArrayList<DataManager.SupplyData> al_send_contents_talk = this.m_dm.getUnsentDataList(this.m_rp.getId(), "Talk");
        ArrayList<DataManager.SupplyData> al_send_contents_img = this.m_dm.getUnsentDataList(this.m_rp.getId(), "Image");
        ArrayList<DataManager.SupplyData> al_send_contents = this.m_dm.sortUnsentSupplyData(al_send_contents_talk, al_send_contents_img);


        for (DataManager.SupplyData sd : al_send_contents) {
            message.add((Byte)(byte)Integer.toString(sd.getSenderId()).toCharArray()[0]);
            AbstractCommand.appendArrayToList(divide_token, message);
            if (sd.getDataKind() == "Talk") {
                message.addAll(sd.getContents());
                AbstractCommand.appendArrayToList(divide_token, message);
            } else if (sd.getDataKind() == "Image") {
                String img_name = sd.getName();
                byte[] a_b_img_name = new byte[img_name.length()];
                for (int i = 0; i < img_name.length(); ) { a_b_img_name[i] = (byte)img_name.toCharArray()[i]; }
                AbstractCommand.appendArrayToList(a_b_img_name, message);
                AbstractCommand.appendArrayToList(divide_token_img, message);
            }
        }

        byte[] a_message = new byte[message.size()];
        for (int i = 0; i < a_message.length; i++) { a_message[i] = (byte)message.get(i); }
        this.m_sp.setContents(a_message);
    }
} // TalkCmd


