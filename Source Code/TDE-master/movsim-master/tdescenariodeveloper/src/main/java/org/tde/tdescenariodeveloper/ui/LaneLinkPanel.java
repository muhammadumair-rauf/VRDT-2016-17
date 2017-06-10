package org.tde.tdescenariodeveloper.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.movsim.network.autogen.opendrive.Lane;
import org.movsim.network.autogen.opendrive.Lane.Link;
import org.movsim.network.autogen.opendrive.OpenDRIVE;
import org.movsim.network.autogen.opendrive.OpenDRIVE.Junction;
import org.movsim.network.autogen.opendrive.OpenDRIVE.Junction.Connection;
import org.movsim.network.autogen.opendrive.OpenDRIVE.Junction.Connection.LaneLink;
import org.movsim.network.autogen.opendrive.OpenDRIVE.Road;
import org.movsim.simulator.roadnetwork.RoadSegment;
import org.tde.tdescenariodeveloper.eventhandling.LaneLinkListener;
import org.tde.tdescenariodeveloper.utils.GraphicsHelper;
/**
 * Class to hold lane links panels
 * @author Shmeel
 * @see Lane
 * @see Link
 * @see LaneLink
 * @see LaneLinkPanel
 * @see LaneLinkListener
 */
public class LaneLinkPanel extends JPanel {
	private static final long serialVersionUID = 76435681L;
	LanesPanel lnPnl;
	private JComboBox<String> cbElementId;
	private JComboBox<String> cbSelectLink;
	GridBagConstraints c,gbc_lbl,gbc_tf;
	Junction jn;
	JPanel linkInfoPnl;
	boolean showFullJunc=true;
	RoadContext rdCxt;
	/**
	 * 
	 * @param rpp contains reference to loaded .xodr file and other panels added to it
	 */
	
