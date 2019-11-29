package set10111.coursework_ontology.elements;

import jade.content.Predicate;
import jade.content.onto.annotations.Slot;
import jade.core.AID;

public class Owns implements Predicate {
	private AID owner;
	private Phone phone;
	
	@Slot (mandatory = true)
	public AID getOwner(){
		return owner;
	}
	@Slot (mandatory = true)
	public void setOwner(AID owner){
		this.owner = owner;
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
