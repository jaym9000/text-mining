# Text Mining over biological data to predict protein stability and solubility
  
  This is a text mining project, I was handed stability and solubility 'rules' (key words to mine from files) to the current Java program.\ 
  This is done by adding JAPE rules that Shawn Kroetsch worked on. 
  I combined these JAPE rules, Java, and GATE (The text mine software) to text mine over the collected data and activate these rules. 
  
  If you are interested in a deeper explenation of the project and my methods, the PDF file explains everything in further details.
  
  [**PLEASE READ INSTRUCTIONS BELOW BEFORE RUNNING PROJECT**]
  
  What you need
-------------
1. Gate NLP tool
1.1 Any of the versions 8.0, 8.1, 8.2, 8.3, 8.4, 8.4.1 [https://gate.ac.uk/download/]
1.2 Assuming you have chosen 8.0, download gate-8.0-build4825-BIN.zip and extract somewhere.
1.3 The installation path may look like C:\users\shawn\...\gate-8.0-build4825 which will be needed later.

2. A Java IDE: You can install it using Toolbox or individually. Toolbox installation will be easier for installing other tools.
2.1 Install the Toolbox app and manage your installation and licensing [https://www.jetbrains.com/toolbox-app/]
2.2 You can also install Intellij IDEA separately [https://www.jetbrains.com/idea/]
2.3. Install the Ultimate version. The license is free for UNB students. Register using the Toolbox.

3. A directory with the input files
3.1 This directory is a corpora of *.txt files which have been converted and cleaned from their pdf sources
3.2 The location of the directory is C:\users\shawn\...\Corpora\dhla-corpus-dev which contains the *.txt files
3.3 Create two new empty directories inside dhla-corpus-dev: 'TMP' for holding cache files and 'XML' for holding XML

4. The project source code as a maven project
4.1 The project directory is probably called 'trunk' which contains the maven project
4.2 It will contain at least pom.xml file and a src directory along with some other directories/files

Set up the project configuration/path
-------------------------------------
1. Open IntelliJ IDEA -> Open or Import -> browse to trunk dir -> OK
2. Project loaded -> Expand trunk[mutation-impact] on the left 'Project' pane
3. Expand src -> main -> resources -> Open project.properties -> Modify the path as GATE_HOME:C
4. Set the gate path as GATE_HOME: YOUR_GATE_INSTALLATION_DIRECTORY e.g. GATE_HOME:C:/users/shawn/.../gate-8.0-build4825
   Note that if you are using Windows, you may have to use '/' instead of the '\'. Save the changes.
5. Open pom.xml -> Go to line 15, <gate.version>GATE_VERSION_NUMBER</gate.version> -> Set the version number e.g.
<gate.version>8.0</gate.version> if you have downloaded 8.0. Any other version of 8.1, 8.2, 8.3, 8.4, 8.4.1 would work.
6. Go to the menu at the top: Run -> Edit Configurations -> Run/Debug Configurations window opens up ->
   Select '+' -> Select 'Application' -> 3 tabs opened up 'Configuration', 'Code Coverage', 'Logs' ->
   On the 'Configurations' tab we need to fill up two values for: 'Main class:' and 'Program arguments:'->
   For the 'Main class' click ... at the right to browse -> window 'Choose Main Class' -> Select 'Main2' -> OK
7. Inside the 'Program arguments' text box, copy and paste the command with the correct path:

-d --miepipe  --cache -i C:\users\shawn\...\Corpora\dhla-corpus-dev -t C:\users\shawn\...\Corpora\dhla-corpus-dev\TMP -o C:\users\shawn\...\Corpora\dhla-corpus-dev\XML

Here 'd' is for showing debug messages, 'cache' is for storing temporary cache files inside the 'TMP' directory with the option 't',
'i' option refers to the input directory 'dhla-corpus-dev' where all the *.txt files are located, and 'o' refers to the output directory
'XML' where output XML files will be stored. The 'miepipe' means that the program will run a pipeline for mutation grounding and then mutation
impact extraction.

8. After correcting the path and pasting, click 'Apply' and 'OK'. Now go to menu Run -> Run 'Unnamed' run the pipeline.
9. Once the pipeline runs, check the TMP and XML directories.
10. In order to save the results into tabular form as CSV files, you need to change the 'Program arguments' and run the program again as follows:
11. Copy and paste the following command:

-d --mie2tab -i C:\users\shawn\...\Corpora\dhla-corpus-dev\XML -o C:\users\shawn\...\Corpora\dhla-corpus-dev

The 'mie2tab' will analyze the XML files as input and produce two CSV files named results-mg.csv and results-mie.csv in the output directory 'dhla-corpus-dev'

Click 'Apply' and 'OK'. Run -> Run 'Unnamed'

Go to 'dhla-corpus-dev' directory to see the csv files.
