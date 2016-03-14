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

package org.gcn.plinguacore.simulator;


import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.gcn.plinguacore.util.HashMultiSet;
import org.gcn.plinguacore.util.MultiSet;
import org.gcn.plinguacore.util.Pair;
import org.gcn.plinguacore.util.PlinguaCoreException;
import org.gcn.plinguacore.util.psystem.Configuration;
import org.gcn.plinguacore.util.psystem.Psystem;
import org.gcn.plinguacore.util.psystem.membrane.ChangeableMembrane;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeMembrane;
import org.gcn.plinguacore.util.psystem.cellLike.membrane.CellLikeNoSkinMembrane;
import org.gcn.plinguacore.util.psystem.membrane.Membrane;
import org.gcn.plinguacore.util.psystem.membrane.MembraneStructure;
import org.gcn.plinguacore.util.psystem.rule.IRule;
import org.gcn.plinguacore.util.psystem.rule.AbstractRule;
import org.gcn.plinguacore.util.psystem.rule.checkRule.CheckRule;
import org.gcn.plinguacore.util.psystem.rule.checkRule.specificCheckRule.NoEvolution;

import org.gcn.plinguacore.util.psystem.cellLike.CellLikeConfiguration;
import org.gcn.plinguacore.util.psystem.rule.AbstractRule;
//import org.gcn.plinguacore.util.psystem.rule.cellLike.Rule;

/**
 * An abstract class for simulators which execute simulation steps in three microsteps:
 * init step, select rules for the whole configuration, execute rules for the whole configuration
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 */

