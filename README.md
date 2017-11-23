This is aמ ongoing project on our Object Oriented course in Ariel university.

This project recives variouse scans captured from the WiggleWifi android app and puts together a main csv file which joins all this information into one. We have implemented an option to filter the main CSV by various choices and export this filter to a KML file which can be viewed on Google Earth.


To run this project you will need to run the RunnableClass.java which is our main class to execute all our code.
Once run, you will be asked to enter the path to your WiggleWifi scans.
Then you will be asked to enter the path to which you want your output.csv file to be created to.
at this point you have an output.csv file ready at the directory you chose.
Now you will have an option to choose a filter and directory path for the filtered KML file.
you can end by entering 0.


The structure of the code is as follows:

-A WiggleLine class which represents a single network scan captured by the WiggleWifi app. This class is used to help us create our final CSV.

-A Wifi class with all network veriables. 

- A singleRecord class which represents a single scan at a specific time & place(point on the map) which includes information regarding that scan (Wifi list, location, time etc...) .

- A Records class which includes a list of SingleRecords. This is our main class which we work with. It has many of our basic methods which we use to export to KML and CSV.

-A main executable class called RunnableClass.


