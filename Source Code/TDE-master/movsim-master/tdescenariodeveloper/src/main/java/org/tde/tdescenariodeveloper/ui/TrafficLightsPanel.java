package org.tde.tdescenariodeveloper.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.movsim.autogen.ControllerGroup;
import org.movsim.autogen.Phase;
import org.movsim.autogen.TrafficLightState;
import org.movsim.network.autogen.opendrive.OpenDRIVE.Controller;
import org.movsim.network.autogen.opendrive.OpenDRIVE.Controller.Control;
import org.movsim.network.autogen.opendrive.OpenDRIVE.Road.Signals.Signal;
import org.tde.tdescenariodeveloper.eventhandling.ControllerGroupListener;
import org.tde.tdescenariodeveloper.eventhandling.PhaseListener;
import org.tde.tdescenariodeveloper.eventhandling.TrafficLightStateListener;
import org.tde.tdescenariodeveloper.utils.GraphicsHelper;
/**
 * This {@link Class} is used to {@link Signal}s, {@link Controller}s, {@link Control}s {@link ControllerGroup}s, {@link Phase}s and {@link TrafficLightState}s 
 * @author Shmeel
 * @see Signal
 * @see ControllerGroup
 * @see Controller
 * @see Control
 * @see TrafficLightState
 */
