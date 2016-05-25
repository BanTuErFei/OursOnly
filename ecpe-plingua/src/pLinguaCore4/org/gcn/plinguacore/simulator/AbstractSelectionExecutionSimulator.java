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
//newly added


import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;


import org.gcn.plinguacore.simulator.cellLike.CellLikeSimulator;
import org.gcn.plinguacore.util.psystem.rule.RulesSet;
import org.gcn.plinguacore.util.psystem.rule.IPriorityRule;

import org.gcn.plinguacore.util.RandomNumbersGenerator;


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
		selectedRules = new LinkedHashMap<Integer, Pair<ChangeableMembrane, MultiSet<Object>>>();

		// TODO Auto-generated constructor stub
	}
	
	protected final Map<Integer, Pair<ChangeableMembrane, MultiSet<Object>>> getSelectedRules() {
		return selectedRules;
	}

	protected abstract String getHead(ChangeableMembrane m);

	/**
	 * Print short information in the info-channel about current configuration
	 * 
	 * @param first
	 *            It is true if this is the first configuration
	 */
	private void printInfoShort(boolean first) {
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
							"Halting configuration (No rule can be selected to be executed in the next step.)");
		} 

		else if(sameConfig){
			getInfoChannel()
				.println(
					"This is a non-halting configuration.");

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

		if ( (hasSelectedRules() || first) && (!sameConfig)) {
			if (!first) {
				getInfoChannel().println(
						"-----------------------------------------------");
				getInfoChannel().println();
				getInfoChannel().println(
						"    STEP: " + currentConfig.getNumber());

			}
			Iterator<Pair<ChangeableMembrane, MultiSet<Object>>> it1 = selectedRules
					.values().iterator();
			
			while (it1.hasNext()) {
				int tempEnergy=0, tempParentEnergy=0;
				Pair<ChangeableMembrane, MultiSet<Object>> pair = it1.next();
				MultiSet<Object> rules = pair.getSecond();
				Iterator<Object> it = rules.entrySet().iterator();

				if (it.hasNext()) {
					getInfoChannel().println();
					getInfoChannel().println(
							"    Rules selected for "
									+ getHead(pair.getFirst()));


					while (it.hasNext()) {
						Object r = it.next();
						getInfoChannel().println(
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

			/* This marks the end of the conditional if(hasSelectedRules() && !sameConfig */
			else if(!hasSelectedRules() || emptyMultiSet()){	/* emptyMultiSet - bacause elements in selectedRules cannot be removed, it has to be checked this way */

				getInfoChannel()
						.println(
								"Halting configuration (No rule can be selected to be executed in the next step).");

			}
			else if(sameConfig){
				getInfoChannel()
						.println(
								"This is a non-halting configuration.");
			}
			else{
				getInfoChannel()
					.println(
						"Error");

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

		/* This one returns equals if the currentConfig and the previous config are the same */

		Iterator<? extends Membrane> itTemp = currentConfig.getMembraneStructure().getAllMembranes()
				.iterator();
		Iterator<? extends Membrane> itPrevTemp = configurationPrev.getMembraneStructure().getAllMembranes()
				.iterator();

		if(currentConfig.getMembraneStructure().getAllMembranes().size() != configurationPrev.getMembraneStructure().getAllMembranes().size()){
			return false;
		}
		while(itTemp.hasNext()){
			if(itPrevTemp.hasNext()){
				if(((ChangeableMembrane)itTemp.next()).equals((ChangeableMembrane)itPrevTemp.next()))
						continue;
				else
					return false;
			}
			
		}
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
		microStepSelectRules();
		
		if (hasSelectedRules()) {
			microStepExecuteRules();
			
			currentConfig.setNumber(currentConfig.getNumber()+1);
		}

		/* For printing */
		if (getVerbosity()>0)
		{
			if (getVerbosity() > 1){
				sameConfig = equals();
				printInfo(false);
			}
			else{
				sameConfig = equals();
				printInfoShort(false);
			}
		}



		return (hasSelectedRules() && !sameConfig);
	}
	
	protected boolean hasSelectedRules(){
		return !selectedRules.isEmpty();
	}
	
	protected void executeRule(IRule r,ChangeableMembrane m,MultiSet<String>environment,long count)
	{	
		boolean isExecuted = r.execute(m,environment,count);


		if(isExecuted == false){	
			Pair<ChangeableMembrane, MultiSet<Object>> tempPair = new Pair<ChangeableMembrane, MultiSet<Object>>(m,
					new HashMultiSet<Object>());

			tempPair = selectedRules.get(m.getId());
			MultiSet<Object> tempMulti = tempPair.getSecond();
			tempMulti.remove(r,count);

			if(selectedRules.containsKey(m.getId())){
				selectedRules.put(m.getId(),tempPair);
			}
			else{
				Pair<ChangeableMembrane, MultiSet<Object>> tempPair2 = new Pair<ChangeableMembrane, MultiSet<Object>>(m,
					new HashMultiSet<Object>());

				tempPair2.setSecond(tempPair.getSecond());
				
				selectedRules.put(m.getId(),tempPair2);
			}
			
		}
		else{
			/* Get the number of executions */

			if(r instanceof AbstractRule){
				if(((AbstractRule)r).getExecutionsDone()!=0){
					/* Change the number of executions in the selected rule */
					
					Pair<ChangeableMembrane, MultiSet<Object>> tempPair = new Pair<ChangeableMembrane, MultiSet<Object>>(m,
					new HashMultiSet<Object>());

					tempPair = selectedRules.get(m.getId());

					MultiSet<Object> tempMulti = tempPair.getSecond();

					tempMulti.remove(r,count);
					tempMulti.add(r,((AbstractRule)r).getExecutionsDone());

				}
			}
		}

	}
	
	protected void microStepExecuteRules() {

		/* Set the prevEnergy */
		Iterator<Pair<ChangeableMembrane, MultiSet<Object>>> itTemp = selectedRules
		.values().iterator();
		
		while (itTemp.hasNext()) {	
			Pair<ChangeableMembrane, MultiSet<Object>> pTemp = itTemp.next();
			ChangeableMembrane mTemp = pTemp.getFirst();
			mTemp.setEnergyPrev();
		}

		Iterator<Pair<ChangeableMembrane, MultiSet<Object>>> it = selectedRules
				.values().iterator();
		MultiSet<IRule> ms1 = new HashMultiSet<IRule>();
		MultiSet<IRule> ms2 = new HashMultiSet<IRule>();
		

		while (it.hasNext()) {
			Pair<ChangeableMembrane, MultiSet<Object>> p = it.next();

			/* Need to add the energy produced in the evolution in the end so as not to disturb with the communication rules in the same step */
			
			int energyEvol = 0;
			
			MultiSet<Object> ms = p.getSecond();
			ChangeableMembrane m = p.getFirst();	
			ms1.clear();
			ms2.clear();
			Iterator<Object> it1 = ms.entrySet().iterator();
			while (it1.hasNext()) {		/* While there is still iterator for multiset; It goes through here per rule */

				Object o = it1.next();	/* Multiset has object Rules */
				if (o instanceof IRule)
				{
					IRule r = (IRule)o;
					if (r.dissolves())
						ms2.add(r);
					else
					if (noEvolution.checkRule(r))
						ms1.add(r, ms.count(r));
					else{
						executeRule(r,m,currentConfig.getEnvironment(),ms.count(r));
						
						if(r instanceof AbstractRule)
							energyEvol = energyEvol + ((AbstractRule)r).energyAdded;
					}	
				}

				
			}
			


			Iterator<IRule>it2= ms1.entrySet().iterator();
			while (it2.hasNext()) {
				IRule r = it2.next();
				executeRule(r,m,currentConfig.getEnvironment(),ms1.count(r));

				if(r instanceof AbstractRule)
					energyEvol = energyEvol + ((AbstractRule)r).energyAdded;
				
			}
			it2 = ms2.entrySet().iterator();
			
			while (it2.hasNext()) {	
				IRule r = it2.next();
				executeRule(r,m,currentConfig.getEnvironment(),ms2.count(r));

				if(r instanceof AbstractRule)
					energyEvol = energyEvol + ((AbstractRule)r).energyAdded;
				
			}
			m.setEnergy(m.getEnergy() + energyEvol);
		
		}
		
	}
	

	public void selectRule(Object r, ChangeableMembrane m, long count) {
	
		Pair<ChangeableMembrane, MultiSet<Object>> p;
		if (selectedRules.containsKey(m.getId()))
			p = selectedRules.get(m.getId());
		else {
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
		/* Before all of this, set the tempEnergy first */
		Iterator<? extends Membrane> it = tmpCnf.getMembraneStructure().getAllMembranes()
		.iterator();
		Iterator<? extends Membrane> it1 = cnf.getMembraneStructure().getAllMembranes().iterator();

		while (it.hasNext()) {
			ChangeableMembrane tempMembrane = (ChangeableMembrane) it.next();
			ChangeableMembrane m = (ChangeableMembrane)it1.next();

			tempMembrane.setEnergyTemp();
			m.setEnergyTemp();
		}

		it = tmpCnf.getMembraneStructure().getAllMembranes().iterator();
		it1 = cnf.getMembraneStructure().getAllMembranes().iterator();
		if(getPsystem().getECPePriority()!=0){

			boolean applied=false;
			while (it.hasNext()) {
				ChangeableMembrane tempMembrane = (ChangeableMembrane) it.next();
				ChangeableMembrane m = (ChangeableMembrane)it1.next();

				if(microStepSelectRules(m,tempMembrane,false)){
					applied=true;
				}

			}
			if(!applied){
					it = tmpCnf.getMembraneStructure().getAllMembranes().iterator();
		    	it1 = cnf.getMembraneStructure().getAllMembranes().iterator();
				while (it.hasNext()) {
				ChangeableMembrane tempM = (ChangeableMembrane) it.next();
				ChangeableMembrane mem = (ChangeableMembrane)it1.next();
				microStepSelectRules(mem, tempM, true);
				}
			}
		}

		/*For nonECPe models */
		else{
			while (it.hasNext()) {
				ChangeableMembrane tempMembrane = (ChangeableMembrane) it.next();
				ChangeableMembrane m = (ChangeableMembrane)it1.next();
				microStepSelectRules(m,tempMembrane);
			}
		}

	}
	
	protected void microStepInit() {

		selectedRules.clear();
		initDate();
	}
	protected boolean microStepSelectRules(ChangeableMembrane m,
			ChangeableMembrane temp, boolean checkedPrio) {
			int ecpe_priority = getPsystem().getECPePriority();
			boolean applicable=false;
			int n=2;
			List<IRule>aux = new ArrayList<IRule>();

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
					while(it.hasNext()){
						IRule r = it.next();
					}

					it = aux.iterator();
					applicable=isPrioApplicable(ecpe_priority ,i,it, m, temp);
					
				}
				if(checkedPrio){

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
							while(it.hasNext()){
								IRule r = it.next();
					}

						it = aux.iterator();
						if(ecpe_priority==1){
							applicable=isPrioApplicable(2 ,i,it,m, temp);
						}
						else{
							applicable=isPrioApplicable(1 ,i,it,m, temp);
						}
					}		
									
				}
		return applicable;

	}

	public boolean isPrioApplicable(int prio,int i,Iterator<IRule> itr, ChangeableMembrane m, ChangeableMembrane temp){
		Iterator<IRule> it= itr;
		boolean applicable=false;
		switch(prio){
			case 1:
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

						if (!(r instanceof IPriorityRule) && count>0 && i!=1){
							applicable=true;
							count = RandomNumbersGenerator.getInstance().nextLong(count);	
						}
						if (count > 0) {
							applicable=true;
							r.executeDummy(temp, (int)count);
							selectRule(r, m, count);
							removeLeftHandRuleObjects(temp, r, count);
						}
					}

			break;

			case 2:

				HashSet<IRule> itCommunicationRule = new HashSet<IRule>();
					while(it.hasNext()){
						IRule r = it.next();
						if(r.isSendIn() || r.isSendOut() || r.isAntiport())
							itCommunicationRule.add(r);
					}

					Iterator<IRule> itComm = itCommunicationRule.iterator();
						
					while (itComm.hasNext()) {
						
						IRule r = itComm.next();	

						long count = r.countExecutions(temp); 	
						if (!(r instanceof IPriorityRule) && count>0 && i!=1){
							applicable=true;
							count = RandomNumbersGenerator.getInstance().nextLong(count);	
							}
						if (count > 0) {
							applicable=true;
							r.executeDummy(temp, (int)count);
							selectRule(r, m, count);
							removeLeftHandRuleObjects(temp, r, count);
						}
					}
			break;
		}
		return applicable;
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
