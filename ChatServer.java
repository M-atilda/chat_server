//���o�̓X�g���[�����g���̂ŁCjava.io.* �� import
import java.io.*;
// �\�P�b�g���g���̂� java.net.* �� import 
import java.net.*;

class ChatServer{
	   // �e�N���C�A���g���L������z��D
	 Worker workers[];
	   // �R���X�g���N�^
	 public ChatServer(){
	     // �|�[�g�ԍ��� 4444�ɂ���D�����}�V���œ����|�[�g���g�����Ƃ�
	     // �ł��Ȃ��̂ŁC���[�U���Ƃɕς��邱��(1023�ȉ��͎g���Ȃ�)
	   int port=1707;
	     // �z����쐬
	   workers=new Worker[100];
	   Socket sock;
	   try{
	       // ServerSocket���쐬
	     ServerSocket servsock=new ServerSocket(port);
	       // �������[�v�Cbreak������܂�
	     while(true){
	         // �N���C�A���g����̃A�N�Z�X�����������D
	       sock=servsock.accept();
	       int i;
	         // �z�񂷂ׂĂɂ���
	       for(i=0;i< workers.length;i++){
	           // �󂢂Ă���v�f����������C
	         if(workers[i]==null){
	             // Worker�������
	           workers[i]=new Worker(i,sock,this);
	             // �Ή�����X���b�h�𑖂点��
	           new Thread(workers[i]).start();
	           break;
	         }
	       }
	       if(i==workers.length){
	         System.out.println("Can't serve");
	       }
	     }
	   } catch(IOException ioe){
	     System.out.println(ioe);
	   }
	 }
	 public static void main(String args[]) throws IOException{
	     // �C���X�^���X��1�������D
	   new ChatServer();
	 }
	   // synchronized �́C�����̂��߂̃L�[���[�h�D���Ȃ��Ă��������Ƃ͂���D
	 public synchronized void sendAll(String s){
	   int i;
	   for(i=0;i< workers.length;i++){
	       // workers[i]����łȂ���Ε�����𑗂�
	     if(workers[i]!=null)
	       workers[i].send(s);
	   }
	 }
	   // �N���C�A���g n �����������ƋL�^���C���̃��[�U�ɑ���D
	 public void remove(int n){
	   workers[n]=null;
	   sendAll("quiting ["+n+"]");
	 }
}