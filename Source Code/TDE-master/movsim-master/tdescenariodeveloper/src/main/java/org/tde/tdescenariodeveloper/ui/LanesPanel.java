package org.tde.tdescenariodeveloper.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.movsim.network.autogen.opendrive.Lane;
import org.movsim.network.autogen.opendrive.OpenDRIVE.Road;
import org.tde.tdescenariodeveloper.eventhandling.LanesPanelListener;
import org.tde.tdescenariodeveloper.exception.LaneException;
import org.tde.tdescenariodeveloper.utils.GraphicsHelper;
/**
 * Class used hold information of lanes of selected {@link Road}
 * @author Shmeel
 * @see Road
 * @see LanesPanelListener
 * @see Lane
 * @see LaneLinkPanel
 * @see LaneException
 */
public class LanesPanel extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8013762005282847367L;
	JTextField tfwidth,maxSpeed;
	JLabel level,posLbl;
	JComboBox<String>cbLanes,cbtype;
	JButton add,remove;
	LaneLinkPanel lnLinkPnl;
	private int lnInd=0;
	RoadContext rdCxt;
	JSlider position;
	/**
	 * 
	 * @param rpp contains reference to loaded .xodr file and other panels added to it
	 * @param lpl {@link LanesPanelListener} listener attached to this {@link LanesPanel}
	 */
	public LanesPanel(RoadContext rpp,LanesPanelListener lpl) {
		rdCxt=rpp;
		lnLinkPnl=new LaneLinkPanel(rdCxt);
		lnLinkPnl.setLanePanel(this);
		setLayout(new GridBagLayout());
		Insets ins=new Insets(5,5,5,5);
		GridBagConstraints gbc_lbl = new GridBagConstraints();
		gbc_lbl.insets = ins;
		gbc_lbl.anchor=GridBagConstraints.NORTHWEST;
		gbc_lbl.fill=GridBagConstraints.BOTH;
		gbc_lbl.weightx=1;
		add=new JButton("Add new",TDEResources.getResources().getAddIcon());
		add.addActionListener(lpl);
		remove=new JButton("Remove",TDEResources.getResources().getRem());
		remove.addActionListener(lpl);
		add(add,gbc_lbl);
		gbc_lbl.weightx=2;
		
		GridBagConstraints gbc_tf = new GridBagConstraints();
		gbc_tf.insets = ins;
		gbc_tf.fill = GridBagConstraints.BOTH;
		gbc_tf.gridwidth=GridBagConstraints.REMAINDER;
		gbc_tf.weightx=1;
		add(remove,gbc_tf);
		gbc_tf.weightx=3;
		
		
		JPanel tmp=new JPanel(new GridBagLayout());
		tmp.setOpaque(false);
		tmp.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		tfwidth=new JTextField(10);
//		tfwidth.setHighlighter(null);
		tfwidth.getDocument().addDocumentListener(lpl);
		JLabel lbl=new JLabel("Lane width");
		lbl.setLabelFor(tfwidth);
		tmp.add(lbl,gbc_lbl);
		tmp.add(tfwidth,gbc_tf);
		add(tmp,gbc_tf);
		
		cbLanes=new JComboBox<String>();
		cbLanes.addActionListener(lpl);
		lbl=new JLabel("Select lane (Id)");
		lbl.setLabelFor(cbLanes);
		add(lbl,gbc_lbl);
		add(cbLanes,gbc_tf);
		
		cbtype=new JComboBox<>();
		cbtype.addActionListener(lpl);
		lbl=new JLabel("Type");
		lbl.setLabelFor(cbtype);
		add(lbl,gbc_lbl);
		add(cbtype,gbc_tf);
		
		level=new JLabel();
		lbl=new JLabel("Level");
		lbl.setLabelFor(level);
		add(lbl,gbc_lbl);
		add(level,gbc_tf);
		

		
		maxSpeed=new JTextField(10);
//		maxSpeed.setHighlighter(null);
		maxSpeed.getDocument().addDocumentListener(lpl);
		lbl=new JLabel("Max speed");
		lbl.setLabelFor(maxSpeed);
		add(lbl,gbc_lbl);
		add(maxSpeed,gbc_tf);
		
		position=new JSlider();
		posLbl=new JLabel("Max speed position");
		posLbl.setLabelFor(position);
		position.setToolTipText("Set position of speed limit sign");
		position.addChangeListener(lpl);
		position.setPreferredSize(new Dimension(100,30));
		add(posLbl,gbc_lbl);
		add(position,gbc_tf);
	}
	/**
	 * used to update lanes' panels from memory
	 */
	public void updatelanesPanel() {
		if(rdCxt.getSelectedRoad()==null)return;
		setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 1, true),"Lane"+(rdCxt.getSelectedRoad().getOdrRoad().getLanes().getLaneSection().get(0).getRight().getLane().size()>1?"s ("+rdCxt.getSelectedRoad().getOdrRoad().getLanes().getLaneSection().get(0).getRight().getLane().size()+")":"") , TitledBorder.LEADING, TitledBorder.TOP, null, null));
		cbLanes.removeAllItems();
		tfwidth.setText(getOdrLanes().get(0).getWidth().get(0).getA()+"");
		for(Lane ln:rdCxt.getSelectedRoad().getOdrRoad().getLanes().getLaneSection().get(0).getRight().getLane())
			cbLanes.addItem(ln.getId()+"");
		cbLanes.setSelectedIndex(lnInd);
		laneChanged();
	}
	/**
	 * used to be notifed if lane selection changes
	 */
	public void laneChanged() {
		cbtype.removeAllItems();
		cbtype.addItem("driving");
		cbtype.addItem("mwyEntry");
		cbtype.addItem("mwyExit");
		remove.setEnabled(getOdrLanes().size()>1);
		if(rdCxt.getLanesPnl().getSelectedLane().getSpeed()!=null && rdCxt.getLanesPnl().getSelectedLane().getSpeed().size()>0){
			position.setMinimum(0);
			position.setMaximum((int)rdCxt.getSelectedRoad().getOdrRoad().getLength());
			position.setValue((int)rdCxt.getLanesPnl().getSelectedLane().getSpeed().get(0).getSOffset());
			position.setVisible(true);
			posLbl.setVisible(true);
		} else{
			position.setVisible(false);
			posLbl.setVisible(false);
		}
		Lane ln=rdCxt.getSelectedRoad().getOdrRoad().getLanes().getLaneSection().get(0).getRight().getLane().get(lnInd);
		cbtype.setSelectedItem(ln.getType()+"");
		level.setText(ln.getLevel()+"");
		maxSpeed.setText(ln.getSpeed()!=null && ln.getSpeed().size()>0?ln.getSpeed().get(0).getMax()+"":"");
		if(ln.getLink()!=null){
			if(!isAdded(lnLinkPnl)){
				GridBagConstraints gbc_tf = new GridBagConstraints();
				gbc_tf.fill = GridBagConstraints.BOTH;
				gbc_tf.gridwidth=GridBagConstraints.REMAINDER;
				add(lnLinkPnl,gbc_tf);
			}
			lnLinkPnl.updateLinkPanel();
		}else{
			if(isAdded(lnLinkPnl))remove(lnLinkPnl);
		}
		rdCxt.updateGraphics();
		
	}
	/**
	 * Used to get selected odr lane
	 * @return reference to {@link Lane}
	 */
	public Lane getSelectedLane(){
		return getOdrLanes().get(lnInd);
	}
	/**
	 * used to get {@link List} lanes of selected {@link Road}
	 * @return
	 */
	public List<Lane> getOdrLanes(){
		return rdCxt.getSelectedRoad().getOdrRoad().getLanes().getLaneSection().get(0).getRight().getLane();
	}
	/**
	 * used to check if Component is added to this {@link LanesPanel}
	 * @param c Components to be checked
	 * @return true if components is added false otherwise
	 */
	public boolean isAdded(Component c){
		return (LanesPanel)c.getParent()==this;
	}
	/**
	 * sets index of the selected lane
	 * @param ind index of lane
	 * @param update if true {@link LanesPanel} will be updated from memory
	 */
	public void setSelectedLane(int ind,boolean update) {
//		try{
//			if(rdCxt.getSelectedRoad()==null)
//				throw new NullPointerException("setSelectedLane: selectedroad is null");
//			else if(ind+1>getOdrLanes().size() || ind<0)throw new LaneException("Lane index out of bounds");
//			else if(ind+1<cbLanes.getItemCount())
//				throw new IllegalSelectorException();
			lnInd=ind;
			if(update)cbLanes.setSelectedIndex(ind);
			
//		}catch(NullPointerException e){
//			GraphicsHelper.showToast(e.getMessage(), rdCxt.getToastDurationMilis());
//		}
//		catch(LaneException e){
//			GraphicsHelper.showToast(e.getMessage(), rdCxt.getToastDurationMilis());
//			lnInd=0;
//			setSelectedLane(ind, update);
//		}
//		catch(IllegalSelectorException e){
//			GraphicsHelper.showToast("Item doesn't exit in list", rdCxt.getToastDurationMilis());
//		}
	}
	/**
	 * used to set index of selected {@link Lane} and to update
	 * @param ind index
	 */
	public void setSelectedLane(int ind) {
		setSelectedLane(ind,true);
	}
	public RoadContext getRdPrPnl() {
		return rdCxt;
	}
	public int getSelectedIndex() {
		return lnInd;
	}
	public JComboBox<String> getCbLanes() {
		return cbLanes;
	}
	public JComboBox<String> getCbtype() {
		return cbtype;
	}
	public JTextField getTfWidth() {
		return tfwidth;
	}
	public JTextField getMaxSpeed() {
		return maxSpeed;
	}
	public JButton getAdd() {
		return add;
	}
	public JButton getRemove() {
		return remove;
	}
	public LaneLinkPanel getLnLinkPnl() {
		return lnLinkPnl;
	}
	/**
	 * Resets this {@link LanesPanel}
	 */
	public void reset() {
		cbLanes.removeAllItems();
		cbtype.removeAllItems();
		tfwidth.setText("");
		level.setText("");
		maxSpeed.setText("");
		lnLinkPnl.reset();
	}
	public JSlider getPosition() {
		return position;
	}
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		GraphicsHelper.drawGradientBackground(g,getWidth(),getHeight());
	}
}
