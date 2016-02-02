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

import server.GenericConsumer;
import server.GenericResource;
import server.PhilosopherMessage;
import server.Server;

public class Philosopher {
	// struct with stats of philosophers
	private static int feedCount;

	enum State {
		THINKING, HUNGRY, EATING
	}

	State mState;
	private String id;
	private SocketConsumer<Socket> mConsumer;
	private Timestamp mTime;
	private Queue<String> fifo;
	private static int ackCount;
	private static IpSender sender;
	private static ArrayList<String> neighboors;
	private static ServerSocket serverSocket;
	private final InetAddress inetAddress = InetAddress.getLocalHost();

	/**
	 * @param args
	 * @throws IOException
	 * @throws UnknownHostException
	 * @throws ClassNotFoundException
	 */
	public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
		neighboors = new ArrayList<String>();
		getNeighboors();

		feedCount = 0;
		ackCount = 0;

		new Philosopher("localhost", 6969);
	}

	public Philosopher(String id, int port) throws IOException {
		serverSocket = new ServerSocket(port);

		this.id = id;
		fifo = new LinkedBlockingQueue<String>();

		try {
			// Cria uma thread para o servidor que ficará escutando outros
			// filosofos
			new Thread() {
				@Override
				public void run() {
					listen();
				}
			}.start();

			while (true) {
				mState = State.THINKING;
				System.out.println("IP: " + inetAddress.getHostAddress() + " is thinking...!");
				Thread.sleep((long) Math.random() % 1000);
				mState = State.HUNGRY;
				System.out.println("IP: " + inetAddress.getHostAddress() + " is hungry...!");
				
				sendMessage(PhilosopherMessage.REQUEST, neighboors.get(0));
				sendMessage(PhilosopherMessage.REQUEST, neighboors.get(1));
				
				//espera por dois acks 
				while (ackCount < 2);
				
				eat();

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
				// Cria um socket para o cliente que está enviando uma
				// requisição
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
						ackCount++;
						// Se dois acks(do filosofo da direita e da esquerda)
						// são recebidos este filosofo pode comer
						if (ackCount == 2) {
							mState = State.EATING;
							ackCount = 0;
							// TODO Wake main thread
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
						break;
					default:
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
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

		while ((line = bufferedReader.readLine()) != null)
			neighboors.add(line);
	}

	private void sendMessage(int type, String ip) throws UnknownHostException, IOException {
		// Cria uma nova conexão com o vizinho
		Socket neighboor = new Socket(ip, 6970);

		PhilosopherMessage message = new PhilosopherMessage();
		message.setType(type);

		// Escreve o objeto a ser enviado e fecha a conexão
		ObjectOutputStream out = new ObjectOutputStream(neighboor.getOutputStream());
		out.writeObject(message);
		out.flush();
		out.close();

		neighboor.close();
	}

	private void eat() throws UnknownHostException {
		mState = State.EATING;
		System.out.println("IP: " + inetAddress.getHostAddress() + " is eating...!");
	}
}
