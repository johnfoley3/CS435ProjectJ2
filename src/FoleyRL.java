/**
 * @author John Foley <jpf7324@truman.edu>
 * @date 12/6/14
 *
 * The main class to run a concurrent solution the region labeling problem in Java.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.CyclicBarrier;

public class FoleyRL {

  /**
   * Main function that's in charge of creating and running the threads
   *
   * @param args should be a file name containing input
   */
  public static void main(String[] args) {

    String filename = args[0];

    // We don't need numThreads, since it's the same as rows,
    // but for simplicity sake I'm going to rename it
    int rows = 0;
    int cols = 0;
    int numThreads = 0;

    ArrayList<ArrayList<Integer>> pixels = new ArrayList<ArrayList<Integer>>();
    ArrayList<ArrayList<ImplicitlyLockingIndex>> regions = new ArrayList<ArrayList<ImplicitlyLockingIndex>>();

    try {

      Scanner reader = new Scanner(new File(filename));

      rows = reader.nextInt();

      cols = reader.nextInt();

      // Loop through each row
      for (int row = 0; row < rows; row++) {

        // Every row gets a temp arraylist
        ArrayList<Integer> line = new ArrayList<Integer>();

        // Read each column value into the row temp list
        for (int col = 0; col < cols; col++) {

          line.add(reader.nextInt());
        }

        // Give the row of ints to the pixel two dimensional array
        pixels.add(line);
      }

      // Potential memory leak; always close your files!
      reader.close();
    } catch (FileNotFoundException e) {

      System.out.println("File is not there ya dummy!");
      e.printStackTrace();
      return;
    }

    numThreads = rows;

    CyclicBarrier barrier = new CyclicBarrier(numThreads);

    ThreadCoordinator overlord = new ThreadCoordinator(numThreads);

    int regionLabel = 0;

    // Initialize the matrix of regions
    for (int row = 0; row < rows; row++) {

      ArrayList<ImplicitlyLockingIndex> temp = new ArrayList<ImplicitlyLockingIndex>();

      for (int col = 0; col < cols; col++) {

        temp.add(new ImplicitlyLockingIndex(regionLabel));

        regionLabel++;
      }

      regions.add(temp);
    }

    ArrayList<Thread> threads = new ArrayList<Thread>();

    // And they're off to the races! (Except not really, I don't have races in my program ;)
    for (int row = 0; row < rows; row++) {

      threads.add(new Thread(new Topographer(regions, row, pixels.get(row), barrier, overlord)));
    }

    for (Thread thread : threads) {

      thread.start();
    }

    for (Thread thread : threads) {

      try {

        thread.join();
      } catch (InterruptedException e) {

        e.printStackTrace();
        return;
      }
    }

    for (int row = 0; row < rows; row++) {

      for (int col = 0; col < cols; col++) {

        System.out.format("%2d", regions.get(row).get(col).get());
      }

      System.out.println();
    }
  }
}