public class TrafficLightsPanel extends JPanel {
	MovsimConfigContext mvCxt;
	JPanel cntPnl;
	private ControllerPanel controllerPanel;
	public TrafficLightsPanel(MovsimConfigContext rdCxt) {
		this.mvCxt=rdCxt;
		cntPnl=new JPanel(new GridBagLayout());
		cntPnl.setOpaque(false);
		setLayout(new GridBagLayout());
		controllerPanel=new ControllerPanel(rdCxt.getRdCxt());
		GridBagConstraints c=new GridBagConstraints();
		c.anchor=GridBagConstraints.CENTER;
		c.fill=GridBagConstraints.BOTH;
		c.weightx=1;
		c.weighty=1;
		c.insets=new Insets(5,10,5,10);
		JScrollPane sp1=new JScrollPane(controllerPanel),sp2=new JScrollPane(cntPnl);
		sp1.setOpaque(false);
		sp1.getViewport().setOpaque(false);
		sp2.setOpaque(false);
		sp2.getViewport().setOpaque(false);
		sp1.setBorder(new TitledBorder(new LineBorder(new Color(150, 150, 150), 1, false), "Signal Controllers", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		sp2.setBorder(new TitledBorder(new LineBorder(new Color(150, 150, 150), 1, false), "Signal Controllers Configurations", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		add(sp1,c);
		c.weightx=3;
//		c.gridwidth=GridBagConstraints.REMAINDER;
		add(sp2,c);
	}
	/**
	 * updates this {@link TrafficLightsPanel}
	 */
	public void updateTrafficLightsPanel(){
		cntPnl.removeAll();
		cntPnl.add(new JLabel("Add signals from road tab"));
		controllerPanel.updateControllerPanel();
		if(mvCxt.getMovsim().getScenario().isSetTrafficLights()){
			fillControllerGroupsPanel(cntPnl, mvCxt);
			revalidate();
			repaint();
		}
	}
	private static void fillControllerGroupsPanel( JPanel cntPnl,MovsimConfigContext mvCxt) {
		cntPnl.removeAll();
		GridBagConstraints c=new GridBagConstraints();
		c.gridwidth=GridBagConstraints.REMAINDER;
		c.anchor=GridBagConstraints.NORTH;
		c.fill=GridBagConstraints.BOTH;
		c.weightx=1;
		c.insets=new Insets(5, 3, 5, 3);
		List<ControllerGroup>controllerGrps=mvCxt.getMovsim().getScenario().getTrafficLights().getControllerGroup();
		for(ControllerGroup s:controllerGrps){
			JPanel p=ControllerGroupToControllerGroupPanel(s, mvCxt);
			p.setOpaque(false);
			p.setBorder(new TitledBorder(new LineBorder(new Color(150, 150, 150), 1, false), "Controller group", TitledBorder.LEADING, TitledBorder.TOP, null, null));
			cntPnl.add(p,c);
		}
	}
	/**
	 * Converts {@link ControllerGroup} to {@link JPanel} 
	 * @param s {@link ControllerGroup} to be converted
	 * @param mvCxt contains reference to loaded .xprj file and other panels added to it
	 * @return {@link JPanel}
	 */
	public static JPanel ControllerGroupToControllerGroupPanel(ControllerGroup s,MovsimConfigContext mvCxt) {
		JPanel main=new JPanel(new GridBagLayout());
		main.setOpaque(false);
		JPanel phases=new JPanel(new GridBagLayout());
		phases.setOpaque(false);
		phases.setBorder(new TitledBorder(new LineBorder(new Color(150, 150, 150), 1, false), "Phases", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		ControllerGroupListener cgl=new ControllerGroupListener(s, mvCxt);
		fillPhasesPanel(s.getPhase(),phases,mvCxt);
		main.setBorder(new LineBorder(TDEResources.getResources().CONTROLLERS_BORDER_COLOR, 1, true));
		phases.setBorder(new LineBorder(TDEResources.getResources().CONTROLLERS_BORDER_COLOR, 1, true));
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.fill=GridBagConstraints.BOTH;
		gbc.insets=new Insets(2, 5, 2, 5);
		gbc.weightx=1;
		gbc.gridwidth=GridBagConstraints.REMAINDER;

		main.add(new JLabel("Controller: "+s.getId()),gbc);
		main.add(phases,gbc);
		
		JButton newPhase=new JButton("Add new phase to this controller group",TDEResources.getResources().getAddIcon());
		newPhase.addActionListener(cgl);
		cgl.setNewPhase(newPhase);
		main.add(newPhase,gbc);
		cgl.setBlocked(false);
		return main;
		
	}
	private static void fillPhasesPanel(List<Phase> phases, JPanel phasesPnl, MovsimConfigContext mvCxt) {
		GridBagConstraints c=new GridBagConstraints();
		c.gridwidth=GridBagConstraints.REMAINDER;
		c.anchor=GridBagConstraints.NORTH;
		c.fill=GridBagConstraints.BOTH;
		c.weightx=1;
		c.insets=new Insets(5, 3, 5, 3);
		
		int i=1;
		for(Phase cc:phases){
			if(i++%2==0 && i>2)c.gridwidth=GridBagConstraints.REMAINDER;
			else c.gridwidth=1;
			JPanel p=phaseToPanel(cc,phases, mvCxt,i>2);
			p.setOpaque(false);
			p.setBorder(new TitledBorder(new LineBorder(new Color(150, 150, 150), 1, false), "Phase", TitledBorder.LEADING, TitledBorder.TOP, null, null));			
			phasesPnl.add(p,c);
		}
	}
	/**
	 * Converts {@link Phase} to {@link JPanel}
	 * @param cc {@link Phase} to be converted
	 * @param phases {@link List} of {@link Phase}s in which above referred {@link Phase} is contained
	 * @param mvCxt contains reference to loaded .xprj file and other panels added to it
	 * @param b if true {@link Phase} is removable
	 * @return {@link JPanel}
	 */
	public static JPanel phaseToPanel(Phase cc, List<Phase> phases,
			MovsimConfigContext mvCxt, boolean b) {
		JPanel main=new JPanel(new GridBagLayout());
		main.setOpaque(false);
		JPanel states=new JPanel(new GridBagLayout());
		states.setOpaque(false);
		states.setBorder(new TitledBorder(new LineBorder(new Color(150, 150, 150), 1, false), "Signal states", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		fillStatesPanel(cc,states,mvCxt);
		
		states.setBorder(new LineBorder(TDEResources.getResources().CONTROLLERS_BORDER_COLOR, 1, true));
		main.setBorder(new LineBorder(TDEResources.getResources().CONTROLLERS_BORDER_COLOR, 1, true));
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.fill=GridBagConstraints.BOTH;
		gbc.insets=new Insets(2, 5, 2, 5);
		gbc.weightx=1;
		gbc.gridwidth=GridBagConstraints.REMAINDER;

		PhaseListener cl=new PhaseListener(cc,phases, mvCxt);
		if(b){
			JButton remove=new JButton("Remove this phase",TDEResources.getResources().getRem());
			remove.addActionListener(cl);
			cl.setRemove(remove);
			main.add(remove,gbc);
		}
		gbc.gridwidth=1;
		main.add(new JLabel("Duration"),gbc);
		JTextField cbId=new JTextField(10);
		cbId.getDocument().addDocumentListener(cl);
		cl.setDuration(cbId);
		cbId.setText(cc.isSetDuration()?cc.getDuration()+"":"");
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		main.add(cbId,gbc);
		

		main.add(states,gbc);
		
//		JButton newState=new JButton("New traffic light state",TDEResources.getResources().getAddIcon());
//		newState.addActionListener(cl);
//		cl.setNewStete(newState);
//		main.add(newState,gbc);
		
		cl.setBlocked(false);
		return main;
	}
	private static void fillStatesPanel(Phase cc, JPanel states,
			MovsimConfigContext mvCxt) {
		GridBagConstraints c=new GridBagConstraints();
		c.gridwidth=GridBagConstraints.REMAINDER;
		c.anchor=GridBagConstraints.NORTH;
		c.fill=GridBagConstraints.BOTH;
		c.weightx=1;
		c.insets=new Insets(5, 3, 5, 3);
		
		int i=1;
		for(TrafficLightState st:cc.getTrafficLightState()){
			if(i++%2==0 && i>2)c.gridwidth=GridBagConstraints.REMAINDER;
			else c.gridwidth=1;
			JPanel p=stateToPanel(st,cc.getTrafficLightState(), mvCxt);
			p.setOpaque(false);
			p.setBorder(new TitledBorder(new LineBorder(new Color(150, 150, 150), 1, false), "State", TitledBorder.LEADING, TitledBorder.TOP, null, null));			
			states.add(p,c);
		}
	}
	/**
	 * Converts {@link TrafficLightState} to {@link JPanel}
	 * @param st {@link TrafficLightState} to be converted
	 * @param states {@link List} of {@link TrafficLightState}s in which above referred State is contained
	 * @param mvCxt contains reference to loaded .xprj file and other panels added to it
	 * @return {@link JPanel}
	 */
	public static JPanel stateToPanel(TrafficLightState st, List<TrafficLightState> states,
			MovsimConfigContext mvCxt) {
		JPanel main=new JPanel(new GridBagLayout());
		main.setOpaque(false);
		main.setBorder(new LineBorder(TDEResources.getResources().CONTROLLERS_BORDER_COLOR, 1, true));
		GridBagConstraints gbc=new GridBagConstraints();
		gbc.fill=GridBagConstraints.BOTH;
		gbc.insets=new Insets(2, 5, 2, 5);
		gbc.weightx=1;
		gbc.gridwidth=GridBagConstraints.REMAINDER;

		
		TrafficLightStateListener cl=new TrafficLightStateListener(st,states, mvCxt);
		
//		JButton remove=new JButton("Remove this state",TDEResources.getResources().getRem());
//		remove.addActionListener(cl);
//		cl.setRemove(remove);
//		main.add(remove,gbc);

		main.add(new JLabel("Signal: "+st.getName()),gbc);
//		ArrayList<String>sgls=new ArrayList<>();
//		for(Road r:mvCxt.getRdCxt().getRn().getOdrNetwork().getRoad()){
//			if(r.isSetSignals()){
//				for(Signal ss:r.getSignals().getSignal()){
//					sgls.add(ss.getId());
//				}
//			}
//		}
//		JComboBox<String> name=new JComboBox<String>(sgls.toArray(new String[sgls.size()]));
//		name.addActionListener(cl);
//		cl.setName(name);
//		name.setSelectedItem(st.getName());
//		gbc.gridwidth=GridBagConstraints.REMAINDER;
//		main.add(name,gbc);
		
		gbc.gridwidth=1;
		main.add(new JLabel("Status"),gbc);
		JComboBox<String> cbStatus=new JComboBox<String>(new String[]{"Red","RedGreen","Green","GreenRed"});
		cbStatus.addActionListener(cl);
		cbStatus.setSelectedItem(st.getStatus().value());
		cl.setCbStatus(cbStatus);
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		main.add(cbStatus,gbc);
		
		gbc.gridwidth=1;
		main.add(new JLabel("Condition"),gbc);
		JComboBox<String> cbCondition=new JComboBox<String>(new String[]{"none","clear","request"});
		cbCondition.addActionListener(cl);
		cbCondition.setSelectedItem(st.getCondition().value());
		cl.setCbCondition(cbCondition);
		gbc.gridwidth=GridBagConstraints.REMAINDER;
		main.add(cbCondition,gbc);
		
		cl.setBlocked(false);
		return main;
	}
	public void reset() {
		cntPnl.removeAll();
	}
	/**
	 * used to get related {@link ControllerGroup} provided a {@link Control}
	 * @param c {@link Controller} of which {@link ControllerGroup} is to be found
	 * @param mvCxt contains reference to loaded .xprj file and other panels added to it
	 * @return {@link ControllerGroup}
	 */
	public static ControllerGroup getControllerGroup(Controller c,MovsimConfigContext mvCxt){
		if(mvCxt.getMovsim().getScenario().isSetTrafficLights()){
			for(ControllerGroup cg:mvCxt.getMovsim().getScenario().getTrafficLights().getControllerGroup()){
				if(cg.getId().equals(c.getId()))return cg;
			}
		}
		return null;
	}
	/**
	 * used to get {@link Controller} provided id of the {@link Controller}
	 * @param id id of {@link Controller}
	 * @param rdCxt contains reference to loaded .xodr file and other panels added to it
	 * @return {@link Controller}
	 */
	public static Controller getController(String id,RoadContext rdCxt){
		if(rdCxt.getMvCxt().getMovsim().getScenario().isSetTrafficLights()){
			for(Controller cg:rdCxt.getRn().getOdrNetwork().getController()){
				if(cg.getId().equals(id))return cg;
			}
		}
		return null;
	}
	public ControllerPanel getControllerPanel() {
		return controllerPanel;
	}
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		GraphicsHelper.drawGradientBackground(g,getWidth(),getHeight());
	}
}
