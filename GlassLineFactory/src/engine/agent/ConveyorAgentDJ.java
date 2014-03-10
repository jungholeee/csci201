package engine.agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import interfaces.IGlassLineAgent;

import engine.agent.Agent;

import shared.*;
import transducer.*;

public class ConveyorAgentDJ extends Agent implements IGlassLineAgent{
	
	/*
	*		{ {=============================================} }
	*		{ {												} }
	*		{ {			DATA								} }
	*		{ {												} }
	*		{ {=============================================} }
	*/
	
	String 		name;	
	public int 	conveyorID;
	
	public enum ConveyorState { MOVING, PASSING, STOPPED, WAITING };
	public enum GlassState { ACCEPT, WAIT, MOVING_ON, ON_BELT, MOVING_OFF, ON_HOLD,
		  DONE, QUEUED };
	public ConveyorState 	cState;
	boolean 				canRecieveGlass = true;
	IGlassLineAgent			previousAgent, nextAgent, shuttle;

	private class MyGlass {
		Glass 			g;
		GlassState 		state;
		IGlassLineAgent	sender;

		MyGlass(Glass gl, GlassState gs, IGlassLineAgent gla) {
			g = gl;
			state = gs;
			sender = gla;
		}
	}
	
	List<MyGlass> 	glassQueue = Collections.synchronizedList(new ArrayList<MyGlass>());

	public Sensor 	entrySensor, exitSensor;
	Transducer 		myTransducer;
	Object 			convargs[] = new Object[1];

	// Integration
	private boolean readyForShuttleGlass;

	/*
	*		{ {=============================================} }
	*		{ {												} }
	*		{ {			CONSTRUCTORS						} }
	*		{ {												} }
	*		{ {=============================================} }
	*/
	
	public ConveyorAgentDJ(String n, int id) {
		super(n);
		readyForShuttleGlass = true;
		name = n;
		cState = ConveyorState.STOPPED;
		conveyorID = id;
		convargs[0] = conveyorID;
		entrySensor = new Sensor(id*2);
		exitSensor = new Sensor(id*2+1);
	}
	
	public ConveyorAgentDJ(String n, IGlassLineAgent prev, IGlassLineAgent next, int id) {
		readyForShuttleGlass = true;
		name = n;
		previousAgent = prev;
		nextAgent = next;
		cState = ConveyorState.STOPPED;
		conveyorID = id;
		convargs[0] = conveyorID;
		entrySensor = new Sensor(id*2);
		exitSensor = new Sensor(id*2+1);
	}

	/*
	*		{ {=============================================} }
	*		{ {												} }
	*		{ {			MESSAGES							} }
	*		{ {												} }
	*		{ {=============================================} }
	*/

	/** Message received from next component when the next component is free to grab the waiting glass */
	public void msgGetGlass(){
		print("Received msgGetGlass from " + nextAgent.getName() + " and sending glass: " + glassQueue.get(0).g.getName());
		if(exitSensor.isActivated()){
			cState = ConveyorState.STOPPED;
		}
		stateChanged();
	}

	/**
	 * Message received from next component to notify this Agent that the glass was handed off
	 * successfully.
	 * @param g Glass reference
	 */
	public void msgGotGlass(IGlassLineAgent rec, Glass g){
		print("Received msgGotGlass for piece: " + g.getName() + " from " + nextAgent.getName());
		synchronized(glassQueue) {
			for(MyGlass mg: glassQueue){
				if(mg.g.equals(g)){
					mg.state = GlassState.MOVING_OFF;
				}
			}
		}
		cState = ConveyorState.PASSING;
		canRecieveGlass = true;
		stateChanged();
	}
	
