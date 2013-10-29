FITS testing tool
======================

A tool to test the [File Information Tool Set (FITS)](https://code.google.com/p/fits/) against a test corpora.
This tool will allow to pass a FITS implementation by argument and test it against a set of files, producing and report on Microsoft Excel format. Optionally, you can pass a ground truth file which will be used to validate the FITS output results in terms of mime type, puid and validation.

## Build

To build the application simply clone the project and execute the following Maven command.

```bash
$ mvn clean package
```
The binary will appear at `target/fits-testing-1.0-SNAPSHOT-jar-with-dependencies.jar`

## Use

To see the usage options execute the command:

```bash
$ java -jar target/fits-testing-1.0-SNAPSHOT-jar-with-dependencies.jar -h
usage: report
 -d <arg>   directory with files to process
 -f <arg>   fits home
 -g <arg>   ground truth file
 -h         print this message
 -o <arg>   output file
```




