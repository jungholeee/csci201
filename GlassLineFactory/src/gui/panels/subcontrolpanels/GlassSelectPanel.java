
package gui.panels.subcontrolpanels;

import gui.panels.ControlPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import shared.Glass;
import shared.enums.ComponentOperations;
import transducer.TChannel;
import transducer.TEvent;
import transducer.Transducer;

/**
 * The GlassSelectPanel class contains buttons allowing the user to select what
 * type of glass to produce.
 */
@SuppressWarnings("serial")
public class GlassSelectPanel extends JPanel implements ItemListener
{
	/** The ControlPanel this is linked to */
	private ControlPanel parent;

	/** The panels for the glass recipe & add glass*/
	JPanel addGlassPanel, customGlassPanel; //panel for the add Glass button
	JPanel recipePanel; //panel with check boxes for recipe of glass for which stations to go through

	/**The buttons for a piece of glass*/
	JButton addGlassButton, addCustomGlassButton;

	/**The check boxes for a recipe*/ 
	//options are: DRILL, GRINDER, CROSS_SEAMER, MANUAL_BREAKOUT, 
	JCheckBox drillCheckbox, grinderCheckbox, crossSeamerCheckbox, manualBreakoutCheckbox;
	JTextField txtNumGlass, txtNumCustomGlass;

	ArrayList<ComponentOperations> operationsOnPart;


	//private int count = 0;
	private JPanel Recipe;
	private JLabel lblAddPartsTo;
	/** Allows the control panel to communicate with the back end and give commands */
	private Transducer transducer;

