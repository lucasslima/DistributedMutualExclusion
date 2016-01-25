package server;

public class JCL_messageImpl implements JCL_message{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1298874117877687170L;
	private int type;
	
	public JCL_messageImpl(){
		
	}
	
	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return type;
	}

	@Override
	public void setType(int type) {
		this.type = type;		
	}

	

}
