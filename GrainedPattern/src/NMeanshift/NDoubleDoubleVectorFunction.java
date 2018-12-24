package NMeanshift;

/**
 * A function that can be applied to two double vectors via {@link NDoubleVector}
 * #apply({@link NDoubleVector} v, {@link NDoubleDoubleVectorFunction} f);
 * 
 */
public interface NDoubleDoubleVectorFunction {

  /**
   * Calculates the result of the left and right value of two vectors at a given
   * index.
   */
  public double calculate(int index, double left, double right);

}
