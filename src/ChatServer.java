/*
 *@file:    ChatServer.java
 *@author:  maeda, iwamoto
 *@date:    17-05-06
 */
package src;

//入出力ストリームを使うので，java.io.* を import
import java.io.*;
// ソケットを使うので java.net.* を import 
import java.net.*;


public class ChatServer {
	// 各クライアントを記憶する配列．
	static Worker workers[] = new Worker[ParamsProvider.getMaxThreadNum()];

    // TODO:
    public static DataManager data_manager;


    // コンストラクタ
	public ChatServer() {
		// ポート番号を 1707にする．同じマシンで同じポートを使うことは
		// できないので，ユーザごとに変えること(1023以下は使えない)
		int port = ParamsProvider.getPortNum();
        System.out.println("[Info]server waiting on the port [" + port + "]");
        DataManager.logging("[Info]server waiting on the port [" + port + "]\n");
		Socket sock;
		try {
			// ServerSocketを作成
			ServerSocket servsock = new ServerSocket(port);
			// 無限ループ，breakが来るまで
			while (true) {
				// クライアントからのアクセスをうけつけた
				sock = servsock.accept();
				int i;
				// 配列すべてについて
				for (i = 0; i < workers.length; i++) {
					// 空いている要素があったら
					if (workers[i] == null) {
						// Workerを作って
						workers[i] = new Worker(i, sock, ChatServer.data_manager);
						// 対応するスレッドを走らせる
						new Thread(workers[i]).start();
						break;
					}
				}
				if (i == workers.length) {
					System.out.println("Can't serve because the threads are full");
				}
			}
		} catch (IOException ioe) {
			System.out.println(ioe);
		}
	} // ChatServer

	// クライアント n が抜けたこと記録し，他のユーザに送る
    // called in the worker thread's run process
	public static void remove(int n) {
		workers[n] = null;
	}
}
