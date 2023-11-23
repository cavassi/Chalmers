
/**
 * A simple class for measuring the time it takes to run some code.
 * If you want to do some serious timing you should consider using
 * something else instead.
 */
public class Stopwatch {
    private long start;

    public Stopwatch() {
        this.reset();
    }

    /**
     * Resets the elapsed time to 0.
     */
    public void reset() {
        this.start = System.currentTimeMillis();
    }

    /**
     * Returns the elapsed time in seconds after last reset.
     */
    public double elapsedTime() {
        long now = System.currentTimeMillis();
        return (now - this.start) / 1000.0;
    }

    /**
     * Prints a timing report and resets the elapsed time.
     */
   public void finished(String task) {
       System.out.printf("%s took %.2f seconds.\n", task, this.elapsedTime());
       this.reset();
   }

}

