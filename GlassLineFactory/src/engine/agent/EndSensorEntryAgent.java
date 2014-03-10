package engine.agent;

import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;
import transducer.Transducer;
import engine.agent.Agent;


public class EndSensorEntryAgent extends Agent implements TReceiver{

	public Transducer transducer;
	public EndConveyorAgent conveyor;
	public gui.panels.DisplayPanel guiDisplayPanel;
	public int index;
	
	// for main
	public EndSensorEntryAgent(String name){
		super(name);
	}
	// for main
	public void setEndSensorEntryAgent(Transducer t, ShuttleAgent s, EndConveyorAgent c, int ind, gui.panels.DisplayPanel gdp){
		transducer = t;
		t.register(this, TChannel.ALL_AGENTS);
		t.register(this, TChannel.SENSOR);
		conveyor = c;
		this.index = ind;
		this.guiDisplayPanel = gdp;
	}
	// for JUnit test
	public EndSensorEntryAgent(Transducer t, ShuttleAgent s, EndConveyorAgent c){
		conveyor = c;
		transducer = t;
		t.register(this, TChannel.ALL_AGENTS);
		t.register(this, TChannel.SENSOR);
	}

	@Override
	public boolean pickAndExecuteAnAction() {
	    return false;
	}
	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		if(channel==TChannel.SENSOR && args[0]==(Object)index){
			if(event==TEvent.SENSOR_GUI_PRESSED) conveyor.msgGlassOnEntrySensor();
			if(event==TEvent.SENSOR_GUI_RELEASED) conveyor.msgGlassOffEntrySensor();
		}	
	}
}
