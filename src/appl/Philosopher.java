package appl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import server.GenericConsumer;
import server.GenericResource;
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
	private static int ackCount;
	private static IpSender sender;
	/**
	 * @param args
	 * @throws IOException 
	 * @throws UnknownHostException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
		String ipServer = "type ip here";
		
		sender = new IpSender(ipServer);
		sender.sendIP();
		
		new Philosopher(Integer.parseInt(args[1]),Integer.parseInt(args[2]));
	}
	
	public Philosopher(int id, int port){
		super(port);
		feedCount = 0;
		ackCount = 0;
		this.id = id;
		fifo = new LinkedBlockingQueue<Integer>();
		try{
			Socket s = new Socket("localhost", 6969);
			GenericResource<Socket> re = new GenericResource<>();
			re.putRegister(s);
			mConsumer = new SocketConsumer<>(re);
			Thread listeningThread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					listen();
				}
			});
			listeningThread.run();
			while (true){
				mState = State.THINKING;
				Thread.sleep( (long) Math.random() % 1000);
				mState = State.HUNGRY;
				//TODO Send request for left and right
				sendMessage(PhilosopherMessage.REQUEST, id);
				sendMessage(PhilosopherMessage.REQUEST, id);
				//TODO Wait for ACTs from left and right
				eat();
				while(!fifo.isEmpty()){
					sendMessage(PhilosopherMessage.ACK, fifo.poll());
				}
			}
		}catch(Exception e){
			//TODO Handle no philosopher on left or right
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
					ackCount++;
					if (ackCount == 2){
						mState = State.EATING;
						ackCount = 0;
						//TODO Wake main thread
					}
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
	private void eat(){
		
	}
}
