// file:    DataManager.java
// author:  uma
// date:    17-06-17
package src;

import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.lang.StringBuilder;
import java.io.IOException;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
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


                /*
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
                */
            }
        }
    } // TimeActioncontroler
    private TimeActionControler ta_controler = new TimeActionControler();


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


    class SupplyData implements Serializable {
        private int m_sender_id;
        private String m_name = "";
        private String m_data_kind;
        private volatile boolean[] m_is_sent;
        private ArrayList<Byte> m_contents;
        private long m_pushed_time;

        public SupplyData(int _sender_id, String _kind, byte[] _contents)
        {
            this.m_sender_id = _sender_id;
            this.m_data_kind = _kind;
            this.m_is_sent = new boolean[ParamsProvider.getMaxClientNum()];
            this.m_contents = new ArrayList<Byte>();
            for (int i = 0; i < _contents.length; i++) { this.m_contents.add((Byte)_contents[i]); }
            this.m_pushed_time = System.currentTimeMillis();
        }
        public SupplyData(int _sender_id, String _kind, byte[] _contents, String _name)
        {
            this(_sender_id, _kind, _contents);
            this.setName(_name);
        }

        public void setName(String _name) { this.m_name = _name; }
        public String getName() { return this.m_name; }
        public int getSenderId() { return this.m_sender_id; }
        public String getDataKind() { return this.m_data_kind; }
        public boolean isSentTo(int _id) { return this.m_is_sent[_id]; }
        public void sendTo(int _id) { this.m_is_sent[_id] = true;}
        public ArrayList<Byte> getContents() { return this.m_contents; }
        public long getPushedTime() { return this.m_pushed_time; }
    } // SupplyData
    private static ArrayList<SupplyData> al_supply_data_pool = new ArrayList<SupplyData>();

    public void pushSupplyData(int _id, String _kind, byte[] _contents, String... _name) throws Exception
    {
        //push receive data to the container
        if (_name.length == 0) {
            this.al_supply_data_pool.add(new SupplyData(_id, _kind, _contents));
        } else {
            this.al_supply_data_pool.add(new SupplyData(_id, _kind, _contents, _name[0]));
        }

        // dump image files
        if (_kind == "Icon") {
            this.saveImage("icon" + _id + ".jpg", _contents);
        } else if (_kind == "Image") {
            this.saveImage(_name[0], _contents);
        }

        // remove the old data when the container is full
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
    public ArrayList<SupplyData> sortUnsentSupplyData(ArrayList<SupplyData>... a_al_unsent_datas) {
        ArrayList<SupplyData> al_result = new ArrayList<SupplyData>();
        for (ArrayList<SupplyData> al : a_al_unsent_datas) {
            al_result.addAll(al);
        }
        al_result.sort( (x, y) -> (int)(x.getPushedTime() - y.getPushedTime()) );
        return al_result;
    }

    public void pushServerTalkData(String message) throws Exception {
        byte[] a_b_message = new byte[message.length()];
        for (int i = 0; i < message.length(); i++) {
            a_b_message[i] = (byte)message.toCharArray()[i];
        }
        this.pushSupplyData(-1, "Talk", a_b_message);
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
        
        try {
            /*
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ParamsProvider.getDumpFileName()));
            al_supply_data_pool = (ArrayList<SupplyData>)ois.readObject();
            ois.close();
            */
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        //add the data dump action executed at the server down
        /*
        Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() { DataManager.dump(); }
            });
        */
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
        if (rp.getKind() != 255) { this.a_login_member[rp.getId()] = true; }
        this.a_last_login_time[rp.getId()] = System.currentTimeMillis();

        if (al_receive_packet.size() > ParamsProvider.getMaxStorageSupplyData()) { al_receive_packet.remove(0); }
        if (al_send_packet.size() > ParamsProvider.getMaxStorageSupplyData()) { al_send_packet.remove(0); }
    }

    private static void saveImage(String _name, byte[] _contents) throws Exception
    {
        try {
            FileOutputStream os = new FileOutputStream("image/" + _name);
            os.write(_contents, 0, _contents.length);
            os.flush();
            os.close();
            DataManager.logging("[Info]save image " + _name + "\n");
        } catch (Exception e) {
            throw e;
        }
    }

    private HashMap<String, byte[]> hm_img_cache = new HashMap<String, byte[]>();
    public byte[] getImageByBytes(String _name)
    {
        byte[] result = new byte[0]; // set default contents for compile

        if (hm_img_cache.containsKey(_name)) {
            result = hm_img_cache.get(_name);
        } else {
            byte[] temp_buffer = new byte[1024];
            try {
                File f = new File("image/" + _name);
                FileInputStream fis = new FileInputStream(f);
                int file_length;
                ArrayList<Byte> al_buffer = new ArrayList<Byte>();

                do {
                    file_length = fis.read(temp_buffer);
                    for (int i = 0; i < file_length; i++) { al_buffer.add((Byte)temp_buffer[i]); }
                } while (file_length == temp_buffer.length);

                result = new byte[al_buffer.size()];
                for (int i = 0; i < result.length; i++) { result[i] = al_buffer.get(i); }

                hm_img_cache.put(_name, result);
                DataManager.logging("[Info]read an image " + _name + "\n");
            } catch (IOException e) {
                DataManager.logging("[Error]failed to read image file <image/" + _name + ">(getImageByBytes:DataManager.java\n)");
                DataManager.logging(e.toString() + "\n");
            }
        }

        return result;
    }






    public static void logging(String message)
    {
        System.out.println(message); // DEBUG
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
        try {
            String message = "[Info]ID" + Integer.toString(rp.getId()) + " says <<" + new String(rp.getContents(), "UTF-8") + ">>"; // convert contents to String data
            DataManager.logging(message);
        } catch (Exception e) {}
    }


    // NOTE: dump when the surver dies
    private static void dump()
    {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ParamsProvider.getDumpFileName()));
            oos.writeObject(al_supply_data_pool);
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
