package appl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import server.ControllerMessage;

public class IpReceiver {
	private static File file;

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		createFile("config.txt");
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
					addHost(message.getMessage());
					
					in.close();
					client.close();
					break;
				case 1: 
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
	
	private static void createFile(String fileName) throws IOException{
		file = new File(fileName);
		if(!file.exists())
			file.createNewFile();
	}
	
	private static void addHost(String hostName) throws IOException{
		FileWriter writer = new FileWriter(file);
		
		BufferedWriter bufferWriter = new BufferedWriter(writer);
		bufferWriter.write(hostName);
		bufferWriter.flush();
		bufferWriter.close();
		
		writer.close();
	}
	
	@SuppressWarnings("resource")
	private static ArrayList<String> getHostNeighboors(String hostName) throws IOException{
		ArrayList<String> hostNeighboors = new ArrayList<String>();
		ArrayList<String> allHosts = new ArrayList<String>();
		String line = null; 
		
		FileReader reader = new FileReader(file);
		
		BufferedReader bufferReader = new BufferedReader(reader);
		
		while((line = bufferReader.readLine()) != null)
			allHosts.add(line);
		
		for(String host : allHosts){
			if(host.equals(hostName)){
				int index = host.indexOf(host);
				
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
