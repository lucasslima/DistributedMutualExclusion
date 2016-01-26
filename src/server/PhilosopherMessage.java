package server;

import java.sql.Timestamp;

public class PhilosopherMessage implements Message{

	/**
	 * 
	 */
	public final static int ACK = 0;
	public final static int REQUEST = 1;
	private static final long serialVersionUID = -1298874117877687170L;
	private int type;
	private Timestamp mTimestamp;
	private int id;
	public PhilosopherMessage(){
		setTimestamp(new Timestamp(System.currentTimeMillis()));
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

	public Timestamp getTimestamp() {
		return mTimestamp;
	}

	public void setTimestamp(Timestamp mTimestamp) {
		this.mTimestamp = mTimestamp;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	

}
