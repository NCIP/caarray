Assumptions
-----------
The current installation assumes you're running on a 'clean' Windows machine. The MySQL default password is curently used (<blank>).

There are three primary steps to getting the installation to work. The first is creating the installation and the second is running the installation. 

1. Run a build to create the caarray.ear by opening a command prompt from your location directory that represents http://gforge.nci.nih.gov/svnroot/caarray2/trunk/software/build and type ant -Dnotest=true

2. To create the installation, open a command prompt from your location directory that represents http://gforge.nci.nih.gov/svnroot/caarray2/trunk/tools/installer and type ant selfextract

3. From the same directory, type ant install (assuming you've already extracted the file from step 2)

4. (Optional) Copy the tools\installer\install.bat, tools\installer\target\dist\caarray-installer.jar and extracted tools\jdk1.5.0_10.zip to flash drive
