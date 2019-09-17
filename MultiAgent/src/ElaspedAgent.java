import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;

public class ElaspedAgent extends Agent {
	
	long t0 = System.currentTimeMillis();
	
	Behaviour loop;
	protected void setup(){
		loop = new TickerBehaviour(this, 300){
			protected void onTick(){
				if(t0 >= 60000){
					System.out.println(System.currentTimeMillis()-t0 +     ": "+ myAgent.getLocalName());
				}else{
					System.out.println("You have reached 1 min");
					myAgent.doDelete();
				}
			}
		};
		addBehaviour(loop);
	}
}
