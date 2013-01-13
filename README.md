This Eclipse plug-in supports JavaScript testing via JsTestDriver with following 2 functions

1. Generate library files and configuration file for JsTestDriver automatically
2. Generate QUnit test code template

[Requirement]
* Java 6.0 or above
* Eclipse 3.5 or above
* JsTestDriver Eclipse plug-in installed

[Instruction]
1. Build Eclipse plug-in
(Easiest way to build Eclipse plug-in is to import the project to Eclipse and export it as "Deployable plug-ins and fragments")
2. Install the plug-in to "dropins" folder of Eclipse
3. Right click the target project and select "New" - "Other..."
![Alt text](jstestdriver-eclipse-project-helper/image/image1.png)
4. Select "JsTestDriver Project Helper" - "JsTestDriver Project Configuration"
![Alt text](jstestdriver-eclipse-project-helper/image/image2.png)
5. Fill the settings
6. Library files are copied to library directory and configuration file is created to the project root directory
7. Select "JsTestDriver Project Helper" - "QUnit Test Case" to create test code template