	/**
	 * Message sent from previous component telling this Agent that it has a piece of Glass to pass
	 * @param sender
	 * @param g
	 */
	public void msgHereIsGlass(IGlassLineAgent sender, Glass g) {
		print("Received msgHereIsGlass for piece: " + g.toString() + " from " + sender.getName() + " state: " + cState);
		
		synchronized(glassQueue) {
			for(MyGlass mg: glassQueue) {
				if(mg.g.equals(g) && mg.state == GlassState.QUEUED) {
					mg.state = GlassState.ACCEPT;
					stateChanged();
					return;
				}
			}
		}
		
		if (!entrySensor.isActivated() || (exitSensor.isActivated() && cState == ConveyorState.MOVING)) {// || cState == ConveyorState.PASSING))
			glassQueue.add(new MyGlass(g, GlassState.ACCEPT, sender));
			stateChanged();
			return;
		}
		
		// Queue glass to tell the sender to wait to pass glass
		glassQueue.add(new MyGlass(g, GlassState.WAIT, sender));
		stateChanged();
		return;
	}

	/**
	 * Message received from next component telling this Agent to wait to send glass on
	 * @param g
	 */
	public void msgWaitToSendGlass(Glass g){
		print("Received msgWaitToSendGlass piece from " + nextAgent.getName() + " for glass: " + g.toString());
		cState = ConveyorState.WAITING;
		// Looping through the glassQueue is a more robust way to do this; if we use the 
		// glassQueue as a traditional queue, we can simply access the glassQueue(0), as
		// the Conveyor should operate in a FIFO manner
		glassQueue.get(0).state = GlassState.ON_HOLD;
	}

