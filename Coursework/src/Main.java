import jade.core.*;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
public class Main {
	
	public static void main(String[] args) {
		Profile myProfile = new ProfileImpl();
		Runtime myRuntime = Runtime.instance();
		try{
			ContainerController myContainer = myRuntime.createMainContainer(myProfile);	
			AgentController rma = myContainer.createNewAgent("rma", "jade.tools.rma.rma", null);
			rma.start();
			
			AgentController manufacturerAgent = myContainer.createNewAgent("manufactuerer", ManufacturerAgent.class.getCanonicalName(), null);
			manufacturerAgent.start();
			
			AgentController sellerAgent1 = myContainer.createNewAgent("supplier1", SupplierAgent1.class.getCanonicalName(), null);
			sellerAgent1.start();
			
			AgentController sellerAgent2 = myContainer.createNewAgent("supplier2", SupplierAgent2.class.getCanonicalName(), null);
			sellerAgent2.start();
			
			AgentController tickerAgent = myContainer.createNewAgent("ticker", TickerAgent.class.getCanonicalName(), null);
			tickerAgent.start();
			
			int numBuyers = 1;
			AgentController customer;
			for(int i=0; i<=numBuyers; i++) {
				customer = myContainer.createNewAgent("customer"+i, CustomerAgent.class.getCanonicalName(), null);
				customer.start();
			}
			
		}
		catch(Exception e){
			System.out.println("Exception starting agent: " + e.toString());
		}
	}
}
