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

To test a FITS against the included corpora, execute the following command:

```bash
$ java -jar target/fits-testing-1.0-SNAPSHOT-jar-with-dependencies.jar -d corpora/files/ -f ../fits-harvard-lts/ -g corpora/groundtruth.xls -o results/results-fits-harvard-lts.xls
```

Note that this command assumes you have downloaded the harvard-lts version of FITS into the parent folder.

## Results

These are test results that resulted from the execution of this tool against several FITS versions.

* Repository: the code repository that provides the FITS version
* Release: the release or branch that identifies the code snapshot
* % Mime type: the percentage of files from the corpora that were identified with the correct mime type
* % Pronom ID: the percentage of files from the corpora that were identified with the correct Pronom ID
* # Features: the average number of features extracted from each file of the corpora
* % Validation: the percentage of correct validations of file formats in corpora
* Results: a link to the results output

Test date: 2013-10-29

| Repository | Release | % Mime Type | % Pronom ID | # Features | % Validation | Results |
|------------|---------|-----------|-----------|-------------|------------|---------|
|[harvard-lts](https://github.com/harvard-lts/fits)|master ([0a1cd57f22](https://github.com/harvard-lts/fits/commit/0a1cd57f22c24f1c8be7ab75607628058505b961))||||||


## License

This software is available under the [LGPL version 3 license](LICENSE). All corpora was created specifically for this project and is available under the [Creative Commons Attribution-ShareAlike 3.0 Unported License](http://creativecommons.org/licenses/by-sa/3.0/deed.en_US").



