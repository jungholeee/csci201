/*package JunitTests;

import junit.framework.TestCase;
import JunitMockAgents.MockNCPopupAgent;
import MockAnimations.PopupSensor;
import MockAnimations.PopupSensor.SensorType;
import StartOfFactory.Glass;
import StartOfFactory.NCCutConveyorAgent;
import StartOfFactory.NCCutConveyorAgent.ConveyorState;
import StartOfFactory.NCCutConveyorAgent.GlassToPassState;
import StartOfFactory.NCCutConveyorAgent.PopupState;
import Transducer.TChannel;
import Transducer.TEvent;
import Transducer.Transducer;

public class OneGlassBeginningConveyorTest extends TestCase{
	public NCCutConveyorAgent conveyorAgent; 
	public Glass glass = new Glass("TestGlass");
	public Transducer trans = new Transducer();
	

	public void testConveyorOneGlass() {
		
		Object[] args = new Object[1];
		SensorType[] type = {SensorType.ENTRY, SensorType.PRE_POPUP};
		PopupSensor sensor = new PopupSensor(type);
		
		MockNCPopupAgent robotPopup = new MockNCPopupAgent("Robot Popup");
		MockNCPopupAgent shuttlePopup = new MockNCPopupAgent("Shuttle Popup");
		
		conveyorAgent = new NCCutConveyorAgent(trans, "ConveyorAgent");
		conveyorAgent.setPopups(robotPopup, shuttlePopup);
		
		//trans.register(conveyorAgent, TChannel.CONVEYOR_AGENT);
		
		
		assertEquals("The glassList should be empty", true,conveyorAgent.glassList.isEmpty());
		assertEquals("The glassToPass state should be PENDING", GlassToPassState.PENDING, conveyorAgent.glassToPassState);
		assertEquals("The conveyor state should be STOPPED", ConveyorState.STOPPED, conveyorAgent.state);
		conveyorAgent.msgHereIsGlass(glass);
		assertEquals("The glassList should have 1 glass in it", 1, conveyorAgent.glassList.size());
		assertEquals("The robotPopupState should be IDLE", PopupState.IDLE, conveyorAgent.getRobotPopupState());
		
		conveyorAgent.pickAndExecuteAnAction();
		args[0] = new Long(System.currentTimeMillis());
		trans.fireEvent(TChannel.CONVEYOR_AGENT, TEvent.START_CONVEYOR_AGENT_STARTED, args);
		assertEquals("The conveyor state should be MOVING", ConveyorState.MOVING, conveyorAgent.state);
		
		
		conveyorAgent.msgAtSensor(sensor, conveyorAgent.glassList.get(0));
		assertEquals("The conveyorState should be STOPPING", ConveyorState.STOPPING, conveyorAgent.state);
		assertEquals("The glass to pass should have a glass", glass, conveyorAgent.glassToPass);
		assertEquals("The glass should be on the prePopup sensor", true, conveyorAgent.glassOnPrePopupSensor);
		
		conveyorAgent.pickAndExecuteAnAction();
		args[0] = new Long(System.currentTimeMillis());
		trans.fireEvent(TChannel.CONVEYOR_AGENT, TEvent.START_CONVEYOR_AGENT_STOPPED, args);
		assertEquals("The conveyor state should be STOPPED", ConveyorState.STOPPED, conveyorAgent.state);
		
		conveyorAgent.pickAndExecuteAnAction();
		assertEquals("Mock shuttle popup should have only 1 mesage. Event log: "+ shuttlePopup.log.toString(), 1, shuttlePopup.log.size());
		assertEquals("Mock shuttle popup should have received msgIHaveGlassPiece . Event log: " + shuttlePopup.log.toString(), true, shuttlePopup.log.containsString("msgIHaveGlassPiece"));
		assertEquals("The glass to pass state should be POPUP_NOTIFIED", GlassToPassState.POPUP_NOTIFIED, conveyorAgent.glassToPassState);

		conveyorAgent.msgIAmReadyForGlass();
		assertEquals("The shuttlePopup state should be WAITING_TO_PASS_GLASS", PopupState.WAITING_TO_PASS_GLASS, conveyorAgent.getShuttlePopupState());
		assertEquals("The glass to pass state should be PASSING_TO_POPUP", GlassToPassState.PASSING_TO_POPUP, conveyorAgent.glassToPassState);
		
		conveyorAgent.pickAndExecuteAnAction();
		assertEquals("Mock shuttle popup should have 2 mesages. Event log: "+ shuttlePopup.log.toString(), 2, shuttlePopup.log.size());
		assertEquals("Mock shuttle popup should have received msgHereIsGlass . Event log: " + shuttlePopup.log.toString(), true, shuttlePopup.log.containsString("msgHereIsGlass"));
		
		
		assertEquals("The glass to pass state should be PASSED_TO_POPUP", GlassToPassState.PASSED_TO_POPUP, conveyorAgent.glassToPassState);
		assertEquals("The glass should not be on the prePopup sensor", false, conveyorAgent.glassOnPrePopupSensor);
		assertEquals("The shuttlePopup state should be IDLE", PopupState.IDLE, conveyorAgent.getShuttlePopupState());

		
		conveyorAgent.pickAndExecuteAnAction();
		args[0] = new Long(System.currentTimeMillis());
		trans.fireEvent(TChannel.CONVEYOR_AGENT, TEvent.START_CONVEYOR_AGENT_STARTED, args);
		assertEquals("The conveyor state should be MOVING", ConveyorState.MOVING, conveyorAgent.state);

	}
}*/
