package set10111.coursework_ontology.elements;

import java.util.concurrent.atomic.AtomicInteger;

import jade.content.Concept;
import jade.content.onto.annotations.Slot;

public class Phone implements Concept {
	
	private int serialNumber;
	private double Quantity;
	private double numDaysDue;
	private double perDayPenalty;
	private double unitPrice;
	
	@Slot (mandatory = true)
	public int getSerialNumber(){
		return serialNumber;
	}
	
	@Slot (mandatory = true)
	public double getQuantity(){
		return Quantity;
	}
	
	@Slot (mandatory = true)
	public void setSerialNumber (int serialNumber){
		this.serialNumber += serialNumber;
	}
	
	@Slot (mandatory = true)
	public void setQuantity(double Quantity){
		this.Quantity = Quantity;
	}
	
	@Slot (mandatory = true)
	public void setNumDaysDue(double numDaysDue){
		this.numDaysDue = numDaysDue;
	}
	
	@Slot (mandatory = true)
	public double getNumDaysDue(){
		return numDaysDue;
	}
	@Slot (mandatory = true)
	public void setPerDayPenalty(double perDayPenalty){
		this.perDayPenalty = perDayPenalty;
	}
	@Slot (mandatory = true)
	public double getPerDaypenalty(){
		return perDayPenalty;
	}
	
	@Slot (mandatory = true)
	public void setUnitPrice(double unitPrice){
		this.unitPrice = unitPrice; 
	}
	
	@Slot (mandatory = true)
	public double getUnitPrice(){
		return unitPrice; 
	}
	

}
