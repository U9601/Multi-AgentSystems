import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

public class TimerAgent extends Agent {
	int w = 15;
	public void setup(){
		addBehaviour(new TickerBehaviour(this, 1000){
			
			protected void onTick() {
				if(w > 0){
					System.out.println(w + " seconds left.");
					w--;
				}else{
					System.out.println("Bye bye");
					myAgent.doDelete();
				}
			}
		});
	}

}
