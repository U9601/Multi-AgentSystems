import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;

import java.util.ArrayList;
import java.util.HashMap;

import jade.content.Concept;
import jade.content.lang.*;
import jade.core.*;
import jade.core.behaviours.*;
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
import set10111.coursework_ontology.elements.Owns;
import set10111.coursework_ontology.elements.Phone;
import set10111.coursework_ontology.elements.RAM;
import set10111.coursework_ontology.elements.Screen;
import set10111.coursework_ontology.elements.SendOrder;
import set10111.coursework_ontology.elements.Storage;

public class CustomerAgent extends Agent {
	private Codec codec = new SLCodec();
	private Ontology ontology = EcommerceOntology.getInstance();
	private AID sellerAID;
	private ArrayList<AID> manufactuers = new ArrayList<>();
	private ArrayList<Phone> phonesToBuy = new ArrayList<>();
	private AID tickerAgent;
	
	
	
	Phone phone = new Phone();
	Screen screen = new Screen();
	Battery battery = new Battery();
	RAM ram = new RAM();
	Storage storage = new Storage();
	Item item = new Item();
	ArrayList<Item> partsList = new ArrayList<>();	
	
	protected void setup(){
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("buyer");
		sd.setName(getLocalName() + "-buyer-agent");
		dfd.addServices(sd);
		try{
			DFService.register(this, dfd);
		}catch(FIPAException e){
			e.printStackTrace();
		}
			
			
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
					
					dailyActivity.addSubBehaviour(new FindSellers(myAgent));
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
	}
	
	public class FindSellers extends OneShotBehaviour{
		
		public FindSellers(Agent a){
			super(a);
		}
		
		@Override
		public void action(){
			DFAgentDescription sellerTemplate = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("manufactuer");
			sellerTemplate.addServices(sd);
			try{
				manufactuers.clear();
				DFAgentDescription[] agentsType1 = DFService.search(myAgent, sellerTemplate);
				for (int i = 0; i < agentsType1.length; i++){
					manufactuers.add(agentsType1[i].getName());
				}
			}catch(FIPAException e){
				e.printStackTrace();
			}
		}
	}
	
	public class SendOrders extends OneShotBehaviour{
		
		public SendOrders(Agent a){
			super(a);
		}
		
		@Override
		public void action(){
			generateOrders();

		     ACLMessage requiredOrder = new ACLMessage(ACLMessage.REQUEST);

		     //there should only be one processor
		     requiredOrder.addReceiver(manufactuers.get(0));
		     requiredOrder.setLanguage(codec.getName());
		     requiredOrder.setOntology(ontology.getName());
		     requiredOrder.setConversationId("test");

		     Order order = new Order();
		     order.setPhone(phone);
		     order.setParts(partsList);
		     order.setCost(0);
		     
		     SendOrder sendOrder = new SendOrder();
		     sendOrder.setCustomer(this.myAgent.getAID());
		     sendOrder.setOrder(order);
		     
		     Action request = new Action();
		     request.setAction(sendOrder);
		     request.setActor(manufactuers.get(0));
		     try {
		         getContentManager().fillContent(requiredOrder, request); //send the wrapper object
		         send(requiredOrder);
		         System.out.println(requiredOrder.toString());
		     } catch (Codec.CodecException ce) {
		         ce.printStackTrace();
		     } catch (OntologyException oe) {
		         oe.printStackTrace();
		     }
		     
		     /*for(Item i : sendOrder.getOrder().getParts()){
		    	 if(i instanceof Storage){
           		  System.out.println("Manufacturor has received Parts: "+((Storage)i).getSize());
           	  }
		     }*/

		 }


	}
	
	public void generateOrders(){
		
    	
		if(Math.random() < 0.5){
			screen.setLength(5);
			partsList.add(screen);
			battery.setCapacity(2000);
			partsList.add(battery);
		}else{
			screen.setLength(7);
			partsList.add(screen);
			battery.setCapacity(3000);
			partsList.add(battery);
		}

		if(Math.random() < 0.5){
			ram.setAmount(4);
			partsList.add(ram);
		}else{
			ram.setAmount(8);
			partsList.add(ram);
		}

		if(Math.random() < 0.5){
			storage.setSize(64);
			partsList.add(storage);
		}else{
			storage.setSize(256);
			partsList.add(storage);
		}
		storage.setSize(20);
		partsList.add(storage);
		
		double price = (Math.floor(100 + 500 * Math.random()));
		
		phone.setQuantity(Math.floor(1+50* Math.random()));
		phone.setSerialNumber(1);
		phone.setUnitPrice(price);
		phone.setNumDaysDue(Math.floor(1 + 10*Math.random()));
		phone.setPerDayPenalty(phone.getQuantity() * Math.floor(1 + 50 * Math.random()));
		phonesToBuy.add(phone);
		
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

			/*ACLMessage sellerDone = new ACLMessage(ACLMessage.INFORM);
			sellerDone.setContent("done");
			for(AID seller : manufactuers){
				sellerDone.addReceiver(seller);
			}
			myAgent.send(sellerDone);
			*/
		}
	}
	
}
