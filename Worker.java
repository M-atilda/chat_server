// ���o�̓X�g���[�����g���̂ŁCjava.io.* �� import
import java.io.*;
// �\�P�b�g���g���̂� java.net.* �� import 
import java.net.*;

// ��l�̃N���C�A���g�Ƃ̒ʐM��S������X���b�h
// �X���b�h��ő��点�邽�� Runnable �C���^�t�F�[�X������
class Worker implements Runnable {
	// �ʐM�̂��߂̃\�P�b�g
	Socket sock;
	// ���̃\�P�b�g����쐬�������o�͗p�̃X�g���[��
	PrintWriter out;
	BufferedReader in;
	// �T�[�o�{�̂̃��\�b�h���Ăяo�����߂ɋL��
	ChatServer chatServer;
	// �S������N���C�A���g�̔ԍ�
	int n;
	// �N���C�A���g�̑���
	static int N=7;
	// �N���C�A���g�������Ă��邩�̔���flag
	static boolean AliveFlag[]=new boolean[N];
	static String DeadOrAlive="FFFFFFF";
	// �N���C�A���g�̃��O�C������
	static long Logintime[]=new long[N];
	static long lifetime=20000;

	// �R���X�g���N�^
	public Worker(int n, Socket s, ChatServer cs) {
		this.n = n;
		chatServer = cs;
		sock = s;
		out = null;
		in = null;
	}

	// �Ή�����X���b�h�� start �������ɌĂ΂��D
	public void run() {
		System.out.println("Thread running:" + Thread.currentThread());
		// 0~6�̔z��index�ɑΉ�
		String usernumber=null;
		// �{���𗬂�����
		String message = "";
		String s=null;
		// �s���̃J�E���g
		int count=0;
		try {
			// �\�P�b�g����X�g���[���̍쐬
			out = new PrintWriter(sock.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			usernumber=in.readLine();
			s=in.readLine();
			 // �\�P�b�g����̓��͂���������C
			while(s!=null&&!(s.equals(""))){
				System.out.println(s);
		    	message+=s;
		    	s=null;
		        count++;
		        s=in.readLine();
		      }
		   // �N���C�A���g�S�̂ɑ���D
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
		        // �������g���e�[�u�������菜��
		      chatServer.remove(n);
		        // �\�P�b�g�����
		      sock.close();
		    }
		    catch(IOException ioe){
		      System.out.println(ioe);
		      chatServer.remove(n);
		    }
		  }
		
	// �Ή�����\�P�b�g�ɕ�����𑗂�
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