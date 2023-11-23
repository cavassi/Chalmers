
import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;


/** 
 * A simple progress bar, inspired by Python's `tqdm` module.
 * You don't have to understand how it works!
 */
class ProgressBar<T> implements Iterable<T> {

    // Class-variable to turn on/off all progress bars.
    public static boolean visible = true;

    // Instance variables.
    Iterable<T> iterable;
    String description;
    double startTime;
    int total;
    int n;
    int interval;
    int unit = 1_000;
    String unitSuffix = "k";
    int barWidth = 40;
    int descrWidth = 25;

    public static ProgressBar<Integer> range(int start, int end, String description) {
        Collection<Integer> collection = new AbstractList<>() {
            @Override
            public Integer get(int index) {
                return start + index;
            }
            @Override
            public int size() {
                return end - start;
            }
        };
        return new ProgressBar<>(collection, description);
    }

    ProgressBar(Collection<T> elements, String description) {
        this(elements, elements.size(), description);
    }

    ProgressBar(int total, String description) {
        this(null, total, description);
    }

    ProgressBar(Iterable<T> iterable, int total, String description) {
        this.startTime = System.nanoTime();
        this.iterable = iterable;
        this.description = description;
        this.total = total;
        this.n = 0;
        this.interval = Math.max(1, Math.min(this.total/200, 100));
        this.setUnit(this.total < 20_000 ? 1 : this.total < 20_000_000 ? 1_000 : 1_000_000);
        this.printInfoline();
    }

    public ProgressBar<T> setUnit(int unit) {
        this.unit = unit;
        switch (unit) {
            case 1: this.unitSuffix = " "; break;
            case 1_000: this.unitSuffix = "k"; break;
            case 1_000_000: this.unitSuffix = "M"; break;
            default: throw new IllegalArgumentException("Can only handle unit == 1 or 1000 or 1_000_000");
        }
        return this;
    }

    public ProgressBar<T> setBarWidth(int barWidth) {
        this.barWidth = barWidth;
        return this;
    }

    public ProgressBar<T> setDescrWidth(int descrWidth) {
        this.descrWidth = descrWidth;
        return this;
    }

    public Iterator<T> iterator() {
        Iterator<T> iter = iterable.iterator();
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                boolean has = iter.hasNext();
                if (!has) {
                    printInfoline();
                    closeInfoline();
                }
                return has;
            }
            @Override
            public T next() {
                n++;
                if (n % interval == 0) {
                    printInfoline();
                }
                return iter.next();
            }
        };
    }

    public void close() {
        this.printInfoline();
        this.closeInfoline();
    }

    public void setValue(int n) {
        if (n > this.n) {
            this.n = n;
            if (n % this.interval == 0) {
                this.printInfoline();
            }
        }
    }

    public void update(int add) {
        this.n += add;
        if (this.n % this.interval == 0) {
            this.printInfoline();
        }
    }

    private void printInfoline() {
        if (this.visible) {
            float percent = 0;
            if (this.total > 0) {
                percent = (float) this.n / this.total;
            }
            int hashes = Math.round(percent * this.barWidth);
            String pbar = "[" + "=".repeat(hashes) + "·".repeat(this.barWidth - hashes) + "]";
            double elapsed = (System.nanoTime() - this.startTime) / 1e9;
            System.err.format(
                "%-" + this.descrWidth + "s %3.0f%% %s %6.0f%s of %.0f%s  | %6.1f s\r",
                this.description, 100 * percent, pbar, 
                (float) this.n/this.unit, this.unitSuffix,
                (float) this.total/this.unit, this.unitSuffix, elapsed
            );
            System.err.flush();
        }
    }

    private void closeInfoline() {
        if (ProgressBar.visible) {
            System.err.println();
        }
    }

}

