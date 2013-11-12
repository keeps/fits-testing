package pt.keep.fits;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import pt.keep.fits.interfaces.FileOutputStrategy;
import pt.keep.fits.interfaces.GroundTruthInputFileStrategy;
import pt.keep.fits.io.csv.CSVFileOutputStrategy;
import pt.keep.fits.io.csv.CSVGroundTruthInputFileStrategy;
import pt.keep.fits.io.xls.XLSFileOutputStrategy;
import pt.keep.fits.io.xls.XLSGroundTruthInputFileStrategy;
import pt.keep.fits.utils.CommandException;
import pt.keep.fits.utils.CommandUtility;
import edu.harvard.hul.ois.fits.FitsMetadataElement;
import edu.harvard.hul.ois.fits.FitsOutput;
import edu.harvard.hul.ois.fits.exceptions.FitsException;
import edu.harvard.hul.ois.fits.identity.ExternalIdentifier;
import edu.harvard.hul.ois.fits.identity.FitsIdentity;
import edu.harvard.hul.ois.fits.tools.ToolInfo;

public class ReportBuilder {

  /**
   * The underlying operating system name.
   */
  private static final String OS_NAME = System.getProperty( "os.name" );

  /**
   * The default startup script name.
   */
  private static String STARTUP_SCRIPT = "/fits.sh";

  /**
   * The different ground truth input file reading strategies.
   */
  private static final Map<String, GroundTruthInputFileStrategy> GT_STRATEGIES = new HashMap<String, GroundTruthInputFileStrategy>();

  /**
   * The different output file writing strategies.
   */
  private static final Map<String, FileOutputStrategy> FO_STRATEGIES = new HashMap<String, FileOutputStrategy>();

  /**
   * This static initializer corrects the default startup script if running
   * under windows and initializes the different types of
   * GroundTruthInputFileStrategy and FileOutputStrategy objects.
   */
  static {

    if ( OS_NAME.startsWith( "Windows" ) ) {
      STARTUP_SCRIPT = "/fits.bat";
    }

    GroundTruthInputFileStrategy xlsGTInputFileStrategy = new XLSGroundTruthInputFileStrategy();
    GT_STRATEGIES.put( ".xls", xlsGTInputFileStrategy );
    GT_STRATEGIES.put( ".xlsx", xlsGTInputFileStrategy );
    GT_STRATEGIES.put( "xls", xlsGTInputFileStrategy );
    GT_STRATEGIES.put( "xlsx", xlsGTInputFileStrategy );

    GroundTruthInputFileStrategy csvGTInputFileStrategy = new CSVGroundTruthInputFileStrategy();
    GT_STRATEGIES.put( ".csv", csvGTInputFileStrategy );
    GT_STRATEGIES.put( "csv", csvGTInputFileStrategy );

    FileOutputStrategy xlsOutputFileStrategy = new XLSFileOutputStrategy();
    FO_STRATEGIES.put( ".xls", xlsOutputFileStrategy );
    FO_STRATEGIES.put( ".xlsx", xlsOutputFileStrategy );
    FO_STRATEGIES.put( "xls", xlsOutputFileStrategy );
    FO_STRATEGIES.put( "xlsx", xlsOutputFileStrategy );

    FileOutputStrategy csvOutputStrategy = new CSVFileOutputStrategy();
    FO_STRATEGIES.put( ".csv", csvOutputStrategy );
    FO_STRATEGIES.put( "csv", csvOutputStrategy );

  }

  /**
   * The {@link GroundTruthInputFileStrategy} that is used by this
   * {@link ReportBuilder}.
   */
  private GroundTruthInputFileStrategy mInputStrategy;

  /**
   * The {@link FileOutputStrategy} that is used by this {@link ReportBuilder}.
   */
  private FileOutputStrategy mOutputStrategy;

  /**
   * Indicates if the {@link ReportBuilder#init(String, String)}method of this
   * {@link ReportBuilder} was called.
   */
  private boolean mInitialized = false;

  /**
   * Initializing this {@link ReportBuilder}.
   * 
   * This method prepares this report builder, so that it can do its job.
   * 
   * @param groundTruthFile
   *          the path the to ground truth file.
   * @param outputFile
   *          the path to the output file.
   */
  public void init( String groundTruthFile, String outputFile ) {
    String gtExt = getExtension( groundTruthFile );
    String outExt = getExtension( outputFile );
    mInputStrategy = GT_STRATEGIES.get( gtExt );
    mOutputStrategy = FO_STRATEGIES.get( outExt );

    if ( mInputStrategy != null && mOutputStrategy != null ) {
      mInitialized = true;
    }

  }

