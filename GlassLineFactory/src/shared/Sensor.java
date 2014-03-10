package shared;

public class Sensor {

	/*
	 * 	{ {=============================================} }
	 * 	{ {												} }
	 * 	{ {			DATA								} }
	 * 	{ {												} }
	 * 	{ {=============================================} }
	 */
	
	boolean 	activated, functioning;
	public int	sensorID;
	
	/*
	 * 	{ {=============================================} }
	 * 	{ {												} }
	 * 	{ {			CONSTRUCTORS						} }
	 * 	{ {												} }
	 * 	{ {=============================================} }
	 */
	
	public Sensor() {	// Used only by the popup.  Spec was changed so no popups have sensors
		activated = false;
		functioning = true;
		sensorID = 99;	// Won't be used
	}
	
	public Sensor(int id) {
		activated = false;
		functioning = true;
		sensorID = id;
	}
	
	/*
	 * 	{ {=============================================} }
	 * 	{ {												} }
	 * 	{ {			FUNCTIONS							} }
	 * 	{ {												} }
	 * 	{ {=============================================} }
	 */
	
	public boolean isActivated() {
		return activated;
	}

	public boolean isFunctioning() {
		return functioning;
	}
	
	public void activate() {
		activated = true;
	}
	
	public void deactivate() {
		activated = false;
	}
}
