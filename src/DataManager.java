// file:    DataManager.java
// author:  uma
// date:    17-06-17
package src;

import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.lang.StringBuilder;
import java.io.IOException;
import java.util.Date;
import java.lang.InterruptedException;


public class DataManager
{
    private static DataManager instance = null;

    private static File log_file;
    private static FileWriter fw;

    private ArrayList<ReceivePacket> al_receive_packet = new ArrayList<ReceivePacket>();
    private ArrayList<SendPacket> al_send_packet = new ArrayList<SendPacket>();


    class TimeActionControler implements Runnable {
        @Override
        public void run()
        {
            while(true) {
                try {
                    Thread.sleep(ParamsProvider.getRoutinePeriod());
                } catch (InterruptedException ie) {
                    DataManager.logging("[Error]error occured in routine process(TimeActionControler.run:DataManager.java)");
                }
                
                
                //routine process
                long current_time = System.currentTimeMillis();
                for (int i = 0; i < ParamsProvider.getMaxClientNum(); i++) {
                    if (current_time - a_last_login_time[i] > ParamsProvider.getTimeOutPeriod()) {
                        a_login_member[i] = false;
                        // remove login key
                        for (int j = 0; j < al_login_data_pool.size(); j++) {
                            if (al_login_data_pool.get(j).m_user_id == i) {
                                al_login_data_pool.remove(j);
                                j--;
                            }
                        }
                    }
                }


                for (int j = 0; j < al_receive_packet.size(); j++) {
                    if (current_time - al_receive_packet.get(j).getProcessedTime() > ParamsProvider.getDiscardOldDataPeriod()) {
                        al_receive_packet.remove(j);
                        j--;
                    }
                }
                for (int j = 0; j < al_send_packet.size(); j++) {
                    if (current_time - al_send_packet.get(j).getProcessedTime() > ParamsProvider.getDiscardOldDataPeriod()) {
                        al_send_packet.remove(j);
                        j--;
                    }
                }
            }
        }
    } // TimeActioncontroler
    private TimeActionControler ta_controler = new TimeActionControler();


    // TODO: access level
    private boolean[] a_login_member;
    private long[] a_last_login_time;

    
    private class LoginInfo {
        public int m_user_id;
        private byte[] m_pass_hash;
        public LoginInfo(int _id, byte[] _pass) {
            this.m_pass_hash = _pass;
            this.m_user_id = _id;
        }
        public boolean equals(Object obj) {
            LoginInfo li = (LoginInfo)obj;
            for (int i = 0; i < 8; i++) {
                if (this.m_pass_hash[i] != li.m_pass_hash[i]) { return false; }
            }
            return true;
        }
    } // LoginInfo
    private ArrayList<LoginInfo> al_login_data_pool = new ArrayList<LoginInfo>();
    public boolean[] getLoginMember() { return this.a_login_member; }


    class SupplyData {
        private int m_sender_id;
        private String m_data_kind;
        private boolean[] m_is_sent;
        private ArrayList<Byte> m_contents;

        public SupplyData(int _sender_id, String _kind, byte[] _contents)
        {
            this.m_sender_id = _sender_id;
            this.m_data_kind = _kind;
            this.m_is_sent = new boolean[ParamsProvider.getMaxClientNum()];
            this.m_contents = new ArrayList<Byte>();
            for (int i = 0; i < _contents.length; i++) { this.m_contents.add((Byte)_contents[i]); }
        }

        public int getSenderId() { return this.m_sender_id; }
        public String getDataKind() { return this.m_data_kind; }
        public boolean isSentTo(int _id) { return this.m_is_sent[_id]; }
        public void sendTo(int _id) { this.m_is_sent[_id] = true;}
        public ArrayList<Byte> getContents() { return this.m_contents; }
    } // SupplyData
    private ArrayList<SupplyData> al_supply_data_pool = new ArrayList<SupplyData>();

    public void pushSupplyData(int _id, String _kind, byte[] _contents) {
        this.al_supply_data_pool.add(new SupplyData(_id, _kind, _contents));
        if (al_supply_data_pool.size() > ParamsProvider.getMaxStorageSupplyData()) { al_supply_data_pool.remove(0); }
    }
    public ArrayList<SupplyData> getUnsentDataList(int _id, String _kind) {
        ArrayList<SupplyData> al_result = new ArrayList<SupplyData>();
        for (SupplyData sd : al_supply_data_pool) {
            if ((sd.getDataKind() == _kind) && !sd.isSentTo(_id)) {
                al_result.add(sd);
                sd.sendTo(_id);
            }
        }
        return al_result;
    }


    private DataManager()
    {
        new Thread(this.ta_controler).start();
        this.a_login_member = new boolean[ParamsProvider.getMaxClientNum()];
        this.a_last_login_time = new long[ParamsProvider.getMaxClientNum()];
        for (int i = 0; i < ParamsProvider.getMaxClientNum(); i++) {
            this.a_login_member[i] = false;
            this.a_last_login_time[i] = 0;
        } // initialize login status arrays
        try {
            log_file = new File(ParamsProvider.getLogFileName());
            fw = new FileWriter(DataManager.log_file, true); } catch(Exception e) { System.out.println("[Error]Can't open logging file(DataManager:DataManager.java)\n");
        }
        // TODO: load the dumped data at the initialization
    }
    // NOTE: singleton
    public static DataManager dmFactory()
    {
        if (DataManager.instance == null) { DataManager.instance = new DataManager(); }
        return DataManager.instance;
    }
    

    public void addCommonKey(int _id, byte[] _passphrase)
    {
        this.al_login_data_pool.add(new LoginInfo(_id, _passphrase));
    }

    public boolean passCheck(int _id, byte[] _passphrase)
    {
        boolean result = false;
        if (this.al_login_data_pool.indexOf(new LoginInfo(_id, _passphrase)) != -1) { result = true; }
        return result;
    }
    
    public void update(ReceivePacket rp, SendPacket sp)
    {
        this.al_receive_packet.add(rp);
        this.al_send_packet.add(sp);
        this.a_login_member[rp.getId()] = true;
        this.a_last_login_time[rp.getId()] = System.currentTimeMillis();
    }






    public static void logging(String message)
    {
        // logのテキストに追加する
        try {
            if (checkBeforeWritefile(DataManager.log_file)) {
                DataManager.fw.write(message);
                DataManager.fw.flush();
            } else {
                System.out.println("[Error]Can't write on " + ParamsProvider.getLogFileName());
            }
        } catch(IOException e) {
            System.out.println(e);
        }
    }
    public static void logging(Packet packet) // override for logging of packet
    {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        StringBuilder message = new StringBuilder(sdf1.format(new Date(packet.getProcessedTime())));
        message.append("[ user:"); message.append(packet.getId());
        message.append(" | kind:"); message.append(packet.getKind());
        message.append(" | size:"); message.append(packet.getContents().length);
        message.append(" | processed_by:"); message.append(packet.getProcessedCommand()); message.append(" ]");
        DataManager.logging("[Info]talk " + message.toString() + "\n");
    }
    public static void logging_talk(Packet rp)
    {
        DataManager.logging(rp);
        // TODO: impl
        //String message; // convert contents to String data
        //this.logging(message);
    }


    // NOTE: dump when the surver dies
    public void dump(String dump_file_name)
    {
        // TODO: impl
    }

    private static boolean checkBeforeWritefile(File file){
	    if (file.exists()) {
            if (file.isFile() && file.canWrite()) {
                return true;
            }
	    }
	    return false;
    }

} // DataManager
