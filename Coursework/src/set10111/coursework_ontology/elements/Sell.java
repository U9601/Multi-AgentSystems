package set10111.coursework_ontology.elements;

import jade.content.AgentAction;
import jade.content.onto.annotations.Slot;
import jade.core.AID;

public class Sell implements AgentAction {
	
	private AID buyer;
	private Phone phone;
	
	@Slot (mandatory = true)
	public AID getBuyer(){
		return buyer;
	}
	
	@Slot (mandatory = true)
	public void setBuyer(AID buyer){
		this.buyer = buyer;
	}
	
	@Slot (mandatory = true)
	public Phone getPhone(){
		return phone;
	}
	
	@Slot (mandatory = true)
	public void setPhone(Phone phone){
		this.phone = phone;
	}

}
