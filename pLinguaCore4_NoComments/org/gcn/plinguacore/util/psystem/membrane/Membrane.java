/* 
 * pLinguaCore: A JAVA library for Membrane Computing
 *              http://www.p-lingua.org
 *
 * Copyright (C) 2009  Research Group on Natural Computing
 *                     http://www.gcn.us.es
 *                      
 * This file is part of pLinguaCore.
 *
 * pLinguaCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * pLinguaCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with pLinguaCore.  If not, see <http://www.gnu.org/licenses/>.
 */


package org.gcn.plinguacore.util.psystem.membrane;

import java.io.Serializable;

import org.gcn.plinguacore.util.HashMultiSet;
import org.gcn.plinguacore.util.InmutableMultiSet;
import org.gcn.plinguacore.util.MultiSet;
import org.gcn.plinguacore.util.psystem.Label;

/**
 * A class which represents a membrane
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
public abstract class Membrane implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 979202030107031549L;

	protected MultiSet<String> multiSet = null;

	protected Label label;
	protected byte charge = 0;
	protected int energy = 0;
	protected int energy2=0;
	protected int prevEnergy = 0;	//JM: this is for printing purposes :)))
	protected int tempEnergy = 0;	//JM: this is for temporary energy execution while selecting :)))


	public Membrane(Label label, byte charge, MultiSet<String> multiSet){
		this(label, charge);
		if (multiSet == null)
			throw new NullPointerException(
					"MultiSet constructor argument shouldn't be null");
		this.multiSet = multiSet;
		
	}
	
	public Membrane(Label label, byte charge, int energy, MultiSet<String> multiSet) {
		this(label, charge,energy);
		if (multiSet == null)
			throw new NullPointerException(
					"MultiSet constructor argument shouldn't be null");
		this.multiSet = multiSet;
		this.energy=energy;
	
	}
	
	public Membrane(Label label, byte charge, int energy,int energy2, MultiSet<String> multiSet) {
		this(label, charge,energy, energy2);
		if (multiSet == null)
			throw new NullPointerException(
					"MultiSet constructor argument shouldn't be null");
		this.multiSet = multiSet;
		this.energy=energy;
		this.energy2=energy2;
	}

	public Membrane(Label label, byte charge){
		this(label);
		this.charge = charge;
	}
	
	public Membrane(Label label, int energy){
		this(label);
		this.energy = energy;
	}
	
	public Membrane(Label label, byte charge, int energy) {
		this(label);
		this.charge = charge;
		this.energy=energy;
	}
	
	
	public Membrane(Label label, byte charge, int energy1, int energy2) {
		this(label);
		this.charge = charge;
		this.energy=energy;
			this.energy2=energy2;
	}
	

	
	

	/**
	 * creates a new membrane with the label given
	 * 
	 * @param label
	 *            the label to set
	 * @throws NullPointerException
	 *             if the label is null
	 * @throws IllegalArgumentException
	 *             if the label is empty
	 */
	public Membrane(Label label) {
		super();
		if (label == null)
			throw new NullPointerException("Null label");
	
		multiSet = new HashMultiSet<String>();
		this.label = label;
	

	}
	
	
	/**
	 * gets the membrane multiset
	 * 
	 * @return the membrane multiset
	 */
	public MultiSet<String> getMultiSet() {	
		return new InmutableMultiSet<String>(multiSet);
	}

	/**
	 * gets the membrane charge value
	 * 
	 * @return The numeric charge value
	 */
	public byte getCharge() {
		return charge;
	}

	/**
	 * @return The label of the membrane
	 */
	public String getLabel() {
		return label.getLabelID();
	}
	
	/**
	 * @return The energy of the membrane
	 */
	public int getEnergy(){
		return energy;
	}
	
	public int getEnergy2(){
		return energy2;
	}
	

	public int getEnergyPrev(){
		return prevEnergy;
	}

	public int getEnergyTemp(){
		return tempEnergy;
	}
	
	public static String getEnergyString(int energy){
		return String.valueOf(energy);
	}
	
	public Label getLabelObj() {
		return label;
	}

	/**
	 * returns a "+" String for positive charges, a "-" String for negative
	 * charges and a "0" String for neutral chages
	 * 
	 * @param charge
	 * @return A String
	 */
	public static String getChargeSymbol(byte charge) {
		if (charge > 0)
			return "+";
		else if (charge < 0)
			return "-";
		else
			return "0";
	}

	@Override
	public String toString() {
		String ch = Membrane.getChargeSymbol(getCharge());
		if (ch.equals("0"))
			ch = "";
		String str = ch + "[";
		if (getMultiSet().size() != 0)
			str += getMultiSet().toString();

		str += "]'" + getLabelObj();
		str += ":" + Integer.toString(getEnergy());		
	
		return str;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + charge;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result
				+ ((multiSet == null) ? 0 : multiSet.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Membrane other = (Membrane) obj;
		if (charge != other.charge)
			return false;
		if (energy != other.energy)	//JM: we added the energy as an indication of equality
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (multiSet == null) {
			if (other.multiSet != null)
				return false;
		} else if (!multiSet.equals(other.multiSet))
			return false;
		return true;
	}



	
}
