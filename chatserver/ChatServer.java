package chatserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class ChatServer extends Thread{

	private static int mainPort = 55005;
	//private static int currentPort = 55006;
	private int port;
	private static Socket[] clients = new Socket[0];

	public ChatServer(int p){
		this.port = p;
	}


	public static void main(String[] args) {
		System.out.println("Initializing Server...");
		Thread t = new ChatServer(55006);			//initially run as 55006, but the rest are non-static
		t.start();
		System.out.println("Finished.");
		System.out.println("Awaiting connections.");
	}
	public void run(){
		try{
			ServerSocket server = new ServerSocket(55005);
			Socket S = server.accept();
			server.close();

			DataOutputStream o = getOut(S);
			o.writeUTF(Integer.toString(this.port));
			S.close();

			ServerSocket main = new ServerSocket(this.port);
			Socket client = main.accept();
			addClient(client);
			main.close();//close the serverSocket

			System.out.println("Allowed Client on port "+(this.port));
			Thread t = new ChatServer(this.port+1);
			t.start();

			DataInputStream in = new DataInputStream(client.getInputStream());

			DataOutputStream out = getOut(client);
			out.writeUTF("+OK");
			
			while(true){
				String a = in.readUTF();//get a message
				System.out.println("Message received from port "+ this.port);//display the message (encoded)
				StreamToAll(a);//send the message to all			
			}
		}catch(IOException e){
			
		}
	}
	/**a method that spits out the message to all the output streams.
	 * @param str
	 * @throws IOException
	 */
	public static void StreamToAll(String str){//method should work.
		for(int i = 0; i<clients.length;i++){
			if(clients[i]!=null){//if it exists
				try{//check it is listening
					getOut(clients[i]).writeUTF(str);//send to client number "i", and move up the list
				}catch (IOException e){}//no need to take any action
			}
		}
	}
	public static DataOutputStream getOut(Socket s) throws IOException{
		OutputStream o = s.getOutputStream();
		return new DataOutputStream(o);	
	}
	public static void addClient(Socket s){
		Socket[] A = new Socket[clients.length+1];
		for(int i = 0;i<clients.length; i++){
			A[i] = clients[i];
		}
		A[A.length-1] = s;
		clients = Arrays.copyOf(A,A.length);
	}

}