	public LaneLinkPanel(RoadContext rpp) {
		rdCxt=rpp;
		linkInfoPnl=new JPanel(new GridBagLayout());
		linkInfoPnl.setOpaque(false);
		setLayout(new GridBagLayout());
		setBorder(new TitledBorder(new LineBorder(new Color(150, 150, 150), 1, false), "Lane link", TitledBorder.LEADING, TitledBorder.ABOVE_TOP, null, null));
		
		cbSelectLink = new JComboBox<>();
		cbSelectLink.setToolTipText("Predecessor/Sucsessor lane");
		cbSelectLink.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		
		c=new GridBagConstraints();
		c.gridwidth=GridBagConstraints.REMAINDER;
		c.anchor=GridBagConstraints.NORTHWEST;
		c.fill=GridBagConstraints.BOTH;
		c.weightx=1;
		gbc_lbl = new GridBagConstraints();
		Insets ins=new Insets(5,5,5,5);
		gbc_lbl.insets = ins;
		gbc_lbl.weightx=2;
		gbc_lbl.anchor=GridBagConstraints.NORTHWEST;
		gbc_lbl.fill=GridBagConstraints.BOTH;
		
		gbc_tf = new GridBagConstraints();
		gbc_tf.insets = ins;
		gbc_tf.fill = GridBagConstraints.BOTH;
		gbc_tf.weightx=3;
		gbc_tf.gridwidth=GridBagConstraints.REMAINDER;
		
		
		
		JLabel lblElementId = new JLabel("id");
		
		cbElementId = new JComboBox<>();
		lblElementId.setLabelFor(cbElementId);
		cbElementId.setToolTipText("Id of predecessor/successor lane");
		cbElementId.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		add(cbSelectLink,c);
		add(linkInfoPnl,c);
	}
	/**
	 * Updates link panels from memory
	 */
	public void updateLinkPanel() {
		linkInfoPnl.removeAll();
		cbElementId.removeAllItems();
		cbSelectLink.removeAllItems();
		cbSelectLink.addItem("Predecessor");
		cbSelectLink.addItem("Successor");
		if (lnPnl.getRdPrPnl().getSelectedRoad().getOdrRoad().isSetLink() && lnPnl.getRdPrPnl().getSelectedRoad().getOdrRoad().getLink().isSetPredecessor()){
			String preid=lnPnl.getRdPrPnl().getSelectedRoad().getOdrRoad().getLink().getPredecessor().getElementId();
			String pretype=lnPnl.getRdPrPnl().getSelectedRoad().getOdrRoad().getLink().getPredecessor().getElementType();
			switch(pretype){
			case "road":
				for(Road r:rdCxt.getRn().getOdrNetwork().getRoad()){
					if(r.getId().equals(preid)){
						for(Lane ln:r.getLanes().getLaneSection().get(0).getRight().getLane()){
							cbElementId.addItem(ln.getId()+"");
						}
						break;
					}
				}
				setLinkFields("Predecessor",false);
				break;
			case "junction":
				for(Junction j:rdCxt.getRn().getOdrNetwork().getJunction()){
					if(preid.equals(j.getId()+"")){
						jn=j;
						break;
					}
				}
				if(jn==null)throw new NullPointerException("Junction not found");
				setLinkFields("Predecessor",true);
				break;
			}
			
		}else{
			if(!lnPnl.getRdPrPnl().getSelectedRoad().getOdrRoad().isSetLink() || !lnPnl.getRdPrPnl().getSelectedRoad().getOdrRoad().getLink().isSetSuccessor())return;
			String sucid=lnPnl.getRdPrPnl().getSelectedRoad().getOdrRoad().getLink().getSuccessor().getElementId();
			String suctype=lnPnl.getRdPrPnl().getSelectedRoad().getOdrRoad().getLink().getSuccessor().getElementType();
			switch(suctype){
			case "road":
				for(Road r:rdCxt.getRn().getOdrNetwork().getRoad()){
					if(r.getId().equals(sucid)){
						for(Lane ln:r.getLanes().getLaneSection().get(0).getRight().getLane()){
							cbElementId.addItem(ln.getId()+"");
						}
						break;
					}
				}
				setLinkFields("Successor",false);
				break;
			case "junction":
				for(Junction j:rdCxt.getRn().getOdrNetwork().getJunction()){
					if(sucid.equals(j.getId()+"")){
						jn=j;
						break;
					}
				}
				if(jn==null)throw new NullPointerException("Junction not found");
				setLinkFields("Successor",true);
				break;
			}
		}
	}
	private void setLinkFields(String linkType, boolean isJunction) {
		ajdustLinkInfoPnl(isJunction);
	}
/**
 * 
 * @param isJunction used to update link panel if lane is connected to a junction
 */
	public void ajdustLinkInfoPnl(boolean isJunction) {
		if(isJunction){
			ArrayList<String>conn=new ArrayList<>();
			ArrayList<String>incom=new ArrayList<>();
			for(Connection cn:jn.getConnection()){
				putOrReject(conn, cn.getConnectingRoad());
				putOrReject(incom, cn.getIncomingRoad());
			}
			String[]connecting=new String[conn.size()];
			String[]incoming=new String[incom.size()];
			for(int i=0;i<conn.size();i++){
				connecting[i]=conn.get(i);
			}
			for(int i=0;i<incom.size();i++){
				incoming[i]=incom.get(i);
			}
			JPanel lables=new JPanel(new GridLayout(1,4,1,1));
			lables.setOpaque(false);
			lables.add(new JLabel(" ID"));
			lables.add(new JLabel(" Conn. road"));
			lables.add(new JLabel(" In. road"));
			lables.add(new JLabel(" Cont. Point"));
			c.insets=new Insets(2,2,2,2);
			linkInfoPnl.add(new JLabel("Junction: "+jn.getId()),c);
			
			linkInfoPnl.add(lables,c);
			for(Connection cn:jn.getConnection()){
				if(showFullJunc)linkInfoPnl.add(conToPnl(cn, connecting, incoming),c);
				else {
					for(LaneLink ll:cn.getLaneLink()){
						if(ll.getTo()==lnPnl.getRdPrPnl().getSelectedRoad().getOdrRoad().getLanes().getLaneSection().get(0).getRight().getLane().get(lnPnl.getSelectedIndex()).getId()){
							linkInfoPnl.add(conToPnl(cn, connecting, incoming),c);
						}
					}
				}
			}
		}else{
			linkInfoPnl.add(new JLabel("Id"),gbc_lbl);
			linkInfoPnl.add(cbElementId,gbc_tf);
		}
		
	}
	/**
	 * Used to fill an {@link ArrayList} rejecting elements already added
	 * @param al {@link ArrayList}
	 * @param key any {@link Object}
	 * @return true if object is added false otherwise
	 */
	public static <T> boolean putOrReject(ArrayList<T>al,T key){
		boolean exists=false;
		for(T s:al){
			if(s.equals(key)){
				exists=true;
				break;
			}
		}
		if(!exists)al.add(key);
		return exists;
	}
	/**
	 * sets parent {@link LanesPanel}
	 * @param lanesPanel
	 */
	public void setLanePanel(LanesPanel lanesPanel) {
		lnPnl=lanesPanel;
	}
	/**
	 * used to convert {@link Connection} to {@link JPanel}
	 * @param cn {@link Connection} to be converted
	 * @param conn ids of connecting {@link Road}s
	 * @param incom ids of incoming {@link Road}s
	 * @return {@link JPanel} representing above referred {@link Connection}
	 */
	public JPanel conToPnl(Connection cn,String[]conn,String[]incom){
		JPanel p=new JPanel(new GridBagLayout());
		p.setOpaque(false);
		p.setBorder(BorderFactory.createLineBorder(new Color(150,150,150)));
		JTextField id=new JTextField(5);
		id.setText(cn.getId());
		
		JComboBox<String>connecting=new JComboBox<String>(conn);
		JComboBox<String>incoming=new JComboBox<String>(incom);
		JComboBox<String>contactPnt=new JComboBox<String>(new String[]{"start","end"});
		
		connecting.setSelectedItem(cn.getConnectingRoad());
		incoming.setSelectedItem(cn.getIncomingRoad());
		contactPnt.setSelectedItem(cn.getContactPoint());
		GridBagConstraints gbc_lbl=new GridBagConstraints();
		gbc_lbl.weightx=1;
		gbc_lbl.fill=GridBagConstraints.BOTH;
		gbc_lbl.insets=new Insets(2, 2, 2, 2);
		gbc_lbl.anchor=GridBagConstraints.NORTHWEST;
		p.add(id,gbc_lbl);
		p.add(connecting,gbc_lbl);
		p.add(incoming,gbc_lbl);
		gbc_lbl.gridwidth=GridBagConstraints.REMAINDER;
		p.add(contactPnt,gbc_lbl);
		Road toRoad=getRoad(Integer.parseInt(cn.getConnectingRoad()));
		Road fromRoad=getRoad(Integer.parseInt(cn.getIncomingRoad()));
		for(LaneLink ll:cn.getLaneLink()){
			if(!showFullJunc)if(ll.getTo()!=lnPnl.getRdPrPnl().getSelectedRoad().getOdrRoad().getLanes().getLaneSection().get(0).getRight().getLane().get(lnPnl.getSelectedIndex()).getId())continue;
			JComboBox<String>from=new JComboBox<String>();
			JComboBox<String>to=new JComboBox<String>();
			for(Lane ln:toRoad.getLanes().getLaneSection().get(0).getRight().getLane()){
				to.addItem(ln.getId()+"");
			}
			to.setSelectedItem(ll.getTo()+"");
			for(Lane ln:fromRoad.getLanes().getLaneSection().get(0).getRight().getLane()){
				from.addItem(ln.getId()+"");
			}
			from.setSelectedItem(ll.getFrom()+"");
			
			gbc_lbl.gridwidth=1;
			p.add(new JLabel("From"),gbc_lbl);

			p.add(from,gbc_lbl);
			p.add(new JLabel("To"),gbc_lbl);
			
			gbc_lbl.gridwidth=GridBagConstraints.REMAINDER;
			p.add(to,gbc_lbl);
		}
		return p;
	}
	/**
	 * used to get {@link Road} provided id of the road
	 * @param id id of {@link Road}
	 * @return returns {@link Road}
	 */
	public Road getRoad(int id){
		for(RoadSegment rs:rdCxt.getRn()){
			if(id==Integer.parseInt(rs.getOdrRoad().getId())){
				return rs.getOdrRoad();
			}
		}
		return null;
	}
	/**
	 * resets all panels
	 */
	public void reset() {
		cbSelectLink.removeAllItems();
		linkInfoPnl.removeAll();
	}
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		GraphicsHelper.drawGradientBackground(g,getWidth(),getHeight());
	}
}
