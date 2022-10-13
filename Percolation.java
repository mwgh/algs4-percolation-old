/******************************************************************************
 *  Compilation: testing
 *  Execution: testing
 *  Name: Mary Wang
 *  Date: 2017 Aug 5
 *  Purpose of program: model a percolation system
 ******************************************************************************/

import edu.princeton.cs.algs4.WeightedQuickUnionUF;
import edu.princeton.cs.algs4.StdRandom;

public class Percolation {
    private int mN;                   // row and column size
    private int mSize;                // grid size
    private int numOpenSites;               // number of open sites
    private boolean[] openSites;            // keep track of open sites
    private WeightedQuickUnionUF ufA;  // track surrounding open sites w/ UF data structure

     // create n-by-n grid, with all sites blocked n^2 time represent blocked? none connected w/ each other
    public Percolation(int n) {
        if (n <= 0) throw new java.lang.IllegalArgumentException("Size must be positive");
        mN = n;
        mSize = mN * mN;
        ufA = new WeightedQuickUnionUF(mSize + 2); // 2 virtual sites

        // extra top and bottom node connected to rows
        // all sites closed except for start and end
        int[] id = new int[mSize + 2]; // grid array
        openSites = new boolean[mSize + 2];

        for (int i = 0; i < id.length; i++) {
            id[i] = i;
            openSites[i] = false;
        }

        // don't count these as open sites
        openSites[0] = true;
        openSites[mSize + 1] = true;
    }

    // open site (row, col) if it is not open already
    public    void open(int row, int col) {
        validate(row, col);

        // open it because it's not open
        if (!isOpen(row, col)) {
            // keep track of open sites
            openSites[xyTo1D(row, col)] = true;
            numOpenSites++;

            // actual logic: union with surrounding open sites
            unionOpenSites(row, col);
        }
    }

    // unions with surrounding open sites if open
    private void unionOpenSites(int row, int col) {
        int i = xyTo1D(row, col);

        // not left edge
        if (col != 1)
            unionOpen(i, i - 1);
        // not right edge
        if (col != mN)
            unionOpen(i, i + 1);
        // not top edge
        if (row != 1)
            unionOpen(i, i - mN);
        else
            ufA.union(0, i);
        // not bottom edge
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

    // convert row, col address to array index
    private int xyTo1D(int row, int col) {
        // validate(row, col); // could be unneccessary
        return ((row - 1) * mN) + col;
    }

    // is site (row, col) open?
    public boolean isOpen(int row, int col) {
        validate(row, col);
        return openSites[xyTo1D(row, col)];
    }

    // is site (row, col) full?
    public boolean isFull(int row, int col) {
        validate(row, col);
        return ufA.connected(0, xyTo1D(row, col));
    }

    // number of open sites
    public     int numberOfOpenSites() {
        return numOpenSites;
    }

    // does the system percolate? full site in bottom row
    public boolean percolates() {
        return ufA.connected(0, mSize + 1);
    }

    private void validate(int row, int col) {
        if (row <= 0 || row > mN || col <= 0 || col > mN) throw new java.lang.IllegalArgumentException("Argument out of bounds"); 
    }

    // test client (optional)
    public static void main(String[] args) {
        // Initialize all sites to be blocked
        Percolation p = new Percolation(5);

        while (!p.percolates()) {
            // choose a site uniformly at random among all _blocked sites_
            int x = StdRandom.uniform(p.mN) + 1;
            int y = StdRandom.uniform(p.mN) + 1;

            // open the site
            p.open(x, y);
        }

        // fraction of open sites is estimated percolation threshold
        System.out.println("The number of open sites is " + p.numberOfOpenSites());
        System.out.println("The fraction of open sites is " + ((double) p.numberOfOpenSites()) / (p.mSize));
    }
}