	/**
	 * Creates a new GlassSelect and links it to the control panel
	 * @param cp
	 *        the ControlPanel linked to it
	 */
	public GlassSelectPanel(ControlPanel cp)
	{
		// initialize transducer
		transducer = new Transducer();
		transducer.startTransducer();

		parent = cp;
		this.setBackground(Color.black);
		this.setForeground(Color.black);
		//array for the operations on a custom glass
		operationsOnPart = new ArrayList<ComponentOperations>();

		//initialize buttons
		//button far glass with all stations
		addGlassButton = new JButton("Add Default Glass");
		addGlassButton.setBackground(Color.white);
		addGlassButton.setForeground(Color.black);
		addGlassButton.setFont(new Font("SansSerif", Font.PLAIN, 18));
		addGlassButton.setHorizontalAlignment(SwingConstants.CENTER);
		addGlassButton.setOpaque(true);
		addGlassButton.setBorderPainted(false);

		// manage layout
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setBackground(Color.black);

		addGlassPanel = new JPanel();
		addGlassPanel.setForeground(Color.WHITE);
		addGlassPanel.setBackground(Color.black);

		// set up buttons
		addGlassButton.addActionListener(new AddGlassButtonListener());
		addGlassButton.setAlignmentX(CENTER_ALIGNMENT);
		addGlassPanel.setLayout(new BoxLayout(addGlassPanel, BoxLayout.X_AXIS));

		//adding components to addGlassPanel
		txtNumGlass = new JTextField();
		txtNumGlass.setText("1");
		txtNumGlass.setPreferredSize(new Dimension(50, 30));
		txtNumGlass.setMaximumSize(new Dimension(50, 30));
		txtNumGlass.setMinimumSize(new Dimension(50, 30));
		addGlassPanel.add(Box.createHorizontalGlue());
		addGlassPanel.add(txtNumGlass);
		addGlassPanel.add(addGlassButton);
		addGlassPanel.add(Box.createHorizontalGlue());

		//adding panels to the glassSelect Panel
		this.add(Box.createVerticalGlue());
		this.add(addGlassPanel);
		this.add(Box.createVerticalGlue());

		Recipe = new JPanel();
		add(Recipe, BorderLayout.WEST);
		Recipe.setForeground(Color.WHITE);
		Recipe.setBackground(Color.BLACK);
		Recipe.setLayout(new BoxLayout(Recipe, BoxLayout.Y_AXIS));
		
		
		lblAddPartsTo = new JLabel("Add Parts To Custom Recipe");
		Recipe.add(lblAddPartsTo);
		lblAddPartsTo.setToolTipText("");
		//lblAddPartsTo.setHorizontalAlignment(SwingConstants.CENTER);
		lblAddPartsTo.setForeground(Color.WHITE);
		lblAddPartsTo.setBackground(Color.BLACK);
		lblAddPartsTo.setFont(new Font("SansSerif", Font.BOLD, 14));

		recipePanel = new JPanel(); //recipe panel with columns
		Recipe.add(recipePanel);
		recipePanel.setForeground(Color.WHITE);
		recipePanel.setBackground(Color.black);
		recipePanel.setLayout(new BoxLayout(recipePanel, BoxLayout.Y_AXIS));
		manualBreakoutCheckbox = new JCheckBox("Manual Breakout");
		recipePanel.add(manualBreakoutCheckbox);

		manualBreakoutCheckbox.setBackground(Color.black);
		manualBreakoutCheckbox.setForeground(Color.white);
		crossSeamerCheckbox = new JCheckBox("Cross Seamer");
		recipePanel.add(crossSeamerCheckbox);

		crossSeamerCheckbox.setBackground(Color.black);
		crossSeamerCheckbox.setForeground(Color.white);

		//initalize checkboxes
		drillCheckbox = new JCheckBox("Drill");
		recipePanel.add(drillCheckbox);


		//set up check boxes
		drillCheckbox.setBackground(Color.black);
		drillCheckbox.setForeground(Color.white);
		grinderCheckbox = new JCheckBox("Grinder");
		recipePanel.add(grinderCheckbox);

		grinderCheckbox.setBackground(Color.black);
		grinderCheckbox.setForeground(Color.white);
		grinderCheckbox.addItemListener(this);
		drillCheckbox.addItemListener(this);
		crossSeamerCheckbox.addItemListener(this);
		manualBreakoutCheckbox.addItemListener(this);

		//button for glass with specified recipie
		addCustomGlassButton = new JButton("Add Custom Glass");
		addCustomGlassButton.setBackground(Color.white);
		addCustomGlassButton.setForeground(Color.black);
		addCustomGlassButton.setFont(new Font("SansSerif", Font.PLAIN, 18));
		addCustomGlassButton.setHorizontalAlignment(SwingConstants.CENTER);
		addCustomGlassButton.setOpaque(true);
		addCustomGlassButton.setBorderPainted(false);

		customGlassPanel = new JPanel();
		customGlassPanel.setLayout(new BoxLayout(customGlassPanel, BoxLayout.X_AXIS));
		Recipe.add(Box.createVerticalGlue());
		Recipe.add(customGlassPanel);
		Recipe.add(Box.createVerticalGlue());
		customGlassPanel.setForeground(Color.WHITE);
		customGlassPanel.setBackground(Color.black);

		addCustomGlassButton.addActionListener(new CustomGlassButtonListener());
		addCustomGlassButton.setAlignmentX(CENTER_ALIGNMENT);
		txtNumCustomGlass = new JTextField();
		txtNumCustomGlass.setText("1");
		txtNumCustomGlass.setPreferredSize(new Dimension(50, 30));
		txtNumCustomGlass.setMaximumSize(new Dimension(50, 30));
		txtNumCustomGlass.setMinimumSize(new Dimension(50, 30));
		customGlassPanel.add(Box.createHorizontalGlue());
		customGlassPanel.add(txtNumCustomGlass);
		customGlassPanel.add(addCustomGlassButton);
		customGlassPanel.add(Box.createHorizontalGlue());

		// set default operations for the custom glass creation
		this.addOnlineComponents(operationsOnPart);
	}

