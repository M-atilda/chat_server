
// 入出力ストリームを使うので，java.io.* を import
import java.io.*;
// ソケットを使うので java.net.* を import 
import java.net.*;
import java.util.ArrayList;
import java.io.file;
import java.io.FileWriter;
import java.io.IOException;

// 一人のクライアントとの通信を担当するスレッド
// スレッド上で走らせるため Runnable インタフェースを実装
class Worker implements Runnable {
	// 通信のためのソケット
	Socket sock;
	// そのソケットから作成した入出力用のストリーム
	PrintWriter out;
	BufferedReader in;
	// サーバ本体のメソッドを呼び出すために記憶
	ChatServer chatServer;
	// 担当するクライアントの番号
	int n;
	// クライアントの総数
	static int N = 7;
	// クライアントが生きているかの判別flag
	static boolean AliveFlag[] = new boolean[N];
	static String DeadOrAlive = "FFFFFFF";
	// クライアントのログイン時間
	static long Logintime[] = new long[N];
	static long lifetime = 20000;
	static ArrayList<Receiveddata> messageList=new ArrayList<Receiveddata>();

	// コンストラクタ
	public Worker(int n, Socket s, ChatServer cs) {
		this.n = n;
		chatServer = cs;
		sock = s;
		out = null;
		in = null;
	}

	// 対応するスレッドが start した時に呼ばれる．
	public void run() {
		System.out.println("Thread running:" + Thread.currentThread());
		// 0~6の配列indexに対応
		String usernumber = null;
		// 行の一時的な確保
		String line = "";
		// 本文の一時的な確保
		String tempMessage = "";
		// 送信時に未読の分を書き並べる
		String allMessage="";
		// 行数のカウント
		int count = 0;
		try {
			// ソケットからストリームの作成
			out = new PrintWriter(sock.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			usernumber = in.readLine();
			line = in.readLine();
			// ソケットからの入力があったら，
			while (line != null && !(line.equals(""))) {
				System.out.println(line);
				tempMessage+=line;
				line = null;
				count++;
				line = in.readLine();
			}
			
			// messageListの生成
			// 引数は左から、誰が送ってきたか、本文全体、すでに送ったかどうか（送信済みの場合はtrue）、いつの投稿か
			messageList.add(new Receiveddata(usernumber,tempMessage,new isSent[N],System.currentTimeMillis()));
			
			// messegeListのデータ数100件超えたら古いものから削除
			if(messageList.size()>100){
				int i=messageList.size()-100;
				for(int j=0;j<i;j++){
					messageList.remove(j);
				}
			}
			
			// logのテキストに追加する
						try{
						      if (checkBeforeWritefile(ChatServer.file)){
						        FileWriter filewriter = new FileWriter(ChatServer.file, true);
						        
						        filewriter.write(messageList.get(messageList.get(messageList.size()).receivedTime+" "+messageList.size()).usernumber+" "+messageList.get(messageList.size()).tempMessage+"/n");

						        filewriter.close();
						      }else{
						        System.out.println("Lolita_log.textに書き込めません");
						      }
						    }catch(IOException e){
						      System.out.println(e);
						    }
						
			
			// allMessegeに未読分を格納
			for(int i=0;i<messageList.size();i++){
				if(!messageList.get(i).isSent[int(usernumber)]){
					allMessege+=messageList.get(i).usernumber;
					allMessege+="roli";
					allMessege+=messageList.get(i).tempMassage;
					allMessege+="roli";
					messageList.get(i).isSent[int(usernumber)]=true;
				};
			}
			
			// 送信してきたクライアントに送る
			// もし自動送信の信号なら、
			if (count == 0) {
				Lifetime(N, Integer.parseInt(usernumber), Logintime, AliveFlag, lifetime);
				AliveFlag[Integer.parseInt(usernumber)] = true;
				DeadOrAlive = shorten(AliveFlag, DeadOrAlive);
			// 手動送信（本文付き）なら、
			} else {
				DeadOrAlive = shorten(AliveFlag, DeadOrAlive);
			}
			chatServer.sendAll(DeadOrAlive+allMessage);
			
			// 自分自身をテーブルから取り除く
			chatServer.remove(n);
			// ソケットを閉じる
			sock.close();
		} catch (IOException ioe) {
			System.out.println(ioe);
			chatServer.remove(n);
		}
	}

	// 対応するソケットに文字列を送る
	public void send(String s) {
		out.println(s);
	}

	private String shorten(boolean[] AliveFlag, String DeadOrAlive) {
		String s = "";
		for (int i = 0; i < AliveFlag.length; i++) {
			if (AliveFlag[i])
				s += "T";
			else
				s += "F";
		}
		return s;
	}

	private void Lifetime(int N, int usernumber, long Logintime[], boolean[] AliveFlag, long lifetime) {
		for (int i = 0; i < N; i++) {
			Logintime[usernumber] = System.currentTimeMillis();
			if (System.currentTimeMillis() - Logintime[i] > lifetime)
				AliveFlag[i] = false;
		}
		return;
	}
}

class Receiveddata{
	String usernumber;
	String tempMassage;
	boolean isSent[];
	long receivedTime;
	public Receiveddata(String user_number,String temp_Massage,boolean is_Sent[],long received_Time){
		user_number=usernumber;
		temp_Massage=tempMassage;
		is_Sent=isSent;
		received_Time=receivedTime;
	}
}

private static boolean checkBeforeWritefile(File file){
    if (file.exists()){
      if (file.isFile() && file.canWrite()){
        return true;
      }
    }

    return false;
  }