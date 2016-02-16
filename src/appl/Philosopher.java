package appl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;

import com.sun.swing.internal.plaf.synth.resources.synth;

import utils.PhilosopherMessage;
import appl.LocalPhilosopher;

public class Philosopher{
	// struct with stats of philosophers
	enum State {
		THINKING, HUNGRY, EATING
	}

	private State 						mState;
	private Timestamp 					mTime;
	private Queue<String> 				fifo;
	private static int 					ackCount;
	private static ArrayList<String> 	neighboors;
	private static ServerSocket 		serverSocket;
	private final InetAddress 			inetAddress = InetAddress.getLocalHost();
	private static int 					port = 6969;
	private final int 					numPhilosophers = 3;
	private Map<String,Thread> 			philosophers;
	private static Philosopher			philosopher;
	public static String				turn; 


	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
		neighboors = new ArrayList<String>();
		getNeighboors();

		ackCount = 0;

		getInstance();
	}
	
	public static synchronized Philosopher getInstance() throws IOException{
		if(philosopher == null)
			philosopher = new Philosopher();
		
		return philosopher;
	}

	public Philosopher() throws IOException {
		philosophers	= (Map<String, Thread>) new TreeMap<String,Thread>();
		serverSocket 	= new ServerSocket(port);
		fifo 			= new LinkedBlockingQueue<String>();
		mState 			= State.THINKING;
		mTime 			= new Timestamp(System.currentTimeMillis());
		ArrayList<String> ports = new ArrayList<String>();
		ports.add("6970");
		ports.add("6971");
		ports.add("6972");
	
		for(int i = 0; i < numPhilosophers; i++){
			final int index = i;
			try{
				philosophers.put(ports.get(i), new Thread() {
					@Override
					public void run() {
						ArrayList<String> aux = new ArrayList<String>();
						if(index == 0){
							aux.add(ports.get(numPhilosophers-1));
							aux.add(ports.get(index+1));
						}else if(index == numPhilosophers - 1){
							aux.add(ports.get(index-1));
							aux.add(ports.get(0));
						}else{
							aux.add(ports.get(index-1));
							aux.add(ports.get(index+1));
						}
						
						try {
							new LocalPhilosopher(ports.get(index), aux);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
				philosophers.get(ports.get(i)).start();
				Thread.sleep(500);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		try {
			// Cria uma thread para o servidor que ficará escutando outros
			// filosofos
			new Thread() {
				@Override
				public void run() {
					listen();
				}
			}.start();
			
			try{
				Thread.sleep(5000);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			while (true) {
				synchronized (this) {
					wait();
				}
				mState = State.THINKING;
				System.err.println("Philosopher of port:  " + turn + " is thinking...!");
				Thread.sleep(1500);
				mState = State.HUNGRY;
				System.err.println("Philosopher of port:  " + turn + " is hungry...!");
				
				mTime = new Timestamp(System.currentTimeMillis());
				sendMessage(PhilosopherMessage.REQUEST, neighboors.get(0));
				sendMessage(PhilosopherMessage.REQUEST, neighboors.get(1));
				
				//espera por dois acks 
				//while (ackCount < 2);
				if (ackCount < 2){
					synchronized (this) {
						this.wait();
					}
				}
				
				mState = State.EATING;
				eat();
				
				while (!fifo.isEmpty()) {
					sendMessage(PhilosopherMessage.ACK, fifo.poll());
				}
				
				sendMessage(2,"localhost", Integer.parseInt(turn));
				turn = "null";
				
				mState = State.THINKING;
			}
		} catch (Exception e) {
			// TODO Handle no philosopher on left or right
			e.printStackTrace();
		}
	}

	private void listen() {
		while (true) {
			try {
				// Cria um socket para o cliente que está enviando uma requisição
				Socket clientSocket = this.serverSocket.accept();
			
				// Cria um objeto para receber uma requisição
				ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
				try {
					// Converte a mensagem recebida para o tipo
					// PhilosopherMessage
					PhilosopherMessage message = (PhilosopherMessage) in.readObject();
					
					String typeRequest = null; 
					if(message.getType() == 0)
						typeRequest = "ack";
					else 
						typeRequest = "request";
					
//					System.out.println("Recebendo " + typeRequest + " do ip: " + message.getId());

					switch (message.getType()) {
					// Ack significa que um release é recebido
					case PhilosopherMessage.ACK:
						synchronized(this){
							ackCount++;
						}
						// Se dois acks(do filosofo da direita e da esquerda)
						// são recebidos este filosofo pode comer
						if (ackCount == 2) {
							synchronized(this){
								this.notify();
							}
						}
						break;
						
					// Request significa que algum filosofo está pedindo para
					// comer
					case PhilosopherMessage.REQUEST:
						// Se o estado deste filoso é "com fome" ele irá
						// comparar o timestamp para saber
						// qual filosofo irá comer
						if (mState == State.HUNGRY) {
							// Se o meu timestamp é menor que o do filosofo que
							// está pedindo, sua requisao será colocada na fila
							if (mTime.getTime() <= message.getTimestamp().getTime()) {
								fifo.add(message.getId());
								// Caso o timestamp do filosofo que está pedindo
								// é menor, então um ack é enviado de volta
							} else {
								sendMessage(PhilosopherMessage.ACK, message.getId());
							}
						}
						else{
							if (mState == State.THINKING)
								sendMessage(PhilosopherMessage.ACK,message.getId());
							else
								fifo.add(message.getId());
						}
						break;
					case PhilosopherMessage.WAKEUP:
						turn = message.getId();
						synchronized (this) {
							this.notify();
						}
						break;
					default:
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				clientSocket.close();
				in.close();
			} catch (IOException e) {

				System.out.println("Server Stopped.");
				return;
			}
		}

	}

	private static void getNeighboors() throws IOException {
		File file = new File("neighboors.txt");

		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);

		String line = null;

		while ((line = bufferedReader.readLine()) != null){
			neighboors.add(line);
		}
	}

	private void sendMessage(int type, String ip) throws UnknownHostException, IOException {
		// Cria uma nova conexão com o vizinho
		try{
			Socket neighboor = new Socket(ip, port);
	
			PhilosopherMessage message = new PhilosopherMessage();
			message.setType(type);
			message.setId(neighboor.getLocalAddress().getHostAddress());
			if (type == PhilosopherMessage.REQUEST){
				message.setTimestamp(mTime);
			}
			
			// Escreve o objeto a ser enviado e fecha a conexão
			ObjectOutputStream out = new ObjectOutputStream(neighboor.getOutputStream());
			out.writeObject(message);
			out.flush();
			out.close();
	
			neighboor.close();
		}
		catch (Exception e){
			synchronized (this) {
				ackCount++;
			}
		}
	}
	
	private void sendMessage(int type, String ip,int port) throws UnknownHostException, IOException {
		// Cria uma nova conexão com o vizinho
		try{
			Socket neighboor = new Socket(ip, port);
	
			PhilosopherMessage message = new PhilosopherMessage();
			message.setType(type);
			message.setId(neighboor.getLocalAddress().getHostAddress());
			if (type == PhilosopherMessage.REQUEST){
				message.setTimestamp(mTime);
			}
			
			// Escreve o objeto a ser enviado e fecha a conexão
			ObjectOutputStream out = new ObjectOutputStream(neighboor.getOutputStream());
			out.writeObject(message);
			out.flush();
			out.close();
	
			neighboor.close();
		}
		catch (Exception e){
			synchronized (this) {
				ackCount++;
			}
		}
	}

	private void eat() throws UnknownHostException, InterruptedException {
		System.err.println("Philosopher of port:  " + turn + " is eating...!");
		synchronized (this) {
			ackCount = 0;
		}
	}
}
