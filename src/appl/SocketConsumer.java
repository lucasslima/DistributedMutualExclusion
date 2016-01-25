package appl;


import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import server.GenericConsumer;
import server.GenericResource;
import server.JCL_message;

// exemplo de um consumidor !!!

public class SocketConsumer<S extends Socket> extends GenericConsumer<S>{
	//create any constructor type 
	public SocketConsumer(GenericResource<S> re) {		
		super(re);			
	}

	@Override
	protected void doSomething(S str) {
		try{
			// TODO Auto-generated method stub
			ObjectInputStream in = new ObjectInputStream(str.getInputStream());
			
			JCL_message msg = (JCL_message) in.readObject();
			System.err.println("Server: " + msg.getType());
			switch (msg.getType()){
				
			case 100:{					
				
				//faz algo...
				//escreve para o remetente se precisar
				//write
				int i = 3456778;
				int j=0;
				while(j<100000000)j++;
				ObjectOutputStream out = new ObjectOutputStream(str.getOutputStream());
				out.writeObject(new Integer(i));
				out.flush();
				out.close();
				in.close();
				break;
			}
			
			}		
			
			str.close();
				
		}catch (Exception e){
			e.printStackTrace();
			
		}
				
	}	

}
