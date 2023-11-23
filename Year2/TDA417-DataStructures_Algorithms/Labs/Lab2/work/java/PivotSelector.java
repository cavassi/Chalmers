
import java.util.Comparator;
import java.util.List;
import java.util.Random;

// This interface abstracts over the pivot selection strategy in quicksort.
// We provide several implementations:
//  * TakeFirstPivot
//  * TakeMiddlePivot
//  * RandomPivot
//  * MedianOfThreePivot
//  * AdaptivePivot

public interface PivotSelector {
    public int pivotIndex(SuffixArray sa, int start, int end);

    // A pivot selector that chooses the first element.
    static final PivotSelector TakeFirstPivot = new PivotSelector() {
        public int pivotIndex(SuffixArray sa, int start, int end) {
            return start;
        }
    };

    // A pivot selector that chooses the middle element.
    static final PivotSelector TakeMiddlePivot = new PivotSelector() {
        public int pivotIndex(SuffixArray sa, int start, int end) {
            return (start + end) / 2;
        }
    };

    // A pivot selector that chooses a random index.
    static final PivotSelector RandomPivot = new PivotSelector() {
        private Random random = new Random();
        public int pivotIndex(SuffixArray sa, int start, int end) {
            return start + random.nextInt(end - start);
        }
    };

    public static int medianOfThreeIndex(SuffixArray sa, int i, int j, int k) {
        int pos_i = sa.index[i];
        int pos_j = sa.index[j];
        int pos_k = sa.index[k];

        // Out of the below three Booleans, two must be equal. (Why?)
        boolean less_or_equal_ij = sa.compareSuffixes(pos_i, pos_j) <= 0;
        boolean less_or_equal_jk = sa.compareSuffixes(pos_j, pos_k) <= 0;
        boolean less_or_equal_ki = sa.compareSuffixes(pos_k, pos_i) <= 0;

        if (less_or_equal_ij == less_or_equal_jk) {
            // The sequence [i], [j], [k] is ascending or descending.
            return j;
        }
        else if (less_or_equal_jk == less_or_equal_ki) {
            // The sequence [j], [k], [i] is ascending or descending.
            return k;
        }
        else { // less_or_equal_ki == less_or_equal_ij
            // The sequence [k], [i], [j] is ascending or descending.
            return i;
        }
    }

    // A pivot selector that uses median of three.
    static final PivotSelector MedianOfThreePivot = new PivotSelector() {
        public int pivotIndex(SuffixArray sa, int start, int end) {
            // We choose the median between the first, middle, last index.
            int mid = (start + end - 1) / 2;
            return medianOfThreeIndex(sa, start, mid, end - 1);
        }
    };

    // A pivot selector that adapts to the range size.
    static final PivotSelector AdaptivePivot = new PivotSelector() {
        public int pivotIndex(SuffixArray sa, int start, int end) {
            int size = end - start;

            // For small arrays, just pick first element.
            if (size < 10) {
                return TakeFirstPivot.pivotIndex(sa, start, end);
            }

            // For medium arrays, pick median-of-three.
            if (size < 100) {
                return MedianOfThreePivot.pivotIndex(sa, start, end);
            }

            // For large arrays, pick median-of-three of median-of-three.
            int lo = start;
            int hi = end - 1;
            int mid = (lo + hi) / 2;

            int d = size / 8;
            int i = medianOfThreeIndex(sa, lo, lo + d, lo + 2 * d);
            int j = medianOfThreeIndex(sa, hi, hi - d, hi - 2 * d);
            int k = medianOfThreeIndex(sa, mid - d, mid, mid + d);
            return medianOfThreeIndex(sa, i, j, k);
        }
    };

}

