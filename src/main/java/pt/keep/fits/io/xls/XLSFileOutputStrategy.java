package pt.keep.fits.io.xls;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import pt.keep.fits.ExtensionStat;
import pt.keep.fits.FileStat;
import pt.keep.fits.interfaces.FileOutputStrategy;

public class XLSFileOutputStrategy implements FileOutputStrategy {

  public void writeResultsToFile( Map<String, ExtensionStat> results, Map<String, List<FileStat>> fileStats,
      String outputFile ) {
    Workbook wb = new HSSFWorkbook();
    Sheet sheet = wb.createSheet( "Resume" );

    int rownum = 0;
    Row row = sheet.createRow( rownum++ );
    Object[] headers = { "Extension", "# Files", "% Mimetype OK", "% Mimetype KO", "% Mimetype ?", "% PUID OK",
        "% PUID KO", "% PUID ?", "% Valid OK", "% Valid KO", "% Valid ?", "Average features extracted",
        "Average processing time (ms)" };
    int cellnum = 0;

    for ( Object obj : headers ) {
      Cell cell = row.createCell( cellnum++ );
      if ( obj instanceof String )
        cell.setCellValue( (String) obj );
      else if ( obj instanceof Integer )
        cell.setCellValue( (Integer) obj );
    }

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
      row = sheet.createRow( rownum++ );
      Object[] data = {
          entry.getKey(),
          entry.getValue().getTotal(),
          ((double) entry.getValue().getRightMimeTypeStatus() / entry.getValue().getTotal()) * 100.0,
          ((double) entry.getValue().getWrongMimeTypeStatus() / entry.getValue().getTotal()) * 100.0,
          ((double) entry.getValue().getUnknownMimeTypeStatus() / entry.getValue().getTotal()) * 100.0,
          ((double) entry.getValue().getRightPUIDStatus() / entry.getValue().getTotal()) * 100.0,
          ((double) entry.getValue().getWrongPUIDStatus() / entry.getValue().getTotal()) * 100.0,
          ((double) entry.getValue().getUnknownPUIDStatus() / entry.getValue().getTotal()) * 100.0,
          entry.getValue().getCorrectlyIdentified() > 0 ? ((double) entry.getValue().getRightValidStatus() / entry
              .getValue().getCorrectlyIdentified()) * 100.0 : 0,
          entry.getValue().getCorrectlyIdentified() > 0 ? ((double) entry.getValue().getWrongValidStatus() / entry
              .getValue().getCorrectlyIdentified()) * 100.0 : 0,
          entry.getValue().getCorrectlyIdentified() > 0 ? ((double) entry.getValue().getUnknownValidStatus() / entry
              .getValue().getCorrectlyIdentified()) * 100.0 : 0,
          (entry.getValue().getFeaturesExtracted() / entry.getValue().getTotal()),
          ((double) entry.getValue().getProcessingTime() / entry.getValue().getTotal())
      };
      cellnum = 0;
      for ( Object obj : data ) {
        Cell cell = row.createCell( cellnum++ );
        if ( obj instanceof String )
          cell.setCellValue( (String) obj );
        else if ( obj instanceof Integer )
          cell.setCellValue( (Integer) obj );
        else if ( obj instanceof Double )
          cell.setCellValue( (Double) obj );
        else if ( obj instanceof HSSFRichTextString )
          cell.setCellValue( (HSSFRichTextString) obj );
      }

      totalWithRightMimeType += entry.getValue().getRightMimeTypeStatus();
      totalWithWrongMimeType += entry.getValue().getWrongMimeTypeStatus();
      totalWithUnknownMimeType += entry.getValue().getUnknownMimeTypeStatus();
      totalWithRightPUID += entry.getValue().getRightPUIDStatus();
      totalWithWrongPUID += entry.getValue().getWrongPUIDStatus();
      totalWithUnknownPUID += entry.getValue().getUnknownPUIDStatus();
      totalWithRightValidStatus += entry.getValue().getRightValidStatus();
      totalWithWrongValidStatus += entry.getValue().getWrongValidStatus();
      totalWithUnknownValidStatus += entry.getValue().getUnknownValidStatus();
      total += entry.getValue().getTotal();
      totalProcessingTime += entry.getValue().getProcessingTime();
      totalFeaturesExtracted += entry.getValue().getFeaturesExtracted();
    }
    row = sheet.createRow( rownum++ );
    Object[] totals = {
        "",
        total,
        (totalWithRightMimeType / total) * 100.0,
        (totalWithWrongMimeType / total) * 100.0,
        (totalWithUnknownMimeType / total) * 100.0,
        (totalWithRightPUID / total) * 100.0,
        (totalWithWrongPUID / total) * 100.0,
        (totalWithUnknownPUID / total) * 100.0,
        (totalWithRightValidStatus / total) * 100.0,
        (totalWithWrongValidStatus / total) * 100.0,
        (totalWithUnknownValidStatus / total) * 100.0,
        (totalFeaturesExtracted / total),
        (totalProcessingTime / total)
    };
    cellnum = 0;
    for ( Object obj : totals ) {
      Cell cell = row.createCell( cellnum++ );
      if ( obj instanceof String )
        cell.setCellValue( (String) obj );
      else if ( obj instanceof Integer )
        cell.setCellValue( (Integer) obj );
      else if ( obj instanceof Double )
        cell.setCellValue( (Double) obj );
      else if ( obj instanceof Long )
        cell.setCellValue( (Long) obj );
    }

