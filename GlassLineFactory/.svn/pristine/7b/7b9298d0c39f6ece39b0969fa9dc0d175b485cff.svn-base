package engine.agent;

import transducer.*;

public class EndSensorPostOvenAgent extends Agent implements TReceiver{

	public Transducer transducer;
	public EndConveyorAgent conveyor;	// OvenConveyor2
	public gui.panels.DisplayPanel guiDisplayPanel;
	public int index;
	
	public EndSensorPostOvenAgent(String name){
		super(name);
	}
	
	public EndSensorPostOvenAgent(Transducer t, OvenAgent oven, EndConveyorAgent c, int ind, gui.panels.DisplayPanel gdp){
		conveyor = c;
		transducer = t;
		t.register(this, TChannel.ALL_AGENTS);
		t.register(this, TChannel.SENSOR);
		this.index = ind;
		this.guiDisplayPanel = gdp;
	}
	
	public void setEndSensorPostOvenAgent(Transducer t, OvenAgent o, EndConveyorAgent c, int ind, gui.panels.DisplayPanel gdp){
		transducer = t;
		t.register(this, TChannel.ALL_AGENTS);
		t.register(this, TChannel.SENSOR);
		conveyor = c;
		this.index = ind;
		this.guiDisplayPanel = gdp;
	}
	
	
	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		// TODO Auto-generated method stub
		if(channel==TChannel.SENSOR && args[0]==(Object)index){
			if(event==TEvent.SENSOR_GUI_PRESSED) conveyor.msgGlassOnEntrySensor();
			if(event==TEvent.SENSOR_GUI_RELEASED) conveyor.msgGlassOffEntrySensor();
		}	
	}

	@Override
	public boolean pickAndExecuteAnAction() {
		return false;
	}
}
