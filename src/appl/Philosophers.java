package appl;

public class Philosophers {
	public enum State {
		THINKING, HUNGRY, EATING
	}
	
	public static State mState; 
	
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	} 
}
