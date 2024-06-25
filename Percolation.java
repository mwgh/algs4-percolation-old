import edu.princeton.cs.algs4.WeightedQuickUnionUF;
import edu.princeton.cs.algs4.StdRandom;

public class Percolation {
    private int mN;                     // Row and column size
    private int mSize;                  // Grid size
    private int openSites;              // Number of open sites
    private CellStatus[] site;          // Keep track of blocked, open, and full sites
    private WeightedQuickUnionUF ufA;   // Percolation union-find

    private enum CellStatus {
        BLOCKED, OPEN, FULL
    }

    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Size must be positive.");
        }

        mN = n;
        mSize = mN * mN;
        ufA = new WeightedQuickUnionUF(mSize + 1);

        site = new CellStatus[mSize + 1];

        for (int i = 0; i < mSize; i++) {
            site[i] = CellStatus.BLOCKED;
        }

        site[mSize] = CellStatus.FULL;
    }

    public void open(int row, int col) {
        validate(row, col);
        if (isOpen(row, col))
            return;

        site[xyTo1D(row, col)] = CellStatus.OPEN;
        openSites++;
        unionSurroundingOpenSites(row, col);

        fill(row, col);
    }
    
    private void fill(int row, int col) {
        if (!isOpen(row, col))
            assert false;

        if (row == 1 || isNeighbourFull(row, col)) {
            site[xyTo1D(row, col)] = CellStatus.FULL;

            if (col != 1 && isOpen(row, col - 1) && !isFull(row, col - 1))
                fill(row, col - 1);

            if (col != mN && isOpen(row, col + 1) && !isFull(row, col + 1))
                fill(row, col + 1);

            if (row != 1 && isOpen(row - 1, col) && !isFull(row - 1, col))
                fill(row - 1, col);

            if (row != mN && isOpen(row + 1, col) && !isFull(row + 1, col))
                fill(row + 1, col);
        }
    }
    
    private boolean isNeighbourFull(int row, int col) {
        boolean isNeighbourFull = false;

        if (col != 1)
            isNeighbourFull |= isFull(row, col - 1);

        if (col != mN)
            isNeighbourFull |= isFull(row, col + 1);

        if (row != 1)
            isNeighbourFull |= isFull(row - 1, col);

        if (row != mN)
            isNeighbourFull |= isFull(row + 1, col);
        
        return isNeighbourFull;
    }

    private void unionSurroundingOpenSites(int row, int col) {
        int i = xyTo1D(row, col);

        if (col != 1) {
            unionOpen(i, i - 1);
        }

        if (col != mN) {
            unionOpen(i, i + 1);
        }

        if (row != 1) {
            unionOpen(i, i - mN);
        } else {
            // Union the site (row, col) with the virtual top site
            ufA.union(i, mSize);
        }

        if (row != mN) {
            unionOpen(i, i + mN);
        }
    }

    private void unionOpen(int i, int next) {
        if (site[next] == CellStatus.OPEN && (ufA.find(i) != ufA.find(next))) {
            ufA.union(i, next);
        }
    }

    private int xyTo1D(int row, int col) {
        return ((row - 1) * mN) + col - 1;
    }

    public boolean isOpen(int row, int col) {
        validate(row, col);
        return site[xyTo1D(row, col)] != CellStatus.BLOCKED;
    }

    public boolean isFull(int row, int col) {
        validate(row, col);
        return site[xyTo1D(row, col)] == CellStatus.FULL;
    }

    public int numberOfOpenSites() {
        return openSites;
    }

    public boolean percolates() {
        for (int x = 1; x <= mN; x++) {
            if (isFull(mN, x))
                return true;
        }
        return false;
    }

    private void validate(int row, int col) {
        if (row <= 0 || row > mN || col <= 0 || col > mN) {
            throw new IllegalArgumentException("Argument out of bounds");
        }
    }

    public static void main(String[] args) {
        Percolation p = new Percolation(5);

        while (!p.percolates()) {
            int x = StdRandom.uniform(p.mN) + 1;
            int y = StdRandom.uniform(p.mN) + 1;

            p.open(x, y);
        }

        System.out.println("The number of open sites is " + p.numberOfOpenSites());
        System.out.println("The fraction of open sites is " + ((double) p.numberOfOpenSites()) / (p.mSize));
    }
}
