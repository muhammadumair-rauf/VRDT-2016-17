package org.tde.tdescenariodeveloper.eventhandling;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import javax.swing.JButton;

import org.movsim.autogen.ControllerGroup;
import org.movsim.autogen.Phase;
import org.movsim.autogen.TrafficLights;
import org.movsim.network.autogen.opendrive.OpenDRIVE.Controller;
import org.movsim.network.autogen.opendrive.OpenDRIVE.Road.Signals;
import org.movsim.network.autogen.opendrive.OpenDRIVE.Road.Signals.Signal;
import org.tde.tdescenariodeveloper.ui.RoadContext;
import org.tde.tdescenariodeveloper.utils.RoadNetworkUtils;
/**
 * Class used to add new Controller
 * @author Shmeel
 *
 */
public class ControllerPanelListener implements ActionListener,Blockable{
	boolean blocked=true;
	RoadContext rdCxt;
	/**
	 * 
	 * @param rdCxt contains reference to loaded .xodr and other added panels in it
	 */
	public ControllerPanelListener(RoadContext rdCxt) {
		this.rdCxt=rdCxt;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(blocked)return;
		JButton src=null;
		if(e.getSource() instanceof JButton)src=(JButton)e.getSource();
//		if(src==rdCxt.getMvCxt().getTrafficLights().getControllerPanel().getAddNew()){
			Controller c=new Controller();
			ControllerGroup cg=new ControllerGroup();
			String id=getUniqueId(rdCxt,"plan");
			c.setId(id);
			cg.setId(id);
			Phase p=new Phase();
			p.setDuration(30);
			cg.getPhase().add(p);
			rdCxt.getRn().getOdrNetwork().getController().add(c);
			if(!rdCxt.getMvCxt().getMovsim().getScenario().isSetTrafficLights())
				rdCxt.getMvCxt().getMovsim().getScenario().setTrafficLights(new TrafficLights());
			rdCxt.getMvCxt().getMovsim().getScenario().getTrafficLights().getControllerGroup().add(cg);
			RoadNetworkUtils.syncTrafficPanels(rdCxt.getMvCxt());
//		}
	}
	/**
	 * used to generate controller id not used yet. 
	 * @param rdCxt contains reference to loaded .xodr and other added panels in it
	 * @param prefix string used as prefix while generating unique id
	 * @return return unique id
	 */
	public static String getUniqueId(RoadContext rdCxt,String prefix){
		String id=prefix+"-"+((int)(Math.random()*1000));
		HashSet<String>old=new HashSet<>();
		for(Controller c:rdCxt.getRn().getOdrNetwork().getController()){
			old.add(c.getId());
		}
		while(old.contains(id)){
			id=prefix+"-"+((int)(Math.random()*1000));
		}
		return id;
	}
	@Override
	public void setBlocked(boolean b) {
		blocked=b;
	}
	/**
	 * used to get id not used in signals
	 * @param {@link Signals} tag
	 * @return
	 */
	public static String getNextId(Signals signals){
		int id=0;
		if(signals.getSignal().size()>0){
			Signal max=Collections.max(signals.getSignal(), new Comparator<Signal>() {

				@Override
				public int compare(Signal o1, Signal o2) {
					return Integer.parseInt(o1.getId())-Integer.parseInt(o2.getId());
				}
			});
			id=Integer.parseInt(max.getId())+1;
		}
		return id+"";
	}
}
