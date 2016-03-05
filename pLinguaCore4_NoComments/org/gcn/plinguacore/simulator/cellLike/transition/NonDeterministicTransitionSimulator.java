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

package org.gcn.plinguacore.simulator.cellLike.transition;

import java.util.ArrayList;

import java.util.Iterator;
import java.util.List;


import org.gcn.plinguacore.simulator.cellLike.CellLikeSimulator;
import org.gcn.plinguacore.util.psystem.Psystem;

import org.gcn.plinguacore.util.psystem.membrane.ChangeableMembrane;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeNoSkinMembrane;
import org.gcn.plinguacore.util.psystem.rule.IRule;
import org.gcn.plinguacore.util.psystem.rule.RulesSet;
import org.gcn.plinguacore.util.psystem.rule.IPriorityRule;

import org.gcn.plinguacore.util.RandomNumbersGenerator;



public final class NonDeterministicTransitionSimulator extends
		CellLikeSimulator {


	/**
	 * 
	 */
	private static final long serialVersionUID = 290211051490843577L;

	public NonDeterministicTransitionSimulator(Psystem psystem) {
		super(psystem);
			
		
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void microStepSelectRules(ChangeableMembrane m,
			ChangeableMembrane temp) {	//JM: 2 Membranes with the same structure
		// TODO Auto-generated method stub
	
		//System.out.println("NonDeterministicTransitionSimulator: microStepSelectRules");
		
		

		int n=2;
		List<IRule>aux = new ArrayList<IRule>();	//JM: List of rules, aha

		//temp.setEnergyTemp();
		//m.setEnergyTemp();
		for (int i=n;i>0;i--)
		{
			Iterator<IRule> it = getPsystem().getRules().iterator(
					temp.getLabel(),
					temp.getLabelObj().getEnvironmentID(),
					temp.getCharge(),true);	//JM: Another list of rules, great
			
			aux.clear();	//JM: ah, it has been cleared!
			while (it.hasNext())
				aux.add(it.next());	//JM: Every rule is transferred to aux
			
			RulesSet.sortByPriority(aux);	//JM: sortByPriority?? Hmmm.... ah, priority meaning the EXPLICIT PRIORITY!!!
			
			it = aux.iterator();	//JM: I don't get it
			
			while (it.hasNext()) {
				IRule r = it.next();	//JM: we're iterating the rules
				long count = r.countExecutions(temp); 	//JM: Apparently when the energy goes here, temp's energy becomes 0!!!
														//JM: ok, apparently, we can't do the energy thingy here
														//JM: Sigh, it seems that we have to to the energy here hahaha
				//JM: We have to create a dummy execution here
				
				//int MinMultiset = r.getMin();	

				if (!(r instanceof IPriorityRule) && count>0 && i!=1)
					count = RandomNumbersGenerator.getInstance().nextLong(count);	//JM: is something happening here?
					
				//System.out.println("NonDeterministicTransitionSimulator " + r.toString() + " has count " + count);

				if (count > 0) {

					/*if(r instanceof CellLikeRule){

						((CellLikeRule)r).executeDummy(temp);	
					}*/
					//System.out.println("NonDeterministicTransitionSimulator: temp membrane energy (before) = " + temp.getEnergy());


					if(temp instanceof CellLikeNoSkinMembrane){
						//System.out.println("NonDeterministicTransitionSimulator: temp parent membrane energy (before) = " + ((CellLikeNoSkinMembrane)temp).getParentMembrane().getEnergy());
					}

					r.executeDummy(temp, (int)count);
					
					//r.executeDummy(m, (int)count);
					//System.out.println("NonDeterministicTransitionSimulator: temp membrane energy (after) = " + temp.getEnergy());

					if(temp instanceof CellLikeNoSkinMembrane){
						//System.out.println("NonDeterministicTransitionSimulator: temp parent membrane energy (after) = " + ((CellLikeNoSkinMembrane)temp).getParentMembrane().getEnergy());
					}
					//System.out.println("NonDeterministicTransitionSimulator: count = " + count);
					//JM: Do something here - I guess it really is here huh?. We have to remove energy once it has been used
					//JM: set Energy to the number of counts that could be implemented

					selectRule(r, m, count);
					removeLeftHandRuleObjects(temp, r, count);
				}
			}
		}
	}

}
