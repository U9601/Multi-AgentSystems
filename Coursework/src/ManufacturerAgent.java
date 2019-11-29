import java.util.ArrayList;
import java.util.HashMap;

import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.*;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.ACLCodec.CodecException;
import set10111.coursework_ontology.EcommerceOntology;
import set10111.coursework_ontology.elements.Battery;
import set10111.coursework_ontology.elements.Item;
import set10111.coursework_ontology.elements.Order;
import set10111.coursework_ontology.elements.Phone;
import set10111.coursework_ontology.elements.RAM;
import set10111.coursework_ontology.elements.Screen;
import set10111.coursework_ontology.elements.SendOrder;
import set10111.coursework_ontology.elements.Storage;


public class ManufacturerAgent extends Agent {
	private Codec codec = new SLCodec();
	private Ontology ontology = EcommerceOntology.getInstance();
	//stock list, with serial number as the key
	private ArrayList<Phone> phonesToMake = new ArrayList<>();
	private ArrayList<AID> customers = new ArrayList<>();
	private ArrayList<AID> suppliers = new ArrayList<>();
	private ArrayList<Item> partsList = new ArrayList<>();
	private ArrayList<Integer> ramList = new ArrayList<>();
	private ArrayList<Integer> screenList = new ArrayList<>();
	private ArrayList<Integer> storageList = new ArrayList<>();
	private ArrayList<Integer> batteryList = new ArrayList<>();
	private HashMap<String, Integer> warehouse = new HashMap<String, Integer>();
	private AID tickerAgent;
	private int customerCounter = 0;
	private boolean storage64 = false;
	private boolean storage256 = false;
	private boolean ram4 = false;
	private boolean ram8 = false;
	private boolean battery2000 = false;
	private boolean battery3000 = false;
	private boolean screen5 = false;
	private boolean screen7 = false;
	Phone ph = new Phone();
    Screen sc = new Screen();
	Battery bt = new Battery();
    RAM ram = new RAM();
    Storage st = new Storage();

	
	
	
	protected void setup(){
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("manufactuer");
		sd.setName(getLocalName() + "-manufactuer-agent");
		dfd.addServices(sd);
		try{
			DFService.register(this, dfd);
		}catch(FIPAException e){
			e.printStackTrace();
		}
		warehouse.put("Battery2000", 0);
		warehouse.put("Battery3000", 0);
		warehouse.put("RAM4", 0);
		warehouse.put("RAM8", 0);
		warehouse.put("Screen5", 0);
		warehouse.put("Screen7", 0);
		warehouse.put("Storage64", 0);
		warehouse.put("Storage256", 0);
		addBehaviour(new TickerWaiter(this));
	
	
	}
	
	
	@Override
	protected void takeDown(){
		try{
			DFService.deregister(this);
		}catch(FIPAException e){
			e.printStackTrace();
		}
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
					dailyActivity.addSubBehaviour(new FindSuppliers(myAgent));
					dailyActivity.addSubBehaviour(new RecieveParts(myAgent));
					dailyActivity.addSubBehaviour(new FindCustomer(myAgent));
					dailyActivity.addSubBehaviour(new RecieveOrders(myAgent));
					//CyclicBehaviour os = new OfferServer(myAgent);
					//myAgent.addBehaviour(os);
					//ArrayList<Behaviours> cyclicBehaviours = new ArrayList<>();
					//cyclicBehaviours.add(os);
					dailyActivity.addSubBehaviour(new SendOrders(myAgent));
					dailyActivity.addSubBehaviour(new EndDay(myAgent));
					myAgent.addBehaviour(dailyActivity);
				}else{
					myAgent.doDelete();
				}
			}else{
				block();
			}
		}
		
		public class RecieveParts extends OneShotBehaviour{
			public RecieveParts (Agent a){
				super(a);
			}
		
		
			@Override
			public void action(){
				MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST), MessageTemplate.MatchConversationId("parts"));
	            ACLMessage msg = myAgent.receive(mt);
	            
	            
	            if(msg != null) {
	                try {
		                ContentElement ce = null;
		                ce = getContentManager().extractContent(msg);
		                
		                Action available = (Action) ce;
		                
		                SendOrder sentOrders = ((SendOrder) available.getAction()); // this is the order requested
		                
		                
		                
	                }catch(OntologyException oe){
	                	oe.printStackTrace();
	                }catch (jade.content.lang.Codec.CodecException ce) {
						ce.printStackTrace();
					}
				
	            }
			}
		}
		
		
		public class FindCustomer extends OneShotBehaviour{
			public FindCustomer(Agent a){
				super(a);
			}
			
			@Override
			public void action(){
				DFAgentDescription customerTemplate = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("buyer");
				customerTemplate.addServices(sd);
				try{
					customers.clear();
					DFAgentDescription[] agentsType1 = DFService.search(myAgent, customerTemplate);
					for (int i = 0; i < agentsType1.length; i++){
						customers.add(agentsType1[i].getName());// this is the AID
					}
				}catch(FIPAException e){
					e.printStackTrace();
				}
			}
		}
		
		public class RecieveOrders extends OneShotBehaviour{
			public RecieveOrders(Agent a){
				super(a);
			}
			
			@Override
			public void action(){
				MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST), MessageTemplate.MatchConversationId("test"));
	            ACLMessage msg = myAgent.receive(mt);
	            
	            if(msg != null) {
	            	
	                try {
	                	
	                	System.out.println(msg);
	                	
	                ContentElement ce = null;
	                ce = getContentManager().extractContent(msg);
	                
	                Action available = (Action) ce;
	                
	                SendOrder sentOrders = ((SendOrder) available.getAction()); // this is the order requested
	                
	                Order order = sentOrders.getOrder();                
	            	ArrayList<Item> items = new ArrayList<>();
	            	items = order.getParts();
	                
	                System.out.println("Manufacturor has received: Serial Number ID: "+ order.getPhone().getSerialNumber() + " Quantity: "+order.getPhone().getQuantity());
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
	             
	                phonesToMake.add(ph);
	                customerCounter++;
	                } catch(OntologyException oe){
	                	oe.printStackTrace();
	                } catch (jade.content.lang.Codec.CodecException ce) {
						ce.printStackTrace();
					}
	            }
	           
			}
		}
		
		public class FindSuppliers extends OneShotBehaviour{
			public FindSuppliers (Agent a){
				super(a);
			}
			
			@Override
			public void action(){
				DFAgentDescription sellerTemplate = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("supplier1");
				
				DFAgentDescription sellerTemplate2 = new DFAgentDescription();
				ServiceDescription sd2 = new ServiceDescription();
				sd2.setType("supplier2");
				
				sellerTemplate.addServices(sd);
				sellerTemplate2.addServices(sd2);
				try{
					suppliers.clear();
					DFAgentDescription[] agentsType1 = DFService.search(myAgent, sellerTemplate);
					for(int i = 0; i<agentsType1.length; i++){
						suppliers.add(agentsType1[i].getName());// this is the AID
					}
					
					DFAgentDescription[] agentsType2 = DFService.search(myAgent, sellerTemplate2);
					for(int i = 0; i<agentsType2.length; i++){
						suppliers.add(agentsType2[i].getName());// this is the AID
					}
					
					
				}catch(FIPAException e) {
					e.printStackTrace();
				}
			
				for (int i = 1; i <= phonesToMake.size(); i++){
					if(warehouse.containsKey("Battery2000") && warehouse.containsValue("0") &&  batteryList.indexOf(i) == 2000){
						//needs battery 2000
						System.out.println(2000);
						battery2000 = true;
						bt.setCapacity(2000);
						partsList.add(bt);
						
					}else if(warehouse.containsKey("Battery3000") && warehouse.containsValue("0") && batteryList.indexOf(i) == 3000){
						//needs battery4000
						System.out.println(4000);
						battery3000 = true;
						bt.setCapacity(3000);
						partsList.add(bt);
					}
					
					if(warehouse.containsKey("RAM4") && warehouse.containsValue("0") && ramList.indexOf(i) == 4){
						//needs RAM4
						System.out.println(4);
						ram4 = true;
						ram.setAmount(4);
						partsList.add(ram);
						
					}else if (warehouse.containsKey("RAM8") && warehouse.containsValue("0") && ramList.indexOf(i) == 8){
						//needs RAM8
						System.out.println(8);
						ram8 = true;
						ram.setAmount(8);
						partsList.add(ram);
					}
					
					if(warehouse.containsKey("Screen5") && warehouse.containsValue("0") && screenList.indexOf(i) == 5){
						//needs Screen5
						System.out.println(5);
						screen5 = true;
						sc.setLength(5);;
						partsList.add(sc);
						
					}else if (warehouse.containsKey("Screen7") && warehouse.containsValue("0") && batteryList.indexOf(i) == 7){
						//needs Screen7
						System.out.println(7);
						screen7 = true;
						sc.setLength(7);;
						partsList.add(sc);
					}
					
					if(warehouse.containsKey("Storage64") && warehouse.containsValue("0") && batteryList.indexOf(i) == 64){
						//needs Storage64
						System.out.println(64);
						storage64 = true;
						st.setSize(64);
						partsList.add(st);
					}else if (warehouse.containsKey("Storage256") && warehouse.containsValue("0") && batteryList.indexOf(i) == 256){
						//needs Storage256
						System.out.println(256);
						storage256 = true;
						st.setSize(256);
						partsList.add(st);
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
						
				ACLMessage requiredOrdered = new ACLMessage(ACLMessage.REQUEST);
				

	            //there should only be one processor
	            requiredOrdered.addReceiver(suppliers.get(0));
	            requiredOrdered.setLanguage(codec.getName());
	            requiredOrdered.setOntology(ontology.getName());
	            

	            Order order = new Order();
			     order.setPhone(ph);
			     order.setParts(partsList);
			     
			     SendOrder sendOrder = new SendOrder();
			     sendOrder.setCustomer(this.myAgent.getAID());
			     sendOrder.setOrder(order);

	            Action request = new Action();
	            request.setAction(sendOrder);
	            request.setActor(suppliers.get(0));
	            try{
	                getContentManager().fillContent(requiredOrdered, request); //send the wrapper object
	                send(requiredOrdered);
	            }catch (OntologyException oe) { 
	            	oe.printStackTrace(); 
	            }catch (jade.content.lang.Codec.CodecException e) {
					e.printStackTrace();
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
}
