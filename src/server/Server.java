package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public abstract class Server {
	protected GenericConsumer<Socket>[] serverThreads;
	protected GenericResource<Socket> serverR;
	protected int port;
	protected ServerSocket serverSocket;
	protected long initialTime;
	
	private int numOfThreads;
	
	public Server(int port){
		this.initialTime = System.nanoTime();		
		this.port = port;
	}
	
	public void begin(){
		try{
			for(int i=0; i<numOfThreads; i++){
				serverThreads[i] = createSocketConsumer(serverR); 
				serverThreads[i].start();
			}
			
			//start listening 
			listen();
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	protected abstract void listen();
    
    protected void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.port);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port " + port, e);
        }
    }
    
    protected abstract GenericConsumer<Socket> createSocketConsumer( GenericResource<Socket> r);
    
    
}
