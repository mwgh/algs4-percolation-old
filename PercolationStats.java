import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private static final double CONFIDENCE_95 = 1.96;
    private int mT;
    private double[] percolationThreshold;

    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0) {
            throw new IllegalArgumentException("Grid size n or number of trials must be positive");
        }

        this.mT = trials;
        this.percolationThreshold = new double[trials];

        for (int i = 0; i < this.mT; i++) {
            Percolation perc = new Percolation(n);
            while (!perc.percolates()) {
                int x = StdRandom.uniform(n) + 1;
                int y = StdRandom.uniform(n) + 1;

                perc.open(x, y);
            }
            this.percolationThreshold[i] = ((double) perc.numberOfOpenSites()) / (n * n);
        }
    }

    public double mean() {
        return StdStats.mean(percolationThreshold);
    }

    public double stddev() {
        return StdStats.stddev(percolationThreshold);
    }

    public double confidenceLo() {
        return mean() - (CONFIDENCE_95 * stddev()) / Math.sqrt(this.mT);
    }

    public double confidenceHi() {
        return mean() + (CONFIDENCE_95 * stddev()) / Math.sqrt(this.mT);
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Needs two arguments");
            System.exit(1);
        }
        PercolationStats ps = new PercolationStats(Integer.parseInt(args[0]), Integer.parseInt(args[1]));

        System.out.println("mean                    = " + ps.mean());
        System.out.println("stddev                  = " + ps.stddev());
        System.out.println("95% confidence interval = [" + ps.confidenceLo() + ", " + ps.confidenceHi() + "]");
    }
}
