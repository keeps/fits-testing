package pt.keep.fits;

public class ExtensionStat {
	String extension;
	int total;
	int valid;
	int notValid;
	int unknownValid;
	int withPUID;
	int withMimeType;
	
	
	int rightValidStatus;
	int rightFormatStatus;
	int rightPUIDStatus;
	int rightMimeTypeStatus;
	int wrongValidStatus;
	int wrongFormatStatus;
	int wrongPUIDStatus;
	int wrongMimeTypeStatus;
	int unknownValidStatus;
	int unknownFormatStatus;
	int unknownPUIDStatus;
	int unknownMimeTypeStatus;
	
	long processingTime;
	
	double featuresExtracted;
	
	
	
	public long getProcessingTime() {
		return processingTime;
	}
	public void setProcessingTime(long processingTime) {
		this.processingTime = processingTime;
	}
	public double getFeaturesExtracted() {
		return featuresExtracted;
	}
	public void setFeaturesExtracted(double featuresExtracted) {
		this.featuresExtracted = featuresExtracted;
	}
	public String getExtension() {
		return extension;
	}
	public void setExtension(String extension) {
		this.extension = extension;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getValid() {
		return valid;
	}
	public void setValid(int valid) {
		this.valid = valid;
	}
	public int getNotValid() {
		return notValid;
	}
	public void setNotValid(int notValid) {
		this.notValid = notValid;
	}
	public int getUnknownValid() {
		return unknownValid;
	}
	public void setUnknownValid(int unknownValid) {
		this.unknownValid = unknownValid;
	}
	public int getWithPUID() {
		return withPUID;
	}
	public void setWithPUID(int withPUID) {
		this.withPUID = withPUID;
	}
	public int getWithMimeType() {
		return withMimeType;
	}
	public void setWithMimeType(int withMimeType) {
		this.withMimeType = withMimeType;
	}
	public int getRightValidStatus() {
		return rightValidStatus;
	}
	public void setRightValidStatus(int rightValidStatus) {
		this.rightValidStatus = rightValidStatus;
	}
	public int getRightFormatStatus() {
		return rightFormatStatus;
	}
	public void setRightFormatStatus(int rightFormatStatus) {
		this.rightFormatStatus = rightFormatStatus;
	}
	public int getRightPUIDStatus() {
		return rightPUIDStatus;
	}
	public void setRightPUIDStatus(int rightPUIDStatus) {
		this.rightPUIDStatus = rightPUIDStatus;
	}
	public int getRightMimeTypeStatus() {
		return rightMimeTypeStatus;
	}
	public void setRightMimeTypeStatus(int rightMimeTypeStatus) {
		this.rightMimeTypeStatus = rightMimeTypeStatus;
	}
	public int getWrongValidStatus() {
		return wrongValidStatus;
	}
	public void setWrongValidStatus(int wrongValidStatus) {
		this.wrongValidStatus = wrongValidStatus;
	}
	public int getWrongFormatStatus() {
		return wrongFormatStatus;
	}
	public void setWrongFormatStatus(int wrongFormatStatus) {
		this.wrongFormatStatus = wrongFormatStatus;
	}
	public int getWrongPUIDStatus() {
		return wrongPUIDStatus;
	}
	public void setWrongPUIDStatus(int wrongPUIDStatus) {
		this.wrongPUIDStatus = wrongPUIDStatus;
	}
	public int getWrongMimeTypeStatus() {
		return wrongMimeTypeStatus;
	}
	public void setWrongMimeTypeStatus(int wrongMimeTypeStatus) {
		this.wrongMimeTypeStatus = wrongMimeTypeStatus;
	}
	public int getUnknownValidStatus() {
		return unknownValidStatus;
	}
	public void setUnknownValidStatus(int unknownValidStatus) {
		this.unknownValidStatus = unknownValidStatus;
	}
	public int getUnknownFormatStatus() {
		return unknownFormatStatus;
	}
	public void setUnknownFormatStatus(int unknownFormatStatus) {
		this.unknownFormatStatus = unknownFormatStatus;
	}
	public int getUnknownPUIDStatus() {
		return unknownPUIDStatus;
	}
	public void setUnknownPUIDStatus(int unknownPUIDStatus) {
		this.unknownPUIDStatus = unknownPUIDStatus;
	}
	public int getUnknownMimeTypeStatus() {
		return unknownMimeTypeStatus;
	}
	public void setUnknownMimeTypeStatus(int unknownMimeTypeStatus) {
		this.unknownMimeTypeStatus = unknownMimeTypeStatus;
	}
	
	
	
}
