import java.util.ArrayList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * @author John Foley <jpf7324@truman.edu>
 * @date 12/6/14
 *
 * A class to represent objects given to threads. This object is in charge of
 * looking at it's neighbors and determining if it should change whatever index
 * it's iterating over to a different region label
 */
public class Topographer implements Runnable {

  // Shared matrix of region labels
  ArrayList<ArrayList<ImplicitlyLockingIndex>> regions;

  // Row number
  int rowNum;

  // Read only row of pixels
  final ArrayList<Integer> row;

  // Shared reusable barrier
  final CyclicBarrier barrier;

  // Signals to continue with the algorithm or not
  ThreadCoordinator overlord;

  // Signals if this thread made a change
  private boolean noChange = false;

  /**
   * Constructor
   *
   * @param regions  Shared matrix of region labels
   * @param rowNum   Row number in the matrix
   * @param row      Read only row of pixels
   * @param barrier  Reusable barrier
   * @param overlord Signals to continue or not
   */
  public Topographer(ArrayList<ArrayList<ImplicitlyLockingIndex>> regions, int rowNum,
                     ArrayList<Integer> row, CyclicBarrier barrier, ThreadCoordinator overlord) {

    this.regions = regions;
    this.rowNum = rowNum;
    this.row = row;
    this.barrier = barrier;
    this.overlord = overlord;
  }

  /**
   * This function is ran automatically when the thread start() is called.
   */
  public void run() {

    while (!overlord.isDone()) {

      labelRegions();

      try {

        barrier.await();
      } catch (InterruptedException ex) {

        return;
      } catch (BrokenBarrierException ex) {

        return;
      }
    }
  }

  /**
   * Iterator over the row and calculate the region labels
   */
  private void labelRegions() {

    for (int col = 0; col < row.size(); col++) {

      int regionLabel = max(col);

      // test to see if the numbers are different
      if (regionLabel != regions.get(rowNum).get(col).get()) {

        // They are, so set it and a change was made
        regions.get(rowNum).get(col).set(regionLabel);
        noChange = false;
      }

      if (noChange) {

        overlord.doneFirstTime();
      }
    }
  }

  private int max(int col) {

    /* Given a matrix of [m x n]  These are the neighbors.
     *  The 0th row of the matrix is the top row,
     *  The m-1th row is the bottom
     *  The 0th column is the left most column
     *  The n-1th column is the right most column
     */
    int up, down, left, right;

    if ((rowNum - 1) < 0) {

      up = 0;
    } else {

      up = regions.get(rowNum - 1).get(col).get();
    }

    if ((rowNum + 1) > regions.size()) {

      down = 0;
    } else {

      down = regions.get(rowNum + 1).get(col).get();
    }

    if ((col - 1) < 0) {

      left = 0;
    } else {

      left = regions.get(rowNum).get(col - 1).get();
    }

    if ((col + 1) > row.size()) {

      right = 0;
    } else {

      right = regions.get(rowNum).get(col + 1).get();
    }

    return Math.max(up,
      Math.max(down,
        Math.max(left,
          right)));
  }

}
