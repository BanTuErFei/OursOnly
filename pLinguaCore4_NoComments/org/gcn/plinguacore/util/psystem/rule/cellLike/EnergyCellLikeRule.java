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

package org.gcn.plinguacore.util.psystem.rule.cellLike;

import org.gcn.plinguacore.util.psystem.rule.IEnergyRule;
import org.gcn.plinguacore.util.psystem.rule.LeftHandRule;
import org.gcn.plinguacore.util.psystem.rule.RightHandRule;

/**
 * This class represents cell-like rules which hold an integer constant
 * representing its execution priority
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */
class EnergyCellLikeRule extends CellLikeRule implements
		Comparable<EnergyCellLikeRule>,IEnergyRule {

	private static final long serialVersionUID = 6712022235568512949L;
	private int energy;

	/**
	 * Creates a new PriorityCellLikeRule instance.
	 * 
	 * @param dissolves
	 *            a boolean parameter which reflects if the rule will dissolve
	 *            the membrane
	 * @param leftHandRule
	 *            the left hand of the rule
	 * @param rightHandRule
	 *            the right hand of the rule
	 * @param energy
	 *            the energy produced or to consume
	 */
	protected EnergyCellLikeRule(boolean dissolves, LeftHandRule leftHandRule,
			RightHandRule rightHandRule, int energy) {
		super(dissolves, leftHandRule, rightHandRule);
		if (energy < 0)
			throw new IllegalArgumentException(
					"Energy argument should be at least 0");
		this.energy = energy;

	}

	/**
	 * Gets the rule energy
	 * 
	 * @return the rule energy
	 */
	@Override
	public int getEnergy() {
		return energy;
	}

	/**
	 * compares the rule to other by comparing their priority
	 * 
	 * @param clr
	 *            the PriorityCellLikeRule instance to compare its priority to
	 * @return 0 if the rules have the same priority a number greater than 0 if
	 *         this instance has more priority than the instance given
	 */
	@Override
	public int compareTo(EnergyCellLikeRule clr) {
		return this.getEnergy() - clr.getEnergy();
	}

	/**
	 * @see org.gcn.plinguacore.util.psystem.rule.cellLike.CellLikeRule#toString()
	 */
	@Override
	public String toString() {
		return "(" + energy + ") " + super.toString();

	}

	/**
	 * @see org.gcn.plinguacore.util.psystem.rule.AbstractRule#hashCode()
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = super.hashCode();
		result = PRIME * result + energy;
		return result;
	}

	/**
	 * @see org.gcn.plinguacore.util.psystem.rule.AbstractRule#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final EnergyCellLikeRule other = (EnergyCellLikeRule) obj;
		if (energy != other.energy)
			return false;
		return true;
	}

}