  public void run( String fitsDirectory, String corporaDirectory, String outputFile, String groundTruth )
      throws FitsException {

    if ( !mInitialized ) {
      throw new RuntimeException(
          "This ReportBuilder is in a bad state. Please call ReportBuilder#init prior to running." );
    }

    List<String> command = new ArrayList<String>( Arrays.asList( fitsDirectory + STARTUP_SCRIPT, "-i" ) );

    File corporaFolder = new File( corporaDirectory );
    Collection<File> files = FileUtils.listFiles( corporaFolder, TrueFileFilter.TRUE,
        FileFilterUtils.makeSVNAware( null ) );

    Map<String, ExtensionStat> results = new TreeMap<String, ExtensionStat>();
    Map<String, List<FileStat>> fileStats = new TreeMap<String, List<FileStat>>();
    for ( File f : files ) {
      try {
        System.out.println( "Processing " + f.getAbsolutePath() );
        command.add( f.getAbsolutePath() );
        long begin = System.currentTimeMillis();
        String fitsOutput = CommandUtility.execute( command );
        long processingTime = System.currentTimeMillis() - begin;
        command.remove( f.getAbsolutePath() );        

        fitsOutput = fitsOutput.substring( fitsOutput.indexOf( "<?xml" ) );

        FitsOutput fitsOut = new FitsOutput( fitsOutput );

        String extension = "N/A";
        extension = getExtension( f.getName() );

        String mimeType = null;
        String puid = null;
        String format = null;
        String valid = null;
        List<String> identificationTools = new ArrayList<String>();
        List<String> validationTools = new ArrayList<String>();
        List<String> extractionTools = new ArrayList<String>();

        if ( fitsOut.getIdentities() != null && fitsOut.getIdentities().size() > 0 ) {
          for ( FitsIdentity fi : fitsOut.getIdentities() ) {
            boolean used = false;
            if ( mimeType == null && fi.getMimetype() != null ) {
              if ( fi.getMimetype().trim().length() > 0 ) {
                mimeType = fi.getMimetype();
              } else {
                mimeType = "None";
              }
              used = true;
            }
            if ( format == null && fi.getFormat() != null && fi.getFormat().length() > 0 ) {
              format = fi.getFormat();
            }
            for ( ExternalIdentifier ei : fi.getExternalIdentifiers() ) {
              if ( ei.getName().toLowerCase().contains( "puid" ) ) {
                if ( puid == null && ei.getValue() != null ) {
                  if ( ei.getValue().length() > 0 ) {
                    puid = ei.getValue();
                  } else {
                    puid = "None";
                  }
                  used = true;
                }
              }
            }
            if ( used ) {
              if ( mimeType == null ) {
                mimeType = "";
              }
              if ( puid == null ) {
                puid = "";
              }
              for ( ToolInfo ti : fi.getReportingTools() ) {
                if ( !identificationTools.contains( ti.getName() + " (" + ti.getName() + ")" ) ) {
                  identificationTools.add( ti.getName() + " (" + ti.getName() + ")" );
                }
              }
            }
          }
        }
        if ( puid.equalsIgnoreCase( "" ) ) {
          puid = null;
        }
        if ( mimeType.equalsIgnoreCase( "" ) ) {
          mimeType = null;
        }
        Map<String, String> features = new Hashtable<String, String>();
        if ( fitsOut.getTechMetadataElements() != null ) {
          for ( FitsMetadataElement fme : fitsOut.getTechMetadataElements() ) {
            if ( !extractionTools.contains( fme.getReportingToolName() + " (" + fme.getReportingToolVersion() + ")" ) ) {
              extractionTools.add( fme.getReportingToolName() + " (" + fme.getReportingToolVersion() + ")" );
            }
            if ( !features.containsKey( fme.getName() ) ) {
              features.put( fme.getName(), fme.getValue() );
            }
          }
        }
        if ( fitsOut.getFileInfoElements() != null ) {
          for ( FitsMetadataElement fme : fitsOut.getFileInfoElements() ) {
            if ( !extractionTools.contains( fme.getReportingToolName() + " (" + fme.getReportingToolVersion() + ")" ) ) {
              extractionTools.add( fme.getReportingToolName() + " (" + fme.getReportingToolVersion() + ")" );
            }
            if ( !features.containsKey( fme.getName() ) ) {
              features.put( fme.getName(), fme.getValue() );
            }
          }
        }

        valid = "";

        if ( fitsOut.getFileStatusElements() != null ) {
          for ( FitsMetadataElement fme : fitsOut.getFileStatusElements() ) {
            if ( fme.getName().equalsIgnoreCase( "valid" ) ) {
              valid = fme.getValue();
            }
            if ( !validationTools.contains( fme.getReportingToolName() + " (" + fme.getReportingToolVersion() + ")" ) ) {
              validationTools.add( fme.getReportingToolName() + " (" + fme.getReportingToolVersion() + ")" );
            }
          }
        }

        FileStat fs = new FileStat();
        fs.setName( f.getAbsolutePath().replace( corporaFolder.getAbsolutePath(), "" ) );
        fs.setMimetype( mimeType );
        fs.setPuid( puid );
        fs.setValid( valid );
        fs.setFormat( format );
        fs.setProcessingTime( processingTime );
        fs.setFeatures( features );
        fs.setExtractionTools( extractionTools );
        fs.setIdentificationTools( identificationTools );
        fs.setValidationTools( validationTools );
        if ( fileStats.keySet().contains( extension ) ) {
          List<FileStat> temp = fileStats.get( extension );
          temp.add( fs );
          fileStats.put( extension, temp );
        } else {
          List<FileStat> temp = new ArrayList<FileStat>();
          temp.add( fs );
          fileStats.put( extension, temp );
        }

        String[] gt = null;
        if ( groundTruth != null ) {
          gt = mInputStrategy.getGroundTruth( groundTruth, f.getAbsolutePath() );
          fs.setGroundTruth( gt );
        }

        ExtensionStat es = new ExtensionStat();
        if ( results.keySet().contains( extension ) ) {
          es = results.get( extension );
        }
        es.setTotal( es.getTotal() + 1 );

        es.setProcessingTime( es.getProcessingTime() + fs.getProcessingTime() );

        if ( puid != null ) {
          es.setWithPUID( es.getWithPUID() + 1 );
        }
        if ( mimeType != null ) {
          es.setWithMimeType( es.getWithMimeType() + 1 );
        }
        if ( valid != null ) {
          if ( valid.equalsIgnoreCase( "true" ) ) {
            es.setValid( es.getValid() + 1 );
          } else if ( valid.equalsIgnoreCase( "false" ) ) {
            es.setNotValid( es.getNotValid() + 1 );
          } else {
            es.setUnknownValid( es.getUnknownValid() + 1 );
          }
        } else {
          es.setUnknownValid( es.getUnknownValid() + 1 );
        }

        if ( gt != null ) {
          boolean mimeOK = false;
          boolean mimeKO = false;
          boolean mimeUnknown = false;
          boolean puidOK = false;
          ;
          boolean puidKO = false;
          boolean puidUnknown = false;

          if ( !isEmpty( gt[1] ) && !isEmpty( mimeType ) && gt[1].equalsIgnoreCase( mimeType ) ) {
            es.setRightMimeTypeStatus( es.getRightMimeTypeStatus() + 1 );
            mimeOK = true;
          } else if ( !isEmpty( gt[1] ) && !isEmpty( mimeType ) && !gt[1].equalsIgnoreCase( mimeType ) ) {
            es.setWrongMimeTypeStatus( es.getWrongMimeTypeStatus() + 1 );
            mimeKO = true;
          } else {
            es.setUnknownMimeTypeStatus( es.getUnknownMimeTypeStatus() + 1 );
            mimeUnknown = false;
          }
          if ( !isEmpty( gt[2] ) && !isEmpty( format ) && gt[2].equalsIgnoreCase( format ) ) {
            es.setRightFormatStatus( es.getRightFormatStatus() + 1 );
          } else if ( !isEmpty( gt[2] ) && !isEmpty( format ) && !gt[2].equalsIgnoreCase( format ) ) {
            es.setWrongFormatStatus( es.getWrongFormatStatus() + 1 );
          } else {
            es.setUnknownFormatStatus( es.getUnknownFormatStatus() + 1 );
          }
          if ( !isEmpty( gt[3] ) && !isEmpty( puid ) && gt[3].equalsIgnoreCase( puid ) ) {
            es.setRightPUIDStatus( es.getRightPUIDStatus() + 1 );
            puidOK = true;
          } else if ( !isEmpty( gt[3] ) && !isEmpty( puid ) && !gt[3].equalsIgnoreCase( puid ) ) {
            es.setWrongPUIDStatus( es.getWrongPUIDStatus() + 1 );
            puidKO = true;
          } else {
            es.setUnknownPUIDStatus( es.getUnknownPUIDStatus() + 1 );
            puidUnknown = true;
          }

          if ( puidOK ) {
            es.setCorrectlyIdentified( es.getCorrectlyIdentified() + 1 );
            if ( !isEmpty( gt[4] ) && !isEmpty( valid ) && gt[4].equalsIgnoreCase( valid ) ) {
              es.setRightValidStatus( es.getRightValidStatus() + 1 );
            } else if ( !isEmpty( gt[4] ) && !isEmpty( valid ) && !gt[4].equalsIgnoreCase( valid ) ) {
              es.setWrongValidStatus( es.getWrongValidStatus() + 1 );
            } else {
              es.setUnknownValidStatus( es.getUnknownValidStatus() + 1 );
            }
          } else if ( puidKO ) {
            // es.setUnknownValidStatus(es.getUnknownValidStatus()+1);
          } else if ( mimeOK ) {
            es.setCorrectlyIdentified( es.getCorrectlyIdentified() + 1 );
            if ( !isEmpty( gt[4] ) && !isEmpty( valid ) && gt[4].equalsIgnoreCase( valid ) ) {
              es.setRightValidStatus( es.getRightValidStatus() + 1 );
            } else if ( !isEmpty( gt[4] ) && !isEmpty( valid ) && !gt[4].equalsIgnoreCase( valid ) ) {
              es.setWrongValidStatus( es.getWrongValidStatus() + 1 );
            } else {
              es.setUnknownValidStatus( es.getUnknownValidStatus() + 1 );
            }
          } else if ( mimeKO ) {
            // es.setUnknownValidStatus(es.getUnknownValidStatus()+1);
          } else {
            // es.setUnknownValidStatus(es.getUnknownValidStatus()+1);
            // es.setIdentificationNotWrong(es.getIdentificationNotWrong()+1);
          }
          es.setFeaturesExtracted( es.getFeaturesExtracted() + fs.getFeatures().size() );

        }
        results.put( extension, es );

      } catch ( CommandException e ) {
        e.printStackTrace();
        System.out.println(e.getOutput());
        command.remove( f.getAbsolutePath() ); // if the command crashes the path is never removed. 
      } catch (Exception e) {
        e.printStackTrace();
      }

    }

    mOutputStrategy.writeResultsToFile( results, fileStats, outputFile );
  }

