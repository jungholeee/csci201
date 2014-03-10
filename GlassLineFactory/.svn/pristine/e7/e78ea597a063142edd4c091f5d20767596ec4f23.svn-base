package engine.agent;

import java.util.ArrayDeque;
import java.util.Queue;

import shared.Glass;
import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;
import transducer.Transducer;

import engine.agent.Agent;

public class EndConveyorAgent extends Agent implements TReceiver{
	
	public boolean TruckOutForDelivery = false;
	public boolean Running;
	public boolean EntrySensor;
	public boolean PrePopupSensor;
	public boolean PreComponentKnowsImFull;
	public boolean PopupReady;
	public boolean TruckReady;
	public ShuttleAgent shuttle; //pre-component for conveyor13
	public OvenAgent oven; //pre-component for conveyor14
	public Transducer transducer;
	public EndTruckAgent truck;
	public EndPopupAgent myPopup;
	public Queue<Glass> ConveyorContents;
	public gui.panels.DisplayPanel guiDisplayPanel;
	public int index;
	
	// for main
	public EndConveyorAgent(String name){
		super(name);
		Running = false;
		EntrySensor = false;
		PrePopupSensor = false;
		PreComponentKnowsImFull = false;
		ConveyorContents = new ArrayDeque<Glass>();		
	}
	
	// for main v2
	public void setEndConveyorAgent (Transducer t, EndTruckAgent truck, ShuttleAgent sh, OvenAgent o, int ind, gui.panels.DisplayPanel gdp){
		this.truck = truck;
		transducer = t;
		t.register(this, TChannel.ALL_AGENTS);
		this.index = ind;
		this.guiDisplayPanel = gdp;
		this.shuttle = sh;
		this.oven = o;
		if(shuttle!=null) shuttle.msgConveyorReady();
		TruckReady = true;
	}	
	

	
	
	// for JUnit test
	public EndConveyorAgent(Transducer t, EndPopupAgent popup){
		Running = false;
		EntrySensor = false;
		PrePopupSensor = false;
		PreComponentKnowsImFull = false;
		ConveyorContents = new ArrayDeque<Glass>();
		transducer = t;
		t.register(this, TChannel.ALL_AGENTS);
		
		myPopup = popup;
	}

	@Override
	public boolean pickAndExecuteAnAction() {
		if(Running && (!TruckReady || TruckOutForDelivery) && PrePopupSensor){
			StopConveyor();
			return true;
		}
		if(!Running && ((TruckReady && !TruckOutForDelivery) || !PrePopupSensor)){
			StartConveyor();
			return true;
		}
		if(Running && TruckReady && PrePopupSensor && !TruckOutForDelivery){
			if(shuttle!=null){
				oven.msgHereIsGlass(ConveyorContents.poll());
			}
			else if(shuttle==null){
				truck.msgHereIsGlass(ConveyorContents.poll());
			}
			//@Brooke this is cause of truck loading problems...find a way to reorganize this...
			TruckReady = false;
			// @Brooke the truck out for deliver is getting set 1 iteration too late it seems :-\
			if(truck == null || !truck.OutForDelivery){
				PrePopupSensor = false;
			}
			stateChanged();
			return true;
		}
		return false;
	}
	
	
	public synchronized void msgHereIsGlass(Glass g){ //From Shuttle/Oven
 	   ConveyorContents.add(g);
 	   if(shuttle==null){
 		   oven.msgConveyorNotReadyForGlass();
 		   this.print("Received Glass from Oven");
 	   }
 	   if(shuttle!=null) this.print("Received Glass from Shuttle");
 	  stateChanged();
	}

	public void msgGlassOnEntrySensor(){ //From TruckEntrySensor
		EntrySensor = true;
		stateChanged();
		print("Entry Sensor activated");
	}

	public void msgGlassOffEntrySensor(){ //From TruckEntrySensor
   		EntrySensor = false;
   		if(shuttle==null) oven.msgConveyorReadyForGlass();
   		if(shuttle!=null) shuttle.msgConveyorReady();
    	stateChanged();
    	print("Entry Sensor deactivated");
	}

	public void msgGlassOnPrePopupSensor(){ //From TruckPrePopupSensor
		PrePopupSensor = true;
    	stateChanged();
    	print("PrePopup Sensor activated");
	}

	public void msgGlassOffPrePopupSensor(){ //From TruckPrePopupSensor
    	PrePopupSensor = false;
	    stateChanged();
	    print("PrePopup Sensor deactivated");
	}

	public void msgPopupReadyForGlass(){ //From TruckPopup
    	PopupReady = true;
    	stateChanged();
    	print("Popup ready");
	}
	
	public void msgTruckReadyForGlass(){ // From Truck
		TruckReady = true;
		stateChanged();
		print("Truck ready");
	}
	
	public void StartConveyor(){
		Running = true;
		Object[] args = new Object[1];
		args[0] = index;
		transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_START, args);
		this.Do("Conveyor starting");
		stateChanged();
	}
	public void StopConveyor(){
		Running = false;
		Object[] args = new Object[1];
		args[0] = index;
		transducer.fireEvent(TChannel.CONVEYOR, TEvent.CONVEYOR_DO_STOP, args);
		this.Do("Conveyor stopping");
		stateChanged();
	}
	
	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
	}

	public void msgTruckOutForDelivery() {
		TruckOutForDelivery = true;
		stateChanged();
	}

	public void msgTruckReturned() {
		TruckOutForDelivery = false;
		if(PrePopupSensor)
			PrePopupSensor = false;
		stateChanged();
	}
}
