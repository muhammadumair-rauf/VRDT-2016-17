package org.tde.tdescenariodeveloper.eventhandling;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;

import org.movsim.autogen.Road;
import org.movsim.autogen.Route;
import org.tde.tdescenariodeveloper.ui.MovsimConfigContext;
import org.tde.tdescenariodeveloper.updation.Conditions;
import org.tde.tdescenariodeveloper.updation.DataToViewerConverter;
import org.tde.tdescenariodeveloper.utils.GraphicsHelper;
/**
 * Class used to listen for chagnes made to {@link Road} in {@link Route}
 * @author Shmeel
 * @see Route
 * @see Road
 * 
 */
public class RoutesRoadToPanelListener implements ActionListener,Blockable{
	Road r;
	MovsimConfigContext mvCxt;
	private JButton removeRoad;
	JComboBox<String>id;
	boolean blocked=true;
	Route rt;
	/**
	 * 
	 * @param mvCxt contains reference to loaded .xprj and other added panels in it
	 * @param r {@link Road} to which this listener is attached
	 * @param rt {@link Route} in which this road is contained
	 */
	public RoutesRoadToPanelListener(MovsimConfigContext mvCxt,Road r,Route rt) {
		this.r=r;
		this.mvCxt=mvCxt;
		this.rt=rt;
	}
	@Override
	public void actionPerformed(ActionEvent e){
		if(blocked)return;
		JButton b=null;
		JComboBox<String>cb=null;
		if(e.getSource() instanceof JButton)b=(JButton)e.getSource();
		if(e.getSource() instanceof JComboBox<?>)cb=(JComboBox<String>)e.getSource();
		if(b==removeRoad){
			if(!rt.getRoad().remove(r)){
				GraphicsHelper.showToast("Couldn't be removed", mvCxt.getRdCxt().getToastDurationMilis());
			}else mvCxt.updatePanels();
		}else if(cb==id){
			String i=(String)id.getSelectedItem();
			if(Conditions.existsIdInRoads(i, mvCxt)){
				if( !DataToViewerConverter.getusedRoadCustomizations(rt.getRoad()).contains(i))
				{
					if ( rt.getRoad().size() ==1 )
						r.setId(i);
					else
					{
						boolean errorFlag = true;
						List<Road> roads = rt.getRoad();
						int index = 0;
						for( Road road:roads )
						{
							index++;
							if ( road.getId().equals(r.getId()) )
							{
								List<String> s ;
								if (index == 1)   // if we are changing first road 
								{
									s =DataToViewerConverter.getSuccessorRoadId(mvCxt, rt.getRoad().get(index-1));
								}
								else{
									s =DataToViewerConverter.getSuccessorRoadId(mvCxt, rt.getRoad().get(index-2));
								}
								if (s.size() > 0 )
								{	
									for (String roadSc:s )
									{
										if ( roadSc.equals(i) )
										{
											if ( rt.getRoad().size() > index && index !=1 )
											{
												Road nextRoad = rt.getRoad().get(index);
												if ( DataToViewerConverter.getPredecessorRoadId(mvCxt, nextRoad, i) )
												{
													r.setId(i);
													errorFlag = false;
													break;
															
												}
												
											}
											else
											{
												r.setId(i);
												errorFlag = false;
												break;
											}
										
										}
									
									}
									break;
								}
								break;
							}
							
						}
						
						if (errorFlag)
						{
							GraphicsHelper.showToast("Selected road doesn't have a suitable link with other roads in this route", mvCxt.getRdCxt().getToastDurationMilis());
							id.setSelectedItem((String)r.getId());
						}
						
						
					}
				
				}
				else {
					GraphicsHelper.showToast("Selected road already in this route", mvCxt.getRdCxt().getToastDurationMilis());
					id.setSelectedItem((String)r.getId());
				}
			}else GraphicsHelper.showToast("Selected road doesn't exist", mvCxt.getRdCxt().getToastDurationMilis()); 
		}
	}
	public void setRemoveRoad(JButton removeRoad) {
		this.removeRoad = removeRoad;
	}
	public void setId(JComboBox<String> id) {
		this.id = id;
	}
	public void setBlocked(boolean blocked) {
		this.blocked = blocked;
	}
}
