package pt.keep.fits.io.xls;

import java.io.File;
import java.io.FileInputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import pt.keep.fits.interfaces.GroundTruthInputFileStrategy;

public class XLSGroundTruthInputFileStrategy implements GroundTruthInputFileStrategy {

  public String[] getGroundTruth( String groundFile, String fileName ) {
    String[] t = new String[5];
    try {
      File excel = new File( groundFile );
      FileInputStream fis = new FileInputStream( excel );

      Workbook wb = new HSSFWorkbook( fis );
      Sheet ws = wb.getSheetAt( 0 );
      int rowNum = ws.getLastRowNum() + 1;
      for ( int i = 0; i < rowNum; i++ ) {
        Row row = ws.getRow( i );
        if ( row != null && row.getCell( 0 ) != null ) {
          if ( fileName.endsWith( row.getCell( 0 ).getStringCellValue() ) ) {
            t[0] = row.getCell( 0 ) != null ? row.getCell( 0 ).getStringCellValue() : "";
            t[1] = row.getCell( 1 ) != null ? row.getCell( 1 ).getStringCellValue() : "";
            t[2] = row.getCell( 2 ) != null ? row.getCell( 2 ).getStringCellValue() : "";
            t[3] = row.getCell( 3 ) != null ? row.getCell( 3 ).getStringCellValue() : "";
            t[4] = row.getCell( 4 ) != null ? row.getCell( 4 ).getStringCellValue() : "";
            break;
          }
        }
      }

    } catch ( Exception e ) {
      e.printStackTrace();
    }

    return t;
  }
}
