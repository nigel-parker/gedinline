## GED-inline

GED-inline is a validator for GEDCOM files. 
GEDCOM files contain text data describing family trees. 
They are used to exchange family tree data between different genealogical 
solutions. See [gedcom.org](https://www.gedcom.org/) for further information.

GED-inline can validate GEDCOM version 5.5 and 5.5.1. Support for the new GEDCOM 5.5.5 standard has beta status.
Meaning that most valid files will be recognised as such, but not all error situations
will be flagged.

The validator is also available for use at [GED-inline](http://ged-inline.elasticbeanstalk.com).

### Getting Started
#### Prerequisites

A recent Java JDK must be installed on your machine. You can verify that it is by typing

```
javac -version
```

on your command line.

#### Building GED-inline

To build the validator, type the following in the project directory:

```
gradlew.bat gedinline
```

on Windows, or

```
./gradlew gedinline
```

on Mac or Linux. If everything goes well you should see a 'BUILD SUCCESSFUL' message.

### Running the standalone version

GED-inline can be run from the command line. Try it out on an example file from the project:

```
java -jar build/libs/gedinline-3.0-beta-2.jar build/resources/test/gedcom-files/olson-555.ged
```

To save the result, redirect the output to a file:

```
java -jar build/libs/gedinline-3.0-beta-2.jar build/resources/test/gedcom-files/olson-555.ged > report.txt
```

### The jar file

GED-inline can also be accessed as a Java library:

```
build/libs/gedinline-3.0-beta-2.jar
```

Validation is performed by the gedinline.main.GedInlineValidator class. The validater requires a GEDCOM file
to analyse and a PrintWriter to write the validation report to. Create it like this:

```
new GedInlineValidator(gedcomFile, outputReportWriter)
```

Use the validate() method to start validation. For further details see the groovydoc at 
build/docs/groovydoc/gedinline/main/GedInlineValidator.html.

### License

This project is licensed under the MIT License - see the [LICENSE.txt](LICENSE.txt) file for details

### Acknowledgments

* Thanks to Michael Kay for the AnselInputStreamReader
* Thanks to Gregory Pakosz for the UnicodeBomInputStream code
