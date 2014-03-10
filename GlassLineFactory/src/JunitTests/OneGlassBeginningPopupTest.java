/*package JunitTests;

import junit.framework.TestCase;
import JunitMockAgents.MockNCCutConveyorAgent;
import JunitMockAgents.MockShuttle;
import JunitMockAgents.MockStockPileRobotAgent;
import StartOfFactory.Glass;
import StartOfFactory.NCPopupAgent;
import StartOfFactory.NCPopupAgent.ConveyorStatePopup;
import StartOfFactory.NCPopupAgent.PopupEvent;
import StartOfFactory.NCPopupAgent.PopupState;
import StartOfFactory.NCPopupAgent.RobotState;
import Transducer.TChannel;
import Transducer.TEvent;
import Transducer.Transducer;

public class OneGlassBeginningPopupTest extends TestCase {
	//The Popup to test
	public NCPopupAgent popupAgent; 
	public Glass glass = new Glass("TestGlass");
	public Transducer trans = new Transducer();
/**
 * @author Cameron McClees
 * testing that the popup receives a glass from the robot and passes it to the conveyor properly
 
	public void testBeginningPopupOneGlass() {
		
		popupAgent = new NCPopupAgent(trans, "PopupAgent");
		MockStockPileRobotAgent stockPileRobot = new MockStockPileRobotAgent("MockStockPileRobot");
		MockNCCutConveyorAgent conveyor = new MockNCCutConveyorAgent("MockConveyor");
		popupAgent.setRobot(stockPileRobot);
		popupAgent.setConveyor(conveyor);

		Object[] args = new Object[2];
		args[0] = new Long(System.currentTimeMillis());
		args[1] = popupAgent;
		
		//testing message from Robot to Conveyor popup
		assertEquals("The popup state should be IS_UP", PopupState.IS_UP, popupAgent.state);
		assertEquals("The popup event should be IDLE", PopupEvent.IDLE, popupAgent.event);
		assertEquals("The RobotState is IDLE", RobotState.IDLE, popupAgent.robotState);
		assertEquals("The waitlist should be empty", true, popupAgent.getWaitList().isEmpty());
		popupAgent.msgIHaveGlassPiece();
		assertEquals("The waitList should now have one piece of glass", 1, popupAgent.getWaitList().size());
		assertEquals("The robotState should be WAITING_TO_PASS_GLASS", RobotState.WAITING_TO_PASS_GLASS, popupAgent.robotState);
		assertEquals("The popup event should be RECIEVE_GLASS", PopupEvent.RECEIVE_GLASS, popupAgent.event);
		
		popupAgent.pickAndExecuteAnAction();
		assertEquals("Mock stockpile Robot should have only 1 mesage. Event log: "+ stockPileRobot.log.toString(), 1, stockPileRobot.log.size());
		assertEquals("Mock stockpile Robot should have received msgIAmReadyForGlass. Event log: " + stockPileRobot.log.toString(), true, stockPileRobot.log.containsString("msgIAmReadyForGlass"));
		assertEquals("The robotState should be NOTIFIED", RobotState.NOTIFIED, popupAgent.robotState);
		assertEquals("The popup event should be WAITING_TO_RECEIVE_GLASS", PopupEvent.WAITING_TO_RECEIVE_GLASS, popupAgent.event);
		
		//testing the robot sending the conveyor popup a piece of glass
		popupAgent.msgHereIsGlass(glass);
		assertEquals("The popup glass is not empty", glass, popupAgent.getPopupGlass());
		assertEquals("The popup event should be PASS_GLASS", PopupEvent.PASS_GLASS, popupAgent.event);
		assertEquals("The robotState should be IDLE", RobotState.IDLE, popupAgent.robotState);
		
		popupAgent.pickAndExecuteAnAction();
		assertEquals("Mock stockpile Robot should not have recieved any messages. Event log: "+ stockPileRobot.log.toString(), 1, stockPileRobot.log.size());
		assertEquals("Mock conveyor should have recieved no messages. Event log: " + conveyor.log.toString(), 0, conveyor.log.size());

		//testing the popup sending the conveyor a glass piece after conveyor is ready for glass
		popupAgent.msgIAmReadyForGlass(); //from conveyor
		assertEquals("The popup event should be SEND_GLASS_TO_CONVEYOR", PopupEvent.SEND_GLASS_TO_CONVEYOR, popupAgent.event);

		popupAgent.pickAndExecuteAnAction();
		assertEquals("The popup state should be ANIMATED", PopupState.ANIMATED, popupAgent.state);
		trans.fireEvent(TChannel.POPUP_AGENT, TEvent.START_POPUP_AGENT_LOWERED, args);
		assertEquals("The popup state should be IS_DOWN", PopupState.IS_DOWN, popupAgent.state);

		popupAgent.pickAndExecuteAnAction();
		assertEquals("Mock conveyor should have recieved message msgHereIsGlass. Event log: " + conveyor.log.toString(), 1, conveyor.log.size());
		assertEquals("Mock conveyor should have recieved message msgHereIsGlass. Event log: " + conveyor.log.toString(), true, conveyor.log.containsString("msgHereIsGlass"));
		assertEquals("The popup event should be IDLE", PopupEvent.IDLE, popupAgent.event);

	}
/**
 * @author Cameron McClees
 * testing to see if the shuttlePopup works
 
	
	public void testEndingPopupOneGlass() {
		popupAgent = new NCPopupAgent(trans, "PopupAgent");
		MockNCCutConveyorAgent conveyor = new MockNCCutConveyorAgent("MockConveyor");
		MockShuttle shuttle = new MockShuttle("MockShuttle");
		popupAgent.setConveyor(conveyor);
		popupAgent.setShuttle(shuttle);

		Object[] args = new Object[2];
		args[0] = new Long(System.currentTimeMillis());
		args[1] = popupAgent;
		
		//testing message from Conveyor to Shuttle popup
		assertEquals("The popup state should be IS_UP", PopupState.IS_UP, popupAgent.state);
		assertEquals("The popup event should be IDLE", PopupEvent.IDLE, popupAgent.event);
		assertEquals("The waitlist should be empty", true, popupAgent.getWaitList().isEmpty());
		assertEquals("The robot shoule be empty", null, popupAgent.getMyRobot());
		assertEquals("The ConveyorState is IDLE", ConveyorStatePopup.IDLE, popupAgent.getConveyorState());
		popupAgent.msgIHaveGlassPiece();
		assertEquals("The waitList should now have one piece of glass", 1, popupAgent.getWaitList().size());
		assertEquals("The conveyorState should be WAITING_TO_PASS_GLASS", ConveyorStatePopup.WAITING_TO_PASS_GLASS, popupAgent.getConveyorState());
		assertEquals("The popup event should be RECIEVE_GLASS", PopupEvent.RECEIVE_GLASS, popupAgent.event);
		
		popupAgent.pickAndExecuteAnAction();
		assertEquals("Mock conveyor should have only 1 mesage. Event log: "+ conveyor.log.toString(), 1, conveyor.log.size());
		assertEquals("Mock conveyor should have received msgIAmReadyForGlass. Event log: " + conveyor.log.toString(), true, conveyor.log.containsString("msgIAmReadyForGlass"));
		assertEquals("The conveyorState should be NOTIFIED", ConveyorStatePopup.NOTIFIED, popupAgent.getConveyorState());
		assertEquals("The popup event should be WAITING_TO_RECEIVE_GLASS", PopupEvent.WAITING_TO_RECEIVE_GLASS, popupAgent.event);
		
		//testing the conveyor sending the shuttle popup a piece of glass
		popupAgent.msgHereIsGlass(glass);
		assertEquals("The popup glass is not empty", glass, popupAgent.getPopupGlass());
		assertEquals("The popup event should be PASS_GLASS", PopupEvent.PASS_GLASS, popupAgent.event);
		assertEquals("The conveyorState should be IDLE", ConveyorStatePopup.IDLE, popupAgent.getConveyorState());
		assertEquals("the waitList should be empty", true, popupAgent.getWaitList().isEmpty());
		
	
		popupAgent.pickAndExecuteAnAction();
		assertEquals("Mock shuttle should not have recieved any messages. Event log: "+ shuttle.log.toString(), 0, shuttle.log.size());
		assertEquals("Mock conveyor should have recieved no messages. Event log: " + conveyor.log.toString(), 1, conveyor.log.size());
		assertEquals("The popup state should be ANIMATED", PopupState.ANIMATED, popupAgent.state);
		trans.fireEvent(TChannel.POPUP_AGENT, TEvent.START_POPUP_AGENT_LOWERED, args);
		assertEquals("The popup state should be IS_DOWN", PopupState.IS_DOWN, popupAgent.state);

		
		popupAgent.pickAndExecuteAnAction();
		assertEquals("Mock shuttle should have recieved message msgHereIsGlass. Event log: " + shuttle.log.toString(), 1, shuttle.log.size());
		assertEquals("Mock shuttle should have recieved message msgHereIsGlass. Event log: " + shuttle.log.toString(), true, shuttle.log.containsString("msgHereIsGlass"));
		assertEquals("The popup event should be IDLE", PopupEvent.IDLE, popupAgent.event);
	}
	
}*/
