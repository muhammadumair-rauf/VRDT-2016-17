package org.tde.tdescenariodeveloper.updation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.movsim.network.autogen.opendrive.OpenDRIVE;
import org.movsim.network.autogen.opendrive.OpenDRIVE.Junction;
import org.movsim.network.autogen.opendrive.OpenDRIVE.Junction.Connection;
import org.movsim.network.autogen.opendrive.OpenDRIVE.Road.Link;
import org.movsim.network.autogen.opendrive.OpenDRIVE.Road.Link.Predecessor;
import org.movsim.network.autogen.opendrive.OpenDRIVE.Road.Link.Successor;
import org.movsim.network.autogen.opendrive.OpenDRIVE.Road;
import org.movsim.simulator.roadnetwork.RoadSegment;
import org.tde.tdescenariodeveloper.eventhandling.JunctionsListener;
import org.tde.tdescenariodeveloper.exception.InvalidInputException;
import org.tde.tdescenariodeveloper.ui.JunctionsPanel;
import org.tde.tdescenariodeveloper.ui.RoadContext;
import org.tde.tdescenariodeveloper.utils.GraphicsHelper;
import org.tde.tdescenariodeveloper.utils.RoadNetworkUtils;
import org.tde.tdescenariodeveloper.validation.JunctionsValidator;
/**
 * This class is used to update data of a junction
 * @author Shmeel
 * @see JunctionsPanel
 * @see JunctionsValidator
 * @see Junction
 * @see JunctionsListener
 */
