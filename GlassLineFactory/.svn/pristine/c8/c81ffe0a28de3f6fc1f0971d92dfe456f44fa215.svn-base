package engine.agent;

import shared.Glass;
import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;
import transducer.Transducer;

import engine.agent.Agent;


public class EndPopupAgent extends Agent implements TReceiver{
	
	public boolean Raised;
	public boolean Lowered;
	public boolean TRobotReady;
	public Transducer transducer;
	public EndRobotAgent TRobot;
	public EndConveyorAgent conveyor;
	public Glass GlassOnPopup; // Can serve as boolean too, false means it’s null, set null when you pass glass
	public gui.panels.DisplayPanel guiDisplayPanel;
	public int index;
	
	// for main
	public EndPopupAgent(String name){
		super(name);
		Raised = false;
		Lowered = true;
		TRobotReady = false;
	}
	// for main
	public void setEndPopupAgent(Transducer t, EndRobotAgent tr, Glass g, EndConveyorAgent c, int ind, gui.panels.DisplayPanel gdp){
		transducer = t;
		t.register(this, TChannel.ALL_AGENTS);
		t.register(this, TChannel.POPUP);
		TRobot = tr;
		conveyor = c;
		c.msgPopupReadyForGlass();
		GlassOnPopup = g;
		this.index = ind;
		this.guiDisplayPanel = gdp;
	}
	// for JUnit test
	public EndPopupAgent(Transducer t, EndRobotAgent tr, Glass g){
		Raised = false;
		Lowered = true;
		TRobotReady = false;
		transducer = t;
		t.register(this, TChannel.ALL_AGENTS);
		t.register(this, TChannel.POPUP);
		TRobot = tr;
		GlassOnPopup = g;
	}

	@Override
	public boolean pickAndExecuteAnAction() {
		if(!Raised && GlassOnPopup!=null){
			Raise();
			return false;
		}
		if(Raised && GlassOnPopup!=null && TRobotReady){
	        TRobot.msgHereIsGlass(GlassOnPopup);
	        TRobotReady = false;
	        GlassOnPopup = null;
	        conveyor.msgPopupReadyForGlass();
	        Object[] args = new Object[1];
	        args[0] = 4;
	        transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_RELEASE_GLASS, args);
	        Lower();
	        return false;
		}
	    if(Lowered && GlassOnPopup!=null) Raise();
	    return false;
	}

	public void msgHereIsGlass(Glass g){ //From Conveyor
		GlassOnPopup = g;
		stateChanged();
		print("Received Glass from Conveyor");
	}

	public void msgTRobotReadyForGlass(){ //From TruckRobot
		TRobotReady = true;
		stateChanged();
		print("Truck Robot is ready for my glass");
	}

	public void msgFinishedRaising(){ //From Transducer
    	Raised = true;
    	stateChanged();
    	print("Finished raising the popup");
	}

	public void msgFinishedLowering(){ //From Transducer
		Lowered = true;
		conveyor.msgPopupReadyForGlass();
		stateChanged();
		print("Finished lowering the popup");
	}

	public void Raise(){
	    Lowered = false;
	    Object[] args = new Object[1];
	    args[0] = index;
	    transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_DO_MOVE_UP, args);
	    print("Raising...");
	}
	public void Lower(){
	    Raised = false;
	    Object[] args = new Object[1];
	    args[0] = index;
	    transducer.fireEvent(TChannel.POPUP, TEvent.POPUP_DO_MOVE_DOWN, args);
	    print("Lowering...");
	}
	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		if(channel==TChannel.POPUP && args[0]==(Object)index){
			if(event==TEvent.POPUP_GUI_MOVED_DOWN) this.msgFinishedLowering(); 
			if(event==TEvent.POPUP_GUI_MOVED_UP) this.msgFinishedRaising();
		}
	}
}
