package engine.agent;

import transducer.TChannel;
import transducer.TEvent;
import transducer.TReceiver;
import transducer.Transducer;
import engine.agent.Agent;

public class EndSensorPrePopupAgent extends Agent implements TReceiver {

	public Transducer transducer;
	public EndConveyorAgent conveyor;
	public gui.panels.DisplayPanel guiDisplayPanel;
	public int index;
	
	public EndSensorPrePopupAgent(String name){
		super(name);
	}
	
	public void setEndSensorPrePopupAgent(Transducer t, EndConveyorAgent c, int ind, gui.panels.DisplayPanel gdp){
		conveyor = c;
		transducer = t;
		t.register(this, TChannel.ALL_AGENTS);
		t.register(this, TChannel.SENSOR);
		this.index = ind;
		this.guiDisplayPanel = gdp;
	}
	
	public EndSensorPrePopupAgent(Transducer t, EndConveyorAgent c){
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
		// TODO Auto-generated method stub
		if(channel==TChannel.SENSOR && args[0]==(Object)index){
			if(event==TEvent.SENSOR_GUI_PRESSED) conveyor.msgGlassOnPrePopupSensor();
			if(event==TEvent.SENSOR_GUI_RELEASED) conveyor.msgGlassOffPrePopupSensor();
		}	
	}

}
