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

  // Shared matrix of pixels
  ArrayList<ArrayList<Integer>> pixels;

  // Row number
  int rowNum;

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
   * @param pixels   Shared matrix of pixels
   * @param rowNum   Row number in the matrix
   * @param barrier  Reusable barrier
   * @param overlord Signals to continue or not
   */
  public Topographer(ArrayList<ArrayList<ImplicitlyLockingIndex>> regions,
                     ArrayList<ArrayList<Integer>> pixels, int rowNum,
                     CyclicBarrier barrier, ThreadCoordinator overlord) {

    this.regions = regions;
    this.pixels = pixels;
    this.rowNum = rowNum;
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

    noChange = true;

    for (int col = 0; col < pixels.get(rowNum).size(); col++) {

      // the max region, or -1 if there are no matches and/or out of bounds
      int regionLabel = max(col, pixels.get(rowNum).get(col));

      // test to see if the numbers are different
      if ((regionLabel > regions.get(rowNum).get(col).get()) && (regionLabel != -1)) {

        // They are, so set it and a change was made
        regions.get(rowNum).get(col).set(regionLabel);

        noChange = false;
      }
    }

    if (noChange) {

      // Signal the coordinator that we made it through without changes
      overlord.doneFirstTime();
    }
  }

  /**
   * Finds the maximum region number of any matching pixels, or returns -1 if there are no matches
   *  or neighbors are out of bounds
   *
   * @param col the current row index
   * @param num the pixel to compare against
   * @return the maximum region label of the num's neighbors
   */
  private int max(int col, int num) {

    /* Given a matrix of [m x n]  These are the neighbors.
     *  The 0th row of the matrix is the top row,
     *  The m-1th row is the bottom
     *  The 0th column is the left most column
     *  The n-1th column is the right most column
     */
    int up, down, left, right;

    /*
     * Check if we're in bounds, and then compare the neighbor numbers
     *  If they match, then keep track of the region number
     *  to find the maximum region number
     */
    // up rowNum - 1
    if ((rowNum - 1) < 0) {

      up = -1;
    } else {

      if (pixels.get(rowNum - 1).get(col) == num) {

        up = regions.get(rowNum - 1).get(col).get();
      } else {

        up = -1;
      }
    }

    // down rowNum + 1
    if ((rowNum + 1) < regions.size()) {

      if (pixels.get(rowNum + 1).get(col) == num) {

        down = regions.get(rowNum + 1).get(col).get();
      } else {

        down = -1;
      }
    } else {

      down = -1;
    }

    // left col - 1
    if ((col - 1) < 0) {

      left = -1;
    } else {

      if (pixels.get(rowNum).get(col - 1) == num) {

        left = regions.get(rowNum).get(col - 1).get();
      } else {

        left = -1;
      }
    }

    // right col + 1
    if ((col + 1) < pixels.get(rowNum).size()) {

      if (pixels.get(rowNum).get(col + 1) == num) {

        right = regions.get(rowNum).get(col + 1).get();
      } else {

        right = -1;
      }
    } else {

      right = -1;
    }

    return Math.max(up, Math.max(down, Math.max(left, right)));
  }
}
