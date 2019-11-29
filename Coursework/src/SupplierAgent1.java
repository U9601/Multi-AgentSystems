import java.util.ArrayList;
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
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import set10111.coursework_ontology.EcommerceOntology;
import set10111.coursework_ontology.elements.Battery;
import set10111.coursework_ontology.elements.Item;
import set10111.coursework_ontology.elements.Order;
import set10111.coursework_ontology.elements.Owns;
import set10111.coursework_ontology.elements.Phone;
import set10111.coursework_ontology.elements.RAM;
import set10111.coursework_ontology.elements.Screen;
import set10111.coursework_ontology.elements.Sell;
import set10111.coursework_ontology.elements.SendOrder;
import set10111.coursework_ontology.elements.Storage;

public class SupplierAgent1 extends Agent {
	
	private Codec codec = new SLCodec();
	private Ontology ontology = EcommerceOntology.getInstance();
	//stock list, with serial number as the key
	private ArrayList<Integer> batteryList = new ArrayList<>();
	private ArrayList<Integer> ramList = new ArrayList<>();
	private ArrayList<Integer> screenList = new ArrayList<>();
	private ArrayList<Integer> storageList = new ArrayList<>();
	private ArrayList<Item> partsList = new ArrayList<>();
	private ArrayList<AID> manufacturers = new ArrayList<>();
	private AID tickerAgent;
	private int dayCounter = 0;
	
	Phone ph = new Phone();
    Screen sc = new Screen();
	Battery bt = new Battery();
    RAM ram = new RAM();
    Storage st = new Storage();

	
	protected void setup(){
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("supplier1");
		sd.setName(getLocalName() + "-supplier1-agent");
		dfd.addServices(sd);
		try{
			DFService.register(this, dfd);
		}catch(FIPAException e){
			e.printStackTrace();
		}
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
	
	}
	
	public class TickerWaiter extends CyclicBehaviour{
		public TickerWaiter(Agent a){
			super(a);
		}
		
