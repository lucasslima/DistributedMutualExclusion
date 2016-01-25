package appl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import server.JCL_message;
import server.JCL_messageImpl;

public class Client {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Client();
	}
	
	public Client(){
		try{
			Socket s = new Socket("localhost", 6969);
						
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
