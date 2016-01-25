package appl;

import server.Server;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import server.GenericConsumer;
import server.GenericResource;

public class MainServer extends Server{
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
			
		int serverPort = 6969;
		new MainServer(serverPort);
	}
	
	public MainServer(int port){
		super(port);
				
		System.err.println("server ok!");
		
		this.begin();
				
	}

	@Override
	protected GenericConsumer<Socket> createSocketConsumer(
			GenericResource<Socket> r) {
		// TODO Auto-generated method stub
		return new SocketConsumer<Socket>(r);
	}

}
