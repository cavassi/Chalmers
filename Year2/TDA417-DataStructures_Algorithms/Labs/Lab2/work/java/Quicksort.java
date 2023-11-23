import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;

class Quicksort extends SuffixSorter {
    SuffixArray sa;
    ProgressBar<Void> progressBar;
    int progressBarSpanSize;

    Quicksort() {
        this.pivotSelector = PivotSelector.MedianOfThreePivot;
    }

    public void sort(SuffixArray suffixArray) {
        this.sa = suffixArray;
        int size = suffixArray.size();
        this.progressBar = new ProgressBar<>(size, "Quicksorting");
        this.progressBarSpanSize = size / 10_000;
        // Don't change this call, the second argument should be `size`:
        this.quicksort(0, size);
        this.progressBar.setValue(size);
        this.progressBar.close();
    }

    public void quicksort(int start, int end) {
        // Hint when completing the code: 
        // The variable `end` points to the element *after* the last one in the interval!

        int size = end - start;

        // Base case: the list to sort has at most one element.
        if (size <= 1) {
            return;
        }

        // Don't update the progress bar unnecessarily often.
        if (size >= progressBarSpanSize) {
            this.progressBar.setValue(start);
        }

        //---------- TASK 3b: Quicksort ---------------------------------------//
//        int pivot = pivotSelector.pivotIndex(sa, start, end);
        int pivot = partition(start, end);
        quicksort(start, pivot);
        quicksort(pivot + 1, end);


        // TODO: Replace these lines with your solution!
        //if (true) throw new UnsupportedOperationException();
        //---------- END TASK 3b ----------------------------------------------//
    }

    public int partition(int start, int end) {
        // Hints when completing the code: 
        // - The variable `end` points to the element *after* the last one in the interval!
        // - You can use the following methods for convenience:
        //   sa.index, sa.swap, sa.compareSuffixes

        // Select the pivot, and find the pivot suffix.
        int pivotIndex = pivotSelector.pivotIndex(sa, start, end);
        int pivotSuffix = sa.index[pivotIndex];

        // Swap the pivot so that it is the first element.
        if (start != pivotIndex) {
            sa.swap(start, pivotIndex);
        }

        // This is the Hoare partition scheme, where the pointers move in opposite direction.
        int lo = start + 1;
        int hi = end - 1;
        
        // This is the value that will be returned in the end - the final position of the pivot.
        int newPivotIndex = -1;

        //---------- TASK 3b: Quicksort ---------------------------------------//
        while (true) {
            while (lo <= hi && sa.compareSuffixes(sa.index[lo], pivotSuffix) < 0) {
                lo++;
            }
            while(hi >= lo && sa.compareSuffixes(sa.index[hi], pivotSuffix) > 0) {
                hi--;
            }
            if (lo > hi) {
                break;
            }

            sa.swap(lo, hi);
            lo++;
            hi--;
        }
        sa.swap(start, hi);
        newPivotIndex = hi;


        // TODO: Replace these lines with your solution!
        //if (true) throw new UnsupportedOperationException();
        //---------- END TASK 3b ----------------------------------------------//

        if (debug) {
            // When debugging, print an excerpt of the suffix array.
            String pivotValue = (
                pivotSuffix + 20 <= sa.size()
                ? sa.text.substring(pivotSuffix, pivotSuffix + 20) + "..."
                : sa.text.substring(pivotSuffix)
            );
            String header = String.format("start: %d, end: %d, pivot: %s", start, end, pivotValue);
            sa.print(header, new int[] {start, newPivotIndex, newPivotIndex+1, end}, " <=> ");
        }

        return newPivotIndex;
    }


    public static void main(String[] args) throws IOException {
        SuffixSorter sorter = new Quicksort();
        SuffixArray sa = new SuffixArray();
        /*
        // Run this for debugging.
        sorter.setDebugging(true);
        sa.setText("ABRACADABRA");
        sa.buildIndex(sorter);
        sa.checkIndex();
        sa.print("ABRACADABRA");
        */

        // Some example performance tests.
        // Wait with these until you're pretty certain that your code works.
        sorter.setDebugging(false);
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for (int k = 1; k < 6; k++) {
            int size = k * 1_000;
            sa.generateRandomText(size, alphabet);
            sa.buildIndex(sorter);
            sa.checkIndex();
            sa.print(String.format("size: %,d, alphabet: '%s'", size, alphabet));
        }


        // What happens if you try different alphabet sizes?
        // (E.g., smaller ("AB") or larger ("ABC....XYZ"))

        // What happens if you use only "A" as alphabet?
        // (Hint: try much smaller test sizes)
    }
}

