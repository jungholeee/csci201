package JunitMockAgents;

import interfaces.NCCutConveyor;
import interfaces.StockPileRobot;
import shared.Glass;

public class MockStockPileRobotAgent extends MockAgent implements StockPileRobot{
	public EventLog log = new EventLog();
	
	public MockStockPileRobotAgent(String name) {
		super(name);
		// TODO Auto-generated constructor stub
		
	}

	@Override
	public void msgAddToStockPile(Glass glass) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Recieved message msgAddToStockPile from PopupAgent"));

	}

	@Override
	public void msgIAmReadyForGlass() {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Recieved message msgIAmReadyForGlass from PopupAgent"));

		
	}

	@Override
	public void setConveyor(NCCutConveyor conveyor) {
		log.add(new LoggedEvent("Set the conveyor to " + conveyor.toString()));
		
	}

}
