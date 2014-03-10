package engine.agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import interfaces.NCCutConveyor;
import interfaces.StockPileRobot;
import shared.Glass;
import shared.enums.ComponentOperations;
import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;
import transducer.Transducer;

public class StockPileRobotAgent extends Agent implements TReceiver, StockPileRobot{

	//Data:
	public List<RobotGlass> glassPile;
	public enum RobotState {IDLE, ANIMATED};
	public enum GlassState {PENDING, CONVEYOR_NOTIFIED, PASSING_TO_CONVEYOR, GIVING_TO_CONVEYOR, PASSED_TO_CONVEYOR};
	private NCCutConveyor conveyor;
	private boolean autoRunning;
	RobotState state;
	ArrayList<ComponentOperations> currentRecipe;
	
	// for gui control
	public int myIndex;
	private long count;
	// delay for automatic creation in milliseconds
	private int creationDelay = 10000;

	public class RobotGlass{

		public GlassState state; // current state of glass

		Glass glass; // reference to the glassobject
		
		public RobotGlass(Glass g){
			this.state = GlassState.PENDING;
			this.glass = g;
		}

	}
	
	class AutoCreate
	{
		List<Object> st;
		public AutoCreate(int time){
			st = Collections.synchronizedList(new ArrayList<Object>());
			st.add(0, (Integer)time);
			st.add(1, null);
		}
		
		public void stopThread(){
			synchronized(st){
				if(st.get(1) != null){
					((AutoCreateThread)st.get(1)).stopThread();
					st.set(1, null);
				}
			}
		}
		
		public void startThread(){
			synchronized(st){
				if(st.get(1) == null)
				{
					st.set(1, new AutoCreateThread((Integer)st.get(0)));
					((AutoCreateThread)st.get(1)).startThread();
				}
				else
					((AutoCreateThread)st.get(1)).interrupt();
			}
		}
		
		public void setTime(int time){
			synchronized(st){
				st.set(0, time);
				
				if(st.get(1) != null){
					this.stopThread();
					this.startThread();
				}
			}
		}
	}
	
	class AutoCreateThread extends Thread
	{
		int sleepTime;
		boolean running;
		boolean init = false;
		
		AutoCreateThread(int sTime){
			super("AutoCreateThread");
			sleepTime = sTime;
		}
		public void run() {
			running = true;
			while(running)
			{
				try{
					Glass newGlass = new Glass("");
					newGlass.setOperationsToPerform(currentRecipe);
					msgAddToStockPile(newGlass);
					Thread.sleep(sleepTime);
					System.out.println("*****************" + this.toString());
				}
				catch(Exception e){}
			}
		}
		
		private void stopThread(){
			this.interrupt();
			running = false;
		}
		
		private void startThread(){
			if(!init){
				this.start();
				init = true;
			}
		}
	}
	
	AutoCreate autoRun;

	Transducer trans; // to talk to gui

	public StockPileRobotAgent(Transducer t, String myName) {
		super(myName);
		trans = t;
		trans.register(this, TChannel.BIN);
		trans.register(this, TChannel.ALL_AGENTS);
		trans.register(this, TChannel.CONTROL_PANEL);
		glassPile = Collections.synchronizedList(new ArrayList<RobotGlass>());
		state = RobotState.IDLE;
		currentRecipe = new ArrayList<ComponentOperations>();
		count = 0;
		autoRunning = false;
		autoRun = new AutoCreate(creationDelay);
	}
	
	//Messages:

	/* (non-Javadoc)
	 * @see StartOfFactory.StockPileRobot#msgAddToStockPile(StartOfFactory.Glass)
	 */
	@Override
	public void msgAddToStockPile(Glass glass) 
	{ 
		glass.setName("Glass" + count++);
		this.Do(this.getName() + " has received message to add " + glass.toString() + " to stockpile.");
		synchronized(glassPile){
			glassPile.add(new RobotGlass(glass)); 
		}
		
		/*for(int i = 0; i < glass.getOperationsToPerform().size(); i++) {
			System.out.println("OPERATIONS GLASS PERFORMS:   " + glass.getOperationsToPerform().get(i) + "      BBC");
		}*/
		//Object[] args = new Object[1];
		//args[0] = glass.getGuiGlass();
		//trans.fireEvent(TChannel.BIN, TEvent.BIN_CREATE_PART, args);
		stateChanged();
	} //can add random generated number (0 or 1) to put in left or right bin

