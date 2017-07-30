/*
 *@file:    Worker.java
 *@author:  maeda, iwamoto
 *@date:    17-05-06
 */
package src;

// 入出力ストリームを使うので，java.io.* を import
import java.io.*;
// ソケットを使うので java.net.* を import 
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


// 一人のクライアントとの通信を担当するスレッド
// スレッド上で走らせるため Runnable インタフェースを実装
class Worker implements Runnable {
	// 通信のためのソケット
	Socket m_sock;
	// そのソケットから作成した入出力用のストリーム
	OutputStream m_outputStream;
	InputStream m_inputStream;
	// サーバ本体のメソッドを呼び出すために記憶
	ChatServer ChatServer;
    // サーバー側でオンメモリに保有するデータの管理者
    DataManager m_data_manager;
	// 担当するクライアントの番号
	int m_client_num;
	// クライアントの総数
	static int N = ParamsProvider.getMaxClientNum();
	// クライアントが生きているかの判別flag
	static boolean AliveFlag[] = new boolean[N];
	static String DeadOrAlive = "FFFFFFF";
	// クライアントのログイン時間
	static long Logintime[] = new long[N];
	static long lifetime = 20000;
	ArrayList<Byte> al_receive_row_message = new ArrayList<Byte>();
	
	// コンストラクタ
	public Worker(int n, Socket s, DataManager dm) {
		this.m_client_num = n;
        this.m_data_manager = dm;
		this.m_sock = s;
		this.m_outputStream = null;
		this.m_inputStream = null;
	}

	// 対応するスレッドが start した時に呼ばれる．
	public void run() {
		// 行の一時的な確保
		String line = "";
		// 本文の一時的な確保
		String tempMessage = "";
		// 送信時に未読の分を書き並べる
		String allMessage = "";
		// 行数のカウント
		int count = 0;

        try {
            this.m_outputStream = this.m_sock.getOutputStream();
            this.m_inputStream = this.m_sock.getInputStream();


            // receive phase
            byte[] temp_buffer = new byte[4096];
            int data_length = 0;

            do {
                data_length = m_inputStream.read(temp_buffer);
                for (int i = 0; i < data_length; i++) {
                    al_receive_row_message.add(temp_buffer[i]);
                }
            } while (data_length == temp_buffer.length);
            //for (int i = 0; i < al_receive_row_message.size(); i++) { System.out.println(al_receive_row_message.get(i)); }
            // receive phase end
            

            
            // interpretation phase
            // get the header data
            int client_id = (int)al_receive_row_message.get(0);
            int packet_kind = (int)al_receive_row_message.get(1).byteValue();
            if (packet_kind < 0) { packet_kind += 256; }
            byte[] common_key = new byte[8];
            for (int i = 0; i < 8; i++) {
                common_key[i] = al_receive_row_message.get(2+i);
            }
            byte[] contents = new byte[al_receive_row_message.size()-10];
            for (int i = 0; i < (al_receive_row_message.size()-10); i++) {
                contents[i] = al_receive_row_message.get(i+10);
            }
            ReceivePacket rp = ReceivePacket.rpFactory(client_id, packet_kind, common_key, contents);
            // interpretation phase end


            // response packet generation phase
            // NOTE: once make a Packet class data for the reservation and referense of sending data
            SendPacket sp;
            try {
                sp = SendPacket.spFactory(rp, m_data_manager);
            } catch (NoneLoginAccessException nlae) {
                // NOTE: client side should treat the mistake of passphrase
                m_sock.close();
                System.out.println(nlae);
                ChatServer.remove(m_client_num);
                return; // quit processing
            }
            byte[] response_data = Packet.convertPacket2Bytes(sp);
            // response packet generation phase end


            //for (int i = 0; i < response_data.length; i++) { System.out.println(response_data[i]); }
            // response phase
            this.send(response_data);
            m_data_manager.update(rp, sp); // update the record about data sending and so on
            //response phase end


            // post process
			m_sock.close(); // ソケットを閉じる
            ChatServer.remove(m_client_num); // 自分自身をテーブルから取り除く
		} catch (IOException ioe) {
            System.out.println("[Error]error occured in Worker thread");
			System.out.println(ioe);
            DataManager.logging("[Error]error occured in Worker thread(run:Worker.java)\n" + ioe.toString() + "\n");
			ChatServer.remove(m_client_num);
		}
	} // run

	// 対応するソケットに文字列を送る
	public void send(byte[] message) {
        try {
            m_outputStream.write(message, 0, message.length);
        } catch (IOException e) {
            //TODO: error management
            DataManager.logging("[Error]faied to send response");
        }
	}
} // Worker
