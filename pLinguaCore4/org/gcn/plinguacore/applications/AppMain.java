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

package org.gcn.plinguacore.applications;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class provides an entry point for using all pLinguaCore functionality
 *
 * @author Research Group on Natural Computing (http://www.gcn.us.es)
 *
 */
public abstract class AppMain {
	private static final String COMPILE = "plingua";
	private static final String SIMULATE = "plingua_sim";




	/**
	 * Main method for using pLingua API
	 *
	 * @param args
	 *            the command-line parameters stated on the documentation
	 */
	public static void main(String[] args) {

		if (args.length<=1)	//JoniMarie(JM): if args does not have sufficient arguments,
		{
			AppCompiler.printCompilerHelp();	//JM: print help
			AppSimulator.printSimulatorHelp();
		}
		String argsCom[] = new String[args.length - 1]; //JM: made a new string array and stored the contents of args to it
		for (int i = 1; i < args.length; i++)
			argsCom[i - 1] = args[i];
		if (args[0].equals(SIMULATE)) {	//JM: if simulate, call simulate
			AppSimulator.main(argsCom);	//JM: where AppSimulator comes in - passes other args
			return;
		}
		if (args[0].equals(COMPILE)) {	//JM: if compile, call compile
			AppCompiler.main(argsCom);	//JM: where AppCompile comes in - passes other args
			return;
		}
		System.out	//if not simulate or compile
				.println("Command not recognized, please type the command again");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); //JM: in the case that the argument was typed again

		try {
			AppMain.main(br.readLine().split(" "));//JM: it will pass through main again
		} catch (IOException e) {

			System.out.println(e.getMessage());
		}

	}

}
