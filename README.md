FITS testing tool
======================

A tool to test the [File Information Tool Set (FITS)](https://code.google.com/p/fits/) against a test corpora.
This tool will allow to pass a FITS implementation by argument and test it against a set of files, producing and report on Microsoft Excel format. Optionally, you can pass a ground truth file which will be used to validate the FITS output results in terms of mime type, puid and validation.

## Build & Use

To build the application simply clone the project and execute the following Maven command: `mvn clean package`  
The binary will appear at `target/fits-testing-1.0-SNAPSHOT-jar-with-dependencies.jar`

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

| | [harvard-lts](https://github.com/harvard-lts/fits)<br>( [master](https://github.com/harvard-lts/fits/commit/0a1cd57f22c24f1c8be7ab75607628058505b961) ) | [openplanets](https://github.com/openplanets/fits)<br>( [master](https://github.com/openplanets/fits/commit/2ff3bc2dc06b05cb9bbbe6778eae80a36743cd51) ) | [openplanets](https://github.com/openplanets/fits)<br>( [gary-master](https://github.com/openplanets/fits/commit/7b0c2dd4c23e0900192fbe4dd6802bfae59a13df) ) | [keeps](https://github.com/keeps/fits)<br>( [master](https://github.com/keeps/fits/commit/8df8c8ac8cc22bcd112886a77b1f1cc496d07e89) )|
| ---------------------------|-------|-------|-------|-------|
| % MIME type valid | 35.4% | 35.4% | 25% | 37.5% |
| % MIME type not valid | 64.6% | 64.6% | 75% | 62.5% |
| % MIME type unknown | 0% | 0% | 0% | 0% |
| % PUID valid | 33.3% | 33.3% | 0% | 66.7% |
| % PUID not valid | 62.5% | 62.5% | 95.8% | 29.2% |
| % PUID unknown | 4.2% | 4.2% | 4.2% | 4.1 |
| % Valid | 18.75% | 18.75% | 18.75% | 10.4 |
| % Not valid | 81.25% | 81.25% | 81.25% | 89.6 |
| % Validity unknown | 0% | 0% | 0% | 0% |
| Average features extracted | 8.3 | 8.3 | 8.4 | 8.8 |
| Time (ms) | 167013 | 164846 | 105972 | 251062 |
| Results | [Results](results/harvard_31102013.xls) | [Results](results/openPlanets_31102013.xls) | [Results](results/gary_31102013.xls) | [Results](results/keeps_31102013.xls) |

Test date: 2013-10-31  
Test environment:
* Processor: Intel® Core™ i5 CPU 650 @ 3.20GHz × 4
* Memory: 7.7 GiB
* Disk: Western Digital WD Black WD1002FAEX 1TB 7200 RPM 64MB Cache SATA 6.0Gb/s
* Operative system: Ubuntu 12.04.3 LTS
* Java virtual machine: OpenJDK 1.6.0_27

Corpora:
* 48 files
* Formats: Adobe Illustrator, Corel Draw, Microsoft Word, Excel and Powerpoint, Libre Office text, presentation and spreadsheet, Lotus Notes
* Manually created ground truth available

Notes:
* The validation and features extraction results depends on the identification process. eg: Fits A identifies a file as a PDF. Fits A validates the file as a PDF and the features extracted are related to a PDF file.
Fits B identifies the same file as a AI. Fits B can't validate the file.
Fits A may get better results on validation and features extraction, but the results are wrong, because the identification is not correct.

## License

This software is available under the [LGPL version 3 license](LICENSE). All corpora was created specifically for this project and is available under the [Creative Commons Attribution-ShareAlike 3.0 Unported License](http://creativecommons.org/licenses/by-sa/3.0/deed.en_US").



