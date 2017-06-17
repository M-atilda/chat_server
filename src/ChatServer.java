package src;

//入出力ストリームを使うので，java.io.* を import
import java.io.*;
// ソケットを使うので java.net.* を import 
import java.net.*;
import java.io.File;

class ChatServer {
	// 各クライアントを記憶する配列．
	Worker workers[];
	public static File file = new File("Lolita_log.txt");

	// コンストラクタ
	public ChatServer() {
		// ポート番号を 1707にする．同じマシンで同じポートを使うことは
		// できないので，ユーザごとに変えること(1023以下は使えない)
		int port = 1707;
		// 配列を作成
		workers = new Worker[20];
		Socket sock;
		try {
			// ServerSocketを作成
			ServerSocket servsock = new ServerSocket(port);
			// 無限ループ，breakが来るまで
			while (true) {
				// クライアントからのアクセスをうけつけた．
				sock = servsock.accept();
				int i;
				// 配列すべてについて
				for (i = 0; i < workers.length; i++) {
					// 空いている要素があったら，
					if (workers[i] == null) {
						// Workerを作って
						workers[i] = new Worker(i, sock, this);
						// 対応するスレッドを走らせる
						new Thread(workers[i]).start();
						break;
					}
				}
				if (i == workers.length) {
					System.out.println("Can't serve");
				}
			}
		} catch (IOException ioe) {
			System.out.println(ioe);
		}
	}

	public static void main(String args[]) throws IOException {
		// インスタンスを1つだけ作る．
		new ChatServer();
		
	}

	// synchronized は，同期のためのキーワード．つけなくても動くことはある．
	public synchronized void sendAll(String s) {
		int i;
		for (i = 0; i < workers.length; i++) {
			// workers[i]が空でなければ文字列を送る
			if (workers[i] != null)
				workers[i].send(s);
		}
	}

	// クライアント n が抜けたこと記録し，他のユーザに送る．
	public void remove(int n) {
		workers[n] = null;
		sendAll("quiting [" + n + "]");
	}
}