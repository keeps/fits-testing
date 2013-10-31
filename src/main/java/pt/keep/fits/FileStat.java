package pt.keep.fits;

import java.util.List;
import java.util.Map;

public class FileStat {
	String name;
	String mimetype;
	String puid;
	String valid;
	String format;
	long processingTime;
	String[] groundTruth;
	Map<String,String> features;
	List<String> identificationTools;
	List<String> validationTools;
	List<String> extractionTools;
	
	
	
	
	
	
	
	public List<String> getIdentificationTools() {
		return identificationTools;
	}
	public void setIdentificationTools(List<String> identificationTools) {
		this.identificationTools = identificationTools;
	}
	public List<String> getValidationTools() {
		return validationTools;
	}
	public void setValidationTools(List<String> validationTools) {
		this.validationTools = validationTools;
	}
	public List<String> getExtractionTools() {
		return extractionTools;
	}
	public void setExtractionTools(List<String> extractionTools) {
		this.extractionTools = extractionTools;
	}
	public Map<String, String> getFeatures() {
		return features;
	}
	public void setFeatures(Map<String, String> features) {
		this.features = features;
	}
	public String[] getGroundTruth() {
		return groundTruth;
	}
	public void setGroundTruth(String[] groundTruth) {
		this.groundTruth = groundTruth;
	}
	public long getProcessingTime() {
		return processingTime;
	}
	public void setProcessingTime(long processingTime) {
		this.processingTime = processingTime;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMimetype() {
		return mimetype;
	}
	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}
	public String getPuid() {
		return puid;
	}
	public void setPuid(String puid) {
		this.puid = puid;
	}
	public String getValid() {
		return valid;
	}
	public void setValid(String valid) {
		this.valid = valid;
	}
	
	
}
