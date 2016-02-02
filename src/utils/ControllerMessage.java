package utils;

@SuppressWarnings("serial")
public class ControllerMessage implements Message {
	private int type;
	private String message;
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getType(){
		return type;
	}
	
	public void setType(int type){
		this.type = type;
	}
}
