package set10111.coursework_ontology.elements;

import jade.content.AgentAction;
import jade.content.onto.annotations.Slot;
import jade.core.AID;

public class SendOrder implements AgentAction{
	
	private AID customer;
	private Order order;
	
	@Slot(mandatory = true)
	public AID getCustomer(){
		return customer;
	}
	
	@Slot (mandatory = true)
	public void setCustomer(AID customer){
		this.customer = customer;
	}
	
	@Slot (mandatory = true)
	public void setOrder(Order order){
		this.order = order;
	}
	@Slot (mandatory = true)
	public Order getOrder(){
		return order;
	}

}
