package appl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import utils.ControllerMessage;

public class IpReceiver {
	private static ArrayList<String> allHosts;
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		InetAddress inetAddress = InetAddress.getLocalHost();
		System.out.println("Iniciando o servidor no ip: " + inetAddress.getHostAddress());
		allHosts = new ArrayList<String>();
		listen();
	}
	
	@SuppressWarnings("resource")
	private static void listen() throws IOException, ClassNotFoundException{
		ServerSocket server = new ServerSocket(6969);
		
		while(true){
			Socket client = server.accept();
           
			ObjectInputStream in = new ObjectInputStream(client.getInputStream());
			ControllerMessage message = (ControllerMessage) in.readObject();
						
			switch(message.getType()){
				case 0: 
					System.out.println("Adicionando o host " + message.getMessage());
					addHost(message.getMessage());
					
					in.close();
					client.close();
					break;
				case 1: 
					System.out.println("Obtendo vizinho do host " + message.getMessage());
					ArrayList<String> neighboors = getHostNeighboors(message.getMessage());
					
					ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
					out.writeObject(neighboors);
					out.flush();
					out.close();
					
					client.close();
					break; 
			}
		}
	}
		
	private static void addHost(String hostName) throws IOException{
		allHosts.add(hostName);
	}
	
	private static ArrayList<String> getHostNeighboors(String hostName) throws IOException{
		ArrayList<String> hostNeighboors = new ArrayList<String>();
				
		for(String host : allHosts){
			if(host.equals(hostName)){
				int index = allHosts.indexOf(host);
				System.out.println("index: " + index);
				
				if(index == 0){
					hostNeighboors.add(allHosts.get(allHosts.size()-1));
					hostNeighboors.add(allHosts.get(1));
				}
				
				else if(index == allHosts.size() - 1){
					hostNeighboors.add(allHosts.get(index-1));
					hostNeighboors.add(allHosts.get(0));
				}
				
				else{
					hostNeighboors.add(allHosts.get(index-1));
					hostNeighboors.add(allHosts.get(index+1));
				}
			}
		}
		
		return hostNeighboors;
	}
}
