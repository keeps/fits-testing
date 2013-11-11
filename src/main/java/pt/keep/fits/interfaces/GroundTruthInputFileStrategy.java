package pt.keep.fits.interfaces;

public interface GroundTruthInputFileStrategy {

  String[] getGroundTruth(String groundTruthFile, String fileName);
}
