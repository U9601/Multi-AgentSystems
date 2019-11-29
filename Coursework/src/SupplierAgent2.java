import java.util.HashMap;

import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.*;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import set10111.coursework_ontology.EcommerceOntology;
import set10111.coursework_ontology.elements.Battery;
import set10111.coursework_ontology.elements.Owns;
import set10111.coursework_ontology.elements.Phone;
import set10111.coursework_ontology.elements.RAM;
import set10111.coursework_ontology.elements.Screen;
import set10111.coursework_ontology.elements.Sell;
import set10111.coursework_ontology.elements.Storage;
public class SupplierAgent2 extends Agent{

	private Codec codec = new SLCodec();
	private Ontology ontology = EcommerceOntology.getInstance();
	//stock list, with serial number as the key
	private HashMap<Integer, Phone> itemsForSale = new HashMap<>();
	
	protected void setup(){
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("supplier2");
		sd.setName(getLocalName() + "-supplier2-agent");
		dfd.addServices(sd);
		try{
			DFService.register(this, dfd);
		}catch(FIPAException e){
			e.printStackTrace();
		}
		
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		
	
	}
	
	private class QueryBehaviour extends CyclicBehaviour{
		@Override
		public void action(){
			//This behaviour should only respond to QUERY_IF messages
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.QUERY_IF);
			ACLMessage msg = receive(mt);
			if (msg != null){
				try{
					ContentElement ce = null;
					System.out.println(msg.getContent()); //print out the message content in SL
					
					ce = getContentManager().extractContent(msg);
					if (ce instanceof Owns){
						Owns owns = (Owns) ce;
						Phone ph = owns.getPhone();
						
						/*RAM ram = (RAM)ph;
						Storage storage = (Storage)ph;
						
						System.out.print("The Amount of RAM is" +((RAM) ph).getAmount());
						System.out.print("The Storage Size is" +((Storage) ph).getSize());
						
						if(itemsForSale.containsKey(((RAM)ph).getAmount())){
							System.out.println("I have the RAM in stock");
						}
						if(itemsForSale.containsKey(((Screen)ph).getLength())){
							System.out.println("I have the screen in stock");
						}*/
					}
				}catch(CodecException ce){
					ce.printStackTrace();
					
				}catch(OntologyException oe){
					oe.printStackTrace();
					
				}
			}else{
				block();
			}
			
		}
		
		private class SellBehaviour extends CyclicBehaviour{
			@Override
			public void action(){
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
				ACLMessage msg = receive(mt);
				if(msg != null){
					try{
						ContentElement ce = null;
						System.out.println(msg.getContent());
						
						ce = getContentManager().extractContent(msg);
						if(ce instanceof Action){
							Concept action = ((Action)ce).getAction();
							if (action instanceof Sell){
								Sell order = (Sell)action;
								Phone ph = order.getPhone();
								
								/*if(ph instanceof RAM){
									if(itemsForSale.containsKey(((RAM)ph).getAmount())){
										System.out.println("Selling RAM" + ((RAM)ph).getAmount());
									}
								}
								if(ph instanceof Storage){
									if(itemsForSale.containsKey(((Storage)ph).getSize())){
										System.out.println("Selling Storage" + ((Storage)ph).getSize());
									}
								}*/
								
							}
							
						}
					}catch(CodecException ce){
						ce.printStackTrace();
					}catch(OntologyException oe){
						oe.printStackTrace();
					}
				}else{
					block();
				}
			}
		}
	}

}
