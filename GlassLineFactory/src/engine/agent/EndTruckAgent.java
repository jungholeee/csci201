package engine.agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import shared.Glass;
import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;
import transducer.Transducer;

import engine.agent.Agent;

public class EndTruckAgent extends Agent implements TReceiver{
	
	public boolean OutForDelivery = false;
	public Transducer transducer;
	public List<Glass> TruckLoad;
	public int maxTruckLoad = 20;
	public EndConveyorAgent conveyor;
	public gui.panels.DisplayPanel guiDisplayPanel;
	public int index;
	//@Brooke attempting to force the conveyor to stop passing the glass past the sensor
	boolean deliver = false;
	Glass passGlass;
	
	// for main
	public EndTruckAgent(String name){
		super(name);
		TruckLoad = new ArrayList<Glass>();
	}
	// for main
	public void setEndTruckAgent(Transducer t, EndConveyorAgent tr, int ind, gui.panels.DisplayPanel gdp){
		transducer = t;
		t.register(this, TChannel.ALL_AGENTS);
		t.register(this, TChannel.TRUCK);
		conveyor = tr;
		this.index = ind;
		this.guiDisplayPanel = gdp;
		//@brooke instantiate a synchronized list
		TruckLoad = Collections.synchronizedList(new ArrayList<Glass>());
	}
	// for JUnit test
	public EndTruckAgent(Transducer t, EndConveyorAgent tr){
		//@brooke instantiate a synchronized list
		TruckLoad = Collections.synchronizedList(new ArrayList<Glass>());
		conveyor = tr;
		transducer = t;
		t.register(this, TChannel.ALL_AGENTS);
		t.register(this, TChannel.TRUCK);
	}
	
	public void msgHereIsGlass(Glass g){ //From Conveyor
		//@Brooke synchronizing
		/*synchronized(TruckLoad){
			TruckLoad.add(g);
		}*/
		passGlass = g;
    	print("Received glass from Conveyor, " + g.getName() + "; new size: " + TruckLoad.size());
    	stateChanged();
	}
	
	public void DeliverLoad(){
		//@Brooke Synchronizing
		synchronized(TruckLoad){
			for(int i = 0; i < TruckLoad.size(); i++){
				//@Brooke deallocate memory
				TruckLoad.set(i, null);
			}
			TruckLoad.clear();
		}
	    Object[] args = new Object[1];
	    args[0] = index;
	    transducer.fireEvent(TChannel.TRUCK, TEvent.TRUCK_DO_EMPTY, args);
	    OutForDelivery = true;
	    stateChanged();
	}

	@Override
	public boolean pickAndExecuteAnAction() {
		if(TruckLoad.size()>0){
			if(TruckLoad.size()>=maxTruckLoad && deliver){ 
				DeliverLoad();
			//Object[] args = new Object[1];
			//args[0] = TruckLoad.get(TruckLoad.size()-1);
			//transducer.fireEvent(TChannel.TRUCK, TEvent.TRUCK_DO_LOAD_GLASS, args);
			//transducer.fireEvent(TChannel.TRUCK, TEvent.TRUCK_DO_LOAD_GLASS, null);
				return true;
			}
		}
		return false;
	}
	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		if(channel==TChannel.TRUCK){
			if(event==TEvent.TRUCK_GUI_LOAD_FINISHED){
				//@Brooke moving the truckload add to here.  Should be accepting 3 pieces, but 3rd piece gets stuck half off conveyor
				synchronized(TruckLoad){
					TruckLoad.add(passGlass);
				}
				passGlass = null;
				//@Brooke this is never being called again.  truck never actually gets next piece of glass
				synchronized(TruckLoad){
					if(TruckLoad.size() < maxTruckLoad){
						this.conveyor.msgTruckReadyForGlass();
					}
					else{
						conveyor.msgTruckOutForDelivery();
						deliver = true;
						stateChanged();
					}
				}
				//if(TruckLoad.size()>=maxTruckLoad) DeliverLoad();
				//conveyor.msgTruckReadyForGlass();
			}
			if(event==TEvent.TRUCK_GUI_EMPTY_FINISHED){
				print("Empty finished, new size: " + TruckLoad.size());
				OutForDelivery = false;
				deliver = false;
				conveyor.msgTruckReadyForGlass();
				conveyor.msgTruckReturned();
				stateChanged();
			}
		}
	}
}
