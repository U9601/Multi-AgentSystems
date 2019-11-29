package set10111.coursework_ontology.elements;

import java.util.ArrayList;

import jade.content.onto.annotations.Slot;

public class RAM extends Item{
	private int amount;
	
	
	@Slot(mandatory = true)
	public Integer getAmount(){
		return amount;
	}
	
	@Slot (mandatory = true)
	public void setAmount(int amount){
		this.amount = amount;
	}
}
	
	