package engine.agent;

import transducer.*;

public class EndSensorPreOvenAgent extends Agent implements TReceiver{

	public Transducer transducer;
	public OvenAgent oven;
	public EndConveyorAgent conveyor;	// OvenConveyor1
	public gui.panels.DisplayPanel guiDisplayPanel;
	public int index;
	
	public EndSensorPreOvenAgent(String name){
		super(name);
	}
	
	public EndSensorPreOvenAgent(Transducer t, OvenAgent oven, EndConveyorAgent c, int ind, gui.panels.DisplayPanel gdp){
		conveyor = c;
		transducer = t;
		t.register(this, TChannel.ALL_AGENTS);
		t.register(this, TChannel.SENSOR);
		this.index = ind;
		this.guiDisplayPanel = gdp;
	}
	
	public void setEndSensorPreOvenAgent(Transducer t, OvenAgent o, EndConveyorAgent c, int ind, gui.panels.DisplayPanel gdp){
		transducer = t;
		t.register(this, TChannel.ALL_AGENTS);
		t.register(this, TChannel.SENSOR);
		conveyor = c;
		this.index = ind;
		this.guiDisplayPanel = gdp;
	}
	
	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		if(channel==TChannel.SENSOR && args[0]==(Object)index){
			if(event==TEvent.SENSOR_GUI_PRESSED) conveyor.msgGlassOnPrePopupSensor();
			if(event==TEvent.SENSOR_GUI_RELEASED) conveyor.msgGlassOffPrePopupSensor();
		}	
	}

	@Override
	public boolean pickAndExecuteAnAction() {
	    return false;
	}
}
