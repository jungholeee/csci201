package shared;

import java.util.ArrayList;
import shared.enums.ComponentOperations;

public class Glass implements Comparable<Glass>{
	private String name;
	//GUIGlass guiGlass;
	ArrayList<ComponentOperations> operationsToPerform;

	
	// constructor for empty operations to perform
	public Glass(String name)
	{
		super();
		//this.guiGlass = guiGlass;
		this.setName(name);
		operationsToPerform = new ArrayList<ComponentOperations>();
	}
	
	// constructor to add a created list of operations to perform
	public Glass(String name, ArrayList<ComponentOperations> operationsToPerform)
	{
		super();
		//this.guiGlass = guiGlass;
		this.setName(name);
		this.operationsToPerform = new ArrayList<ComponentOperations>();
		this.operationsToPerform.addAll(operationsToPerform);
	}

	// returns name of glass piece
	public String toString()
	{
		return this.getName();
	}
	
	// returns reference to the guiglass
	/*public GUIGlass getGuiGlass()
	{
		return guiGlass;
	}*/
	
	// adds a single operation to perform on the glass
	public void addOperationToPerform(ComponentOperations componentToAdd)
	{
		operationsToPerform.add(componentToAdd);
	}
	
	// sets operations to perform to a list of operations
	public void setOperationsToPerform(ArrayList<ComponentOperations> operationsToPerform)
	{
		this.operationsToPerform.addAll(operationsToPerform);
	}
	
	// returns the list of operations to perform
	public ArrayList<ComponentOperations> getOperationsToPerform()
	{
		return operationsToPerform;
	}

	public int compareTo(Glass arg0) {
		return 0;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