public abstract class AbstractSelectionExecutionSimulator extends AbstractSimulator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1997974622465213429L;
	private boolean firstTime = true;
	
	private Map<Integer, Pair<ChangeableMembrane, MultiSet<Object>>> selectedRules;

	private Configuration configurationPrev;
	private boolean sameConfig = false;	


	protected static final CheckRule noEvolution = new NoEvolution();
	

	public AbstractSelectionExecutionSimulator(Psystem psystem) {
		super(psystem);
		//System.out.println("AbstractSelectionExecutionSimulator");
		selectedRules = new LinkedHashMap<Integer, Pair<ChangeableMembrane, MultiSet<Object>>>();
		//configurationPrev = new Object(); //JM: Because configuration cannot be instantiated
		


		// TODO Auto-generated constructor stub
	}
	
	protected final Map<Integer, Pair<ChangeableMembrane, MultiSet<Object>>> getSelectedRules() {
		return selectedRules;
	}

	protected abstract String getHead(ChangeableMembrane m);
	
	//protected abstract String getHeadPrev(ChangeableMembrane m);	//JM: Returns the previous head with the previous energy, not the energy after rule execution

	/**
	 * Print short information in the info-channel about current configuration
	 * 
	 * @param first
	 *            It is true if this is the first configuration
	 */
	private void printInfoShort(boolean first) {
		//System.out.println("AbstractSelectionExecutionSimulator (printInfoShort) : sameConfig " + sameConfig);
		if ((!selectedRules.isEmpty() || first) && sameConfig==false) {

			getInfoChannel().println(
					"***********************************************");
			getInfoChannel().println();
			getInfoChannel().println(
					"    CONFIGURATION: " + (currentConfig.getNumber()));	//JM: Config number
			if (isTimed()) {
				long mem = Runtime.getRuntime().maxMemory()
						- Runtime.getRuntime().freeMemory();
				mem = mem / 1024;
				getInfoChannel().println("    TIME: " + getTime() + " s.");	//JM: time
				getInfoChannel().println("    MEMORY: " + mem + " Kb");	//JM: memory
			}
			getInfoChannel().println();

			//System.out.println("AbstractSelectionExecutionSimulator :: came here for the initial configurationPrev");

			printInfoMembraneShort(currentConfig.getMembraneStructure());
			if (!currentConfig.getEnvironment().isEmpty()) {
				getInfoChannel().println(
						"    ENVIRONMENT: " + currentConfig.getEnvironment());	//JM: environment
				getInfoChannel().println();
			}
		}
		else if(!hasSelectedRules()){
			Iterator<? extends Membrane> it = currentConfig.getMembraneStructure().getAllMembranes()
					.iterator();
			while (it.hasNext())
				printInfoMembrane((ChangeableMembrane)it.next());
			if (!currentConfig.getEnvironment().isEmpty()) {
				getInfoChannel().println(
						"    ENVIRONMENT: " + currentConfig.getEnvironment());	//JM: environment
				getInfoChannel().println();
			}
			getInfoChannel()
					.println(
							"Halting configuration (No rule can be selected to be executed in the next step)");
		} 

		else if(sameConfig){
			getInfoChannel()
				.println(
					"(1) This is a non-halting configuration");

		}
		else{
			getInfoChannel()
				.println(
					"I don't know");
		}

		configurationPrev = (CellLikeConfiguration)currentConfig.clone();	//JM: Added for trap symbol

	}
	

	/**
	 * Print large information in the info-channel about current configuration
	 * 
	 * @param first
	 *            It is true if this is the first configuration
	 */
	private void printInfo(boolean first) {
		//System.out.println("AbstractSelectionExecutionSimulator (printInfo) : sameConfig " + sameConfig);
		//System.out.println("(1)hasSelectedRules() == " + hasSelectedRules());
		//System.out.println("sameConfig == " + sameConfig);

		if ( (hasSelectedRules() || first) && (!sameConfig)) {
			if (!first) {
				getInfoChannel().println(
						"-----------------------------------------------");
				getInfoChannel().println();
				getInfoChannel().println(
						"    STEP: " + currentConfig.getNumber());
				System.out.println("\nAbstractExecutionSimulator : STEP " + currentConfig.getNumber());

			}
			Iterator<Pair<ChangeableMembrane, MultiSet<Object>>> it1 = selectedRules
					.values().iterator();


			//System.out.println("rules.hasNext() == " + it1.hasNext());			
			while (it1.hasNext()) {
				int tempEnergy=0, tempParentEnergy=0;
				Pair<ChangeableMembrane, MultiSet<Object>> pair = it1.next();
				MultiSet<Object> rules = pair.getSecond();
				Iterator<Object> it = rules.entrySet().iterator();
				//System.out.println("pair.toString() = " + pair.toString());	
				if (it.hasNext()) {
					getInfoChannel().println();
					getInfoChannel().println(
							"    Rules selected for "
									+ getHead(pair.getFirst()));

					System.out.println();
					System.out.println(
							"    Rules selected for "
									+ getHead(pair.getFirst()));

					while (it.hasNext()) {
						Object r = it.next();
						getInfoChannel().println(
								"    " + rules.count(r) + " * " + r.toString());	//JM: this toString implements everything HAHAHAHA

						System.out.println(
							"    " + rules.count(r) + " * " + r.toString());

					}
				}
			}
			getInfoChannel().println();
			getInfoChannel().println(
					"***********************************************");
			getInfoChannel().println();
			getInfoChannel().println(
					"    CONFIGURATION: " + (currentConfig.getNumber()));
			if (isTimed()) {
				getInfoChannel().println("    TIME: " + getTime() + " s.");
				getInfoChannel().println(
						"    MEMORY USED: "
								+ Runtime.getRuntime().totalMemory() / 1024);
				getInfoChannel().println(
						"    FREE MEMORY: " + Runtime.getRuntime().freeMemory()
								/ 1024);
				getInfoChannel().println(
						"    TOTAL MEMORY: " + Runtime.getRuntime().maxMemory()
								/ 1024);
			}
			getInfoChannel().println();

			//System.out.println("AbstractSelectionExecutionSimulator : printInfo :: after Config");

			

			Iterator<? extends Membrane> it = currentConfig.getMembraneStructure().getAllMembranes()
						.iterator();
				while (it.hasNext())
					printInfoMembrane((ChangeableMembrane)it.next());
				if (!currentConfig.getEnvironment().isEmpty()) {
					getInfoChannel().println(
							"    ENVIRONMENT: " + currentConfig.getEnvironment());	//JM: environment
					getInfoChannel().println();
				}

			} 

			//This marks the end of the conditional if(hasSelectedRules() && !sameConfig chuchu)
			else if(!hasSelectedRules() || emptyMultiSet()){	//emptyMultiSet - bacause elements in selectedRules cannot be removed, it has to be checked this way

				getInfoChannel()
						.println(
								"Halting configuration (No rule can be selected to be executed in the next step)");

			}
			else if(sameConfig){


				getInfoChannel()
						.println(
								"This is a non-halting configuration");
				System.out
						.println(
								"This is a non-halting configuration");

				//System.out.println("(2)hasSelectedRules() == " + hasSelectedRules());

				//We just gonna print what's after the configuration

				if (!first) {
					getInfoChannel().println(
							"-----------------------------------------------");
					getInfoChannel().println();
					getInfoChannel().println(
							"    STEP: " + currentConfig.getNumber());
					System.out.println("\nAbstractExecutionSimulator : STEP " + currentConfig.getNumber());

				}		


				Iterator<Pair<ChangeableMembrane, MultiSet<Object>>> it1 = selectedRules
					.values().iterator();

				
			System.out.println("(2) rules.hasNext() == " + it1.hasNext());	
				while (it1.hasNext()) {
					/*getInfoChannel().println("hasSelectedRules()");*/
					System.out.println("inside rules.hasNext()");	
					int tempEnergy=0, tempParentEnergy=0;
					Pair<ChangeableMembrane, MultiSet<Object>> pair = it1.next();

					System.out.println("pair.toString() = " + pair.toString());	
					MultiSet<Object> rules = pair.getSecond();
					Iterator<Object> it = rules.entrySet().iterator();

					System.out.println("rules.entrySet.hasNext() == " + it.hasNext());	
					if (it.hasNext()) {
						//System.out.println("inside rules.entrySet.hasNext()");
						getInfoChannel().println();
						getInfoChannel().println(
								"    Rules selected for "
										+ getHead(pair.getFirst()));

						System.out.println();
						System.out.println(
								"    Rules selected for "
										+ getHead(pair.getFirst()));

						while (it.hasNext()) {
							Object r = it.next();
							getInfoChannel().println(
									"    " + rules.count(r) + " * " + r.toString());	//JM: this toString implements everything HAHAHAHA

							System.out.println(
								"    " + rules.count(r) + " * " + r.toString());

						}
					}
				}
				getInfoChannel().println();
				getInfoChannel().println(
						"***********************************************");
				getInfoChannel().println();
				getInfoChannel().println(
						"    CONFIGURATION: " + (currentConfig.getNumber()));
				if (isTimed()) {
					getInfoChannel().println("    TIME: " + getTime() + " s.");
					getInfoChannel().println(
							"    MEMORY USED: "
									+ Runtime.getRuntime().totalMemory() / 1024);
					getInfoChannel().println(
							"    FREE MEMORY: " + Runtime.getRuntime().freeMemory()
									/ 1024);
					getInfoChannel().println(
							"    TOTAL MEMORY: " + Runtime.getRuntime().maxMemory()
									/ 1024);
				}
				getInfoChannel().println();



				//System.out.println("AbstractSelectionExecutionSimulator : printInfo :: after Config");

				

				Iterator<? extends Membrane> it = currentConfig.getMembraneStructure().getAllMembranes()
							.iterator();
					while (it.hasNext())
						printInfoMembrane((ChangeableMembrane)it.next());
					if (!currentConfig.getEnvironment().isEmpty()) {
						getInfoChannel().println(
								"    ENVIRONMENT: " + currentConfig.getEnvironment());	//JM: environment
						getInfoChannel().println();
					}

				//End of printing

			}
			else{
				getInfoChannel()
					.println(
						"I don't know");

			}

		
		configurationPrev = (CellLikeConfiguration)currentConfig.clone();	//JM: Added for trap symbol


	}

	public boolean emptyMultiSet(){
		Iterator<Pair<ChangeableMembrane, MultiSet<Object>>> iter = selectedRules
			.values().iterator();

		while(iter.hasNext()){
			Pair<ChangeableMembrane,MultiSet<Object>> p1 = iter.next();
			if(p1.getSecond().size()!=0){
				return false;
			}
		}
		return true;

	}


	public boolean equals(){

		//JM: This one returns equals if the currentConfig and the previous config are the same

		//System.out.println("equals()");
		Iterator<? extends Membrane> itTemp = currentConfig.getMembraneStructure().getAllMembranes()
				.iterator();
		Iterator<? extends Membrane> itPrevTemp = configurationPrev.getMembraneStructure().getAllMembranes()
				.iterator();

		//System.out.println("equals()2");
		if(currentConfig.getMembraneStructure().getAllMembranes().size() != configurationPrev.getMembraneStructure().getAllMembranes().size()){
			//System.out.println("equals()3 = false");
			return false;
		}

		//JM: This assumes that 
		while(itTemp.hasNext()){
			//System.out.println("while itTemp.hasNext()");
			if(itPrevTemp.hasNext()){
				//System.out.println("while itPrevTemp.hasNext");
				if(((ChangeableMembrane)itTemp.next()).equals((ChangeableMembrane)itPrevTemp.next())){
					//System.out.println("Is it here huhuhu");
					continue;
				}	
				else{
					//System.out.println("I don't really know why huhuhu");
					return false;
				}
			}
			//System.out.println("Maybe here");
			
		}
		//System.out.println("equals()4 = true");
		return true;
	}


	@Override
	public void reset() {
		super.reset();
		if (getVerbosity()>0)
		{
		if (getVerbosity() > 1)
			printInfo(true);
		else
			printInfoShort(true);
		}
	}
	@Override
	protected final boolean specificStep() throws PlinguaCoreException {

		//System.out.println("AbstractSelectionExecutionSimulator : specificStep :: entered , sameConfig = " + sameConfig);		
		if (firstTime) {
			if (getVerbosity()>0)
			{
			if (getVerbosity() > 1){
				printInfo(true);
			}
			else
				printInfoShort(true);
			}
			firstTime = false;
		}
		microStepInit();
		
		//JM: Before this, set the energy for everything first

		microStepSelectRules();
		
		if (hasSelectedRules()) {
			microStepExecuteRules();	//JM: see?? execution goes first before selection!!!
			//currentConfig = (Configuration)currentConfig.clone();
			
			currentConfig.setNumber(currentConfig.getNumber()+1);
		}

		//JM: printing
		System.out.println("specificStep");
		if (getVerbosity()>0)
		{
			//System.out.println("HERE");
			if (getVerbosity() > 1){
				//System.out.println("HERE2");
				sameConfig = equals();
				//System.out.println("AbstractSelectionExecutionSimulator (specificStep) : sameConfig = " + sameConfig);
				printInfo(false);
			}
			else{
				sameConfig = equals();
				//System.out.println("AbstractSelectionExecutionSimulator (specificStep) : sameConfig = " + sameConfig);
				printInfoShort(false);
			}
		}

		//System.out.println("returns " + (hasSelectedRules() && !sameConfig));

		return (hasSelectedRules() && !sameConfig);
	}
	
	protected boolean hasSelectedRules()
	{
		return !selectedRules.isEmpty();
	}
	
	protected void executeRule(IRule r,ChangeableMembrane m,MultiSet<String>environment,long count)
	{
		
		System.out.println("AbstractSelectionExecutionSimulator: executeRule " + r.toString() + " with count " + count);
		//System.out.println("executeRule before: :: label = " + m.getLabel() + " energy = " + m.getEnergy());
		boolean isExecuted = r.execute(m,environment,count);
		//boolean isExecuted = r.isExecuted();

		System.out.println("AbstractSelectionExecutionSim IsExecuted = " + isExecuted);
		//System.out.println("m.getId() " + m.getId());
		//System.out.println("Rule " + r.toString());


		if(isExecuted == false){
			//System.out.println("AbstractSelectionExecutionSimulator:: " + "inside isExecuted");
			//JM: remove the rule from the selectedRules
			

			//System.out.println("AbstractSelectionExecutionSimulator : executeRule :: contains m's ID = " + selectedRules.containsKey(m.getId()));
			
			Pair<ChangeableMembrane, MultiSet<Object>> tempPair = new Pair<ChangeableMembrane, MultiSet<Object>>(m,
					new HashMultiSet<Object>());

			tempPair = selectedRules.get(m.getId());
			//System.out.println("PUTSELECTEDRULES = " + tempPair.toString() + " with count " + count);
			
			MultiSet<Object> tempMulti = tempPair.getSecond();

			//System.out.println("tempMulti = " + tempMulti.toString());
			tempMulti.remove(r,count);
			//System.out.println("tempMulti (after removal) = " + tempMulti.toString());
			//System.out.println("tempMulti (after removal) count = " + tempMulti.size());


			//We should remove the pair altogether if the multiSet of object is already empty
			if(tempMulti.size() == 0){
				//we need to remove tempPair to selectedRules
				//System.out.println("tempMulti size == 0");
				//selectedRules.keySet().remove((Object)(m.getId()));

			}


			if(selectedRules.containsKey(m.getId())){	//JM: Of course it contains it
				selectedRules.put(m.getId(),tempPair);

				//System.out.println("putSelectedRules (after executionsDone) = " + tempPair.toString());
			}
			else{
				Pair<ChangeableMembrane, MultiSet<Object>> tempPair2 = new Pair<ChangeableMembrane, MultiSet<Object>>(m,
					new HashMultiSet<Object>());

				tempPair2.setSecond(tempPair.getSecond());
				
				selectedRules.put(m.getId(),tempPair2);
			}
			
		}
		else{
			//JM: Get the number of Executions

			//System.out.println("HERE1");
			if(r instanceof AbstractRule){
				if(((AbstractRule)r).getExecutionsDone()!=0){
					//JM: Change the number of executions in the selected rule
					//System.out.println("HERE2");
					Pair<ChangeableMembrane, MultiSet<Object>> tempPair = new Pair<ChangeableMembrane, MultiSet<Object>>(m,
					new HashMultiSet<Object>());

					tempPair = selectedRules.get(m.getId());
					//System.out.println("HERE3");
					MultiSet<Object> tempMulti = tempPair.getSecond();

					tempMulti.remove(r,count);
					tempMulti.add(r,((AbstractRule)r).getExecutionsDone());
				}
			}
			//System.out.println("HERE4");
		}


		//System.out.println("executeRule after: :: label = " + m.getLabel() + " energy = " + m.getEnergy());
	}
	
	protected void microStepExecuteRules() {

		//System.out.println("AbstractSelectionExecutionSimulator : microStepExecuteRules");	//JM: it goes through here per step	

		//JM: to set the prevEnergy
		Iterator<Pair<ChangeableMembrane, MultiSet<Object>>> itTemp = selectedRules
		.values().iterator();	//JM: selectedRules iterator for values	
		while (itTemp.hasNext()) {	//JM: while there is still a pair; goes through here per membrane
			Pair<ChangeableMembrane, MultiSet<Object>> pTemp = itTemp.next();	//JM: values iterator
			ChangeableMembrane mTemp = pTemp.getFirst();
			mTemp.setEnergyPrev();
		}
		//JM: setting prevnergy finished



		Iterator<Pair<ChangeableMembrane, MultiSet<Object>>> it = selectedRules
				.values().iterator();	//JM: selectedRules iterator for values
		MultiSet<IRule> ms1 = new HashMultiSet<IRule>();	//JM: multiset of IRule
		MultiSet<IRule> ms2 = new HashMultiSet<IRule>();	//JM: multiset of IRule
		

		//System.out.println("AbstractSelectionExecutionSimulator : microStepExecuteRules :: after setting previous energy");



		while (it.hasNext()) {	//JM: while there is still a pair; goes through here per membrane
			Pair<ChangeableMembrane, MultiSet<Object>> p = it.next();	//JM: values iterator

			//System.out.println("AbstractSelectionExecutionSimulator : microStepExecuteRules :: inside membrane");
			//JM: we also need to add the energy produced in the evolution in the end so as not to disturb with the communication rules in the same step
			//JM: get all the energy produced in the evolution
			//JM: add all energy per time step per membrane
			int energyEvol = 0;
			
			MultiSet<Object> ms = p.getSecond();	//JM: multisets
			ChangeableMembrane m = p.getFirst();	//JML membranes
			ms1.clear();
			ms2.clear();
			Iterator<Object> it1 = ms.entrySet().iterator();	//JM: iterator for multiset

			while (it1.hasNext()) {		//JM: while there is still iterator for multiset; I think it goes through here per rule
				//System.out.println("AbstractSelectionExecutionSimulator : microStepExecuteRules :: inside a rule");
				//System.out.println("AbstractSelectionExecutionSimulator : microStepExecuteRules : before rule execution energy generated : " + energyEvol);
				Object o = it1.next();	//JM: multiset has object Rules
				if (o instanceof IRule)
				{
					IRule r = (IRule)o;
					if (r.dissolves())
					{
						ms2.add(r);
					}
					else
					if (noEvolution.checkRule(r))
					{
						ms1.add(r, ms.count(r));
						//System.out.println("REGLA: "+r.toString());
					}
					else
						//System.out.println("AbstractSelectionExecutionSimulator :: before executeRule :: else part");
						executeRule(r,m,currentConfig.getEnvironment(),ms.count(r));	//JM: executeRule
						
						//JM: I added this
						if(r instanceof AbstractRule){
							energyEvol = energyEvol + ((AbstractRule)r).energyAdded;

							//System.out.println("AbstractSelectionExecutionSimulator : microStepExecuteRules : after rule "  + r.toString() + " execution energy generated : " + energyEvol);
						}		
				}

				
			}
			


			Iterator<IRule>it2= ms1.entrySet().iterator();	//JM: this is for the noevolution
			while (it2.hasNext()) {
				IRule r = it2.next();
				//System.out.println("AbstractSelectionExecutionSimulator :: before executeRule :: noevolution");
				executeRule(r,m,currentConfig.getEnvironment(),ms1.count(r));

				if(r instanceof AbstractRule){
					energyEvol = energyEvol + ((AbstractRule)r).energyAdded;

					//System.out.println("AbstractSelectionExecutionSimulator : microStepExecuteRules : after rule "  + r.toString() + " execution energy generated : " + energyEvol);
				}	
				
			}
			it2 = ms2.entrySet().iterator();
			
			while (it2.hasNext()) {	//JM: this is for dissolves
				IRule r = it2.next();
				//System.out.println("AbstractSelectionExecutionSimulator :: before executeRule :: dissolution");
				executeRule(r,m,currentConfig.getEnvironment(),ms2.count(r));

				if(r instanceof AbstractRule){
					energyEvol = energyEvol + ((AbstractRule)r).energyAdded;

					//System.out.println("AbstractSelectionExecutionSimulator : microStepExecuteRules : after rule "  + r.toString() + " execution energy generated : " + energyEvol);
				}
				
			}

			//System.out.println("AbstractSelectionExecutionSimulator : energyEvol = " + energyEvol);
			//System.out.println("AbstractSelectionExecutionSimulator : energy of membrane after = " + (m.getEnergy() + energyEvol));
			m.setEnergy(m.getEnergy() + energyEvol);
		
		}
		
	}
	

	public void selectRule(Object r, ChangeableMembrane m, long count) {

		//System.out.println("AbstractSelectionExecutionSimulator: selectRule");
		//System.out.println("AbstractSelectionExecutionSimulator SelectRule :: label = " + m.getLabel() + " energy in the membrane = " + m.getEnergy());
		System.out.println("AbstractSelectionExecutionSimulator : selectRule " + r.toString() + " count = " + count);
		
		Pair<ChangeableMembrane, MultiSet<Object>> p;
		if (selectedRules.containsKey(m.getId()))
			p = selectedRules.get(m.getId());
		else {
			//System.out.println("AbstractSelectionExecutionSimulator : selectRule :: Does Not containsKey" );
			p = new Pair<ChangeableMembrane, MultiSet<Object>>(m,
					new HashMultiSet<Object>());

			selectedRules.put(m.getId(), p);
		}
		p.getSecond().add(r, count);
	}
	
	
	
	protected void microStepSelectRules() throws PlinguaCoreException {

		microStepSelectRules(currentConfig,(Configuration)currentConfig.clone());

	}
	
	
	
	protected void microStepSelectRules(Configuration cnf, Configuration tmpCnf)
	{
		//System.out.println("AbstractSelectionExecutionSimulator : microStepSelectRules >>");

		//JM: Before all of this, set the tempEnergy first
		Iterator<? extends Membrane> it = tmpCnf.getMembraneStructure().getAllMembranes()
		.iterator();
		Iterator<? extends Membrane> it1 = cnf.getMembraneStructure().getAllMembranes().iterator();

		while (it.hasNext()) {
			ChangeableMembrane tempMembrane = (ChangeableMembrane) it.next();
			ChangeableMembrane m = (ChangeableMembrane)it1.next();

			tempMembrane.setEnergyTemp();
			m.setEnergyTemp();

		}

		//JM: setting tempEnergy end


		it = tmpCnf.getMembraneStructure().getAllMembranes()
		.iterator();
		it1 = cnf.getMembraneStructure().getAllMembranes().iterator();

		while (it.hasNext()) {
			ChangeableMembrane tempMembrane = (ChangeableMembrane) it.next();
			ChangeableMembrane m = (ChangeableMembrane)it1.next();

			//System.out.println("AbsSelecExecSim::\n1st membrane: label = " + m.getLabel() + " energy = " + m.getEnergy());
			//System.out.println("2nd membrane: label = " + tempMembrane.getLabel() + " energy = " + tempMembrane.getEnergy());
			//JM: apparently int the 2nd membrane, the energy becomes 0
			//JM: 2 things, where does tmpCnf comes from and where does it lead to??
			//JM: for ECPE, it goes to NonDeterministicTransitionSimulator

			microStepSelectRules(m,tempMembrane);

		}
	}
	
	protected void microStepInit() {

		//System.out.println("microStepInit");
		selectedRules.clear();
		initDate();
	}

	/**
	 * Select rules for a specific membrane
	 * 
	 * @param m
	 *            A membrane
	 */
	protected abstract void microStepSelectRules(ChangeableMembrane membrane,
			ChangeableMembrane tempMembrane);

	
	protected abstract void printInfoMembrane(ChangeableMembrane membrane);
	protected abstract void printInfoMembraneShort(MembraneStructure membranes);
	
	protected abstract void removeLeftHandRuleObjects(ChangeableMembrane membrane,
			IRule r, long count);

}
