package server;


import java.util.concurrent.ConcurrentLinkedQueue;

public class GenericResource<S> {
	private ConcurrentLinkedQueue<S> registers;
	private boolean finished, serverFinished;
	
	public GenericResource(){
		this.registers = new ConcurrentLinkedQueue<S>();
		finished= false;
		serverFinished=false;
	}
	
	public void putRegister(S register){
		
			this.registers.offer(register);
			wakeup();
		
	}	
	
	
	public S getRegister() throws Exception{
		
			if(!this.registers.isEmpty())
				return this.registers.poll();
			else {
				if(finished==false)
					suspend();
				return null;		
			}		
	}
	
	private synchronized void suspend()throws Exception{
		wait();
	}
	
	private synchronized void wakeup(){
		notify();
	}
	
	public int getNumOfRegisters(){
		return this.registers.size();
	}
	
	public synchronized void setFinished(){
		this.finished = true;
		this.notifyAll();
	}
	
	public boolean isFinished(){
		return this.finished;
	}
	
	public boolean isStopped(){
		return serverFinished;
	}
	
	public synchronized void stopServer(){
		serverFinished = true;
	}
}
