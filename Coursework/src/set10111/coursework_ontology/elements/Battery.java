package set10111.coursework_ontology.elements;

import java.util.ArrayList;

import jade.content.Concept;
import jade.content.onto.annotations.Slot;

public class Battery extends Item {
	private int capacity;
	
	
	@Slot(mandatory = true)
	public Integer getCapacity(){
		return capacity;
	}
	
	@Slot (mandatory = true)
	public void setCapacity(int capacity){
		this.capacity = capacity;
	}

	
	

}
