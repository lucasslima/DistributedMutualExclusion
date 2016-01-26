package appl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;
import java.util.function.Consumer;

import server.JCL_message;
import server.JCL_messageImpl;

public class Client {

	private int feedCount;
	private int id;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Client(Integer.parseInt(args[1]));
	}
	
	public Client(int id){
		feedCount = 0;
		try{
			Socket s = new Socket("localhost", 6969);
			while (true){
				Thread.sleep( (long) Math.random() % 1000);
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