	/* (non-Javadoc)
	 * @see StartOfFactory.StockPileRobot#msgIAmReadyForGlass()
	 */
	@Override
	public void msgIAmReadyForGlass() { 
		this.Do(this.getName() + " has received message that conveyor is ready for glass.");
		//only change the first glass to have state CONVEYOR_NOTIFIED
		RobotGlass g = findGlassByState(GlassState.CONVEYOR_NOTIFIED);
		if(g != null){
			synchronized(glassPile){
				g.state = GlassState.PASSING_TO_CONVEYOR;
			}
			this.Do(this.getName() + " is setting state of " + g.glass.toString() + " to PASSING_TO_CONVEYOR.");
			stateChanged();
		}
	}

	 //Scheduler:
	public boolean pickAndExecuteAnAction(){
		RobotGlass glass;
		glass = findGlassByState(GlassState.PENDING);
		if(glass != null)
		{
			askConveyor(glass);
			return true;
		}
		glass = findGlassByState(GlassState.PASSING_TO_CONVEYOR);
		if(glass != null)
		{
			giveGlassToConveyor(glass);
			return true;
		}
		glass = findGlassByState(GlassState.PASSED_TO_CONVEYOR);
		if(glass != null)
		{
			removeGlass(glass);
			return true;
		}

		return false;
	}

	//Actions:
	public void askConveyor(RobotGlass glass) {
		this.Do(" sending message to " + conveyor.toString() + " that it has " + glass.glass.toString() + " to pass.");
		conveyor.msgIHaveGlass();
		synchronized(glassPile){
			glass.state = GlassState.CONVEYOR_NOTIFIED;
		}
		stateChanged();
	}

	public void giveGlassToConveyor(RobotGlass g) {
		// do gui
		// can check for left or right bin here and select appropriate bin
		state = RobotState.ANIMATED;
		// done with gui
		this.Do(this.getName() + " is passing " + g.glass.toString() + " to " + conveyor.toString() + ".");
		synchronized(glassPile)
		{
			g.state = GlassState.GIVING_TO_CONVEYOR;
		}
		//Object[] args = new Object[1];
		//args[0] = g.glass.getGuiGlass();
		//this.Do("The args that are getting passed are.... " + args2[0].toString() + " the gui glass is " + g.glass.getGuiGlass().toString());
		//this.Do("DO THEY MATCH?   " + args2[0].equals(g.glass.getGuiGlass()));
		trans.fireEvent(TChannel.BIN, TEvent.BIN_CREATE_PART, null);
		stateChanged();
	}
	
	public void removeGlass(RobotGlass glass) {
		conveyor.msgHereIsGlass(glass.glass);
		synchronized(glassPile){
			glassPile.remove(glass);
		}
		stateChanged();
	}

	//Helper Functions
	public RobotGlass findGlassByState(GlassState state){
		synchronized(glassPile)
		{
			for(RobotGlass g:glassPile){
				if(g.state == state)
					return g;
			}
		}
		return null;
	}
	
	public void setMyIndex(int index)
	{
		this.myIndex = index;
	}

	/* (non-Javadoc)
	 * @see StartOfFactory.StockPileRobot#setPopup(StartOfFactory.NCPopupAgent)
	 */
	@Override
	public void setConveyor(NCCutConveyor conveyor)
	{
		this.conveyor = conveyor;
	}
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// gui animation is finished
		/*if(event == TEvent.BIN_CREATE_PART)
		{
			this.msgAddToStockPile((AgentGlass)args[0]);
		}*/
		if(event == TEvent.BIN_PART_CREATED && state == RobotState.ANIMATED){
			RobotGlass g = findGlassByState(GlassState.GIVING_TO_CONVEYOR);
			if(g!= null)
			{
				state = RobotState.IDLE;
				synchronized(glassPile){
					g.state = GlassState.PASSED_TO_CONVEYOR;
				}
				stateChanged();
			}
		}
		
		else if(event == TEvent.START && !autoRunning){
			autoRun.startThread();
			autoRunning = true;
		}
		
		else if(event == TEvent.STOP && autoRunning){
			try{
				autoRun.stopThread();
			}
			catch(Exception e){}
			autoRunning = false;
		}
		
		else if(event == TEvent.SET_RECIPE){
			currentRecipe.clear();
			currentRecipe.addAll((ArrayList<ComponentOperations>)args[0]);
		}		
		
	}

	public void setState(RobotState state) {
		this.state = state;
	}

	public RobotState getState() {
		return state;
	}
	public String toString()
	{
		return this.getName();
	}
	
	public void setCreationDelay(int value)
	{
		if(value < 1000)
			value = 1000;
		creationDelay = value;
		autoRun.setTime(creationDelay);
	}
}
