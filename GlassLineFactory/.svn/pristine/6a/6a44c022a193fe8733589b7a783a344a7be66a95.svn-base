package JunitMockAgents;

import interfaces.Shuttle;
import shared.Glass;

public class MockShuttle extends MockAgent implements Shuttle{
	public EventLog log = new EventLog();
	
	public MockShuttle(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void msgHereIsGlass(Glass popupGlass) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Recieved message msgHereIsGlass from PopupAgent"));

	}

	@Override
	public void msgIHaveGlassPiece() {
		// TODO Auto-generated method stub
		
	}

}
