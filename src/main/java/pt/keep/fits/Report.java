package pt.keep.fits;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import pt.keep.fits.utils.CommandUtility;
import edu.harvard.hul.ois.fits.FitsMetadataElement;
import edu.harvard.hul.ois.fits.FitsOutput;
import edu.harvard.hul.ois.fits.exceptions.FitsException;
import edu.harvard.hul.ois.fits.identity.ExternalIdentifier;
import edu.harvard.hul.ois.fits.identity.FitsIdentity;
import edu.harvard.hul.ois.fits.tools.ToolInfo;

public class Report {


	public void run(String fitsDirectory, String corporaDirectory, String outputFile, String groundTruth) throws IOException, FitsException{
		String osName = System.getProperty("os.name");
		List<String> command;
		if (osName.startsWith("Windows")) {
			command = new ArrayList<String>(Arrays.asList(fitsDirectory+"/fits.bat","-i"));

		}else{
			command = new ArrayList<String>(Arrays.asList(fitsDirectory+"/fits.sh","-i"));
		}
		File corporaFolder = new File(corporaDirectory);
		Collection<File> files =  FileUtils.listFiles(corporaFolder, TrueFileFilter.TRUE, FileFilterUtils.makeSVNAware(null));

		Map<String, ExtensionStat> results = new TreeMap<String,ExtensionStat>();
		Map<String, List<FileStat>> fileStats = new TreeMap<String,List<FileStat>>();
		for(File f : files){
			try{
				System.out.println("Processing "+f.getAbsolutePath());
				String extension = "N/A";
				if(f.getName().contains(".")){
					extension = f.getName().substring(f.getName().lastIndexOf(".")+1).toLowerCase();
				}
				command.add(f.getAbsolutePath());
				long begin = System.currentTimeMillis();
				String fitsOutput = CommandUtility.execute(command);
				long processingTime = System.currentTimeMillis() - begin;

				command.remove(f.getAbsolutePath());
				fitsOutput = fitsOutput.substring(fitsOutput.indexOf("<?xml"));

				FitsOutput fitsOut = new FitsOutput(fitsOutput);

				String mimeType = null;
				String puid = null;
				String format=null;
				String valid=null;
				List<String> identificationTools = new ArrayList<String>();
				List<String> validationTools = new ArrayList<String>();
				List<String> extractionTools = new ArrayList<String>();

				
				
				if(fitsOut.getIdentities()!=null && fitsOut.getIdentities().size()>0){
					for(FitsIdentity fi : fitsOut.getIdentities()){
						boolean used = false;
						if(mimeType==null && fi.getMimetype()!=null){
							if(fi.getMimetype().trim().length()>0){
								mimeType = fi.getMimetype();
							}else{
								mimeType="None";
							}
							used=true;
						}
						if(format==null && fi.getFormat()!=null && fi.getFormat().length()>0){
							format = fi.getFormat();
						}
						for(ExternalIdentifier ei : fi.getExternalIdentifiers()){
							if(ei.getName().toLowerCase().contains("puid")){
								if(puid==null && ei.getValue()!=null){
									if(ei.getValue().length()>0){
										puid = ei.getValue();
									}else{
										puid="None";
									}
									used=true;
								}
							}
						}
						if(used){
							if(mimeType==null){
								mimeType="";
							}
							if(puid==null){
								puid="";
							}
							for(ToolInfo ti : fi.getReportingTools()){
								if(!identificationTools.contains(ti.getName() + " ("+ti.getName()+")")){
									identificationTools.add(ti.getName() + " ("+ti.getName()+")");
								}
							}
						}
					}
				}
				if(puid.equalsIgnoreCase("")){
					puid=null;
				}
				if(mimeType.equalsIgnoreCase("")){
					mimeType=null;
				}
				Map<String,String> features = new Hashtable<String, String>();
				if(fitsOut.getTechMetadataElements()!=null){
					for(FitsMetadataElement fme : fitsOut.getTechMetadataElements()){
						if(!extractionTools.contains(fme.getReportingToolName()+ " ("+fme.getReportingToolVersion()+")")){
							extractionTools.add(fme.getReportingToolName()+ " ("+fme.getReportingToolVersion()+")");
						}
						if(!features.containsKey(fme.getName())){
							features.put(fme.getName(), fme.getValue());
						}
					}
				}
				if(fitsOut.getFileInfoElements()!=null){
					for(FitsMetadataElement fme : fitsOut.getFileInfoElements()){
						if(!extractionTools.contains(fme.getReportingToolName()+ " ("+fme.getReportingToolVersion()+")")){
							extractionTools.add(fme.getReportingToolName()+ " ("+fme.getReportingToolVersion()+")");
						}
						if(!features.containsKey(fme.getName())){
							features.put(fme.getName(), fme.getValue());
						}
					}
				}
				
				valid="";

				if(fitsOut.getFileStatusElements()!=null){
					for(FitsMetadataElement fme : fitsOut.getFileStatusElements()){
						if(fme.getName().equalsIgnoreCase("valid")){
							valid = fme.getValue();
						}
						if(!validationTools.contains(fme.getReportingToolName()+ " ("+fme.getReportingToolVersion()+")")){
							validationTools.add(fme.getReportingToolName()+ " ("+fme.getReportingToolVersion()+")");
						}
					}
				}
				


				FileStat fs = new FileStat();
				fs.setName(f.getAbsolutePath().replace(corporaFolder.getAbsolutePath(), ""));
				fs.setMimetype(mimeType);
				fs.setPuid(puid);
				fs.setValid(valid);
				fs.setFormat(format);
				fs.setProcessingTime(processingTime);
				fs.setFeatures(features);
				fs.setExtractionTools(extractionTools);
				fs.setIdentificationTools(identificationTools);
				fs.setValidationTools(validationTools);
				if(fileStats.keySet().contains(extension)){
					List<FileStat> temp = fileStats.get(extension);
					temp.add(fs);
					fileStats.put(extension, temp);
				}else{
					List<FileStat> temp = new ArrayList<FileStat>();
					temp.add(fs);
					fileStats.put(extension, temp);
				}

				String[] gt = null;
				if(groundTruth!=null){
					gt = getGroundTruth(groundTruth, f.getAbsolutePath());
					fs.setGroundTruth(gt);
				}

				ExtensionStat es = new ExtensionStat();
				if(results.keySet().contains(extension)){
					es = results.get(extension);
				}
				es.setTotal(es.getTotal()+1);
				
				es.setProcessingTime(es.getProcessingTime()+fs.getProcessingTime());
				
				
				
				if(puid!=null){
					es.setWithPUID(es.getWithPUID()+1);
				}
				if(mimeType!=null){
					es.setWithMimeType(es.getWithMimeType()+1);
				}
				if(valid!=null){
					if(valid.equalsIgnoreCase("true")){
						es.setValid(es.getValid()+1);
					}else if(valid.equalsIgnoreCase("false")){
						es.setNotValid(es.getNotValid()+1);
					}else{
						es.setUnknownValid(es.getUnknownValid()+1);
					}
				}else{
					es.setUnknownValid(es.getUnknownValid()+1);
				}
				
				
				
				if(gt!=null){
					boolean mimeOK=false;
					boolean mimeKO=false;
					boolean mimeUnknown=false;
					boolean puidOK=false;;
					boolean puidKO=false;
					boolean puidUnknown=false;
					
					if(!isEmpty(gt[1]) && !isEmpty(mimeType) && gt[1].equalsIgnoreCase(mimeType)){
						es.setRightMimeTypeStatus(es.getRightMimeTypeStatus()+1);
						mimeOK=true;
					}else if(!isEmpty(gt[1]) && !isEmpty(mimeType) && !gt[1].equalsIgnoreCase(mimeType)){
						es.setWrongMimeTypeStatus(es.getWrongMimeTypeStatus()+1);
						mimeKO=true;
					}else{
						es.setUnknownMimeTypeStatus(es.getUnknownMimeTypeStatus()+1);
						mimeUnknown=false;
					}
					if(!isEmpty(gt[2]) && !isEmpty(format) && gt[2].equalsIgnoreCase(format)){
						es.setRightFormatStatus(es.getRightFormatStatus()+1);
					}else if(!isEmpty(gt[2]) && !isEmpty(format) && !gt[2].equalsIgnoreCase(format)){
						es.setWrongFormatStatus(es.getWrongFormatStatus()+1);
					}else{
						es.setUnknownFormatStatus(es.getUnknownFormatStatus()+1);
					}
					if(!isEmpty(gt[3]) && !isEmpty(puid) && gt[3].equalsIgnoreCase(puid)){
						es.setRightPUIDStatus(es.getRightPUIDStatus()+1);
						puidOK=true;
					}else if(!isEmpty(gt[3]) && !isEmpty(puid) && !gt[3].equalsIgnoreCase(puid)){
						es.setWrongPUIDStatus(es.getWrongPUIDStatus()+1);
						puidKO=true;
					}else{
						es.setUnknownPUIDStatus(es.getUnknownPUIDStatus()+1);
						puidUnknown=true;
					}
					
					
					if(puidOK){
						es.setCorrectlyIdentified(es.getCorrectlyIdentified()+1);
						if(!isEmpty(gt[4]) && !isEmpty(valid) && gt[4].equalsIgnoreCase(valid)){
							es.setRightValidStatus(es.getRightValidStatus()+1);
						}else if(!isEmpty(gt[4]) && !isEmpty(valid) && !gt[4].equalsIgnoreCase(valid)){
							es.setWrongValidStatus(es.getWrongValidStatus()+1);
						}else{
							es.setUnknownValidStatus(es.getUnknownValidStatus()+1);
						}
					}else if(puidKO){
						//es.setUnknownValidStatus(es.getUnknownValidStatus()+1);
					}else if(mimeOK){
						es.setCorrectlyIdentified(es.getCorrectlyIdentified()+1);
						if(!isEmpty(gt[4]) && !isEmpty(valid) && gt[4].equalsIgnoreCase(valid)){
							es.setRightValidStatus(es.getRightValidStatus()+1);
						}else if(!isEmpty(gt[4]) && !isEmpty(valid) && !gt[4].equalsIgnoreCase(valid)){
							es.setWrongValidStatus(es.getWrongValidStatus()+1);
						}else{
							es.setUnknownValidStatus(es.getUnknownValidStatus()+1);
						}
					}else if(mimeKO){
						//es.setUnknownValidStatus(es.getUnknownValidStatus()+1);
					}else{
						//es.setUnknownValidStatus(es.getUnknownValidStatus()+1);
						//es.setIdentificationNotWrong(es.getIdentificationNotWrong()+1);
					}
					es.setFeaturesExtracted(es.getFeaturesExtracted()+fs.getFeatures().size());
					
				}
				results.put(extension, es);


			}catch(Exception e){
				e.printStackTrace();
			}


		}


		Workbook wb = new HSSFWorkbook();
		Sheet sheet = wb.createSheet("Resume");

		int rownum = 0;
		Row row = sheet.createRow(rownum++);
		Object[] headers = {"Extension","# Files","% Mimetype OK","% Mimetype KO","% Mimetype ?","% PUID OK","% PUID KO","% PUID ?","% Valid OK","% Valid KO","% Valid ?","Average features extracted","Average processing time (ms)"};
		int cellnum = 0;

		for (Object obj : headers){
			Cell cell = row.createCell(cellnum++);
			if(obj instanceof String)
				cell.setCellValue((String)obj);
			else if(obj instanceof Integer)
				cell.setCellValue((Integer)obj);
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
		double total=0;
		long totalProcessingTime=0;
		double totalFeaturesExtracted=0;
		
		for(Map.Entry<String, ExtensionStat> entry : results.entrySet()){
			row = sheet.createRow(rownum++);
			Object[] data = {
				entry.getKey(),
				entry.getValue().getTotal(),
				((double)entry.getValue().getRightMimeTypeStatus()/entry.getValue().getTotal())*100.0,
				((double)entry.getValue().getWrongMimeTypeStatus()/entry.getValue().getTotal())*100.0,
				((double)entry.getValue().getUnknownMimeTypeStatus()/entry.getValue().getTotal())*100.0, 
				((double)entry.getValue().getRightPUIDStatus()/entry.getValue().getTotal())*100.0,
				((double)entry.getValue().getWrongPUIDStatus()/entry.getValue().getTotal())*100.0,
				((double)entry.getValue().getUnknownPUIDStatus()/entry.getValue().getTotal())*100.0,
				entry.getValue().getCorrectlyIdentified()>0?((double)entry.getValue().getRightValidStatus()/entry.getValue().getCorrectlyIdentified())*100.0:0,
				entry.getValue().getCorrectlyIdentified()>0?((double)entry.getValue().getWrongValidStatus()/entry.getValue().getCorrectlyIdentified())*100.0:0,
				entry.getValue().getCorrectlyIdentified()>0?((double)entry.getValue().getUnknownValidStatus()/entry.getValue().getCorrectlyIdentified())*100.0:0,
				(entry.getValue().getFeaturesExtracted()/entry.getValue().getTotal()),
				((double)entry.getValue().getProcessingTime()/entry.getValue().getTotal())
			};
			cellnum = 0;
			for (Object obj : data){
				Cell cell = row.createCell(cellnum++);
				if(obj instanceof String)
					cell.setCellValue((String)obj);
				else if(obj instanceof Integer)
					cell.setCellValue((Integer)obj);
				else if(obj instanceof Double)
					cell.setCellValue((Double)obj);
				else if(obj instanceof HSSFRichTextString)
					cell.setCellValue((HSSFRichTextString)obj);
			}
			
			totalWithRightMimeType+=entry.getValue().getRightMimeTypeStatus();
			totalWithWrongMimeType+=entry.getValue().getWrongMimeTypeStatus();
			totalWithUnknownMimeType+=entry.getValue().getUnknownMimeTypeStatus();
			totalWithRightPUID+=entry.getValue().getRightPUIDStatus();
			totalWithWrongPUID+=entry.getValue().getWrongPUIDStatus();
			totalWithUnknownPUID+=entry.getValue().getUnknownPUIDStatus();
			totalWithRightValidStatus+=entry.getValue().getRightValidStatus();
			totalWithWrongValidStatus+=entry.getValue().getWrongValidStatus();
			totalWithUnknownValidStatus+=entry.getValue().getUnknownValidStatus();
			total+=entry.getValue().getTotal();
			totalProcessingTime+=entry.getValue().getProcessingTime();
			totalFeaturesExtracted+=entry.getValue().getFeaturesExtracted();
		}
		row = sheet.createRow(rownum++);
		Object[] totals = {
			"",
			total,
			(totalWithRightMimeType/total)*100.0,
			(totalWithWrongMimeType/total)*100.0,
			(totalWithUnknownMimeType/total)*100.0, 
			(totalWithRightPUID/total)*100.0,
			(totalWithWrongPUID/total)*100.0,
			(totalWithUnknownPUID/total)*100.0,
			(totalWithRightValidStatus/total)*100.0,
			(totalWithWrongValidStatus/total)*100.0,
			(totalWithUnknownValidStatus/total)*100.0,
			(totalFeaturesExtracted/total),
			(totalProcessingTime/total)
		};
		cellnum = 0;
		for (Object obj : totals){
			Cell cell = row.createCell(cellnum++);
			if(obj instanceof String)
				cell.setCellValue((String)obj);
			else if(obj instanceof Integer)
				cell.setCellValue((Integer)obj);
			else if(obj instanceof Double)
				cell.setCellValue((Double)obj);
			else if(obj instanceof Long)
				cell.setCellValue((Long)obj);
		}

		for(Map.Entry<String, List<FileStat>> entry : fileStats.entrySet()){
			Sheet sheetExtension = wb.createSheet(entry.getKey());
			rownum = 0;
			row = sheetExtension.createRow(rownum++);
			Object[] headersFile = {"Name","Mimetype","Format","PUID","Valid","#Features extracted","Features extracted","Tools used for identification","Tools used for features extraction","Tools used for validation","Processing time (ms)"};
			cellnum = 0;

			for (Object obj : headersFile){
				Cell cell = row.createCell(cellnum++);
				if(obj instanceof String)
					cell.setCellValue((String)obj);
				else if(obj instanceof Integer)
					cell.setCellValue((Integer)obj);
				else if(obj instanceof Long)
					cell.setCellValue((Long)obj);
			}

			CellStyle redStyle = wb.createCellStyle();
			redStyle.setFillForegroundColor(HSSFColor.RED.index);  
			redStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);  

			CellStyle greenStyle = wb.createCellStyle();
			greenStyle.setFillForegroundColor(HSSFColor.GREEN.index);  
			greenStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);  

			CellStyle yellowStyle = wb.createCellStyle();
			yellowStyle.setFillForegroundColor(HSSFColor.YELLOW.index);  
			yellowStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);  




