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
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import utils.PhilosopherMessage;

public class LocalPhilosopher {
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
	private static String 				id; 

	public LocalPhilosopher(String port, String turn,ArrayList<String> neighboors) throws IOException {
		this.neighboors	= neighboors;
		id				= port;
		ackCount 		= 0;
		serverSocket 	= new ServerSocket(Integer.parseInt(port));
		fifo 			= new LinkedBlockingQueue<String>();
		mState 			= State.THINKING;
		mTime 			= new Timestamp(System.currentTimeMillis());
		
		
		
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
				mState = State.THINKING;
				Thread.sleep(1500);
				mState = State.HUNGRY;
				
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
				synchronized (this) {
					turn = port;
					wait();
				}
				
				while (!fifo.isEmpty()) {
					sendMessage(PhilosopherMessage.ACK, fifo.poll());
				}
				
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

	private void sendMessage(int type, String port) throws UnknownHostException, IOException {
		// Cria uma nova conexão com o vizinho
		try{
			Socket neighboor = new Socket("localhost", Integer.parseInt(port));
	
			PhilosopherMessage message = new PhilosopherMessage();
			message.setType(type);
			message.setId(id);
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
}
