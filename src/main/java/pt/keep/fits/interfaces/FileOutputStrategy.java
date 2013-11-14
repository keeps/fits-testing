package pt.keep.fits.interfaces;

import java.util.List;
import java.util.Map;

import pt.keep.fits.ExtensionStat;
import pt.keep.fits.FileStat;

public interface FileOutputStrategy {

  void writeResultsToFile(Map<String, ExtensionStat> results, Map<String, List<FileStat>> fileStats, String outputFile);
}
