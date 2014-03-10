package engine.agent;

import interfaces.IGlassLineAgent;
import shared.Glass;
import transducer.*;


public class ShuttleAgent extends Agent implements IGlassLineAgent {

	//***** DATA *****
	
	private Glass glass;
	private Agent prevComponent;
	private Agent nextComponent;
	public enum ShuttleState {READY, SHUTTLE_GLASS, DONE_SHUTTLING, DO_NOTHING}
	private ShuttleState shuttleState;
	//Transducer trans;
	
	public ShuttleAgent(String name) {
		super(name);
		shuttleState = ShuttleState.DO_NOTHING;
	}
	
	//***** MESSAGES *****

	public void msgConveyorReady() {
		print("received msgConveyorReady from " + nextComponent);
		shuttleState = ShuttleState.READY;
		stateChanged();
	}
	
	public void msgHereIsGlass(Glass glass) {
		print("received msgHereIsGlass from " + prevComponent + " GLASS REFERENCE: " + glass);
		this.glass = glass;
		shuttleState = ShuttleState.SHUTTLE_GLASS;
		stateChanged();
	}
	
	public void msgDoneShuttling() {
		print("received msgDoneShuttling");
		shuttleState = ShuttleState.DONE_SHUTTLING;
		stateChanged();
	}
	
	//***** SCHEDULER *****
	
	public boolean pickAndExecuteAnAction() {
		if (shuttleState == ShuttleState.READY) {
			prevComponentImReady();
			return true;
		}
		if (shuttleState == ShuttleState.SHUTTLE_GLASS) {
			moveGlass();
			return true;
		}
		if (shuttleState == ShuttleState.DONE_SHUTTLING) {
			giveGlassToConveyor();
			return true;
		}
		return false;
	}

	//***** ACTIONS *****
	
	private void prevComponentImReady() {
		print("informing " + prevComponent + " that I'm ready to shuttle a glass panel");
		shuttleState = ShuttleState.DO_NOTHING;
		if (prevComponent instanceof ConveyorAgentJT) {
			((ConveyorAgentJT) prevComponent).msgNextComponentReady();
		}	
		else if (prevComponent instanceof NCCutConveyorAgent) {
			((NCCutConveyorAgent) prevComponent).msgShuttleReady();
		}
		stateChanged();
	}
	
	private void moveGlass() {
		print("SHUTTLING GLASS");
		shuttleState = ShuttleState.DO_NOTHING;
		
		//Hack because there is not agent interaction with shuttle GUI
		this.msgDoneShuttling();
		stateChanged();
	}
	
	private void giveGlassToConveyor() {
		print("giving glass to " + nextComponent);
		shuttleState = ShuttleState.DO_NOTHING;
		if (nextComponent instanceof ConveyorAgentJT)
			((ConveyorAgentJT) nextComponent).msgHereIsGlass(glass);
		else if (nextComponent instanceof ConveyorAgentDJ) 
			((ConveyorAgentDJ) nextComponent).msgHereIsGlass(this, glass);
		else if (nextComponent instanceof EndConveyorAgent)
			((EndConveyorAgent) nextComponent).msgHereIsGlass(glass);
		//send message msgImFree() to the previous conveyor that shuttle is done shuttle
		
		glass = null;
		stateChanged();
	}
	
	// ***** GETTERS/SETTERS *****
	
	public void setPrevComponent(Agent prevComponent) {
		this.prevComponent = prevComponent;
	}
	
	public void setNextComponent(Agent nextComponent) {
		this.nextComponent = nextComponent;
	}

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// TODO Auto-generated method stub
		
	}
	

	/*
	*		{ {=============================================} }
	*		{ {												} }
	*		{ {			INTEGRATION MESSAGES				} }
	*		{ {												} }
	*		{ {=============================================} }
	*/
	
	// Unused.  Implemented to support IGlassLineAgent interface.
	// Integration is supported in ConveyorAgentDJ
	public void msgHereIsGlass(IGlassLineAgent sender, Glass g) {}
	public void msgGotGlass(IGlassLineAgent r, Glass g) {}
	public void msgGetGlass() {}
	public void msgWaitToSendGlass(Glass g) {}
	public int getID() { return 0; }
}