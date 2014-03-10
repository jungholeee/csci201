/*package JunitTests;

import junit.framework.TestCase;
import JunitMockAgents.MockNCPopupAgent;
import StartOfFactory.Glass;
import StartOfFactory.StockPileRobotAgent;
import StartOfFactory.StockPileRobotAgent.GlassState;
import StartOfFactory.StockPileRobotAgent.RobotState;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;

public class OneGlassStockPileRobotTest extends TestCase {
	//** The StockPileRobot to test 
	public StockPileRobotAgent stockPileRobot;
	public Glass glass = new Glass("TestGlass");
	public Transducer trans = new Transducer();

		//
	 // @author cmcclees
	 // Unit Test for the stockpile robot with one glass 
	 //
	public void testStockPileOneGlass() {
		this.stockPileRobot = new StockPileRobotAgent(trans, "StockPileRobot");
		MockNCPopupAgent popup = new MockNCPopupAgent("Popup");
		stockPileRobot.setPopup(popup);
		Object[] args = new Object[1];

		assertEquals("The stockpile should be empty", true,stockPileRobot.glassPile.isEmpty());
		assertEquals("The RobotState is IDLE", RobotState.IDLE, stockPileRobot.getState());
		
		stockPileRobot.msgAddToStockPile(glass);
		assertEquals("The stockpile should now have 1 piece of glass: ", 1,stockPileRobot.glassPile.size());
		assertEquals("The RobotGlass in the stockpile should have state PENDING", GlassState.PENDING, stockPileRobot.glassPile.get(0).state);
		assertEquals("Mock popup should have an empty event log before the stockPileRobot's scheduler is called. Mock popup's event log reads: "+ popup.log.toString(), 0, popup.log.size());

		stockPileRobot.pickAndExecuteAnAction();
		assertEquals("The RobotGlass in the stockpile should have state POPUP_NOTIFIED", GlassState.POPUP_NOTIFIED, stockPileRobot.glassPile.get(0).state);
		assertEquals("Mock popup should have only 1 mesage. Event log: "+ popup.log.toString(), 1, popup.log.size());
		assertEquals("Mock popup should have received msgIHaveGlassPiece . Event log: " + popup.log.toString(), true, popup.log.containsString("msgIHaveGlassPiece"));

		//simulates Popup sending message to get glass from Stockpile
		stockPileRobot.msgIAmReadyForGlass();
		assertEquals("The stockpile should still have 1 piece of glass: ", 1,stockPileRobot.glassPile.size());
		assertEquals("The RobotGlass in the stockpile should have state PASSING_TO_POPUP", GlassState.PASSING_TO_POPUP, stockPileRobot.glassPile.get(0).state);
		assertEquals("Mock popup should have 1 event in log before the stockPileRobot's scheduler is called. Mock popup's event log reads: "+ popup.log.toString(), 1, popup.log.size());

		stockPileRobot.pickAndExecuteAnAction();
		assertEquals("The RobotGlass in the stockpile should have state GIVING_TO_POPUP", GlassState.GIVING_TO_POPUP, stockPileRobot.glassPile.get(0).state);
		assertEquals("The RobotState is ANIMATED", RobotState.ANIMATED, stockPileRobot.getState());
		
		args[0] = new Long(System.currentTimeMillis());
		trans.fireEvent(TChannel.STOCKPILE_ROBOT_AGENT, TEvent.GLASS_RECEIVED_FROM_BIN, args);
		assertEquals("The RobotGlass in the stockpile should have state PASSED_TO_POPUP", GlassState.PASSED_TO_POPUP, stockPileRobot.glassPile.get(0).state);
		
		stockPileRobot.pickAndExecuteAnAction();
		assertEquals("Mock popup should have 2 message. Event log: "+ popup.log.toString(), 2, popup.log.size());
		assertEquals("Mock popup should have received msgHereIsGlass. Event log: " + popup.log.toString(), true, popup.log.containsString("msgHereIsGlass"));

	}



}*/
