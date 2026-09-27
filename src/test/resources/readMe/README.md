## GED-inline

GED-inline is a validator for GEDCOM files. GEDCOM files contain text data describing family trees. They are used to exchange family tree data between different genealogical solutions. See [gedcom.io](https://gedcom.io/) for further information.

GED-inline can validate GEDCOM version 5.5, 5.5.1 and 7.0. However for 7.0 there is currently no support for GEDZIP and incomplete support for extensions. In addition there is some support for the unofficial GEDCOM 5.5.5 standard.

The validator is also available for online use at [GED-inline](https://ged-inline.org).

### Getting Started
#### Prerequisites

A Java JDK must be installed on your machine. You can verify this by typing

```
javac -version
```

on your command line. GED-inline 4 requires Java 17 or later. Use the `main` branch:

| Java version |   Branch   | GED-inline version | Status                                   | Tested specifically with          |
|:-------------|:----------:|:------------------:|------------------------------------------|-----------------------------------|
| Java 17 ++   |    main    | V4.x.x             | Current development                      | Java 17, Java 21, Java 23, Java 25 |
| Java 17 ++   |  v4-main   | V4.x.x             | Kept for existing users, same as main    | Java 17, Java 21, Java 23, Java 25 |
| Java 8 - 16  |  v3-main   | V3.x.x             | Deprecated, no further fixes             | Java 8, Java 11                   |

The current version of GED-inline is ${version}

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
java -jar build/libs/gedinline-${version}.jar build/resources/test/gedcom-files/harvey-70.ged
```

To save the result, redirect the output to a file:

```
java -jar build/libs/gedinline-${version}.jar build/resources/test/gedcom-files/harvey-70.ged > report.txt
```

Records are validated on a pool of worker threads, one per available processor, while the file itself is read on
the calling thread. The report is the same whatever the number of threads. Use `--parallelism N` (or `-p N`) to
choose the number of validation threads yourself; `--parallelism 1` gives the single-threaded behaviour of earlier
versions:

```
java -jar build/libs/gedinline-${version}.jar --parallelism 1 build/resources/test/gedcom-files/harvey-70.ged
```

### The jar file

GED-inline can also be accessed as a Java library:

```
build/libs/gedinline-${version}.jar
```

Validation is performed by the gedinline.main.GedInlineValidator class. The validater requires a GEDCOM file to analyse and a PrintWriter to write the validation report to. Create it like this:

```
new GedInlineValidator(gedcomFile, outputReportWriter)
```

Use the validate() method to start validation. By default records are validated on as many threads as there are
available processors; call setParallelism(n) before validate() to change this, with 1 meaning single-threaded. For
further details see the groovydoc at build/docs/groovydoc/gedinline/main/GedInlineValidator.html.

### License

This project is licensed under the MIT License - see the [LICENSE.txt](LICENSE.txt) file for details

### Acknowledgments

* Thanks to Michael Kay for the AnselInputStreamReader
* Thanks to Gregory Pakosz for the UnicodeBomInputStream code
