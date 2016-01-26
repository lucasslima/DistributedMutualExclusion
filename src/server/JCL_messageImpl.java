package server;

import java.sql.Timestamp;

public class JCL_messageImpl implements JCL_message{

	/**
	 * 
	 */
	public static int ACK = 0;
	public static int RELEASE = 1;
	private static final long serialVersionUID = -1298874117877687170L;
	private int type;
	private Timestamp mTimestamp;
	private int message;
	public JCL_messageImpl(){
		mTimestamp = new Timestamp(System.currentTimeMillis());
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
