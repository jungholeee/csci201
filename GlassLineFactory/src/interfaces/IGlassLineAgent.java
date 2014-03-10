package interfaces;

import shared.*;

public interface IGlassLineAgent {
	public abstract void msgHereIsGlass(IGlassLineAgent sender, Glass g);
	
	public abstract void msgGotGlass(IGlassLineAgent receiver, Glass g);
	
	public abstract void msgGetGlass();
	
	public abstract void msgWaitToSendGlass(Glass g);
	
	public abstract String getName();
	
	public abstract int getID();
	
	// Added to support integration
	public abstract void msgConveyorReady();
}