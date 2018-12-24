package NMeanshift;

public interface NDistanceMeasurer {

  public double measureDistance(double[] set1, double[] set2);

  public double measureDistance(NDoubleVector vec1, NDoubleVector vec2);

}
