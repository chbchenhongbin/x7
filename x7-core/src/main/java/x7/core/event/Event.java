package x7.core.event;

import x7.core.event.IEventOwner;


/**
 * 
 * @author Wangyan
 *
 */
public class Event implements IEvent{

	private String type;
	private IEventOwner owner;
	
	public void setType(String type) {
		this.type = type;
	}

	public Event(String type){
		this.type = type;
	}
	
	public String getType(){
		return this.type;
	}
	
	public IEventOwner getOwner(){
		return this.owner;
	}
	public void setOwner(IEventOwner owner){
		this.owner = owner;
	}

	@Override
	public String toString() {
		return "Event [type=" + type + ", owner=" + owner + "]";
	}
}


