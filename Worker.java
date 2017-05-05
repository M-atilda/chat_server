// 入出力ストリームを使うので，java.io.* を import
import java.io.*;
// ソケットを使うので java.net.* を import 
import java.net.*;

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
	static int N=7;
	// クライアントが生きているかの判別flag
	static boolean AliveFlag[]=new boolean[N];
	static String DeadOrAlive="FFFFFFF";
	// クライアントのログイン時間
	static long Logintime[]=new long[N];
	static long lifetime=20000;

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
		String usernumber=null;
		// 本文を流し込む
		String message = "";
		String s=null;
		// 行数のカウント
		int count=0;
		try {
			// ソケットからストリームの作成
			out = new PrintWriter(sock.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			usernumber=in.readLine();
			s=in.readLine();
			 // ソケットからの入力があったら，
			while(s!=null&&!(s.equals(""))){
				System.out.println(s);
		    	message+=s;
		    	s=null;
		        count++;
		        s=in.readLine();
		      }
		   // クライアント全体に送る．
		      if(count==0){
		    	
		    	  Lifetime(N,Integer.parseInt(usernumber),Logintime,AliveFlag,lifetime);
		    	  AliveFlag[Integer.parseInt(usernumber)]=true;
		    	  DeadOrAlive=shorten(AliveFlag,DeadOrAlive);
		    	  chatServer.sendAll(DeadOrAlive);
		    	  System.out.println(DeadOrAlive);
		      }
		      else {
		    	  DeadOrAlive=shorten(AliveFlag,DeadOrAlive);
		    	  chatServer.sendAll(DeadOrAlive+usernumber+"roli"+message);
		      }
		        // 自分自身をテーブルから取り除く
		      chatServer.remove(n);
		        // ソケットを閉じる
		      sock.close();
		    }
		    catch(IOException ioe){
		      System.out.println(ioe);
		      chatServer.remove(n);
		    }
		  }
		
	// 対応するソケットに文字列を送る
	public void send(String s) {
		out.println(s);
	}
	private String shorten(boolean[] AliveFlag,String DeadOrAlive){
		String s="";
		for(int i=0;i<AliveFlag.length;i++){
			if(AliveFlag[i])s+="T";
			else s+="F";
		}
		return s;
	}
	private void Lifetime(int N,int usernumber,long Logintime[],boolean[] AliveFlag,long lifetime){
		for(int i=0;i<N;i++){
			Logintime[usernumber]=System.currentTimeMillis();
			if(System.currentTimeMillis()-Logintime[i]>lifetime) AliveFlag[i]=false;
		}
		return;
	}
}