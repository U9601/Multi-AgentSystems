import java.util.ArrayList;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class TickerAgent extends Agent{
	public static final int NUM_DAYS = 5;
	@Override
	protected void setup(){
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("ticker-agent");
		sd.setName(getLocalName() + "-ticker-agent");
		dfd.addServices(sd);
		try{
			DFService.register(this, dfd);
		}catch(FIPAException e){
			e.printStackTrace();
		}
		
		doWait(5000);
		addBehaviour(new SynchAgentsBehaviour(this));
	}
	
	@Override
	protected  void takeDown(){
		try{
			DFService.deregister(this);
		}catch(FIPAException e){
			e.printStackTrace();
		}
		
	}
	
	
	public class SynchAgentsBehaviour extends Behaviour{
		private int step = 0;
		private int numFinReceived = 0;
		private int day = 0;
		private ArrayList<AID> simulationAgents = new ArrayList<>();
		
		public SynchAgentsBehaviour(Agent a){
			super(a);
		}
		
		@Override
		public void action(){
			switch (step){
			case 0:
				DFAgentDescription template1 = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("buyer");
				template1.addServices(sd);
				
				DFAgentDescription template2 = new DFAgentDescription();
				ServiceDescription sd2 = new ServiceDescription();
				sd2.setType("manufactuer");
				template2.addServices(sd2);
				
				/*DFAgentDescription template3 = new DFAgentDescription();
				ServiceDescription sd3 = new ServiceDescription();
				sd3.setType("supplier1");
				template3.addServices(sd3);
				
				/*DFAgentDescription template4 = new DFAgentDescription();
				ServiceDescription sd4 = new ServiceDescription();
				sd4.setType("suppplier2");
				template4.addServices(sd4);
				*/
				
				try{
					DFAgentDescription[] agentsType1 = DFService.search(myAgent, template1);
					for(int i = 0; i < agentsType1.length; i++){
						simulationAgents.add(agentsType1[i].getName());
					}
					
					DFAgentDescription[] agentsType2 = DFService.search(myAgent, template2);
					for(int i = 0; i < agentsType2.length; i++){
						simulationAgents.add(agentsType2[i].getName());
					}
					
					/*DFAgentDescription[] agentsType3 = DFService.search(myAgent, template3);
					for(int i = 0; i < agentsType3.length; i++){
						simulationAgents.add(agentsType3[i].getName());
					}
					
					DFAgentDescription[] agentsType4 = DFService.search(myAgent, template4);
					for(int i = 0; i < agentsType4.length; i++){
						simulationAgents.add(agentsType4[i].getName());
					}*/
				}catch(FIPAException e){
					e.printStackTrace();
				}
				
				ACLMessage tick = new ACLMessage(ACLMessage.INFORM);
				tick.setContent("new day");
				for (AID id : simulationAgents){
					tick.addReceiver(id);
				}
				myAgent.send(tick);
				step++;
				day++;
				break;
			
			case 1:
				MessageTemplate mt = MessageTemplate.MatchContent("done");
				ACLMessage msg = myAgent.receive(mt);
				if (msg != null){
					numFinReceived++;
					if(numFinReceived >= simulationAgents.size()){
						step++;
					}
				}else{
					block();
				}
			}
		}
		
		@Override
		public boolean done(){
			return step == 2;
		}
		
		@Override
		public void reset(){
			super.reset();
			step = 0;
			simulationAgents.clear();
			numFinReceived = 0;
		}
		
		@Override
		public int onEnd(){
			System.out.println("End of day " +day);
			if (day == NUM_DAYS){
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				msg.setContent("terminate");
				for (AID agent: simulationAgents){
					msg.addReceiver(agent);
				}
				myAgent.send(msg);
				myAgent.doDelete();
			}else{
				reset();
				myAgent.addBehaviour(this);
			}
			
			return 0;
		}
		
			
	}

}
