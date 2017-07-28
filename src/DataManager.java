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


public class DataManager
{
    private static DataManager instance = null;

    private static File log_file = new File(ParamsProvider.getLogFileName());

    private ArrayList<ReceivePacket> al_receive_packet = new ArrayList<ReceivePacket>();
    private ArrayList<SendPacket> al_send_packet = new ArrayList<SendPacket>();


    class TimeActionControler implements Runnable {
        @Override
        public void run()
        {
            //TODO: impl
        }
    } // TimeActioncontroler
    private TimeActionControler ta_controler = new TimeActionControler();


    // TODO: access level
    public boolean[] a_login_member;
    public long[] a_last_login_time;

    
    class LoginInfo {
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
    ArrayList<LoginInfo> al_login_data_pool = new ArrayList<LoginInfo>();


    private DataManager()
    {
        // TODO: load the dumped data at the initialization
        new Thread(this.ta_controler).start();
        this.a_login_member = new boolean[ParamsProvider.getMaxClientNum()];
        this.a_last_login_time = new long[ParamsProvider.getMaxClientNum()];
        for (int i = 0; i < ParamsProvider.getMaxClientNum(); i++) {
            this.a_login_member[i] = false;
            this.a_last_login_time[i] = 0;
        } // initialize login status arrays 
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
            if (checkBeforeWritefile(DataManager.log_file)){
                FileWriter fw = new FileWriter(DataManager.log_file, true);
                fw.write(message);
                fw.close();
            }else{
                System.out.println("Can't write on " + ParamsProvider.getLogFileName());
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
        DataManager.logging(message.toString());
    }
    public void logging_talk(Packet rp)
    {
        this.logging(rp);
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
	    if (file.exists()){
            if (file.isFile() && file.canWrite()){
                return true;
            }
	    }
	    return false;
    }

} // DataManager