			for(FileStat fs : entry.getValue()){
				row = sheetExtension.createRow(rownum++);
				Object[] data = {fs.getName(),fs.getMimetype(),fs.getFormat(),fs.getPuid(),fs.getValid(),fs.getFeatures().size(),StringUtils.join(fs.getFeatures().keySet(), " / "),StringUtils.join(fs.getIdentificationTools(),","),StringUtils.join(fs.getExtractionTools(),","),StringUtils.join(fs.getValidationTools(),","), fs.getProcessingTime()};
				cellnum = 0;
				for (int i=0;i<data.length;i++){
					Object obj = data[i];

					Cell cell = row.createCell(cellnum++);
					if(obj instanceof String){
						if(fs.getGroundTruth()!=null && i>0 && i<5){
							if(((String)obj)==null || ((String)obj).trim().equalsIgnoreCase("")){
								if(fs.getGroundTruth()[i]==null || fs.getGroundTruth()[i].trim().equalsIgnoreCase("")){

								}else{
									cell.setCellStyle(yellowStyle);
								}
							}else if(fs.getGroundTruth()[i]==null || fs.getGroundTruth()[i].trim().equalsIgnoreCase("")){

							}else if(((String)obj).equalsIgnoreCase(fs.getGroundTruth()[i])){
								cell.setCellStyle(greenStyle);
							}else{
								cell.setCellStyle(redStyle);
							}
						}
						cell.setCellValue((String)obj);
					}else if(obj instanceof Long){
						cell.setCellValue((Long)obj);
					}else if(obj instanceof Double){
						cell.setCellValue((Double)obj);
					}else if(obj instanceof Integer){
						cell.setCellValue((Integer)obj);
					}

				}
			}

		}





		FileOutputStream fileOut = new FileOutputStream(outputFile);
		wb.write(fileOut);
		fileOut.close();
	}

	
	private boolean isEmpty(String s){
		if(s==null || s.trim().equalsIgnoreCase("")){
			return true;
		}else{
			return false;
		}
	}

	private String[] getGroundTruth(String groundFile, String fileName){
		String[] t = new String[5];
		try{
			File excel = new File(groundFile);
			FileInputStream fis = new FileInputStream(excel);

			Workbook wb = new HSSFWorkbook(fis);
			Sheet ws = wb.getSheetAt(0);
			int rowNum = ws.getLastRowNum()+1;
			for (int i=0; i<rowNum; i++){
				Row row = ws.getRow(i);
				if(row != null && row.getCell(0)!=null){
					if(fileName.endsWith(row.getCell(0).getStringCellValue())){
						t[0] = row.getCell(0)!=null?row.getCell(0).getStringCellValue():"";
						t[1] = row.getCell(1)!=null?row.getCell(1).getStringCellValue():"";
						t[2] = row.getCell(2)!=null?row.getCell(2).getStringCellValue():"";
						t[3] = row.getCell(3)!=null?row.getCell(3).getStringCellValue():"";
						t[4] =row.getCell(4)!=null?row.getCell(4).getStringCellValue():"";
						break;
					}
				}
			}

		}catch(Exception e){

			e.printStackTrace();
		}
		return t;
	}
	private void printHelp(Options opts) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("report", opts );
	}

	public static void main(String[] args) {

		try{
			Report r = new Report();
			Options options = new Options();
			options.addOption("d",true, "directory with files to process");
			options.addOption("f",true,"fits home");
			options.addOption("o",true, "output file");
			options.addOption("g",true, "ground truth file");
			options.addOption("h",false,"print this message");



			CommandLineParser parser = new GnuParser();
			CommandLine cmd = parser.parse(options, args);

			if(cmd.hasOption("h")) {
				r.printHelp(options);
				System.exit(0);
			}

			if(!cmd.hasOption("d") || !cmd.hasOption("f") || !cmd.hasOption("o")){
				r.printHelp(options);
				System.exit(0);
			}
			r.run(cmd.getOptionValue("f"), cmd.getOptionValue("d"), cmd.getOptionValue("o"),cmd.getOptionValue("g"));



		}catch(Exception e){
			e.printStackTrace();
		}

	}

}


