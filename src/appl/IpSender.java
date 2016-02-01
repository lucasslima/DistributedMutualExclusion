package appl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import server.ControllerMessage;

public class IpSender {
	private  Socket s;
	private  String ip;
	
	public IpSender(String ipServer) throws UnknownHostException, IOException{
		s = new Socket(ipServer, 6969);
		InetAddress inetAddres = InetAddress.getLocalHost();
		ip = inetAddres.getHostAddress();

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
		
		in.close();
		out.close();			
		s.close();
		
		return ips;
	}
}