		@Override
		public void action(){
			MessageTemplate mt = MessageTemplate.or(MessageTemplate.MatchContent("new day"),
					MessageTemplate.MatchContent("terminate"));
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null){
				if(tickerAgent == null){
					tickerAgent = msg.getSender();
				}
				if(msg.getContent().equals("new day")){
					SequentialBehaviour dailyActivity = new SequentialBehaviour();
					
					//dailyActivity.addSubBehaviour(new FindManufacturer(myAgent));
					//dailyActivity.addSubBehaviour(new RetrieveOrders(myAgent));
					//dailyActivity.addSubBehaviour(new SendOrders(myAgent));
					dailyActivity.addSubBehaviour(new EndDay(myAgent));
					myAgent.addBehaviour(dailyActivity);
				}else{
					myAgent.doDelete();
				}
			}else{
				block();
			}
		}
	}
	
	public class FindManufacturer extends OneShotBehaviour{
		
		public FindManufacturer(Agent a){
			super(a);
		}
		
		@Override
		public void action(){
			DFAgentDescription manufacturerTemplate = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("manufactuer");
			
			manufacturerTemplate.addServices(sd);
			try{
				manufacturers.clear();
				DFAgentDescription[] agentsType1 = DFService.search(myAgent, manufacturerTemplate);
				for(int i = 0; i<agentsType1.length; i++){
					manufacturers.add(agentsType1[i].getName());// this is the AID
				}
				
				
			}catch(FIPAException e) {
				e.printStackTrace();
			}
		}
	}
	
	public class RetrieveOrders extends OneShotBehaviour{
		public RetrieveOrders (Agent a){
			super(a);
		}
		
		@Override
		public void action(){
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage msg = myAgent.receive(mt);
            
            if(msg != null) {
            	  try{
            		ContentElement ce = null;
	                ce = getContentManager().extractContent(msg);
	                
	                Action available = (Action) ce;
	                
	                SendOrder sentOrders = ((SendOrder) available.getAction()); // this is the order requested
	                
	                Order order = sentOrders.getOrder();                
	            	ArrayList<Item> items = order.getParts();
	            	
	            	System.out.println("Supplier has received stock request: "+ order.getPhone().getSerialNumber());
	            		for(Item parts : items){
	                	 
		                	  if(parts instanceof Screen){
		                		  System.out.println("Manufacturor has received Parts: "+((Screen) parts).getLength());
		                		  screenList.add(((Screen) parts).getLength());
		                		  
		                	  }
		                	  if(parts instanceof Battery){
		                		  System.out.println("Manufacturor has received Parts: "+((Battery)parts).getCapacity());
		                		  batteryList.add(((Battery)parts).getCapacity());
		                	  }
		                	  if(parts instanceof Storage){
		                		  System.out.println("Manufacturor has received Parts: "+((Storage)parts).getSize());
		                		  storageList.add(((Storage)parts).getSize());
		                	  }
		                	  if(parts instanceof RAM){
		                		  System.out.println("Manufacturor has received Parts: "+((RAM)parts).getAmount());
		                		  ramList.add(((RAM)parts).getAmount());
		                	  }
		                	  
		            	 }
	                }catch(OntologyException oe){
	                	oe.printStackTrace();
	                }catch (jade.content.lang.Codec.CodecException ce) {
						ce.printStackTrace();
					}
            	  
            }
		}
	}
	
	public class SendOrders extends OneShotBehaviour{
		public SendOrders (Agent a){
			super(a);
		}
		
		@Override
		public void action(){
			if(dayCounter == 1){
				dayCounter = 0;
				int size = ramList.size();
				for(int i = 0 ; i < size; i++){
					bt.setCapacity(i);
					partsList.add(bt);
					sc.setLength(i);
					partsList.add(sc);
					ram.setAmount(i);
					partsList.add(ram);
					st.setSize(i);;
					partsList.add(st);
				}
				
				ACLMessage requiredOrder = new ACLMessage(ACLMessage.REQUEST);
				

	            //there should only be one processor
	            requiredOrder.addReceiver(manufacturers.get(0));
	            requiredOrder.setLanguage(codec.getName());
	            requiredOrder.setOntology(ontology.getName());
	            requiredOrder.setConversationId("parts");
	            

	            Order order = new Order();
			     order.setPhone(ph);
			     order.setParts(partsList);
			     int i = 0;
			     for(Item parts : partsList){
	                	 
	               	  if(parts instanceof Screen){
	               		  if(screenList.get(i) == 5){
	               			  order.setCost(100);
	               		  }else{
	               			  order.setCost(150);
	               		  }
	               		  
	               	  }
	               	  if(parts instanceof Battery){
	               		  if(batteryList.get(i) == 2000){
	               			  order.setCost(70);
	               		  }else{
	               			  order.setCost(100);
	               		  }
	               	  }
	               	  if(parts instanceof Storage){
	               		 if(storageList.get(i) == 64){
	               			 order.setCost(25);
	               		 }else{
	               			 order.setCost(50);
	               		 }
	               	  }
	               	  if(parts instanceof RAM){
	               		  if(ramList.get(i) == 4){
	               			  order.setCost(30);
	               		  }else{
	               			  order.setCost(60);
	               		  }
	               	  }
	               	  i++;
               	  
			     }
			     
			     SendOrder sendOrder = new SendOrder();
			     sendOrder.setCustomer(this.myAgent.getAID());
			     sendOrder.setOrder(order);

	            Action request = new Action();
	            request.setAction(sendOrder);
	            request.setActor(manufacturers.get(0));
	            try{
	                getContentManager().fillContent(requiredOrder, request); //send the wrapper object
	                send(requiredOrder);
	            }catch (OntologyException oe) { 
	            	oe.printStackTrace(); 
	            }catch (jade.content.lang.Codec.CodecException e) {
					e.printStackTrace();
				}
	            order.resetCost();
			}else{
				dayCounter++;
			}			
		}
	}
	
	

	public class EndDay extends OneShotBehaviour{
		public EndDay (Agent a){
			super(a);
		}
		
		@Override
		public void action(){
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(tickerAgent);
			msg.setContent("done");
			myAgent.send(msg);
		}
	}
	

}
