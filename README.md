Find latest executable at main folder: "projectExecutable_v1.0.1.jar" 

*About*

This is an ongoing project on our Object Oriented course in Ariel university.
Authors: Daniel Binyamin id: 204258651 & Tal Gropper 203012323.

This project recives variouse scans captured from the WiggleWifi android app and puts together a main csv file which joins all this information into one.
We have implemented an option to filter the main CSV by various choices and export this filter to a KML file which can be viewed on Google Earth.
We also implemented an algorithm to find user location by 3 MACs ans their signals, and another algorithm to find Rother location its MAC address.
Out project has 3 threads monitoring its resources:
1. A Thread for the wiggleWifi scans directory.
2. A Thread for combined external csv files added.
3. A Thread for combined external Database tables added.

*Build process by Gradle*
This project can be built by Gradle.
By executing "build" task for this project, a few tasks will be performed:
1. The code will be compiled.
2. Some JUnit tests will run to make sure the program works well.
3. A JAR and ZIP files will be created.
4. A JavaDoc will be created.
5. Some temporary files will be deleted after compilation.


*Run Instrucios from code*
The program can be controlled by a Graphic User Interface.
To run the program you will need to run "MainWinfowUI" class.
Once run, you will be asked to enter the path to your WiggleWifi scans.
Then you will be asked to enter the path to which you want your output.csv file to be created to.
At this point you have an output.csv file ready at the directory you chose.
Now you will have a few options:
1. Filter the data and create a KML file with a timeline, which can be viewd in Google-Earth desktop application.
2. Locate a router of a Wifi network by a few parameters.
3. Locate your location by a few parameters (wifi networks and their signals).
4. Add more records from a CSV file, or from a mySql table from a remote server.
you can end by entering 0.


*Code Structure*
- A Graphic-UI class.
- A console-UI class. This class information from the user and send it the "ProgramCore" class.
- A program-core class, which is the "brain" of the program. This class holds a "Records" object and depends on the user requests, filter or do other manipulations on the information.
- A Records class, which includes a list of SingleRecords. This is our main data-class which we work with. It has many of our basic methods which we use to export to KML and CSV.
- A SingleRecord class, which represents a single scan at a specific time and place (point on the map), which includes information regarding that scan (Wifi list, location, time etc...).
- A Wifi class with all network veriables. 
- A WiggleLine class, which represents a single network scan captured by the WiggleWifi app. This class is used to help us create our final CSV.
- A Filter class, which holds a list of Conditions that the data should be filtered by.
- A Condition interface which implemented by a lambda-function for each condition specified by the user. This interface has only one method "test", which gets a SingleRecord object and return TRUE value if the given SingleRecord object matches the boolean expression that was set bt the lambda function.
- A filter-for-records class, which actually wraps Filter object with a toString method and a few more. This class was declared as "serializable".
- A locate-Router-Algo class, which represents an implementation for an algorithm that gets a MAC address, and return the estimated location of that MAC (or router device).
- A find-user-Algo class, which represents an implementation for an algorithm that gets a few MAC addresses and their signals recorded at a point of time, and return the estimated location of the user.
***A UML ‫‪Class‬‬ diagram class can be found also in the docs folder (Project/Object Orinted Project/docs) of this project***


To handle the KML types we used the JAK (Java api for KML) api which is part of de.micromata projects. We found a version of this api in .jar format and used it because it is more comfertable to work with as opposed to open source. We picked this api because after some research online we came to conclusion that this api is the one vastly used and which has the most example.
For a kick start we used examples from:
https://labs.micromata.de/projects/jak/quickstart.html