	public class AddGlassButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent ae)
		{
			System.out.println("Control Panel ADD CUSTOM GLASS button clicked.");
			if (parent.getTransducer() == null)
			{
				System.out.println("No transducer connected!");
			}
			else
			{
				try{
					int num = Integer.parseInt(txtNumGlass.getText());
					System.out.println("Adding " + num + " pieces of default glass to the robot.");
					ArrayList<ComponentOperations> co = new ArrayList<ComponentOperations>();
					addComponents(co);
					for(int i = 0; i < num; i++)
					{
						Glass ag = new Glass("", co);
						parent.getGuiParent().getStockPileRobot().msgAddToStockPile(ag);
						//parent.getTransducer().fireEvent(TChannel.BIN, TEvent.BIN_CREATE_PART, args);
						//count++;
					}
				}
				catch(Exception ex)
				{
					JOptionPane.showMessageDialog(null, "You must enter an integer number.");
				}
			}
		}
	}

	public class CustomGlassButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent ae)
		{
			System.out.println("Control Panel ADD CUSTOM GLASS button clicked.");
			if (parent.getTransducer() == null)
			{
				System.out.println("No transducer connected!");
			}
			else
			{
				if(operationsOnPart.size() <= 6) {
					addOnlineComponents(operationsOnPart);
				}
				try{
					int num = Integer.parseInt(txtNumCustomGlass.getText());
					System.out.println("Adding " + num + " pieces of custom glass to the robot.");
					for(int i = 0; i < num; i++){
						Glass ag = new Glass("", operationsOnPart);
						parent.getGuiParent().getStockPileRobot().msgAddToStockPile(ag);
						//count++;
						//parent.getTransducer().fireEvent(TChannel.BIN, TEvent.BIN_CREATE_PART, args);
					}
				}
				catch(Exception e){
					JOptionPane.showMessageDialog(null, "You must enter an integer number.");
				}
			}
		}
	}


	/** Listens to the check boxes. */
	public void itemStateChanged(ItemEvent e) {

		Object source = e.getItemSelectable();

		//check box item listener for creating recipe
		if (source == drillCheckbox && e.getStateChange() == ItemEvent.SELECTED) {
			operationsOnPart.add(ComponentOperations.DRILL);
		} else if (source == grinderCheckbox && e.getStateChange() == ItemEvent.SELECTED) {
			operationsOnPart.add(ComponentOperations.GRINDER);
		} else if (source == manualBreakoutCheckbox && e.getStateChange() == ItemEvent.SELECTED) {
			operationsOnPart.add(ComponentOperations.MANUALBREAKOUT);
		} else if (source == crossSeamerCheckbox && e.getStateChange() == ItemEvent.SELECTED) {
			operationsOnPart.add(ComponentOperations.CROSSSEAMER);
		}

		//check box item listener for creating recipe
		if (source == drillCheckbox && e.getStateChange() == ItemEvent.DESELECTED) {
			operationsOnPart.remove(ComponentOperations.DRILL);
		} else if (source == grinderCheckbox && e.getStateChange() == ItemEvent.DESELECTED) {
			operationsOnPart.remove(ComponentOperations.GRINDER);
		} else if (source == manualBreakoutCheckbox && e.getStateChange() == ItemEvent.DESELECTED) {
			operationsOnPart.remove(ComponentOperations.MANUALBREAKOUT);
		} else if (source == crossSeamerCheckbox && e.getStateChange() == ItemEvent.DESELECTED) {
			operationsOnPart.remove(ComponentOperations.CROSSSEAMER);
		}
		
		// change recipe
		Object args[] = new Object[1];
		args[0] = operationsOnPart;
		parent.getTransducer().fireEvent(TChannel.BIN, TEvent.SET_RECIPE, args);
	}
	// adds all online necessary components to glass
	// otherwise adds all as default
	private void addOnlineComponents(ArrayList<ComponentOperations> co)
	{
		co.add(ComponentOperations.BREAKOUT);
		co.add(ComponentOperations.CUTTER);
		co.add(ComponentOperations.OVEN);
		co.add(ComponentOperations.PAINT);
		co.add(ComponentOperations.UVLAMP);
		co.add(ComponentOperations.WASHER);
	}
	// adds selected components to the glass
	// otherwise adds all as default
	private void addComponents(ArrayList<ComponentOperations> co)
	{
		// only have default for now
		co.add(ComponentOperations.BREAKOUT);
		co.add(ComponentOperations.CROSSSEAMER);
		co.add(ComponentOperations.CUTTER);
		co.add(ComponentOperations.DRILL);
		co.add(ComponentOperations.GRINDER);
		co.add(ComponentOperations.OVEN);
		co.add(ComponentOperations.PAINT);
		co.add(ComponentOperations.UVLAMP);
		co.add(ComponentOperations.WASHER);
	}
	/**
	 * Returns the parent panel
	 * @return the parent panel
	 */
	public ControlPanel getGuiParent()
	{
		return parent;
	}
}
