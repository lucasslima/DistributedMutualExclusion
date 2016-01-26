package appl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

import server.GenericConsumer;
import server.GenericResource;
import server.Message;
import server.PhilosopherMessage;
import server.Server;

public class Philosopher extends Server{

	private int feedCount;
	enum State{
		THINKING,
		HUNGRY,
		EATING
	}
	State mState;
	private int id;
	private SocketConsumer<Socket> mConsumer;
	private Timestamp mTime;
	private Queue<Integer> fifo;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Philosopher(Integer.parseInt(args[1]),Integer.parseInt(args[2]));
	}
	
	public Philosopher(int id, int port){
		super(port);
		feedCount = 0;
		this.id = id;
		fifo = new LinkedBlockingQueue<Integer>();
		try{
			Socket s = new Socket("localhost", 6969);
			GenericResource<Socket> re = new GenericResource<>();
			re.putRegister(s);
			mConsumer = new SocketConsumer<>(re);
			while (true){
				Thread.sleep( (long) Math.random() % 1000);
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	protected GenericConsumer<Socket> createSocketConsumer(GenericResource<Socket> r) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void listen() {
		while(! serverR.isStopped()){
          try {
          	Socket clientSocket = this.serverSocket.accept();
              ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
              try{
            	  PhilosopherMessage message = (PhilosopherMessage) ois.readObject();
            	  switch (message.getType()) {
				case PhilosopherMessage.ACK:
					
					break;
				case PhilosopherMessage.REQUEST:
					if (mState == State.HUNGRY){
						if (mTime.getTime() <= message.getTimestamp().getTime()){
							fifo.add(message.getId());
						}
						else{
							sendMessage(PhilosopherMessage.ACK,message.getId());
						}
					}
					break;
				default:
					break;
				}
              }catch(Exception e){
            	  e.printStackTrace();
              }
          } catch (IOException e) {
              if(serverR.isStopped()) {
                  System.out.println("Server Stopped.") ;
                  return;
              }
              throw new RuntimeException(
                  "Error accepting client connection", e);
          } 
          
          
      }
      System.out.println("Server Stopped.") ;
		
	}
	private void sendMessage(int type,int id){
		
	}
}
