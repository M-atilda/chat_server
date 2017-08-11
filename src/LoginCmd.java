// file:    LoginCmd.java
// author:  uma
// date:    17-07-28
package src;

import java.security.MessageDigest;
import java.nio.ByteBuffer;
import java.io.*;


public class LoginCmd extends AbstractCommand
{
    LoginCmd(ReceivePacket rp, DataManager dm)
    {
        this.m_rp = rp;
        this.m_dm = dm;
    }

    @Override
    String getName() { return "LoginCmd"; }

    @Override
    boolean validation_check() {
        try {
            DataManager.logging("[Info]ID" +
                                this.m_rp.getId() +
                                " tries to login with password " +
                                new String(this.m_rp.getContents(), "UTF-8") + "\n");
        } catch (Exception e) {
            System.out.println("[Error]failed to encode the passphrase(make_contents:LoginCmd.java)\n");
        }
        return this.registerdPasswordCheck(this.m_rp.getId(), this.m_rp.getContents());
    }


    @Override
    public void execute(SendPacket sp) throws NoneLoginAccessException
    {
        this.m_sp = sp;

        if (this.m_rp == null) { System.out.println("rp"); }
        if (this.m_sp == null) { System.out.println("sp"); }
        if (this.m_dm == null) { System.out.println("dm"); }
        
        // template method patern? (more subdivision)
        if (validation_check()) {
            this.make_header();
            this.make_contents();
        } else { // invalid access
            this.make_failed_header();
            this.make_failed_contents();
        }
    }

    
    
    @Override
    protected void make_header()
    {
        // make 64 bits hash value for common key
        byte[] pass_hash = new byte[8];
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            long now = System.currentTimeMillis();
            byte[] now_bytes = ByteBuffer.allocate(8).putLong(now).array();
            byte[] hash_val = md5.digest(now_bytes); // 16 bytes array
            for (int i = 0; i < 8; i++) { pass_hash[i] = hash_val[i]; }
        } catch (Exception ex) {
            System.out.println("[Error]catch an exception while generating a hash value(make_header:LoginCmd.java)\n");
            this.m_dm.logging("[Error]failed to generating hash value(make_header:LoginCmd.java)\n");
        }

        this.m_sp.init(this.m_rp.getId(), 254, pass_hash, new byte[0]);
        this.m_dm.addCommonKey(this.m_rp.getId(), pass_hash);
    }

    private void make_failed_header()
    {
        byte[] null_pass_hash = new byte[8];
        for (int i = 0; i < 8; i++) { null_pass_hash[i] = (byte)0; }
        this.m_sp.init(this.m_rp.getId(), 254, null_pass_hash, new byte[0]);
    }


    @Override
    protected void make_contents() {
        byte[] result = new byte[1];
        result[0] = (byte)'T';
        this.m_sp.setContents(result);
    }
    
    private void make_failed_contents()
    {
        byte[] result = new byte[1];
        result[0] = (byte)'F';
        this.m_sp.setContents(result);
    }




    public boolean registerdPasswordCheck(int _id, byte[] _pass)
    {
        String target_pass = "";
        try {
            target_pass = new String(_pass, "UTF-8");
        } catch (Exception e) {
            System.out.println(e);
            this.m_dm.logging("[Error]" + this.m_rp.getId() + "failed to encode the passphrase(make_contents:LoginCmd.java)\n");
            return false;
        }
        FileReader fr = null;
        BufferedReader br = null;
        try{
            String FS = File.separator;
            File f = new File(ParamsProvider.getPassFileName());

            fr = new FileReader(f);
            br = new BufferedReader(fr);
            String s;
            while ((s=br.readLine()) != null) {
                if (Integer.parseInt(s.substring(0, 1)) == _id) {
                    String registerd_pass = s.substring(2);
                    if (target_pass.equals(registerd_pass)) {
                        //System.out.println("[Debug]ID" + this.m_rp.getId() + " login\n");
                        DataManager.logging("[Info]ID" + this.m_rp.getId() + " login\n");
                        return true;
                    } else { break; }
                }
            }

            br.close();
        } catch (IOException e) {
            System.out.println(e);
            DataManager.logging("[Error]failed to open password file(registerdPasswordCheck:LoginCmd.java)\n");
        }
        DataManager.logging("[Warn]ID" + this.m_rp.getId() + " failed to login for invalid password\n");
        return false;
    }
} // LoginCmd

