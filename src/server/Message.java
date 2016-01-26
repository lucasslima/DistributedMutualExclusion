/**
 * 
 */
package server;

import java.io.Serializable;

/**
 * @author Joubert
 * @version 1.0
 * 
 * enables any type of messages in Java Ca&La
 */
public interface Message extends Serializable{
	
	public abstract int getType();
	public abstract void setType(int type);
	
}
