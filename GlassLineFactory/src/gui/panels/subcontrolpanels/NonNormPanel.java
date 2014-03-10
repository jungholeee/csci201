
package gui.panels.subcontrolpanels;

import gui.panels.ControlPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * The NonNormPanel is responsible for initiating and managing non-normative
 * situations. It contains buttons for each possible non-norm.
 * 
 * The non-normative situations are:
 * 1.  Drill 1 offline
 * 2.  Drill 2 offline
 * 3.  xseamer 1 offline
 * 4.  xseamer 2 offline
 * 5.  grinder 1 offline
 * 6.  grinder 2 offline
 * 7.
 * 8.
 */
@SuppressWarnings("serial")
public class NonNormPanel extends JPanel implements ItemListener
{
	/** The number of different havoc actions that exist */
	public static final int NUM_NON_NORMS = 6;
	JPanel customGlassPanel;
	private JToggleButton tglbtnCrossSeamer1, tglbtnCrossSeamer2;
	private JToggleButton tglbtnDrill1, tglbtnDrill2;
	private JToggleButton tglbtnGrinder1, tglbtnGrinder2;
	private JLabel lblControlWorkstations;
	private JPanel WorkstationControl, controls;

	/** The control panel this is linked to */
	ControlPanel parent;

	/** List of buttons for each non-norm */
	List<JButton> nonNormButtons;

	/** Title label **/
	JLabel titleLabel;

