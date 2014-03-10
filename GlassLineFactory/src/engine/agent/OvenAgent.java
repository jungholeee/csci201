package engine.agent;

import gui.panels.DisplayPanel;

import java.util.Queue;

import shared.Glass;
import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;
import transducer.Transducer;
import engine.agent.Agent;

public class OvenAgent extends Agent implements TReceiver{

	public Transducer transducer;
	public Queue<Glass> OvenContents;
	public boolean Running;
	public gui.panels.DisplayPanel guiDisplayPanel;
	public int index;
	public EndConveyorAgent OvenConveyor1;
	public EndConveyorAgent OvenConveyor2;
	public boolean ConveyorReady = true;
	public boolean loading = false;
	public boolean actionperformed = false;
	public Glass myglass = null;
	/**
	 * @Brooke adding these booleans to prevent spamming of console and adding multiple events to transducer
	 */
	boolean waitingOnAnimation = false;
	boolean releasing = false;
	
	
	public OvenAgent(String name) {
		super(name);
	}

	public void setOvenAgent(Transducer t, EndConveyorAgent conveyor1, EndConveyorAgent conveyor2, int i, DisplayPanel dPanel) {
		// TODO Auto-generated method stub
		transducer = t;
		t.register(this, TChannel.ALL_AGENTS);
		t.register(this, TChannel.OVEN);
		OvenConveyor1 = conveyor1;
		OvenConveyor2 = conveyor2;
		this.index = i;
		guiDisplayPanel = dPanel;
		ConveyorReady = true;
		conveyor1.msgTruckReadyForGlass();
	}
	
	public void msgHereIsGlass(Glass g){
		loading = true;
		myglass = g;
		print("received glass!");
		//@Brooke the GUIOnline component does not use this event
		//Object[] args = new Object[1];
		//args[0] = g.getGuiGlass();
		//transducer.fireEvent(TChannel.OVEN, TEvent.WORKSTATION_DO_LOAD_GLASS, args);
		//print("Loading Glass!");
		//actionperformed = false;
		stateChanged();
	}
	
	/**
	 * @deprecated
	 * @Brooke use pickAndExecuteAnAction instead of msgDoneLoading and msgDoneWithAction
	 */
	public void msgDoneLoading(){
		loading = false;
		//@Brooke removing GUIGlass references
		//Object[] args = new Object[1];
		//args[0] = myglass.getGuiGlass();
		transducer.fireEvent(TChannel.OVEN, TEvent.WORKSTATION_DO_ACTION, null);
		print("Doing Action!");
		stateChanged();
	}
	
	/**
	 * @deprecated
	 * @Brooke use pickAndExecuteAnAction instead of msgDoneLoading and msgDoneWithAction
	 */
	public void msgDoneWithAction(){
		actionperformed = true;
		//Object[] args = new Object[0];
		transducer.fireEvent(TChannel.OVEN, TEvent.WORKSTATION_RELEASE_GLASS, null);
		print("Releasing Glass!");
		OvenConveyor2.msgHereIsGlass(myglass);
		myglass = null;
		OvenConveyor1.msgTruckReadyForGlass();
		stateChanged();
	}
	
	/**
	 * @Brooke adding actions to replace code in scheduler
	 */
	
	private void loadGlass(){
		print("Loading Glass!");
		actionperformed = false;
		waitingOnAnimation = true;
		stateChanged();
	}

	/**
	 * @Brooke adding actions to replace code in scheduler
	 */
	private void doGUIAction(){
		this.Do(" doing gui Action!");
		transducer.fireEvent(TChannel.OVEN, TEvent.WORKSTATION_DO_ACTION, null);
		waitingOnAnimation = true;
		stateChanged();
	}

	/**
	 * @Brooke adding actions to replace code in scheduler
	 */
	private void releaseGlass(){
		transducer.fireEvent(TChannel.OVEN, TEvent.WORKSTATION_RELEASE_GLASS, null);
		print("Releasing Glass!");
		releasing = true;
		stateChanged();
	}
	
	public void msgConveyorNotReadyForGlass(){
		ConveyorReady = false;
		stateChanged();
	}
	public void msgConveyorReadyForGlass() {
		ConveyorReady = true;
		stateChanged();
	}


	@Override
	//@Brooke use pickAndExecuteAnAction instead of msgDoneLoading and msgDoneWithAction
	public boolean pickAndExecuteAnAction() {
		if(myglass!=null && loading == true && !waitingOnAnimation){
			/**
			 * @Brooke replacing with loadGlass()
			 */
			// online components don't have this TEvent
			//Object[] args = new Object[0];
			//transducer.fireEvent(TChannel.OVEN, TEvent.WORKSTATION_DO_LOAD_GLASS, null);
			//print("Loading Glass!");
			//actionperformed = false;
			//waitingOnAnimation = true;
			//stateChanged();
			loadGlass();
			return true;
		}
		//@Brooke added a boolean for waiting on animation to not spam the console, avoid calling do action multiple times
		if(loading==false && myglass!=null && actionperformed==false && !waitingOnAnimation){
			/**
			 * @Brooke replacing with doGUIAction()
			 */
			/*Object[] args = new Object[0];
			transducer.fireEvent(TChannel.OVEN, TEvent.WORKSTATION_DO_ACTION, null);
			print("Doing Action!");
			waitingOnAnimation = true;
			stateChanged();*/
			doGUIAction();
			return true;
		}
		//@Brooke added a boolean to avoid recalling release glass & to not spam console
		if(actionperformed == true && myglass!=null && ConveyorReady && !releasing){
			/**
			 * @Brooke replacing with releaseGlass()
			 */
			/*//Object[] args = new Object[0];
			transducer.fireEvent(TChannel.OVEN, TEvent.WORKSTATION_RELEASE_GLASS, null);
			print("Releasing Glass!");
			releasing = true;
			//OvenConveyor2.msgHereIsGlass(myglass);
			//myglass = null;
			//OvenConveyor1.msgPopupReadyForGlass();
			stateChanged();*/
			releaseGlass();
			return true;
		}
		return false;
	}
	



	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		//@Brooke using all of the GUI TEvents
		//@Brooke use boolean states and pickAndExecuteAnAction to control what is happening instead of msgDoneLoading and msgDoneWithAction
		if(channel==TChannel.OVEN){
			if(event==TEvent.WORKSTATION_LOAD_FINISHED){ 
				loading = false;
				waitingOnAnimation = false;
				//this.msgDoneLoading();
				stateChanged();
			}
			if(event==TEvent.WORKSTATION_GUI_ACTION_FINISHED){
				actionperformed = true; 
				waitingOnAnimation = false;
				//this.msgDoneWithAction();
				stateChanged();
			}
			if(event==TEvent.WORKSTATION_RELEASE_FINISHED){
				OvenConveyor2.msgHereIsGlass(myglass);
				myglass = null;
				releasing = false;
				OvenConveyor1.msgTruckReadyForGlass();
				stateChanged();
			}
		}
	}

	
	
	
	

}
