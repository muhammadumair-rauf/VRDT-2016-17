package org.tde.tdescenariodeveloper.eventhandling;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.movsim.autogen.AccelerationModelType;
import org.movsim.autogen.ModelParameterACC;
import org.movsim.autogen.ModelParameterGipps;
import org.movsim.autogen.ModelParameterIDM;
import org.movsim.autogen.ModelParameterKrauss;
import org.movsim.autogen.ModelParameterMOBIL;
import org.movsim.autogen.ModelParameterNSM;
import org.movsim.autogen.ModelParameterNewell;
import org.tde.tdescenariodeveloper.updation.Conditions;
import org.tde.tdescenariodeveloper.utils.GraphicsHelper;
/**
 * Class used to listen changes made to model parameters
 * @author Shmeel
 * @see AccelerationModelType
 * @see ModelParameterACC
 * @see ModelParameterGipps
 * @see ModelParameterIDM
 * @see ModelParameterKrauss
 * @see ModelParameterMOBIL
 * @see ModelParameterNewell
 * @see ModelParameterNSM
 */
public class ModelParamTextFieldListener implements DocumentListener,Blockable {
	JTextField tf;
	AccelerationModelType accT;
	int time;
	boolean blocked=false;
	/**
	 * 
	 * @param tf {@link JTextField} to which this listener is attached
	 * @param at {@link AccelerationModelType} model
	 */
	public ModelParamTextFieldListener(JTextField tf,AccelerationModelType at) {
		this.tf=tf;
		accT=at;
	}
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
	/**
	 * Called when text is changed of the field
	 * @param e document event
	 */
	private void textChanged(DocumentEvent e) {
		if(blocked)return;
		if(!Conditions.isValid(tf, ""))return;
		if(accT.isSetModelParameterACC()){
			changeACC();
		}
		else if(accT.isSetModelParameterIDM()){
			changeIDM();
		}
		else if(accT.isSetModelParameterGipps()){
			changeGipps();
		}
		else if(accT.isSetModelParameterKrauss()){
			changeKrauss();
		}
		else if(accT.isSetModelParameterNewell()){
			changeNewell();
		}
		else if(accT.isSetModelParameterNSM()){
			changeNSM();
		}
	}
	/**
	 * Called if set model was Newell 
	 */
	private void changeNewell() {
		try{
			double d=Double.parseDouble(tf.getText());
			GraphicsHelper.makeBlack(tf);
			switch(tf.getName()){
			case "v0":
				accT.getModelParameterNewell().setV0(d);
				break;
			case "s0":
				accT.getModelParameterNewell().setS0(d);
				break;
			}
		}catch(NumberFormatException e2){
			GraphicsHelper.makeRed(tf);
		}
	}
	/**
	 * Called if set model was NSM 
	 */
	private void changeNSM() {
		try{
			double d=Double.parseDouble(tf.getText());
			GraphicsHelper.makeBlack(tf);
			switch(tf.getName()){
			case "v0":
				accT.getModelParameterNSM().setV0(d);
				break;
			case "s0":
				accT.getModelParameterNSM().setS0(d);
				break;
			case "pSlowDown":
				accT.getModelParameterNSM().setPSlowdown(d);
				break;
			case "pSlowStart":
				accT.getModelParameterNSM().setPSlowStart(d);
				break;
			}
		}catch(NumberFormatException e2){
			GraphicsHelper.makeRed(tf);
		}
	}
	/**
	 * Called if set model was Gipps 
	 */
	private void changeGipps() {
		try{
			double d=Double.parseDouble(tf.getText());
			GraphicsHelper.makeBlack(tf);
			switch(tf.getName()){
			case "v0":
				accT.getModelParameterGipps().setV0(d);
				break;
			case "s0":
				accT.getModelParameterGipps().setS0(d);
				break;
			case "a":
				accT.getModelParameterGipps().setA(d);
				break;
			case "b":
				accT.getModelParameterGipps().setB(d);
				break;
			}
		}catch(NumberFormatException e2){
			GraphicsHelper.makeRed(tf);
		}
	}
	/**
	 * Called if set model was Krauss 
	 */
	private void changeKrauss() {
		try{
			double d=Double.parseDouble(tf.getText());
			GraphicsHelper.makeBlack(tf);
			switch(tf.getName()){
			case "v0":
				accT.getModelParameterKrauss().setV0(d);
				break;
			case "s0":
				accT.getModelParameterKrauss().setS0(d);
				break;
			case "a":
				accT.getModelParameterKrauss().setA(d);
				break;
			case "b":
				accT.getModelParameterKrauss().setB(d);
				break;
			case "Epsilon":
				accT.getModelParameterKrauss().setEpsilon(d);
				break;
			}
		}catch(NumberFormatException e2){
			GraphicsHelper.makeRed(tf);
		}
	}
	/**
	 * Called if set model was IDM 
	 */
	private void changeIDM() {
		try{
			double d=Double.parseDouble(tf.getText());
			GraphicsHelper.makeBlack(tf);
			switch(tf.getName()){
			case "v0":
				accT.getModelParameterIDM().setV0(d);
				break;
			case "s0":
				accT.getModelParameterIDM().setS0(d);
				break;
			case "s1":
				accT.getModelParameterIDM().setS1(d);
				break;
			case "T":
				accT.getModelParameterIDM().setT(d);
				break;
			case "a":
				accT.getModelParameterIDM().setA(d);
				break;
			case "b":
				accT.getModelParameterIDM().setB(d);
				break;
			case "Delta":
				accT.getModelParameterIDM().setDelta(d);
				break;
			}
		}catch(NumberFormatException e2){
			GraphicsHelper.makeRed(tf);
		}
	}
	/**
	 * Called if set model was ACC 
	 */
	private void changeACC() {
		try{
			double d=Double.parseDouble(tf.getText());
			GraphicsHelper.makeBlack(tf);
			switch(tf.getName()){
			case "v0":
				accT.getModelParameterACC().setV0(d);
				break;
			case "s0":
				accT.getModelParameterACC().setS0(d);
				break;
			case "s1":
				accT.getModelParameterACC().setS1(d);
				break;
			case "T":
				accT.getModelParameterACC().setT(d);
				break;
			case "a":
				accT.getModelParameterACC().setA(d);
				break;
			case "b":
				accT.getModelParameterACC().setB(d);
				break;
			case "Delta":
				accT.getModelParameterACC().setDelta(d);
				break;
			case "Coolness":
				accT.getModelParameterACC().setCoolness(d);
				break;
			}
		}catch(NumberFormatException e2){
			GraphicsHelper.makeRed(tf);
		}
	}
	@Override
	public void setBlocked(boolean b) {
		blocked=b;
	}
}
