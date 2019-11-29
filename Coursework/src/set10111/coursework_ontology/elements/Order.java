package set10111.coursework_ontology.elements;

import java.util.ArrayList;

import jade.content.Concept;
import jade.content.onto.annotations.Slot;
import jade.core.AID;

public class Order implements Concept {
	
	private Phone phone;
	private ArrayList<Item> parts = new ArrayList<Item>();
	private int cost;
	
	@Slot (mandatory = true)
	public void setPhone(Phone phone){
		this.phone = phone;
	}
	
	@Slot (mandatory = true)
	public Phone getPhone(){
		return phone;
	}
	@Slot (mandatory = true)
	public void setParts(ArrayList<Item> parts){
		if(this.parts.isEmpty()){
			this.parts.addAll(parts);
		}else{
			this.parts.clear();
			this.parts.addAll(parts);
		}
	}
	
	@Slot (mandatory = true)
	public ArrayList<Item> getParts(){
		return parts;
	}
	
	@Slot (mandatory = true)
	public int getCost(){
		return cost;
	}
	
	@Slot (mandatory = true)
	public void setCost(int cost){
		this.cost += cost;
	}
	
	@Slot (mandatory = true)
	public void resetCost(){
		this.cost = 0;
	}

}
