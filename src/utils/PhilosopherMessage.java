package utils;

import java.sql.Timestamp;

public class PhilosopherMessage implements Message{
	public final static int ACK = 0;
	public final static int REQUEST = 1;
	public final static int WAKEUP = 2; 
	private static final long serialVersionUID = -1298874117877687170L;
	private int type;
	private Timestamp mTimestamp;
	private String id;
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	

}
