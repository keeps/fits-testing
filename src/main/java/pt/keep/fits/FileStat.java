package pt.keep.fits;

public class FileStat {
	String name;
	String mimetype;
	String puid;
	String valid;
	String format;
	long processingTime;
	String[] groundTruth;
	
	
	
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
