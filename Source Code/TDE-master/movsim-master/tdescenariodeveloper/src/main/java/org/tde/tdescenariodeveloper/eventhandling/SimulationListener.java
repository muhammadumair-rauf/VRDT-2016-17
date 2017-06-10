package org.tde.tdescenariodeveloper.eventhandling;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import org.movsim.autogen.Road;
import org.movsim.autogen.TrafficComposition;
import org.movsim.autogen.TrafficSource;
import org.movsim.autogen.VehicleType;
import org.tde.tdescenariodeveloper.ui.MovsimConfigContext;
import org.tde.tdescenariodeveloper.updation.Conditions;
import org.tde.tdescenariodeveloper.updation.DataToViewerConverter;
import org.tde.tdescenariodeveloper.utils.GraphicsHelper;
import org.tde.tdescenariodeveloper.utils.MovsimScenario;
/**
 * Class used to listen for changes made to simulation properties
 * @author Shmeel
 * @see Simulation
 * @see VehicleType
 * @see Road
 * @see TrafficComposition
 * @see TrafficSource
 * @see Inflow
 */
public class SimulationListener implements ItemListener, DocumentListener,ActionListener,Blockable {
	JTextField tmSt,dur,seed;
	JCheckBox crEx,withSeed;
	MovsimConfigContext mvCxt;
	JButton add,addRoad;
	TrafficComposition tc;
	List<Road>rdList;
	/**
	 * 
	 * @param mvCxt contains reference to loaded .xprj and other added panels in it
	 * @param tmSt {@link JTextField} time step
	 * @param dur  {@link JTextField} duration
	 * @param seed {@link JTextField} seed
	 * @param crEx {@link JCheckBox} crash on exit
	 * @param withSeed {@link JCheckBox} use seed 
	 * @param add {@link JButton} to add new {@link TrafficComposition}
	 * @param addRoad to add new {@link Road}
	 */
	public SimulationListener(MovsimConfigContext mvCxt,JTextField tmSt, JTextField dur, JTextField seed,
			JCheckBox crEx, JCheckBox withSeed, JButton add, JButton addRoad) {
		this.mvCxt=mvCxt;
		this.add=add;
		this.tmSt = tmSt;
		this.dur = dur;
		this.seed = seed;
		this.crEx = crEx;
		this.withSeed = withSeed;
		this.addRoad=addRoad;
	}
	private boolean blocked=true;
	@Override
	public void changedUpdate(DocumentEvent e) {
		textChanged(e);
	}
	@Override
	public void insertUpdate(DocumentEvent e) {
		textChanged(e);
	}
	@Override
	public void removeUpdate(DocumentEvent e) {
		textChanged(e);
	}
	@Override
	public void itemStateChanged(ItemEvent e) {
		if(blocked)return;
		JCheckBox src=(JCheckBox)e.getSource();
		if(src==crEx){
			mvCxt.getMovsim().getScenario().getSimulation().setCrashExit(crEx.isSelected());
		}else if(src==withSeed){
			mvCxt.getMovsim().getScenario().getSimulation().setWithSeed(withSeed.isSelected());
		}
	}
	public void textChanged(DocumentEvent e){
		if(blocked)return;
		Document d=e.getDocument();
		if(d==tmSt.getDocument()){
			if(!Conditions.isValid(tmSt, mvCxt.getMovsim().getScenario().getSimulation().getTimestep()))
				return;
			try{
				double d2=Double.parseDouble(tmSt.getText());
				GraphicsHelper.makeBlack(tmSt);
				mvCxt.getMovsim().getScenario().getSimulation().setTimestep(d2);
			}catch(NumberFormatException e2){
				GraphicsHelper.makeRed(tmSt);
			}
		}else if(d==dur.getDocument()){
			if(!Conditions.isValid(dur, mvCxt.getMovsim().getScenario().getSimulation().getDuration()))
				return;
			try{
				double d2=Double.parseDouble(dur.getText());
				GraphicsHelper.makeBlack(dur);
				mvCxt.getMovsim().getScenario().getSimulation().setDuration(d2);
			}catch(NumberFormatException e2){
				GraphicsHelper.makeRed(dur);
			}
		}else if(d==seed.getDocument()){
			if(!Conditions.isValid(seed, mvCxt.getMovsim().getScenario().getSimulation().getSeed()))
				return;
			try{
				int d2=Integer.parseInt(seed.getText());
				GraphicsHelper.makeBlack(seed);
				mvCxt.getMovsim().getScenario().getSimulation().setSeed(d2);
			}catch(NumberFormatException e2){
				GraphicsHelper.makeRed(seed);
			}
		}
	}
	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(blocked)return;
		JButton src=(JButton)e.getSource();
		if(src==add){
			String s=DataToViewerConverter.getNotUsedPrototypeLabel(mvCxt,tc);
			if(s!=null){
				VehicleType vt=MovsimScenario.getVehicleType();
				vt.setLabel(s);
				tc.getVehicleType().add(vt);
				mvCxt.updatePanels();
			}
			else GraphicsHelper.showMessage("All available vehicle prototypes are already used, make another prototype to add new vehicle type");
		}else if(src==addRoad){
			String s=DataToViewerConverter.getNotUsedRoadCustomizationId(mvCxt, rdList);
			if(s!=null){
				Road rd=new Road();
				rd.setId(s);
				if(rdList.add(rd)){
					mvCxt.updatePanels();
				}
				else
					GraphicsHelper.showToast("Road couldn't be added", mvCxt.getRdCxt().getToastDurationMilis());
			}
			else GraphicsHelper.showMessage("All roads are already customized");
		}
	}
	public void setTc(TrafficComposition tc) {
		this.tc = tc;
	}
	public void setRdList(List<Road> rdList) {
		this.rdList = rdList;
	}
}