    for ( Map.Entry<String, List<FileStat>> entry : fileStats.entrySet() ) {
      Sheet sheetExtension = wb.createSheet( entry.getKey() );
      rownum = 0;
      row = sheetExtension.createRow( rownum++ );
      Object[] headersFile = { "Name", "Mimetype", "Format", "PUID", "Valid", "#Features extracted",
          "Features extracted", "Tools used for identification", "Tools used for features extraction",
          "Tools used for validation", "Processing time (ms)" };
      cellnum = 0;

      for ( Object obj : headersFile ) {
        Cell cell = row.createCell( cellnum++ );
        if ( obj instanceof String )
          cell.setCellValue( (String) obj );
        else if ( obj instanceof Integer )
          cell.setCellValue( (Integer) obj );
        else if ( obj instanceof Long )
          cell.setCellValue( (Long) obj );
      }

      CellStyle redStyle = wb.createCellStyle();
      redStyle.setFillForegroundColor( HSSFColor.RED.index );
      redStyle.setFillPattern( HSSFCellStyle.SOLID_FOREGROUND );

      CellStyle greenStyle = wb.createCellStyle();
      greenStyle.setFillForegroundColor( HSSFColor.GREEN.index );
      greenStyle.setFillPattern( HSSFCellStyle.SOLID_FOREGROUND );

      CellStyle yellowStyle = wb.createCellStyle();
      yellowStyle.setFillForegroundColor( HSSFColor.YELLOW.index );
      yellowStyle.setFillPattern( HSSFCellStyle.SOLID_FOREGROUND );

      for ( FileStat fs : entry.getValue() ) {
        row = sheetExtension.createRow( rownum++ );
        Object[] data = { fs.getName(), fs.getMimetype(), fs.getFormat(), fs.getPuid(), fs.getValid(),
            fs.getFeatures().size(), StringUtils.join( fs.getFeatures().keySet(), " / " ),
            StringUtils.join( fs.getIdentificationTools(), "," ), StringUtils.join( fs.getExtractionTools(), "," ),
            StringUtils.join( fs.getValidationTools(), "," ), fs.getProcessingTime() };
        cellnum = 0;
        for ( int i = 0; i < data.length; i++ ) {
          Object obj = data[i];

          Cell cell = row.createCell( cellnum++ );
          if ( obj instanceof String ) {
            if ( fs.getGroundTruth() != null && i > 0 && i < 5 ) {
              if ( ((String) obj) == null || ((String) obj).trim().equalsIgnoreCase( "" ) ) {
                if ( fs.getGroundTruth()[i] == null || fs.getGroundTruth()[i].trim().equalsIgnoreCase( "" ) ) {

                } else {
                  cell.setCellStyle( yellowStyle );
                }
              } else if ( fs.getGroundTruth()[i] == null || fs.getGroundTruth()[i].trim().equalsIgnoreCase( "" ) ) {

              } else if ( ((String) obj).equalsIgnoreCase( fs.getGroundTruth()[i] ) ) {
                cell.setCellStyle( greenStyle );
              } else {
                cell.setCellStyle( redStyle );
              }
            }
            cell.setCellValue( (String) obj );
          } else if ( obj instanceof Long ) {
            cell.setCellValue( (Long) obj );
          } else if ( obj instanceof Double ) {
            cell.setCellValue( (Double) obj );
          } else if ( obj instanceof Integer ) {
            cell.setCellValue( (Integer) obj );
          }

        }
      }

    }

    FileOutputStream fos = null;
    try {

      fos = new FileOutputStream( outputFile );
      wb.write( fos );

    } catch ( IOException e ) {
      e.printStackTrace();
    } finally {
      if ( fos != null ) {
        try {
          fos.close();
        } catch ( IOException e ) {
          e.printStackTrace();
        }
      }
    }
  }

}
