package JunitMockAgents;

import interfaces.NCCutConveyor;
import shared.Glass;

public class MockNCCutConveyorAgent extends MockAgent implements NCCutConveyor{
	public MockNCCutConveyorAgent(String name) {
		super(name);
	}

	public EventLog log = new EventLog();

	@Override
	public void msgHereIsGlass(Glass g) {
		log.add(new LoggedEvent("Recieved message msgHereIsGlass from PopupAgent"));
		
	}

	@Override
	public void msgIAmReadyForGlass() {
		log.add(new LoggedEvent("Recieved message msgIAmReadyForGlass from PopupAgent"));
	}
	
	public void msgIHaveTheGlass(){
		log.add(new LoggedEvent("Received message msgIHaveGlass from cutter"));
	}

	@Override
	public void msgIHaveGlass() {
		log.add(new LoggedEvent("Recieved message msgIHaveGlass"));
		
	}

	@Override
	public void msgAtExitSensor(int index) {

		log.add(new LoggedEvent("Recieved message msgAtExitSensor"));
		
	}
	
	public void msgIhaveTheGlass(){
		log.add(new LoggedEvent("Received message that the cutter has the glass piece loaded."));
	}
}
