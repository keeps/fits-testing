package pt.keep.fits.io.csv;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import pt.keep.fits.interfaces.GroundTruthInputFileStrategy;
import au.com.bytecode.opencsv.CSVReader;

public class CSVGroundTruthInputFileStrategy implements GroundTruthInputFileStrategy {

  public String[] getGroundTruth( String groundTruthFile, String fileName ) {
    String[] truth = new String[5];
    try {
      CSVReader reader = new CSVReader( new FileReader( groundTruthFile ), ';' );
      String[] nextLine;
      while ( (nextLine = reader.readNext()) != null ) {
        String tmpFileName = nextLine[0];
        if ( tmpFileName != null && !tmpFileName.equals( "" ) && fileName.endsWith( tmpFileName ) ) {
          truth[0] = nextLine[0] != null ? nextLine[0] : "";
          truth[1] = nextLine[1] != null ? nextLine[1] : "";
          truth[2] = nextLine[2] != null ? nextLine[2] : "";
          truth[3] = nextLine[3] != null ? nextLine[3] : "";
          truth[4] = nextLine[4] != null ? nextLine[4] : "";

          break;
        }
      }
    } catch ( FileNotFoundException e ) {
      e.printStackTrace();
    } catch ( IOException e ) {
      e.printStackTrace();
    }
    return truth;
  }

}
