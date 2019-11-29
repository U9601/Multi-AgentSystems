package set10111.coursework_ontology.elements;

import java.util.ArrayList;

import jade.content.Concept;
import jade.content.onto.annotations.Slot;

public class Storage extends Item {
private int size;

	
	@Slot(mandatory = true)
	public Integer getSize(){
		return size;
	}
	
	@Slot (mandatory = true)
	public void setSize(int size){
		this.size = size;
	}
}