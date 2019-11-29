package set10111.coursework_ontology.elements;

import java.util.ArrayList;

import jade.content.Concept;
import jade.content.onto.annotations.Slot;

public class Screen extends Item{
private int length;

	
	@Slot(mandatory = true)
	public Integer getLength(){
		return length;
	}
	
	@Slot (mandatory = true)
	public void setLength(int length){
		this.length = length;
	}
}
	
	