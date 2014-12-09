import java.util.ArrayList;

/**
 * @author John Foley <jpf7324@truman.edu>
 * @date 12/6/14
 *
 * This class keeps track of how many threads finish their first time through.
 * NOTE: There should only be one of these, and a reference is distributed to
 *  each thread to share.
 */
public class ThreadCoordinator {

  private int doneFirstTimeCount;
  private int numThreads;

  /**
   * Creates a ThreadCoordinator object
   *
   * @param numThreads number of threads to count up to
   */
  public ThreadCoordinator(int numThreads) {

    doneFirstTimeCount = 0;
    this.numThreads = numThreads;
  }

  /**
   * Tells all the threads to continue or not
   *
   * @return boolean to continue or not
   */
  public boolean isDone() {

    synchronized (this) {

      // if the amount of threads that finished first time through is equal
      // to the amount of threads, then the solution is complete
      if (doneFirstTimeCount == numThreads) {

        // only returns; the count remains the same, so should return true repeatedly
        return true;
      } else {

        // the first thread will reset, so it will be false for all the others
        doneFirstTimeCount = 0;
        return false;
      }
    }
  }

  /**
   * A thread will call this to signal it ran through once without changes
   */
  public void doneFirstTime() {
    synchronized (this) {

      doneFirstTimeCount++;
    }
  }
}
