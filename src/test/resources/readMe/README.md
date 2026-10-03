## GED-inline

GED-inline is a validator for GEDCOM files. GEDCOM files contain text data describing family trees. They are used to exchange family tree data between different genealogical solutions. See [gedcom.io](https://gedcom.io/) for further information.

GED-inline can validate GEDCOM version 5.5, 5.5.1 and 7.0. However for 7.0 there is currently no support for GEDZIP and incomplete support for extensions. In addition there is some support for the unofficial GEDCOM 5.5.5 standard.

The validator is also available for online use at [GED-inline](https://ged-inline.org).

### Getting Started
#### Prerequisites

GED-inline 4 requires Java 17 or later. You can check which version you have by typing

```
java -version
```

on your command line. GED-inline has been tested specifically with Java 17, Java 21, Java 23 and Java 25.

#### Downloading GED-inline

The simplest way to get GED-inline is to download the file `gedinline-${version}.jar` from the
[latest release](https://github.com/nigel-parker/gedinline/releases/latest). Nothing else needs to be installed,
and there is nothing to build.

The current version of GED-inline is ${version}

#### Building GED-inline from source

You only need to build GED-inline yourself if you want to change the code or try changes that have not been released
yet. Building needs a Java JDK, not just Java. You can verify that one is installed by typing

```
javac -version
```

Use the `main` branch:

| Java version |   Branch   | GED-inline version | Status                                   |
|:-------------|:----------:|:------------------:|------------------------------------------|
| Java 17 ++   |    main    | V4.x.x             | Current development                      |
| Java 17 ++   |  v4-main   | V4.x.x             | Kept for existing users, same as main    |
| Java 8 - 16  |  v3-main   | V3.x.x             | Deprecated, no further fixes             |

To build the validator, type the following in the project directory:

```
gradlew.bat gedinline
```

on Windows, or

```
./gradlew gedinline
```

on Mac or Linux. If everything goes well you should see a 'BUILD SUCCESSFUL' message, and the jar file is in
`build/libs/gedinline-${version}.jar`.

### Running the standalone version

GED-inline is run from the command line. In the folder where you saved the jar file, type

```
java -jar gedinline-${version}.jar my-family-tree.ged
```

where `my-family-tree.ged` is the GEDCOM file you want to validate. To save the result, redirect the output to a
file:

```
java -jar gedinline-${version}.jar my-family-tree.ged > report.txt
```

If you have built GED-inline from source, you can try it out on an example file from the project:

```
java -jar build/libs/gedinline-${version}.jar build/resources/test/gedcom-files/harvey-70.ged
```

### The jar file

GED-inline can also be used as a Java library. Add the jar file to your classpath: either the downloaded
`gedinline-${version}.jar`, or `build/libs/gedinline-${version}.jar` if you built it yourself. The jar is
self-contained: it includes the libraries GED-inline needs (Groovy 4 and Apache Commons Validator).

Validation is performed by the gedinline.main.GedInlineValidator class. The validator requires a GEDCOM file to analyse and a PrintWriter to write the validation report to. Create it like this:

```
new GedInlineValidator(gedcomFile, outputReportWriter)
```

Use the validate() method to start validation. If you build from source, `./gradlew gedinline` also generates the
groovydoc; for further details see build/docs/groovydoc/gedinline/main/GedInlineValidator.html.

### License

This project is licensed under the MIT License - see the [LICENSE.txt](LICENSE.txt) file for details

### Acknowledgments

* Thanks to Michael Kay for the AnselInputStreamReader
* Thanks to Gregory Pakosz for the UnicodeBomInputStream code
