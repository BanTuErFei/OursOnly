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
import java.util.HashSet;


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
	
		int ecpe_priority = getPsystem().getECPePriority();

		//System.out.println("ecpe ecpe_priority = " + ecpe_priority);
		int n=2;
		List<IRule>aux = new ArrayList<IRule>();

		if(ecpe_priority == 0){

			for (int i=n;i>0;i--)
			{
				Iterator<IRule> it = getPsystem().getRules().iterator(
						temp.getLabel(),
						temp.getLabelObj().getEnvironmentID(),
						temp.getCharge(),true);	

				aux.clear();
				while (it.hasNext())
					aux.add(it.next());	
					
				RulesSet.sortByPriority(aux);
					
				it = aux.iterator();
					
				while (it.hasNext()) {
					IRule r = it.next();	
					long count = r.countExecutions(temp); 
					System.out.println("NonDeterministic : r = " + r.toString() + " count = " + count);	

					if (!(r instanceof IPriorityRule) && count>0 && i!=1)
						count = RandomNumbersGenerator.getInstance().nextLong(count);	
						
					
					if (count > 0) {

						r.executeDummy(temp, (int)count);

						selectRule(r, m, count);
						removeLeftHandRuleObjects(temp, r, count);
					}
				}
			}
		}
		else{
			
			//Evolution first
			if(ecpe_priority == 1){
				for (int i=n;i>0;i--)
				{
					Iterator<IRule> it = getPsystem().getRules().iterator(
							temp.getLabel(),
							temp.getLabelObj().getEnvironmentID(),
							temp.getCharge(),true);	

					System.out.println("temp(evol) label " + temp.getLabel());

					aux.clear();
					while (it.hasNext())
						aux.add(it.next());	

					System.out.println("aux " + aux.size());
						
					RulesSet.sortByPriority(aux);
						
					it = aux.iterator();
					while(it.hasNext()){
						IRule r = it.next();
						System.out.println("aux : " + r.toString());
					}



					it = aux.iterator();

					//categorize Rules between evolution and communication
					HashSet<IRule> itEvolutionRule = new HashSet<IRule>();
					while(it.hasNext()){
						IRule r = it.next();
						if(r.isEvolution()){
							itEvolutionRule.add(r);
						}
					}

					Iterator<IRule> itEvol = itEvolutionRule.iterator();
						
					while (itEvol.hasNext()) {
						IRule r = itEvol.next();
						long count = r.countExecutions(temp); 	
						System.out.println("evol First " + r.toString() + " count = " + count + " i = " + i);	

						if (!(r instanceof IPriorityRule) && count>0 && i!=1)
							count = RandomNumbersGenerator.getInstance().nextLong(count);	
							
						if (count > 0) {
							r.executeDummy(temp, (int)count);
							selectRule(r, m, count);
							removeLeftHandRuleObjects(temp, r, count);
						}
					}
				}

				//Then communication rule
				for (int i=n;i>0;i--)
				{
					Iterator<IRule> it = getPsystem().getRules().iterator(
							temp.getLabel(),
							temp.getLabelObj().getEnvironmentID(),
							temp.getCharge(),true);	

					System.out.println("temp(comm) label " + temp.getLabel());
					aux.clear();
					while (it.hasNext())
						aux.add(it.next());	

					System.out.println("aux2 " + aux.size());
						
					RulesSet.sortByPriority(aux);
						
					it = aux.iterator();
					while(it.hasNext()){
						IRule r = it.next();
						System.out.println("aux2 : " + r.toString());
					}

					it = aux.iterator();

					//categorize Rules between evolution and communication
					HashSet<IRule> itCommunicationRule = new HashSet<IRule>();
					while(it.hasNext()){
						IRule r = it.next();
						if(r.isSendin() || r.isSendout() || r.isAntiport())
							itCommunicationRule.add(r);
					}

					Iterator<IRule> itComm = itCommunicationRule.iterator();
						
					while (itComm.hasNext()) {
						IRule r = itComm.next();	
						long count = r.countExecutions(temp); 	

						System.out.println("comm Second " + r.toString() + " count = " + count + " i = " + i);	

						if (!(r instanceof IPriorityRule) && count>0 && i!=1)
							count = RandomNumbersGenerator.getInstance().nextLong(count);	
							
						if (count > 0) {
							r.executeDummy(temp, (int)count);
							selectRule(r, m, count);
							removeLeftHandRuleObjects(temp, r, count);
						}
					}
				}

			}

			else if(ecpe_priority == 2){

				//Communication rule first
				for (int i=n;i>0;i--)
				{
					Iterator<IRule> it = getPsystem().getRules().iterator(
							temp.getLabel(),
							temp.getLabelObj().getEnvironmentID(),
							temp.getCharge(),true);	

					aux.clear();
					while (it.hasNext())
						aux.add(it.next());	
						
					RulesSet.sortByPriority(aux);
						
					it = aux.iterator();

					//categorize Rules between evolution and communication
					HashSet<IRule> itCommunicationRule = new HashSet<IRule>();
					while(it.hasNext()){
						IRule r = it.next();
						if(r.isSendin() || r.isSendout() || r.isAntiport())
							itCommunicationRule.add(r);
				
					}

					Iterator<IRule> itComm = itCommunicationRule.iterator();
						
					while (itComm.hasNext()) {
						IRule r = itComm.next();	
						long count = r.countExecutions(temp); 	

						if (!(r instanceof IPriorityRule) && count>0 && i!=1)
							count = RandomNumbersGenerator.getInstance().nextLong(count);	
							
						if (count > 0) {
							r.executeDummy(temp, (int)count);
							selectRule(r, m, count);
							removeLeftHandRuleObjects(temp, r, count);
						}
					}
				}

				//Then evolution rule
				for (int i=n;i>0;i--)
				{
					Iterator<IRule> it = getPsystem().getRules().iterator(
							temp.getLabel(),
							temp.getLabelObj().getEnvironmentID(),
							temp.getCharge(),true);	

					aux.clear();
					while (it.hasNext())
						aux.add(it.next());	
						
					RulesSet.sortByPriority(aux);
						
					it = aux.iterator();

					//categorize Rules between evolution and communication
					
					HashSet<IRule> itEvolutionRule = new HashSet<IRule>();
					while(it.hasNext()){
						IRule r = it.next();
						if(r.isEvolution())
							itEvolutionRule.add(r);
					}

					Iterator<IRule> itEvol = itEvolutionRule.iterator();		
					while (itEvol.hasNext()) {
						IRule r = itEvol.next();	
						long count = r.countExecutions(temp); 	

						if (!(r instanceof IPriorityRule) && count>0 && i!=1)
							count = RandomNumbersGenerator.getInstance().nextLong(count);	
							
						if (count > 0) {
							r.executeDummy(temp, (int)count);
							selectRule(r, m, count);
							removeLeftHandRuleObjects(temp, r, count);
						}
					}
				}

			}

		}
		
	}

}
