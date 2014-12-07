/**
 * @author John Foley <jpf7324@truman.edu>
 * @date 12/6/14
 *
 * This class represents an index that can be read from and set to atomically
 */
public class ImplicitlyLockingIndices {

  private int num;

  /**
   * Create a new Implicitly locking index with a default value
   */
  public ImplicitlyLockingIndices() {

    num = 0;
  }

  /**
   * Create a new Implicitly locking index with an initial value.
   *
   * @param num initial value
   */
  public ImplicitlyLockingIndices(int num) {

    this.num = num;
  }

  /**
   * Atomically return the value of this index
   *
   * @return num
   */
  public int get() {

    synchronized (this) {

      return num;
    }
  }

  /**
   * Atomically set the value of this index
   *
   * @param value the value the index will be set to
   */
  public void set(int value) {

    synchronized (this) {

      num = value;
    }
  }
}
