package engine.agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import interfaces.NCCutConveyor;
import shared.Glass;
import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;
import transducer.Transducer;

public class NCCutConveyorAgent extends Agent implements TReceiver,
NCCutConveyor {
	// Data:
	//list of the glass on the conveyor, with states
	public List<ConveyorGlass> glassList;
	Boolean stopped = true; // dont move conveyor if nothing on it

	// for gui, indexes correspond to which sensor is being triggered
	public int myIndex;
	public int myExitSensorIndex;
	public int myEntrySensorIndex;
	public int myShuttleExitSensorIndex;
	public gui.panels.DisplayPanel guiDisplayPanel;

	public enum ConveyorState {
		STARTING, MOVING, STOPPING, STOPPED
	};

	public ConveyorState state;


	//public enum PopupState {IDLE, WAITING_TO_PASS_GLASS, NOTIFIED, PASSING_GLASS};
	//private PopupState robotPopupState;
	//private PopupState shuttlePopupState;


	public enum AgentState {IDLE, WAITING_TO_PASS_GLASS, NOTIFIED, PASSING_GLASS};
	AgentState entryAgentState;
	AgentState exitAgentState;

	//the state of glass on the conveyor
	public enum GlassToPassState {
		PENDING, NEXT_NOTIFIED, PASSING_TO_NEXT, MOVING_OFF_CONVEYOR, MOVED_OFF_CONVEYOR, PASSED_TO_NEXT, ON_SENSOR
	};


	//glass with state of glass
	public class ConveyorGlass{

		public GlassToPassState state; // current state of glass

		Glass glass; // reference to the glassobject

		public ConveyorGlass(Glass g){
			this.state = GlassToPassState.PENDING;
			this.glass = g;
		}

	}


	//public GlassToPassState glassToPassState = GlassToPassState.PENDING;


	//public Glass glassToPass; // the piece of glass at end of conveyor to be put on the shuttle

	//the entry Agent is the agent that is going to pass glass to the conveyor 
	public Agent entryAgent;
	//exit agent is the agent recieving glass from the conveyor
	public Agent exitAgent;
	Transducer trans; // to talk to gui

	List<Integer> waitingGlass;

	public boolean glassOnExitSensor;
	public boolean glassOnEntrySensor;
	public boolean shuttleReady;

	public NCCutConveyorAgent(Transducer t, gui.panels.DisplayPanel panel, String name) {
		super(name);
		trans = t;
		trans.register(this, TChannel.CONVEYOR);
		trans.register(this, TChannel.SENSOR);
		trans.register(this, TChannel.ALL_AGENTS);
		myExitSensorIndex = -1;
		myEntrySensorIndex = -1;
		myShuttleExitSensorIndex = -1;

		glassList = Collections.synchronizedList(new ArrayList<ConveyorGlass>());
		waitingGlass = Collections.synchronizedList(new ArrayList<Integer>());
		guiDisplayPanel = panel;
		state = ConveyorState.STOPPED;
		glassOnExitSensor = false;
		glassOnEntrySensor = false;
		shuttleReady = false;
	}

	public void setEntryAgent(Agent entryAgent)
	{
		this.entryAgent = entryAgent;
	}

	public void setExitAgent(Agent exitAgent)
	{
		this.exitAgent = exitAgent;
	}

	// Messages:
	// from popup
	/*
	 * (non-Javadoc)
	 * 
	 * @see StartOfFactory.NCCutConveyor#msgHereIsGlass(StartOfFactory.Glass)
	 */
	@Override
	public void msgHereIsGlass(Glass g) {
		this.Do(" has received glass piece " + g.toString()
				+ ".");
		synchronized(glassList)
		{
			glassList.add(new ConveyorGlass(g));
		}

		synchronized(waitingGlass)
		{
			waitingGlass.remove(0);
			if(waitingGlass.size() > 0)
				entryAgentState = AgentState.WAITING_TO_PASS_GLASS;
			else
				entryAgentState = AgentState.IDLE;
		}
		//Object[] args = new Object[2];
		//args[0] = myIndex;
		//args[1] = g.getGuiGlass();
		//trans.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_ADD_PART, args);
		//robotPopupState = PopupState.IDLE;
		stateChanged();
	}
	
	// from cutter agent
	public void msgIHaveTheGlass()
	{
		this.Do(" received message that the cutter has loaded the glass.");
		synchronized(glassList)
		{
			ConveyorGlass g = findGlassByState(GlassToPassState.MOVING_OFF_CONVEYOR);
			if(g != null)
			{
				g.state = GlassToPassState.MOVED_OFF_CONVEYOR;
				stateChanged();
			}

		}
	}

	public void msgShuttleReady()
	{
		this.Do("received message that the shuttle is ready.   BBC");
		shuttleReady = true;
		stateChanged();
	}

	// from entry agent
	public void msgIHaveGlass()
	{
		this.Do("Received message that " + entryAgent.toString() + " wants to pass glass.   BBC");
		// the entry agent wants to pass glass
		synchronized(waitingGlass)
		{
			waitingGlass.add(1);
			if(waitingGlass.size() == 1)
			{
				entryAgentState = AgentState.WAITING_TO_PASS_GLASS;
				stateChanged();
			}
		}
	}

	// from popup
	/*
	 * (non-Javadoc)
	 * 
	 * @see StartOfFactory.NCCutConveyor#msgIAmReadyForGlass()
	 */
	@Override
	public void msgIAmReadyForGlass() {
		//shuttlePopupState = PopupState.WAITING_TO_PASS_GLASS;
		this.Do(" received message that " + exitAgent.toString() + " is ready for glass.   BBC");
		exitAgentState = AgentState.WAITING_TO_PASS_GLASS;
		synchronized(glassList) {
			for(int i = 0; i < glassList.size(); i++) {
				if(glassList.get(i).state == GlassToPassState.NEXT_NOTIFIED)
					glassList.get(i).state = GlassToPassState.PASSING_TO_NEXT;
			}

		}
		stateChanged();
	}

	// from transducer??
	/*
	 * (non-Javadoc)
	 * 
	 * @see StartOfFactory.NCCutConveyor#msgAtSensor(MockAnimations.PopupSensor,
	 * StartOfFactory.Glass)
	 */
	@Override
	// this needs to be reworked, need to figure out how to know which sensor is the prepopup and
	// the eventFired goes on SENSOR channel and is called SENSOR_GUI_PRESSED
	public void msgAtExitSensor(int index) {
		// is s the prepopup sensor, then stop the conveyor
		ConveyorGlass g = null;
		synchronized(glassList)
		{
			//for(int i = 0; i < glassList.size(); i++)
			//{
			//if(glassList.get(i).getGuiGlass() == g)
			//{
			//System.out.println("The index of the GUI Glass to use to get the glass in Glass list is:  " + index + "   BBC");
			//:) this was : glassToPass = glassList.get(i);
			// :) since the glass will be removed hopefully it will only access the index at 0
			//:) the index that is being passed in is the number of total glass in the factoyr
			for(int i = 0; i < glassList.size(); i++)
			{
				if(glassList.get(i).state == GlassToPassState.PENDING)
				{
					g = glassList.get(i);
					glassList.get(i).state = GlassToPassState.ON_SENSOR;
					this.Do("THE GLASS TO PASS IS..... " + glassList.get(i).glass.getName() + "   BBC");
					break;
				}
			}
			//break;
			//}
			//}
		}

		//this should not happen
		if(g == null)
		{
			this.Do(" something went wrong...");
			return;
		}
		this.Do(" has " + g.glass.toString()
				+ " at the exit sensor with state: " + g.state + "  BBC");
		//:)glassToPassState = GlassToPassState.PENDING;
		glassOnExitSensor = true;
		//immediately stop the conveyor
		Object[] args = new Object[1];
		args[0] = myIndex;
		trans.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_STOP, args);
		state = ConveyorState.STOPPED;
		stateChanged();

	}


	// Scheduler:
	public boolean pickAndExecuteAnAction() {
		/*if(glassList.size() == 2 && glassOnExitSensor && glassOnEntrySensor)
		{
			System.out.println(glassList.get(0).toString() + ": " + glassList.get(0).state);
			System.out.println(glassList.get(1).toString() + ": " + glassList.get(1).state);
			System.out.println(exitAgentState);
			System.out.println(entryAgentState);
			System.out.println(state);
			stateChanged();
			return true;
		}*/
		ConveyorGlass glassToPass = null;

		// attempt to get more glass on conveyor
		if(!glassOnEntrySensor && entryAgentState == AgentState.WAITING_TO_PASS_GLASS)
		{
			getGlassFromEntryAgent();
			return true;
		}

		// start or stop conveyor
		if (state == ConveyorState.STOPPING) {
			stopConveyor();
			return true;
		}

		if (state == ConveyorState.STARTING) {
			startConveyor();
			return true;
		}
		// conveyor is moving but has no glass on it
		if(state == ConveyorState.MOVING)
		{
			synchronized(glassList)
			{
				if(glassList.size() == 0)
				{
					stopConveyor();
					return true;
				}
			}
		}

		// conveyor is stopped but has glass it needs to move
		if(state == ConveyorState.STOPPED && !glassOnExitSensor)
		{
			synchronized(glassList)
			{
				if(glassList.size() > 0)
				{
					startConveyor();
					return true;
				}
			}
		}

		// get rid of the glass on sensor first
		//added helper function to search through the glass to see which one has state Passed_to_next
		glassToPass = findGlassByState(GlassToPassState.PASSED_TO_NEXT);
		if(glassToPass != null)
		{
			//System.out.println("****THE GLASS IS : " + glassToPass.glass.getName() + "  BBC");
			removeGlass(glassToPass.glass);
			return true;
		}
		
		glassToPass = findGlassByState(GlassToPassState.ON_SENSOR);
		if(glassToPass != null) {
			if (state == ConveyorState.STOPPED) {

				askExitAgent(glassToPass);
				return true;
			}
		}
		
		// set glass to pass state for shuttle
		glassToPass = findGlassByState(GlassToPassState.NEXT_NOTIFIED);
		if(shuttleReady && glassToPass != null)
		{
			synchronized(glassList){
				glassToPass.state = GlassToPassState.PASSING_TO_NEXT;
			}
			stateChanged();
			return true;
		}
		
		// restart conveyor to move glass off
		glassToPass = findGlassByState(GlassToPassState.PASSING_TO_NEXT);
		if(glassToPass != null && exitAgentState == AgentState.WAITING_TO_PASS_GLASS) {	
			if (state == ConveyorState.STOPPED) {
				startConveyor();//state = ConveyorState.STARTING;
				synchronized(glassList)
				{
					glassToPass.state = GlassToPassState.MOVING_OFF_CONVEYOR;
					stateChanged();
				}
				return true;
			}
		}
		
		if(glassToPass != null && shuttleReady)
		{
			if(state == ConveyorState.STOPPED)
			{
				startConveyor();
				synchronized(glassList)
				{
					glassToPass.state = GlassToPassState.MOVING_OFF_CONVEYOR;
					stateChanged();
				}
				stateChanged();
				return true;
			}
		}
		
		/* pass to shuttle agent */
		glassToPass = findGlassByState(GlassToPassState.MOVING_OFF_CONVEYOR);
		if(glassToPass != null){
			if(shuttleReady && state == ConveyorState.MOVING){
				//this.Do("the state of the glass to Pass is:  " + glassToPass.state + "      BBC");
				passGlassToExitAgent(glassToPass);
				return true;
			}
		}
		
		/* pass to brooke and cameron agent */
		glassToPass = findGlassByState(GlassToPassState.MOVED_OFF_CONVEYOR);
		if(glassToPass != null)
		{
			passGlassToExitAgent(glassToPass);
			return true;
		}

		return false;
	}


	//Helper Functions
	public ConveyorGlass findGlassByState(GlassToPassState state){
		synchronized(glassList)
		{
			for(ConveyorGlass g:glassList){
				if(g.state == state)
					return g;
			}
		}
		return null;
	}


	// Actions:
	public void passGlassToExitAgent(ConveyorGlass glassToPass) {
		if(exitAgent.getClass() == ShuttleAgent.class)
		{
			ShuttleAgent temp = (ShuttleAgent) exitAgent;
			this.Do(" passing glass " + glassToPass.glass + " to shuttle  BBC");
			temp.msgHereIsGlass(glassToPass.glass);
			/*synchronized(glassList) {
				glassToPass.state = GlassToPassState.PASSING_TO_NEXT;
			}*/
			shuttleReady = false;
		}

		else if(exitAgent.getClass() == CutterAgent.class)
		{
			synchronized(glassList){
				glassToPass.state = GlassToPassState.PASSED_TO_NEXT;
			}
			CutterAgent temp = (CutterAgent)exitAgent;
			temp.msgHereIsGlass(glassToPass.glass);
		}

		this.Do(" is passing " + glassToPass.glass.toString()
				+ " to " + exitAgent.toString() + "    BBC");
		//glassOnExitSensor = false;
		//glassToPass = null;
		//removeGlass(glassToPass);
		//exitAgentState = AgentState.IDLE;
		stateChanged();
	}

	public void removeGlass(Glass g)
	{
		synchronized(glassList)
		{
			for(int i = 0; i < glassList.size(); i++)
			{
				if(glassList.get(i).glass == g)
				{
					glassList.remove(i);
					exitAgentState = AgentState.IDLE;
					//:) glassToPass = null;
					//:) glassToPassState = GlassToPassState.PENDING;
					stateChanged();
					return;
				}
			}
		}

	}

	public void getGlassFromEntryAgent()
	{
		// the conveyor can take more glass if there isnt anything on prepopup sensor
		if(!glassOnEntrySensor)
		{
			this.Do(" is ready to receive glass from " + entryAgent.toString() + "     BBC");
			if(entryAgent.getClass() == StockPileRobotAgent.class)
			{
				StockPileRobotAgent temp = (StockPileRobotAgent)entryAgent;
				temp.msgIAmReadyForGlass();
			}
			else if(entryAgent.getClass() == CutterAgent.class)
			{
				CutterAgent temp = (CutterAgent)entryAgent;
				temp.msgIAmReadyForGlass();
			}
			entryAgentState = AgentState.NOTIFIED;
			stateChanged();
		}
	}

	public void askExitAgent(ConveyorGlass glassToPass) {
		// shuttle doesn't have this message
		/*if(exitAgent.getClass() == ShuttleAgent.class)
		{
			ShuttleAgent temp = (ShuttleAgent) exitAgent;
			temp.msgIHaveGlassPiece();
		}*/
		if(exitAgent.getClass() == CutterAgent.class)
		{
			this.Do(" is notifying " + exitAgent.toString()
					+ " that it needs to pass glass.    BBC");
			CutterAgent temp = (CutterAgent)exitAgent;
			temp.msgIHaveGlassPiece();
			exitAgentState = AgentState.NOTIFIED;
		}

		glassToPass.state = GlassToPassState.NEXT_NOTIFIED;
		stateChanged();
	}

	public void stopConveyor() {

		Object[] args = new Object[1];
		args[0] = myIndex;
		this.Do(" is stopping.");
		// send event to transducer
		trans.fireEvent(TChannel.CONVEYOR,
				TEvent.CONVEYOR_DO_STOP, args);
		state = ConveyorState.STOPPED;
		stateChanged();
	}

	public void startConveyor() {
		Object[] args = new Object[1];
		args[0] = myIndex;
		this.Do(" is starting.");
		// send event to transducer
		trans.fireEvent(TChannel.CONVEYOR,
				TEvent.CONVEYOR_DO_START, args);
		state = ConveyorState.MOVING;
		stateChanged();
	}

	public Agent getEntryAgent() {
		return entryAgent;
	}

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {	
		// correct sensor
		if((Integer)args[0] == this.myExitSensorIndex)
		{
			// at an important sensor		
			if(channel == TChannel.SENSOR && event == TEvent.SENSOR_GUI_PRESSED)
			{
				//if the gui sensor is pressed sends the conveyor a message telling it that an exit sensor was triggered
				//this.msgAtExitSensor(guiDisplayPanel.getActivePieces().get((Integer)args[1]));
				//System.out.println("the value in args[1] is:  " + args[1] + "  BBC");
				this.msgAtExitSensor((Integer)args[1]);			
			}
			if(channel == TChannel.SENSOR && event == TEvent.SENSOR_GUI_RELEASED)
			{
				glassOnExitSensor = false;
				state = ConveyorState.STARTING;
				stateChanged();
			}
		}
		if((Integer)args[0] == this.myEntrySensorIndex)
		{
			// at an important sensor		
			if(channel == TChannel.SENSOR && event == TEvent.SENSOR_GUI_PRESSED)
			{
				glassOnEntrySensor = true;
				stateChanged();
			}
			if(channel == TChannel.SENSOR && event == TEvent.SENSOR_GUI_RELEASED)
			{
				glassOnEntrySensor = false;
				stateChanged();
			}
		}
		if((Integer)args[0] == this.myShuttleExitSensorIndex && event == TEvent.SENSOR_GUI_RELEASED)
		{
			synchronized(glassList)
			{
				for(ConveyorGlass g : glassList)
				{
					if(g.state == GlassToPassState.MOVING_OFF_CONVEYOR)
					{
						this.Do("**************" + g.glass.getName() + " has finished shuttling.");
						g.state = GlassToPassState.PASSED_TO_NEXT;
						stateChanged();
					}
				}
			}
		}
	}

	public void setEntryAgentState(AgentState robotState) {
		this.entryAgentState = robotState;
	}

	public AgentState getEntryAgentState() {
		return entryAgentState;
	}

	public void setMyIndex(int index)
	{
		this.myIndex = index;
	}

	public void setExitSensorIndex(int index)
	{
		myExitSensorIndex = index;
	}

	public void setEntrySensorIndex(int index)
	{
		myEntrySensorIndex = index;
	}

	public void setShuttleExitSensorIndex(int index)
	{
		myShuttleExitSensorIndex = index;
	}

	public void setExitAgentState(AgentState shuttleState) {
		this.exitAgentState = shuttleState;
	}

	public AgentState getExitAgentState() {
		return exitAgentState;
	}

	public String toString()
	{
		return this.getName();
	}
}