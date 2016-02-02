package appl;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import utils.ControllerMessage;

public class IpSender {
	private  Socket s;
	private  String ip;
	
	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException{
		IpSender ipSender = new IpSender("192.168.0.112");
		boolean firstTime = false;
		
		if(firstTime)
			ipSender.sendIP();
		else 
			ipSender.getIps();
	}
	
	public IpSender(String ipServer) throws UnknownHostException, IOException{
		s = new Socket(ipServer, 6969);
		ip = s.getLocalAddress().getHostAddress();
		System.out.println("My ip: " + ip);

	}
	
	public void sendIP() throws UnknownHostException, IOException, ClassNotFoundException{
		ControllerMessage message = new ControllerMessage();
		message.setType(0);
		message.setMessage(ip);
		
		ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
		out.writeObject(message);
		out.flush();
		out.close();			
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<String> getIps() throws IOException, ClassNotFoundException{
		ControllerMessage message = new ControllerMessage();
		message.setType(1);
		message.setMessage(ip);
		
		ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
		out.writeObject(message);
		out.flush();
		
		ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(s.getInputStream()));
		ArrayList<String> ips = (ArrayList<String>) in.readObject();
		
		createFile(ips);
		
		in.close();
		out.close();			
		s.close();
		
		return ips;
	}
	
	private void createFile(ArrayList<String> neighboors) throws IOException{
		File file = new File("neighboors.txt");
		if(!file.exists())
			file.createNewFile();
		
		FileWriter fileWriter = new FileWriter(file);
		BufferedWriter bufferWriter = new BufferedWriter(fileWriter);
		
		for(String neighboor : neighboors){
			bufferWriter.write(neighboor);
			bufferWriter.newLine();
		}
		
		bufferWriter.flush();
		bufferWriter.close();
		
		fileWriter.close();
	}
}
