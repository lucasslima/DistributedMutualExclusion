package server;

public abstract class GenericConsumer<S> extends Thread{
	
	protected GenericResource<S> re;
		
	public GenericConsumer(GenericResource<S> re){
		this.re = re;		
	}
	
	public void run(){
		try {
				S str = null;
				
				while((re.isFinished()==false)||(re.getNumOfRegisters()!=0)){
					if ((str = re.getRegister())!=null){
						//fazer algo com o recurso que foi consumido
						doSomething(str);
					}
				}					
				
			} catch (Exception e) {
				
				e.printStackTrace();
			}		
	}
	
	protected abstract void doSomething(S str);

}