  private boolean isEmpty( String s ) {
    if ( s == null || s.trim().equalsIgnoreCase( "" ) ) {
      return true;
    } else {
      return false;
    }
  }

  private String getExtension( String fileName ) {
    String extension = "";

    if ( fileName != null && !fileName.equals( "" ) && fileName.contains( "." ) ) {
      extension = fileName.substring( fileName.lastIndexOf( "." ) + 1 ).toLowerCase();
    }

    return extension;
  }

  private void printHelp( Options opts ) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp( "report", opts );
  }

  public static void main( String[] args ) {
    Logger.getRootLogger().setLevel(Level.OFF);
    try {
      ReportBuilder r = new ReportBuilder();
      Options options = new Options();
      options.addOption( "d", true, "directory with files to process" );
      options.addOption( "f", true, "fits home" );
      options.addOption( "o", true, "output file" );
      options.addOption( "g", true, "ground truth file" );
      options.addOption( "h", false, "print this message" );

      CommandLineParser parser = new GnuParser();
      CommandLine cmd = parser.parse( options, args );

      if ( cmd.hasOption( "h" ) ) {
        r.printHelp( options );
        System.exit( 0 );
      }

      if ( !cmd.hasOption( "d" ) || !cmd.hasOption( "f" ) || !cmd.hasOption( "o" ) ) {
        r.printHelp( options );
        System.exit( 0 );
      }

      r.init( cmd.getOptionValue( "g" ), cmd.getOptionValue( "o" ) );
      r.run( cmd.getOptionValue( "f" ), cmd.getOptionValue( "d" ), cmd.getOptionValue( "o" ), cmd.getOptionValue( "g" ) );

    } catch ( Exception e ) {
      e.printStackTrace();
    }

  }

}