public class JunctionsUpdater {
	RoadContext rdCxt;
	JunctionsValidator validator;
	/**
	 * 
	 * @param rd contains reference to loaded .xodr file and other panels added to it
	 */
	public JunctionsUpdater(RoadContext rd) {
		this.rdCxt=rd;
		validator=new JunctionsValidator(rd);
	}
	/**
	 * add new junction with default values
	 * @return
	 */
	public Junction addNewJunc() {
		int id=getNextId(rdCxt);
		Junction j=new Junction();
		j.setId(id+"");
		j.setName("");
		rdCxt.getRn().getOdrNetwork().getJunction().add(j);
		RoadNetworkUtils.refresh(rdCxt);
		return j;
	}
	/**
	 * used to remove a junction
	 */
	public void removeJunc() {
		String id=rdCxt.getAppFrame().getJp().getSelectedJn();
		rdCxt.getAppFrame().getJp();
		rdCxt.getAppFrame().getJp();
		Junction j=getJunction(id, rdCxt);
		if(rdCxt.getRn().getOdrNetwork().getJunction().remove(j)){
			rdCxt.getAppFrame().getJp().getCbSelectJunc().removeItem(id+"");
			for(OpenDRIVE.Junction.Connection c:j.getConnection()){
				JunctionsUpdater.removeConnectionFromJunction(rdCxt, j, c);
			}
			RoadNetworkUtils.refresh(rdCxt);
		}
		if(rdCxt.getRn().getOdrNetwork().getJunction()==null || rdCxt.getRn().getOdrNetwork().getJunction().size()<1){
			rdCxt.getAppFrame().getJp().getCbSelectJunc().removeAllItems();
		}
	}
	/**
	 * Used to add new {@link Connection} to selected {@link Junction}
	 */
	public void addNewConn() {
		String id=rdCxt.getAppFrame().getJp().getSelectedJn();
		rdCxt.getAppFrame().getJp();
		Junction j=getJunction(id, rdCxt);
		
		Connection cn=new Connection();
		cn.setId(getNextId(j)+"");
		String[]vls=GraphicsHelper.valuesFromUser("Enter values for new connection","Connecting road id","Incoming road id" , "Sink road id");
		if(vls[0].equals("") || vls[1].equals("") || vls[2].equals("") )return;
		
		try{
			Integer.parseInt(vls[0]);
			Integer.parseInt(vls[1]);
			if(!validator.existsRoad(vls[0]) || !validator.existsRoad(vls[1]))throw new InvalidInputException("Roads reffered don't exist");
			if(vls[1].equals(vls[0]) )throw new InvalidInputException("Connecting and incoming road should be different");
			if(!vls[2].equals(vls[1]) && !vls[2].equals(vls[0]) )throw new InvalidInputException("Sink road should be from connecting or incoming road");
			JunctionsPanel.isPredecessorJunction(rdCxt.getRn().findByUserId(vls[0]).getOdrRoad());
			cn.setConnectingRoad(vls[0]);
			cn.setIncomingRoad(vls[1]);
			cn.setContactPoint("start");
			j.getConnection().add(cn);
			RoadNetworkUtils.refresh(rdCxt);
			rdCxt.getAppFrame().getJp().setSelectedJn(id+"");
			Road connectingRoad = rdCxt.getRn().findByUserId(vls[0]).getOdrRoad();
			Road incomingRoad = rdCxt.getRn().findByUserId(vls[1]).getOdrRoad();
			incomingRoad.setJunction(id);
			if(!connectingRoad.isSetLink())connectingRoad.setLink(new Link());
			if(!incomingRoad.isSetLink())incomingRoad.setLink(new Link());
			if ( vls[2].equals(vls[0]))
			{
				incomingRoad.getLink().setSuccessor(new Successor());
				incomingRoad.getLink().getSuccessor().setElementType("junction");
				incomingRoad.getLink().getSuccessor().setElementId(id);
				incomingRoad.getLink().getSuccessor().setContactPoint("start");
			    connectingRoad.getLink().setPredecessor(new Predecessor());
			    connectingRoad.getLink().getPredecessor().setElementType("junction");
			    connectingRoad.getLink().getPredecessor().setContactPoint("end");
			    connectingRoad.getLink().getPredecessor().setElementId(id);
			}else
			{
				connectingRoad.getLink().setSuccessor(new Successor());
				connectingRoad.getLink().getSuccessor().setElementType("junction");
				connectingRoad.getLink().getSuccessor().setElementId(id);
				connectingRoad.getLink().getSuccessor().setContactPoint("start");
				incomingRoad.getLink().setPredecessor(new Predecessor());
				incomingRoad.getLink().getPredecessor().setElementType("junction");
				incomingRoad.getLink().getPredecessor().setContactPoint("end");
				incomingRoad.getLink().getPredecessor().setElementId(id);
			}
		}catch(NumberFormatException e){
			GraphicsHelper.showToast(e.getMessage(), rdCxt.getToastDurationMilis());
		}catch(IllegalArgumentException e2){
			GraphicsHelper.showToast(e2.getMessage(), rdCxt.getToastDurationMilis());
		}
	}
	/**
	 * used to get id of connection 1 greater than id of last {@link Connection}
	 * @param j  {@link Junction} under consideration
	 * @return id to be used for next {@link Connection}
	 */
	public static int getNextId(Junction j){
		Connection max=null;
		int cnid=0;
		if(j.getConnection().size()>0){
			max=Collections.max(j.getConnection(),new Comparator<Connection>() {
				@Override
				public int compare(Connection o1, Connection o2) {
					return Integer.parseInt(o1.getId())-Integer.parseInt(o2.getId());
				}
			});
			cnid=Integer.parseInt(max.getId())+1;
		}
		return cnid;
	}
	/**
	 * used to get id of next {@link Junction}
	 * @param rdCxt contains reference to loaded .xodr file and other panels added to it
	 * @return id 1 greater than id of last {@link Junction}
	 */
	public static int getNextId(RoadContext rdCxt){
		int id=1001;
		if(rdCxt.getRn().getOdrNetwork().getJunction().size()>0){
			Junction max=Collections.max(rdCxt.getRn().getOdrNetwork().getJunction(),new Comparator<Junction>() {
				@Override
				public int compare(Junction o1, Junction o2) {
					return Integer.parseInt(o1.getId())-Integer.parseInt(o2.getId());
				}
			});
			int id2=Integer.parseInt(max.getId())+1;
			if(id<=id2)id=id2;
		}
		return id;
	}
	/**
	 * used to clear {@link Junction} information from a given {@link Road}
	 * @param rd {@link Road}
	 * @param rdCxt contains reference to loaded .xodr file and other panels added to it
	 */
	public static void clearJunction(RoadSegment rd,RoadContext rdCxt){//rd is connecting road, adjusts related succ,pred
		if(!rd.getOdrRoad().getJunction().equals("-1")){
			Junction j=getJunction(rd.getOdrRoad().getJunction(), rdCxt);
			if(j!=null){
				if(j.getConnection()!=null || j.getConnection().size()>0){
					ArrayList<RoadSegment>incomingRds=JunctionsPanel.getJunctionRoadSegments("incoming", rdCxt, j);
					for(RoadSegment rs:incomingRds){
						if (rs.getOdrRoad().getLink() != null
								&& rs.getOdrRoad().getLink().isSetPredecessor()
								&& rs.getOdrRoad().getLink().getPredecessor()
										.getElementType().equals("junction")
								&& rs.getOdrRoad().getLink().getPredecessor()
										.getElementId().equals(j.getId()))
							rs.getOdrRoad().getLink().setPredecessor(null);
						if (rs.getOdrRoad().getLink() != null
								&& rs.getOdrRoad().getLink().isSetSuccessor()
								&& rs.getOdrRoad().getLink().getSuccessor()
										.getElementType().equals("junction")
								&& rs.getOdrRoad().getLink().getSuccessor()
										.getElementId().equals(j.getId()))
							rs.getOdrRoad().getLink().setSuccessor(null);
					}
				}
				if(!rdCxt.getRn().getOdrNetwork().getJunction().remove(j)){
					GraphicsHelper.showToast("Junction "+j.getId()+" couldn't be removed", rdCxt.getToastDurationMilis());
				}else
					rd.getOdrRoad().setJunction("-1");
			}else{
				GraphicsHelper.showToast("Junction "+rd.getOdrRoad().getJunction()+" referred in road "+rd.userId()+" not found!", rdCxt.getToastDurationMilis());
			}
		}
	}
	/**
	 * used to check and get a connection having given connecting {@link Road} and incoming {@link Road}
	 * @param connecting {@link Road}
	 * @param incoming {@link Road}
	 * @param prJn {@link Junction} under consideration
	 * @return
	 */
	public static Connection getConnection(String connecting, String incoming,
			Junction prJn) {
		Connection cn=null;
		for(Connection c:prJn.getConnection()){
			if(c.isSetConnectingRoad() && c.isSetConnectingRoad() && c.getIncomingRoad().equals(incoming) && c.getConnectingRoad().equals(connecting))return c;
		}
		return cn;
	}
	/**
	 * used to get junction provided id of junction
	 * @param id of {@link Junction}
	 * @param rdCxt contains reference to loaded .xodr file and other panels added to it
	 * @return {@link Junction}
	 */
	public static Junction getJunction(String id,RoadContext rdCxt) {
		try{
			for(Junction j:rdCxt.getRn().getOdrNetwork().getJunction()){
				if(id.equals(j.getId()))return j;
			}
		}catch(NullPointerException e){
			return null;
		}
		return null;
	}
	
