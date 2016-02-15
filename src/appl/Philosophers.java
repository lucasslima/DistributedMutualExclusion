package appl;

public class Philosophers {
	public enum State {
		THINKING, HUNGRY, EATING
	}
	
	private int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	} 
}
