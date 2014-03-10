package engine.agent;

import java.util.*;

import interfaces.Cutter;
import interfaces.NCCutConveyor;

import shared.Glass;
import transducer.*;

public class CutterAgent extends Agent implements TReceiver, Cutter {

	Transducer trans;
	List<Integer> waitingGlass;
	Glass glass;
	NCCutConveyor entryConveyor, exitConveyor;
	enum CutterState {IDLE, ANIMATED, ANIMATION_FINISHED, GLASS_RELEASED, RELEASE_CALLED, RELEASE_GLASS, GLASS_LOADED};
	CutterState state;
	enum ConveyorState {IDLE, PASSING_TO_CONVEYOR, PASSED_TO_CONVEYOR, CONVEYOR_NOTIFIED};
	ConveyorState entryConveyorState, exitConveyorState;

	public CutterAgent(Transducer t, String name)
	{
		super(name);
		trans = t;
		trans.register(this, TChannel.CUTTER);
		trans.register(this, TChannel.ALL_AGENTS);
		state = CutterState.IDLE;
		entryConveyorState = exitConveyorState = ConveyorState.IDLE;
		glass = null;
		waitingGlass = Collections.synchronizedList(new ArrayList<Integer>());
	}

	@Override
	public boolean pickAndExecuteAnAction() {
		if(entryConveyorState == ConveyorState.PASSING_TO_CONVEYOR && glass == null)
		{
			this.Do(this.getName() + " getting glass   BBC");
			getGlass();
			return true;
		}
		if(glass != null && exitConveyorState == ConveyorState.PASSING_TO_CONVEYOR && state == CutterState.GLASS_RELEASED) 
		{
			startPassingToNext();
			return true;
		}
		if(glass != null && state == CutterState.GLASS_LOADED)
		{
			doAnimation();
			return true;
		}
		if(glass != null && state == CutterState.ANIMATION_FINISHED && exitConveyorState != ConveyorState.CONVEYOR_NOTIFIED)
		{
			notifyNext();
			return true;
		}
		if(glass != null && state == CutterState.GLASS_RELEASED && exitConveyorState == ConveyorState.IDLE)
		{
			passGlass();
			return true;
		}
		if(glass != null && state == CutterState.RELEASE_GLASS)
         {
	         releaseGlass();
	         return true;
         }

		return false;
	}

	private void passGlass() {
		exitConveyor.msgHereIsGlass(glass);
		glass = null;
		state = CutterState.IDLE;
	}

	private void doAnimation()
	{
		this.Do(" running animation.  BBC");
		state = CutterState.ANIMATED;
		//Object[] args = new Object[1];
		//args[0] = glass.getGuiGlass();
		trans.fireEvent(TChannel.CUTTER, TEvent.WORKSTATION_DO_ACTION, null);
	}

	private void releaseGlass()
	{
		trans.fireEvent(TChannel.CUTTER, TEvent.WORKSTATION_RELEASE_GLASS, null);
		state = CutterState.RELEASE_CALLED;
		exitConveyorState = ConveyorState.IDLE;
		stateChanged();
	}

	private void notifyNext()
	{
		this.Do(" notifying " + exitConveyor.toString() + " that the cutter has glass to pass." + glass.getName() + "   BBC");
		exitConveyor.msgIHaveGlass();
		exitConveyorState = ConveyorState.CONVEYOR_NOTIFIED;
		//state = CutterState.RELEASE_GLASS;
		stateChanged();
	}

	private void getGlass()
	{
		this.Do(" notifying " + entryConveyor.toString() + " that the cutter is ready for glass.   BBC");
		entryConveyor.msgIAmReadyForGlass();
		entryConveyorState = ConveyorState.CONVEYOR_NOTIFIED;
		stateChanged();
	}

	public void startPassingToNext()
	{
		this.Do(" giving " + exitConveyor.toString() + " glass from the cutter.   BBC");
		//exitConveyor.msgHereIsGlass(glass);
		//Object[] args = new Object[1];
		//args[0] = glass.getGuiGlass();
		//trans.fireEvent(TChannel.CUTTER, TEvent.WORKSTATION_RELEASE_GLASS, null);
		//releaseGlass();
		//glass = null;
		//state = CutterState.RELEASE_CALLED;
		//state = CutterState.IDLE;
		synchronized(waitingGlass)
		{
			//this.Do("removing glass, the waitingGlass list size is:  " + waitingGlass.size() + " the glass is:  " + glass.getName() + "   BBC");
			// :)waitingGlass.remove(0);
			//glass = null;
			if(waitingGlass.size() > 0)
				entryConveyorState = ConveyorState.PASSING_TO_CONVEYOR;
			else
				entryConveyorState = ConveyorState.IDLE;
		}
		state = CutterState.RELEASE_GLASS;
		exitConveyorState = ConveyorState.PASSED_TO_CONVEYOR;
		stateChanged();
	}

	@Override
	public void eventFired(TChannel channel, TEvent event, Object[] args) {
		if(event == TEvent.WORKSTATION_GUI_ACTION_FINISHED)
		{
			state = CutterState.ANIMATION_FINISHED;
			stateChanged();
		}

		if(event == TEvent.WORKSTATION_RELEASE_FINISHED)
		{
			state = CutterState.GLASS_RELEASED;
			stateChanged();
		}
		
		if(event == TEvent.WORKSTATION_LOAD_FINISHED)
		{
			state = CutterState.GLASS_LOADED;
			entryConveyor.msgIHaveTheGlass();
			stateChanged();
		}

		/*else if(event == TEvent.WORKSTATION_RELEASE_FINISHED)
         {
         //exitConveyorState = ConveyorState.PASSING_TO_CONVEYOR;
         //stateChanged();
         exitConveyorState = ConveyorState.IDLE;
         glass = null;
         stateChanged();
         }*/

	}

	public void msgIHaveGlassPiece()
	{
		synchronized(waitingGlass)
		{
			waitingGlass.add(1);
			this.Do(this.name + "  has recieved msgIHaveGlassPiece and added a number to waitingList    BBC");
			if(waitingGlass.size() == 1)
				entryConveyorState = ConveyorState.PASSING_TO_CONVEYOR;
			stateChanged();
		}
	}

	public void msgHereIsGlass(Glass glass)
	{
		this.glass = glass;	
		this.Do(this.name + "  has recieved msgHereIsGlass and now the glass ==  " + this.glass.getName() + "     BBC");
		synchronized(waitingGlass)
		{
			waitingGlass.remove(0);
			if(waitingGlass.size() > 0)
				entryConveyorState = ConveyorState.PASSING_TO_CONVEYOR;
			else
				entryConveyorState = ConveyorState.IDLE;
		}
		stateChanged();
	}

	public void msgIAmReadyForGlass()
	{
		exitConveyorState = ConveyorState.PASSING_TO_CONVEYOR;
		state = CutterState.RELEASE_GLASS;
		stateChanged();
	}

	public void setConveyors(NCCutConveyor entryConveyor, NCCutConveyor exitConveyor)
	{
		this.entryConveyor = entryConveyor;
		this.exitConveyor = exitConveyor;
	}

}
