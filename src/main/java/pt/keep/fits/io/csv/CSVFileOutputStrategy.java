package pt.keep.fits.io.csv;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import pt.keep.fits.ExtensionStat;
import pt.keep.fits.FileStat;
import pt.keep.fits.interfaces.FileOutputStrategy;
import au.com.bytecode.opencsv.CSVWriter;

public class CSVFileOutputStrategy implements FileOutputStrategy {

  public void writeResultsToFile( Map<String, ExtensionStat> results, Map<String, List<FileStat>> fileStats,
      String outputFile ) {
    try {
      CSVWriter writer = new CSVWriter( new FileWriter( outputFile ), ';' );

      String[] headers = { "Extension", "# Files", "% Mimetype OK", "% Mimetype KO", "% Mimetype ?", "% PUID OK",
          "% PUID KO", "% PUID ?", "% Valid OK", "% Valid KO", "% Valid ?", "Average features extracted",
          "Average processing time (ms)" };

      writer.writeNext( headers );

      double totalWithRightMimeType = 0;
      double totalWithWrongMimeType = 0;
      double totalWithUnknownMimeType = 0;
      double totalWithRightPUID = 0;
      double totalWithWrongPUID = 0;
      double totalWithUnknownPUID = 0;
      double totalWithRightValidStatus = 0;
      double totalWithWrongValidStatus = 0;
      double totalWithUnknownValidStatus = 0;
      double total = 0;
      long totalProcessingTime = 0;
      double totalFeaturesExtracted = 0;

      for ( Map.Entry<String, ExtensionStat> entry : results.entrySet() ) {
        ExtensionStat stat = entry.getValue();
        String[] data = {
            entry.getKey(),
            entry.getValue().getTotal() + "",
            stat.metricInPercent( stat.getRightMimeTypeStatus() ) + "",
            stat.metricInPercent( stat.getWrongMimeTypeStatus() ) + "",
            stat.metricInPercent( stat.getUnknownMimeTypeStatus() ) + "",
            stat.metricInPercent( stat.getRightPUIDStatus() ) + "",
            stat.metricInPercent( stat.getWrongPUIDStatus() ) + "",
            stat.metricInPercent( stat.getUnknownPUIDStatus() ) + "",
            stat.getCorrectlyIdentified() > 0 ? (((double) stat.getRightValidStatus() / stat.getCorrectlyIdentified()) * 100.0)
                + ""
                : "0",
            stat.getCorrectlyIdentified() > 0 ? (((double) stat.getWrongValidStatus() / stat.getCorrectlyIdentified()) * 100.0)
                + ""
                : "0",
            stat.getCorrectlyIdentified() > 0 ? (((double) stat.getUnknownValidStatus() / stat.getCorrectlyIdentified()) * 100.0)
                + ""
                : "0",
            (stat.getFeaturesExtracted() / stat.getTotal()) + "",
            ((double) stat.getProcessingTime() / stat.getTotal()) + ""
        };

        totalWithRightMimeType += stat.getRightMimeTypeStatus();
        totalWithWrongMimeType += stat.getWrongMimeTypeStatus();
        totalWithUnknownMimeType += stat.getUnknownMimeTypeStatus();
        totalWithRightPUID += stat.getRightPUIDStatus();
        totalWithWrongPUID += stat.getWrongPUIDStatus();
        totalWithUnknownPUID += stat.getUnknownPUIDStatus();
        totalWithRightValidStatus += stat.getRightValidStatus();
        totalWithWrongValidStatus += stat.getWrongValidStatus();
        totalWithUnknownValidStatus += stat.getUnknownValidStatus();
        total += stat.getTotal();
        totalProcessingTime += stat.getProcessingTime();
        totalFeaturesExtracted += stat.getFeaturesExtracted();

        writer.writeNext( data );
      }
      
      String[] totals = {
          "",
          total + "",
          ((totalWithRightMimeType / total) * 100.0) + "",
          ((totalWithWrongMimeType / total) * 100.0) + "",
          ((totalWithUnknownMimeType / total) * 100.0) + "",
          ((totalWithRightPUID / total) * 100.0) + "",
          ((totalWithWrongPUID / total) * 100.0) + "",
          ((totalWithUnknownPUID / total) * 100.0) + "",
          ((totalWithRightValidStatus / total) * 100.0) + "",
          ((totalWithWrongValidStatus / total) * 100.0) + "",
          ((totalWithUnknownValidStatus / total) * 100.0) + "",
          (totalFeaturesExtracted / total) + "",
          (totalProcessingTime / total) + ""
      };
      writer.writeNext( totals );
      writer.close();
      
      // TODO Write files for the rest of the sheets ... may be outputFile + [_ext]
    } catch ( IOException e ) {
      e.printStackTrace();
    }
  }

}