	public static void removeConnectionFromJunction(
			org.tde.tdescenariodeveloper.ui.RoadContext rdCxt,
			org.movsim.network.autogen.opendrive.OpenDRIVE.Junction junction, 
			org.movsim.network.autogen.opendrive.OpenDRIVE.Junction.Connection connection
			){
		Road connectingRoad = rdCxt.getRn().findByUserId(connection.getConnectingRoad()).getOdrRoad();
		Road incomingRoad = rdCxt.getRn().findByUserId(connection.getIncomingRoad()).getOdrRoad();
		connectingRoad.setJunction("-1");
		connectingRoad.getLink().setSuccessor(null);
		if (!connectingRoad.getLink().isSetInclude() &&
			!connectingRoad.getLink().isSetNeighbor() &&
			!connectingRoad.getLink().isSetPredecessor() &&
			!connectingRoad.getLink().isSetSuccessor() &&
			!connectingRoad.getLink().isSetUserData()	
			)connectingRoad.setLink(null);
		incomingRoad.getLink().setPredecessor(null);
		if (!incomingRoad.getLink().isSetInclude() &&
			!incomingRoad.getLink().isSetNeighbor() &&
			!incomingRoad.getLink().isSetPredecessor() &&
			!incomingRoad.getLink().isSetSuccessor() &&
			!incomingRoad.getLink().isSetUserData()	
			)incomingRoad.setLink(null);
		for(OpenDRIVE.Junction.Connection.LaneLink laneLink: connection.getLaneLink()){
			LinkUpdater.removeLaneLinkFromJunction(rdCxt, connection, laneLink);
		}
	}	
}