	/*
	*		{ {=============================================} }
	*		{ {												} }
	*		{ {			TRANSDUCER (GUI)					} }
	*		{ {												} }
	*		{ {=============================================} }
	*/

	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		if(event == TEvent.SENSOR_GUI_PRESSED && (Integer)args[0] == entrySensor.sensorID) {
			//print("Received event entry SENSOR_GUI_PRESSED");
			entrySensor.activate();
			stateChanged();
			return;
		}
		if(event == TEvent.SENSOR_GUI_PRESSED && (Integer)args[0] == (Integer)exitSensor.sensorID) {
			//print("Received exit SENSOR_GUI_PRESSED for glass: " + glassQueue.get(0).g.getName());
			canRecieveGlass = false;
			exitSensor.activate();
			cState = ConveyorState.STOPPED;
			myTransducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_STOP, convargs);
			stateChanged();
			return;
		}
		if(event == TEvent.SENSOR_GUI_RELEASED && (Integer)args[0] == exitSensor.sensorID) {
			//print("Received exit SENSOR_GUI_RELEASED for glass: " + glassQueue.get(0).g.getName());
			exitSensor.deactivate();
			synchronized(glassQueue) {
				for(MyGlass mg:glassQueue) {
					if(mg.state == GlassState.MOVING_OFF) {
						mg.state = GlassState.DONE;
					}
				}
			}
			stateChanged();
			return;
		}
		/*
		 * I think we're going to run into problems when there are pieces of glass right next to each other/possible collisions
		 * because of the way the GUI is configured.  What may fix it is changing when the sensor events are fired to when the 
		 * beginning of the plate is touched and then after that, when the center of the glass matches up to the sensor dot
		 * That way, we wouldn't have issues... I think.
		 */
		if(event == TEvent.SENSOR_GUI_RELEASED && (Integer)args[0] == entrySensor.sensorID) {
			//print("Received entry SENSOR_GUI_RELEASED");
			readyForShuttleGlass = true;
			synchronized(glassQueue) {
				for(MyGlass mg:glassQueue) {
					if(mg.state == GlassState.MOVING_ON) {
						mg.state = GlassState.ON_BELT;
						entrySensor.deactivate();
						stateChanged();
						return;
					}
				}
			}
		}
	}


	/*
	*		{ {=============================================} }
	*		{ {												} }
	*		{ {			SCHEDULER							} }
	*		{ {												} }
	*		{ {=============================================} }
	*/

	/** Scheduler */
	public boolean pickAndExecuteAnAction(){
		/*print("Glass list contains: " + glassQueue.size() + " in state: " + cState);
		synchronized(glassQueue) {
			for(MyGlass mg: glassQueue) {
				System.out.println("\t" + mg.g.getName() + " with state " + mg.state);
			}
		}*/
		
		//Only called if there is a shuttle before the conveyor
		if(shuttle != null && readyForShuttleGlass) {
			tellShuttleReady();
			return true;
		}
		
		synchronized(glassQueue) {
			for(MyGlass mg: glassQueue){
				if(mg.state == GlassState.WAIT){
					rejectGlass(mg);
					return true;
				}
			}
		}
		
		if(cState != ConveyorState.WAITING) {
			synchronized(glassQueue) {
				for(MyGlass mg:glassQueue){
					if(mg.state == GlassState.DONE){
						removeGlass(mg);
						return true;
					}
				}
			}
		
			synchronized(glassQueue) {
				for(MyGlass mg:glassQueue){
					if(mg.state == GlassState.ACCEPT){
						acceptGlass(mg);
						return true;
					}
				}
			}
	
			if(!glassQueue.isEmpty()) {
				if (cState == ConveyorState.STOPPED){
					attemptToPassGlass();
					return true;
				}
		
				if (cState == ConveyorState.PASSING){
					passGlass();
					return true;
				}
			}
		}
		
		return false;	
	}


	/*
	*		{ {=============================================} }
	*		{ {												} }
	*		{ {			ACTIONS 							} }
	*		{ {												} }
	*		{ {=============================================} }
	*/

	/** Sends the previous shuttle the message that this Agent is ready to receive Glass
	 * (msgConveyorReady)
	 */
	private void tellShuttleReady(){
		readyForShuttleGlass = false;
		shuttle.msgConveyorReady();
	}
	
	/**
	 * Sends the previous component msgWaitToSendGlass
	 * **This function needs to be changed if the entry sensor plate is a "launch pad"
	 * @param mg
	 */
	private void rejectGlass(MyGlass mg){
		print("Rejecting glass: " + mg.g.getName() + " from " + mg.sender.getName());
		if(shuttle == null) {
			mg.sender.msgWaitToSendGlass(mg.g);
		}
		mg.state = GlassState.QUEUED;
		stateChanged();
	}

	/**
	 * Sends the previous component msgGotGlass
	 * and starts the conveyor, pulling the GUIGlass onto the conveyor
	 * @param mg
	 */
	private void acceptGlass(MyGlass mg){
		print("Accepting glass: " + mg.g.getName() + " from " + mg.sender.getName());
		
		if(!exitSensor.isActivated()) {
			myTransducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_START, convargs);
			cState = ConveyorState.MOVING;
		}
		
		mg.state = GlassState.MOVING_ON;
		
		if(shuttle == null) {
			mg.sender.msgGotGlass(this, mg.g);
		}
		
		stateChanged();
	}

	/**
	 * Sends the next component msgHereIsGlass for the first glass in the queue, which should be
	 * the glass that is tripping the exit sensor
	 */
	private void attemptToPassGlass(){
		if(exitSensor.isActivated()) {
			//print("Attempting to pass " + glassQueue.get(0).g.toString() + " to " + nextAgent.getName());
			cState = ConveyorState.WAITING;
			nextAgent.msgHereIsGlass(this, glassQueue.get(0).g);			
			stateChanged();
		}
	}

	/**
	 * Starts the GUIConveyor to pass the glass on to the next component.  At the same time, if there
	 * is a piece of Glass that was waiting to be accepted onto the conveyor, send that Glass's sender
	 * msgGetGlass
	 */
	private void passGlass(){
		print("Starting Conveyor to pass " + glassQueue.get(0).g.toString());
		myTransducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_START, convargs);
		glassQueue.get(0).state = GlassState.MOVING_OFF;
		
		// Pull from previous component if there was a piece waiting
		synchronized(glassQueue) {
			for(MyGlass mg: glassQueue){
				if(mg.state == GlassState.QUEUED){
					if(shuttle == null) {
						mg.sender.msgGetGlass();
					}
					//System.out.println(this.getName() + ": Moving " + mg.g.toString() + " on to Conveyor");
					//myTransducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_START, convargs);
				}
			}
		}
		cState = ConveyorState.MOVING;
		stateChanged();
	}

	/** Removes glass from the glassQueue */
	private void removeGlass(MyGlass mg){
		//print("Removing glass " + mg.g.getName());
		glassQueue.remove(mg);
	}

	/*
	 * 	{ {=============================================} }
	 * 	{ {												} }
	 * 	{ {			EXTRA								} }
	 * 	{ {												} }
	 * 	{ {=============================================} }
	 */
	
	/** Returns this Agent's name */
	public String getName() {
		return this.name;
	}
	
	/** Returns this Agent's ID (for GUI Connection purposes) */
	public int getID() {
		return this.conveyorID;
	}
	
	/** 
	 * Sets the ShuttleAgent from which the Conveyor will receive glass (Integrated)
	 * @param a
	 */
	public void setShuttleAgent(ShuttleAgent a){
		shuttle = a;
	}
	
	/**
	 * Sets the Agent from which the Conveyor will receive Glass (previous in line)
	 * @param ga
	 */
	public void setPreviousAgent(IGlassLineAgent ga) {
		previousAgent = ga;
	}
	
	/**
	 * Sets the Agent that Conveyor will pass Glass to. (Next in line)
	 * @param ga
	 */
	public void setNextAgent(IGlassLineAgent ga) {
		nextAgent = ga;
	}
	
	/** Returns the state of the Conveyor in String form */
	public String getState() {
		return this.cState.toString();
	}
	
	/** Test function returning the number of pieces of Glass on/associated with the Conveyor */
	public int getNumGlass() {
		return glassQueue.size();
	}
	
	/**
	 * Test function returning the state of a given piece of Glass in the Conveyor's ArrayList
	 * @param g
	 * @return
	 */
	public String getGlassQueueState(Glass g) {
		synchronized(glassQueue) {
			for(MyGlass mg: glassQueue) {
				if(mg.g.equals(g)) {
					return mg.state.toString();
				}
			}
		}
		return "ERROR";
	}
	
	/**
	 * Test function returning the name of the sender of the given piece of Glass
	 * @param g
	 * @return
	 */
	public String getGlassQueueSender(Glass g) {
		synchronized(glassQueue) {
			for(MyGlass mg: glassQueue) {
				if(mg.g.equals(g)) {
					return mg.sender.getName();
				}
			}
		}
		return "ERROR";
	}
	
	/**
	 * Function designed to add glass to the line skipping the handoff (for v0)
	 * @param g
	 */
	public void msgAddGlass(Glass g) {
		glassQueue.add(new MyGlass(g, GlassState.ON_BELT, null));
		stateChanged();
	}
	
	/**
	 * Function designed to add glass to the line with a given state (hack)
	 * @param g
	 * @param sta
	 * @param sen
	 */
	public void addGlass(Glass g, GlassState sta, IGlassLineAgent sen) {
		glassQueue.add(new MyGlass(g, sta, sen));
	}
	
	/**
	 * Function designed to add glass to the line with a given state (hack)
	 * @param g
	 * @param sta
	 * @param sen
	 */
	public void addGlass(Glass g, GlassState sta, Agent sen) {
		glassQueue.add(new MyGlass(g, sta, (IGlassLineAgent)sen));
	}
	
	/**
	 * Sets transducer and registers the conveyor to the appropriate channel
	 * @param t Transducer passed in
	 */
	public void setTransducer(Transducer t) {
		this.myTransducer = t;
		myTransducer.register(this, TChannel.CONVEYOR);
		myTransducer.register(this, TChannel.SENSOR);
		myTransducer.register(this, TChannel.BIN);	//hacks
	}
	
	/*
	*		{ {=============================================} }
	*		{ {												} }
	*		{ {			INTEGRATION MESSAGES				} }
	*		{ {												} }
	*		{ {=============================================} }
	*/
	
	// Unused.  Implemented to support integration with ShuttleAgent
	public void msgConveyorReady() {}

}