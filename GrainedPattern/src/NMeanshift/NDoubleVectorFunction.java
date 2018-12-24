package NMeanshift;

/**
 * A function that can be applied to a double vector via {@link NDoubleVector}
 * #apply({@link NDoubleVectorFunction} f);
 */
public interface NDoubleVectorFunction {

  /**
   * Calculates the result with a given index and value of a vector.
   */
  public double calculate(int index, double value);

}
