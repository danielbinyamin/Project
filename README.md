*About*/n
This is an ongoing project on our Object Oriented course in Ariel university.
Authors: Daniel Binyamin & Tal Gropper.
This project recives variouse scans captured from the WiggleWifi android app and puts together a main csv file which joins all this information into one. We have implemented an option to filter the main CSV by various choices and export this filter to a KML file which can be viewed on Google Earth.

*Run Instrucios*/n
The program now has only console-UI.
In a few weeks a GUI will be added.
For now, To run the program you will need to run "consoleUI" class.
Once run, you will be asked to enter the path to your WiggleWifi scans.
Then you will be asked to enter the path to which you want your output.csv file to be created to.
At this point you have an output.csv file ready at the directory you chose.
Now you will have a few options:
1. Filter the data and create a KML file with a timeline, which can be viewd in Google-Earth desktop application.
2. Locate a router of a Wifi network by a few parameters.
3. Locate your location by a few parameters (wifi networks and their signals).
you can end by entering 0.


*Code Structure*/n
- A WiggleLine class which represents a single network scan captured by the WiggleWifi app. This class is used to help us create our final CSV.
- A Wifi class with all network veriables. 
- A SingleRecord class which represents a single scan at a specific time & place(point on the map) which includes information regarding that scan (Wifi list, location, time etc...) .
- A Records class which includes a list of SingleRecords. This is our main class which we work with. It has many of our basic methods which we use to export to KML and CSV.
- A console-UI class.
- A program-core class which is the "brain" of the program.
- A locate-Router-Algo which take care of calculationg the estimated location of a given MAC address.
- A find-user-algo which take care of calculationg the estimated location of a the user itself by given wifi networks and their values at a specific location.

To handle the KML types we used the JAK (Java api for KML) api which is part of de.micromata projects. We found a version of this api in .jar format and used it because it is more comfertable to work with as opposed to open source. We picked this api because after some research online we came to conclusion that this api is the one vastly used and which has the most example.
For a kick start we used examples from:
https://labs.micromata.de/projects/jak/quickstart.html

