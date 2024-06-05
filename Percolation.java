/******************************************************************************
 *  Date: 2017 Aug 5
 *  Purpose of program: model a percolation system
 ******************************************************************************/

import edu.princeton.cs.algs4.WeightedQuickUnionUF;
import edu.princeton.cs.algs4.StdRandom;

public class Percolation {
    private int mN;                    // Row and column size
    private int mSize;                 // Grid size
    private int numOpenSites;          // Number of open sites
    private boolean[] openSites;       // Keep track of open sites
    private WeightedQuickUnionUF ufA;  // Track surrounding open sites w/ UF data structure

     // Create n-by-n grid, with all sites blocked n^2 time represent blocked? none connected w/ each other
    public Percolation(int n) {
        if (n <= 0) throw new java.lang.IllegalArgumentException("Size must be positive");
        mN = n;
        mSize = mN * mN;
        ufA = new WeightedQuickUnionUF(mSize + 2); // 2 virtual sites

        int[] id = new int[mSize + 2];
        openSites = new boolean[mSize + 2];

        for (int i = 0; i < id.length; i++) {
            id[i] = i;
            openSites[i] = false;
        }

        openSites[0] = true;
        openSites[mSize + 1] = true;
    }

    // Open site (row, col) if it is not open already
    public    void open(int row, int col) {
        validate(row, col);

        if (!isOpen(row, col)) {
            openSites[xyTo1D(row, col)] = true;
            numOpenSites++;

            unionOpenSites(row, col);
        }
    }

    // Unions with surrounding open sites if open
    private void unionOpenSites(int row, int col) {
        int i = xyTo1D(row, col);

        // Not left edge
        if (col != 1)
            unionOpen(i, i - 1);
        // Not right edge
        if (col != mN)
            unionOpen(i, i + 1);
        // Not top edge
        if (row != 1)
            unionOpen(i, i - mN);
        else
            ufA.union(0, i);
        // Not bottom edge
        if (row != mN)
            unionOpen(i, i + mN);
        else
            ufA.union(mSize + 1, i);
    }

    private void unionOpen(int i, int next) {
        if (!ufA.connected(i, next) && openSites[next]) {
            ufA.union(i, next);
        }
    }

    // Convert row, col address to array index
    private int xyTo1D(int row, int col) {
        return ((row - 1) * mN) + col;
    }

    // Is site (row, col) open?
    public boolean isOpen(int row, int col) {
        validate(row, col);
        return openSites[xyTo1D(row, col)];
    }

    // Is site (row, col) full?
    public boolean isFull(int row, int col) {
        validate(row, col);
        return ufA.connected(0, xyTo1D(row, col));
    }

    // Number of open sites
    public     int numberOfOpenSites() {
        return numOpenSites;
    }

    // Does the system percolate? Full site in bottom row
    public boolean percolates() {
        return ufA.connected(0, mSize + 1);
    }

    private void validate(int row, int col) {
        if (row <= 0 || row > mN || col <= 0 || col > mN) throw new java.lang.IllegalArgumentException("Argument out of bounds."); 
    }

    // Test client (optional)
    public static void main(String[] args) {
        // Initialize all sites to be blocked
        Percolation p = new Percolation(5);

        while (!p.percolates()) {
            // Choose a site uniformly at random
            int x = StdRandom.uniform(p.mN) + 1;
            int y = StdRandom.uniform(p.mN) + 1;

            // Open the site
            p.open(x, y);
        }

        // Fraction of open sites is estimated percolation threshold
        System.out.println("The number of open sites is " + p.numberOfOpenSites());
        System.out.println("The fraction of open sites is " + ((double) p.numberOfOpenSites()) / (p.mSize));
    }
}
