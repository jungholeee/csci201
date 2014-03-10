package engine.agent;

import java.util.PriorityQueue;

import interfaces.IGlassLineAgent;


import shared.Glass;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;

public class ConveyorAgentJT extends Agent implements IGlassLineAgent {

	//***** DATA *****
	
	private PriorityQueue<Glass> glass = new PriorityQueue<Glass>();
	private Agent prevComponent;
	private Agent nextComponent;
	private boolean powerOn;
	private boolean entrySensorOn;
	private boolean exitSensorOn;
	private boolean nextComponentReady;
	private boolean readyForGlass;
	private boolean releasing;
	
	//Variables for interaction with GUI
	private Transducer trans;
	private int myIndex;
	private int entrySensorIndex;
	private int exitSensorIndex;
		
	public ConveyorAgentJT(Transducer t, String name, int myInd, int entryInd, int exitInd) {
		super(name);
		trans = t;
		trans.register(this, TChannel.CONVEYOR);
		trans.register(this, TChannel.SENSOR);
		trans.register(this, TChannel.ALL_AGENTS);
		myIndex = myInd; 
		entrySensorIndex = entryInd;
		exitSensorIndex = exitInd;
		entrySensorOn = false;
		exitSensorOn = false;
		nextComponentReady = false;
		readyForGlass = true; 
		powerOn();
	}
	
	//***** MESSAGES *****
	
	public void msgHereIsGlass(Glass glass) {
		print("received msgHereIsGlass from PreviousComponent");
		this.glass.add(glass);
		stateChanged();
	}
	
	public void msgExitSensorOn() {
		print("received msgExitSensorOn");
		exitSensorOn = true;
		stateChanged();
	}
	
	public void msgExitSensorOff() {
		print("received msgExitSensorOff");
		exitSensorOn = false;
		releasing = false;
		stateChanged();
	}
	
	public void msgEntrySensorOn() {
		print("received msgEntrySensorOn");
		entrySensorOn = true;
		stateChanged();
	}

	public void msgEntrySensorOff() {
		print("received msgEntrySensorOff");
		entrySensorOn = false;
		readyForGlass = true;
		stateChanged();
	}
	
	public void msgNextComponentReady() {
		print("received msgNextComponenentReady from " + nextComponent);
		nextComponentReady = true;
		stateChanged();
	}
	
	//***** SCHEDULER *****
	
	public boolean pickAndExecuteAnAction() {
		if (powerOn) {		
				if (exitSensorOn && glass.size() > 0) {
					if (nextComponentReady) {
						handOffGlass();
						return true;
					}
					//If time fix disconnect between GUI and agent
					else if (!nextComponentReady && !releasing){
						//System.out.println("nextComponentReady = " + nextComponentReady);
						shutDown();
						return true;
					}
				}
				if (readyForGlass) {
					informPrevComponent();
					return true;
				}
		}			
		if (!powerOn) {
			if (nextComponentReady) {
				powerOn();
				return true;
			}
		}
			
		return false;
	}
	
	//***** ACTIONS *****
	
	private void shutDown() {
		print("SHUTTING DOWN");
		powerOn = false;
		
		//msg GUI to shut down conveyor
		Object[] args = new Object[1];
		args[0] = myIndex;
		trans.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_STOP, args);
		
		stateChanged();
	}
	
	private void powerOn() {
		print("POWERING ON");
		powerOn = true;
		
		//msg GUI to power on conveyor
		Object[] args = new Object[1];
		args[0] = myIndex;
		trans.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_START, args);
		
		stateChanged();
	}
	
	private void handOffGlass() {
		print("handing off glass to " + nextComponent);
		if (nextComponent instanceof ConveyorWorkstationJT) {
			((ConveyorWorkstationJT) nextComponent).msgHereIsGlass(glass.poll());
		}
		else if (nextComponent instanceof ShuttleAgent) {
			((ShuttleAgent) nextComponent).msgHereIsGlass(glass.poll());
		}
		nextComponentReady = false;	
		releasing = true;
		stateChanged();
	}
	
	private void informPrevComponent() {
		print("informing previous component that I'm ready to accept a glass panel");
		readyForGlass = false;
		
		if (prevComponent instanceof ShuttleAgent) {
			((ShuttleAgent) prevComponent).msgConveyorReady();
		}
		else if (prevComponent instanceof ConveyorWorkstationJT) {
			((ConveyorWorkstationJT) prevComponent).msgNextConveyorReady();
		}	
		else if (prevComponent instanceof PopupAgentDJ) {	//Added for integration (JY)
			((PopupAgentDJ)prevComponent).msgNextComponentReady();
		}
		stateChanged();
	}

	//***** GETTERS/SETTERS *****
	
	public void setPrevComponent(Agent prevComponent) {
		this.prevComponent = prevComponent;
	}
	
	public void setNextComponent(Agent nextComponent) {
		this.nextComponent = nextComponent;
	}

	public void setIndicies(int myIndex, int myEntrySensorIndex, int myExitSensorIndex) {
		this.myIndex = myIndex;
		this.entrySensorIndex = myEntrySensorIndex;
		this.exitSensorIndex = myExitSensorIndex;
	}
	
	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		if (channel == TChannel.SENSOR) {
			if ((Integer)args[0] == this.entrySensorIndex) {
				if (event == TEvent.SENSOR_GUI_PRESSED) {
					this.msgEntrySensorOn();
				}
				else if (event == TEvent.SENSOR_GUI_RELEASED) {
					this.msgEntrySensorOff();
				}	
			}
			else if ((Integer)args[0] == this.exitSensorIndex) {	
				if (event == TEvent.SENSOR_GUI_PRESSED) {
					this.msgExitSensorOn();
				}
				else if (event == TEvent.SENSOR_GUI_RELEASED) {
					this.msgExitSensorOff();
				}
			}
		}
	}
	
	/*
	*		{ {=============================================} }
	*		{ {												} }
	*		{ {			INTEGRATION MESSAGES				} }
	*		{ {												} }
	*		{ {=============================================} }
	*/
	
	// Unused.  Implemented to support IGlassLineAgent interface.
	// Integration is supported in PopupAgentDJ
	public void msgHereIsGlass(IGlassLineAgent sender, Glass g) {
		print("Received msgHereIsGlass from PreviousComponent*");
		this.glass.add(g);
		stateChanged();
	}
	public void msgGotGlass(IGlassLineAgent r, Glass g) {}
	public void msgGetGlass() {}
	public void msgWaitToSendGlass(Glass g) {}
	public int getID() { return 0; }
	public void msgConveyorReady() {}
}



