// file:    LoginCmd.java
// author:  uma
// date:    17-07-28
package src;

import java.security.MessageDigest;
import java.nio.ByteBuffer;
import java.io.*;


public class LoginCmd extends AbstractCommand
{
    @Override
    String getName() { return "LoginCmd"; }

    @Override
    boolean validation_check() {
        return this.registerdPasswordCheck(this.m_rp.getContents());
    }


    @Override
    public void execute(ReceivePacket rp, SendPacket sp, DataManager dm) throws NoneLoginAccessException
    {
        System.out.println("LoginCmd execute method");
        this.m_rp = rp;
        this.m_sp = sp;
        this.m_dm = dm;

        if (rp == null) { System.out.println("rp"); }
        if (sp == null) { System.out.println("sp"); }
        if (dm == null) { System.out.println("dm"); }
        
        // template method patern? (more subdivision)
        if (validation_check())
            {
                make_header();
                make_contents();
            }
        else // invalid access
            {
                throw new NoneLoginAccessException(this.m_rp.getId());
            }
    }

    
    
    @Override
    protected void make_header()
    {
        // make 64 bits hash value for common key
        byte[] pass_hash = new byte[8];
        try
            {
                MessageDigest md5 = MessageDigest.getInstance("MD5");
                long now = System.currentTimeMillis();
                byte[] now_bytes = ByteBuffer.allocate(8).putLong(now).array();
                byte[] hash_val = md5.digest(now_bytes); // 16 bytes array
                for (int i = 0; i < 8; i++) { pass_hash[i] = hash_val[i]; }
            }
        catch (Exception ex)
            { System.out.println("catch an exception while generating a hash value"); }

        this.m_sp.init(this.m_rp.getId(), 254, pass_hash, new byte[0]);
        this.m_dm.addCommonKey(this.m_rp.getId(), pass_hash);
    }

    @Override
    protected void make_contents() {
        System.out.println("[Login]");
        this.m_dm.logging("[Login] ID:" + this.m_rp.getId() + "PASS:" + this.m_rp.getPass());
        byte[] result = new byte[1];
        result[0] = (byte)'T';
        this.m_sp.setContents(result);
    }

    public static boolean registerdPasswordCheck(byte[] _pass)
    {
        String target_pass = "";
        try {
            target_pass = new String(_pass, "UTF-8");
        } catch (Exception e) {
            System.out.println(e);
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
                if (target_pass.equals(s)) { return true; }
            }

            br.close();
        } catch (IOException e) {
            System.out.println(e);
            DataManager.logging("[Login] FAILED TO OPEN PASSWORD FILE");
        }
        return false;
    }
} // LoginCmd