	/**
	 * Creates a new HavocPanel and links the control panel to it
	 * 
	 * @param cp
	 *        the ControlPanel linked to it
	 */
	public NonNormPanel(ControlPanel cp)
	{
		parent = cp;

		this.setBackground(Color.black);
		this.setForeground(Color.black);

		// set up layout
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		// set up button panel
		JPanel buttonPanel = new JPanel();
		GridLayout grid = new GridLayout(NUM_NON_NORMS / 2, 2);
		grid.setVgap(2);
		grid.setHgap(2);
		buttonPanel.setBackground(Color.black);
		buttonPanel.setLayout(grid);

		// make title
		titleLabel = new JLabel("NON NORMATIVES");
		titleLabel.setForeground(Color.white);
		titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 22));
		JPanel titleLabelPanel = new JPanel();
		titleLabelPanel.add(titleLabel);
		// titleLabelPanel.setAlignmentY(JLabel.CENTER_ALIGNMENT);
		titleLabelPanel.setBackground(Color.black);
		// add to panel
		this.add(titleLabelPanel);

		JPanel colorLinesPanel1 = new JPanel();
		colorLinesPanel1.setPreferredSize(new Dimension(350, 2));
		colorLinesPanel1.setBackground(Color.black);
		ImageIcon cl = new ImageIcon("imageicons/singleColoredLine.png");
		JLabel clLabel1 = new JLabel(cl);
		colorLinesPanel1.add(clLabel1);
		this.add(colorLinesPanel1);

		JPanel colorLinesPanel2 = new JPanel();
		colorLinesPanel2.setPreferredSize(new Dimension(350, 40));
		colorLinesPanel2.setBackground(Color.black);
		JLabel clLabel2 = new JLabel();
		colorLinesPanel2.add(clLabel2);
		this.add(colorLinesPanel2);

		WorkstationControl = new JPanel();
		WorkstationControl.setForeground(Color.WHITE);
		WorkstationControl.setBackground(Color.BLACK);
		add(WorkstationControl, BorderLayout.EAST);
		WorkstationControl.setLayout(new BorderLayout(0, 0));

		controls = new JPanel();
		controls.setBorder(new EmptyBorder(5, 20, 0, 20));
		controls.setForeground(Color.WHITE);
		controls.setBackground(Color.BLACK);
		WorkstationControl.add(controls);
		controls.setLayout(new GridLayout(3, 2, 10, 10));

		tglbtnDrill1 = new JToggleButton("Drill 1 Offline");
		tglbtnDrill1.setForeground(Color.BLACK);
		tglbtnDrill1.setBackground(Color.GREEN);
		tglbtnDrill1.setFont(new Font("SansSerif", Font.BOLD, 12));
		tglbtnDrill1.setHorizontalAlignment(SwingConstants.CENTER);
		tglbtnDrill1.setOpaque(true);
		tglbtnDrill1.setBorderPainted(false);
		tglbtnDrill1.addItemListener(this);
		controls.add(tglbtnDrill1);

		tglbtnDrill2 = new JToggleButton("Drill 2 Offline");
		tglbtnDrill2.setForeground(Color.BLACK);
		tglbtnDrill2.setBackground(Color.GREEN);
		tglbtnDrill2.setFont(new Font("SansSerif", Font.BOLD, 12));
		tglbtnDrill2.setHorizontalAlignment(SwingConstants.CENTER);
		tglbtnDrill2.setOpaque(true);
		tglbtnDrill2.setBorderPainted(false);
		tglbtnDrill2.addItemListener(this);
		controls.add(tglbtnDrill2);

		tglbtnCrossSeamer1 = new JToggleButton("X-Seamer 1 Offline");
		tglbtnCrossSeamer1.setAlignmentY(Component.TOP_ALIGNMENT);
		tglbtnCrossSeamer1.setBackground(Color.PINK);
		tglbtnCrossSeamer1.setForeground(Color.BLACK);
		tglbtnCrossSeamer1.setFont(new Font("SansSerif", Font.BOLD, 12));
		tglbtnCrossSeamer1.setHorizontalAlignment(SwingConstants.CENTER);
		tglbtnCrossSeamer1.setOpaque(true);
		tglbtnCrossSeamer1.setBorderPainted(false);
		tglbtnCrossSeamer1.addItemListener(this);
		controls.add(tglbtnCrossSeamer1);

		tglbtnCrossSeamer2 = new JToggleButton("X-Seamer 2 Offline");
		tglbtnCrossSeamer2.setAlignmentY(Component.TOP_ALIGNMENT);
		tglbtnCrossSeamer2.setBackground(Color.PINK);
		tglbtnCrossSeamer2.setForeground(Color.BLACK);
		tglbtnCrossSeamer2.setFont(new Font("SansSerif", Font.BOLD, 12));
		tglbtnCrossSeamer2.setHorizontalAlignment(SwingConstants.CENTER);
		tglbtnCrossSeamer2.setOpaque(true);
		tglbtnCrossSeamer2.setBorderPainted(false);
		tglbtnCrossSeamer2.addItemListener(this);
		controls.add(tglbtnCrossSeamer2);

		tglbtnGrinder1 = new JToggleButton("Grinder 1 Offline");
		tglbtnGrinder1.setBackground(Color.CYAN);
		tglbtnGrinder1.setForeground(Color.BLACK);
		tglbtnGrinder1.setFont(new Font("SansSerif", Font.BOLD, 12));
		tglbtnGrinder1.setHorizontalAlignment(SwingConstants.CENTER);
		tglbtnGrinder1.setOpaque(true);
		tglbtnGrinder1.setBorderPainted(false);
		tglbtnGrinder1.addItemListener(this);
		controls.add(tglbtnGrinder1);

		tglbtnGrinder2 = new JToggleButton("Grinder 2 Offline");
		tglbtnGrinder2.setBackground(Color.CYAN);
		tglbtnGrinder2.setForeground(Color.BLACK);
		tglbtnGrinder2.setFont(new Font("SansSerif", Font.BOLD, 12));
		tglbtnGrinder2.setHorizontalAlignment(SwingConstants.CENTER);
		tglbtnGrinder2.setOpaque(true);
		tglbtnGrinder2.setBorderPainted(false);
		tglbtnGrinder2.addItemListener(this);
		controls.add(tglbtnGrinder2);

		lblControlWorkstations = new JLabel("Control Workstations");
		lblControlWorkstations.setBackground(Color.BLACK);
		lblControlWorkstations.setForeground(Color.WHITE);
		lblControlWorkstations.setFont(new Font("SansSerif", Font.BOLD, 16));
		WorkstationControl.add(lblControlWorkstations, BorderLayout.NORTH);
	}

	/**
	 * Returns the parent panel
	 * 
	 * @return the parent panel
	 */
	public ControlPanel getGuiParent()
	{
		return parent;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		Object source = e.getItemSelectable();
		//toggle button item listener to take workstations offline
		if (source == tglbtnCrossSeamer1 && e.getStateChange() == ItemEvent.SELECTED) {
			try {
				parent.getGuiParent().robotc1.stopAgent();
				parent.getGuiParent().popup1.msgWorkstationFull(parent.getGuiParent().robotc1);
			}
			catch(Exception ex){
				//expected
			}

		} if (source == tglbtnCrossSeamer2 && e.getStateChange() == ItemEvent.SELECTED) {
			try {
				parent.getGuiParent().robotc2.stopAgent();
				parent.getGuiParent().popup1.msgWorkstationFull(parent.getGuiParent().robotc2);
			}
			catch(Exception ex){
				//expected
			}

		} 
		if (source == tglbtnDrill1 && e.getStateChange() == ItemEvent.SELECTED) {
			try {
				parent.getGuiParent().robotd1.stopAgent();
				parent.getGuiParent().popup0.msgWorkstationFull(parent.getGuiParent().robotd1);
			}
			catch(Exception ex) {
				//expected
			}

		} 
		if (source == tglbtnDrill2 && e.getStateChange() == ItemEvent.SELECTED) {
			try {
				parent.getGuiParent().robotd2.stopAgent();
				parent.getGuiParent().popup0.msgWorkstationFull(parent.getGuiParent().robotd2);
			}
			catch(Exception ex) {
				//expected
			}

		} 
		if (source == tglbtnGrinder1 && e.getStateChange() == ItemEvent.SELECTED) {
			try {
				parent.getGuiParent().robotg1.stopAgent();
				parent.getGuiParent().popup2.msgWorkstationFull(parent.getGuiParent().robotg1);
			}
			catch (Exception ex) {
				//expected
			}

		}
		if (source == tglbtnGrinder2 && e.getStateChange() == ItemEvent.SELECTED) {
			try {
				parent.getGuiParent().robotg2.stopAgent();
				parent.getGuiParent().popup2.msgWorkstationFull(parent.getGuiParent().robotg2);
			}
			catch (Exception ex) {
				//expected
			}
		}

		//toggle button item listener to take workstations online
		if (source == tglbtnCrossSeamer1 && e.getStateChange() == ItemEvent.DESELECTED) {
			parent.getGuiParent().robotc1.startThread();
			if(!parent.getGuiParent().robotc1.hasGlass()) {
				parent.getGuiParent().popup1.msgWorkstationIdle(parent.getGuiParent().robotc1);
			}
		}
		if (source == tglbtnCrossSeamer2 && e.getStateChange() == ItemEvent.DESELECTED) {
			parent.getGuiParent().robotc2.startThread();
			if(!parent.getGuiParent().robotc2.hasGlass()) {
				parent.getGuiParent().popup1.msgWorkstationIdle(parent.getGuiParent().robotc2);
			}
		}
		if (source == tglbtnDrill1 && e.getStateChange() == ItemEvent.DESELECTED) {
			parent.getGuiParent().robotd1.startThread();
			if(!parent.getGuiParent().robotd1.hasGlass()) {
				parent.getGuiParent().popup0.msgWorkstationIdle(parent.getGuiParent().robotd1);
			}
		}
		if (source == tglbtnDrill2 && e.getStateChange() == ItemEvent.DESELECTED) {
			parent.getGuiParent().robotd2.startThread();
			if(!parent.getGuiParent().robotd2.hasGlass()) {
				parent.getGuiParent().popup0.msgWorkstationIdle(parent.getGuiParent().robotd2);
			}
		}
		if (source == tglbtnGrinder1 && e.getStateChange() == ItemEvent.DESELECTED) {
			parent.getGuiParent().robotg1.startThread();
			if(!parent.getGuiParent().robotg1.hasGlass()) {
				parent.getGuiParent().popup2.msgWorkstationIdle(parent.getGuiParent().robotg1);
			}
		}
		if (source == tglbtnGrinder2 && e.getStateChange() == ItemEvent.DESELECTED) {
			parent.getGuiParent().robotg2.startThread();
			if(!parent.getGuiParent().robotg2.hasGlass()) {
				parent.getGuiParent().popup2.msgWorkstationIdle(parent.getGuiParent().robotg2);
			}
		}
		
	}

}
