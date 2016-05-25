# ECPe implementation in Plingua

Authors:
Jimenez, Joni Marie
Castillo, Algina 
Juayong, Richelle
Hernandez, Nestine



An implementation of Evolution Communication P Systems with energy in Plingua by extending the existing model <evolution_communication>.


Limitations
————-
- Plingua(.pli) file is the only input file format that can be used in plingua_sim command to generate correct output file in this model.
	(.xml and .bin will not give desired output)
- Tested in Windows 7(64-bit),Windows 10(64-bit) and Mac OS X Yosemite(v10.10.5).
- Used and modified plinguaCore 4.0 found in http://www.p-lingua.org/wiki/index.php/Download#pLinguaCore_4.0


Dependency
————-
- Java 1.8.0_65

Installation
————-
1. Download and extract src folder or the Source Codes folder in https://github.com/BanTuErFei/ECPe.
2. Open command prompt/terminal
3. In the command prompt/terminal, navigate inside the directory of the src/plinguacore folder.

4. Type 
	java org/gcn/plinguacore/applications/AppMain plingua_sim -pli [inputFile.pli] -o [outputFile.txt] 
	in the command prompt.
For the list of commands available, just type:
	java org/gcn/plinguacore/applications/AppMain plingua_sim

5. Check outputFile in the directory of the src folder.


Input format
————-
	- Evolution Rule: [a]'h-->[b]$e : R;
	- Send-in Rule: a[]'h$energy1 --> [a] : R;
	- Send-out: [b]'h$energy2--> b[] : R;
	- Antiport: a[b]'h$energy1,$energy2 -->b[a] : R;
    where a and b are objects, h is the membrane label, e, where e is an integer >= 0, is the energy produced, energy1 is the energy that will be consumed in the parent membrane of membrane h, energy2 is the energy is the energy consumed in the membrane h, and R is the range in the form 1<variable<n.


Test Inputs and Outputs
————-
	-3sat.pli and vcp.pli are based on (1).
	-anbn.pli is based on (2).


Folders
—————-
src - contains the P-Lingua source code
input - contains the .pli files.
	input/Mecosim - contains input for Mecosim
	input/General Inputs - contains input for general solutions	


References:
(1) Hernandez, Nestine Hope., Juayong, Richelle Ann., Adorna, Henry. "On Communication Complexity of Some Hard Problems in ECPe Systems". Membrane Computing 14th International Conference. 2013.
(2) Juayong, Richelle Ann. (2014). Introduction to Evolution-Communication P systems with Energy [CS 297 Presentation]. Retrieved from https://sites.google.com/site/richjuayong/class-works/cs-297-introduction-to-ecpe-systems.

feedback: richelleann.juayong@gmail.com
