
class MultikeyQuicksort extends SuffixSorter {
    SuffixArray sa;
    ProgressBar<Void> progressBar;
    int progressBarSpanSize;

    MultikeyQuicksort() {
        this.pivotSelector = PivotSelector.TakeFirstPivot;
    }

    public void sort(SuffixArray suffixArray) {
        this.sa = suffixArray;
        int size = suffixArray.size();
        this.progressBarSpanSize = size / 10_000;
        this.progressBar = new ProgressBar<>(size, "Multikey quicksorting");
        // Don't change this call, the second argument should be `size`:
        this.multikeyQuicksort(0, size, 0);
        this.progressBar.setValue(size);
        this.progressBar.close();
    }

    public void multikeyQuicksort(int start, int end, int offset) {
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

        //---------- TASK 5: Multikey quicksort -------------------------------//
        // TODO: Replace these lines with your solution!
        if (true) throw new UnsupportedOperationException();
        //---------- END TASK 5 -----------------------------------------------//
    }


    private class IndexPair {
        int start;
        int end;
        IndexPair(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }

    private char getCharAtOffset(int i, int offset) {
        int pos = sa.index[i] + offset;
        return pos < sa.text.length() ? sa.text.charAt(pos) : '\0';
    };

    public IndexPair partition(int start, int end, int offset) {
        // Hints when completing the code: 
        // - The variable `end` points to the element *after* the last one in the interval!
        // - You can use the following methods for convenience:
        //   getCharAtOffset, sa.swap

        // Select the pivot, and find the pivot character.
        int pivotIndex = pivotSelector.pivotIndex(sa, start, end);
        char pivotChar = getCharAtOffset(pivotIndex, offset);

        // Swap the pivot so that it is the first element.
        if (start != pivotIndex) {
            sa.swap(start, pivotIndex);
        }

        // Initialise the middle pointers.
        int middleStart = start;
        int middleEnd = end;

        //---------- TASK 5: Multikey quicksort -------------------------------//
        // TODO: Replace these lines with your solution!
        if (true) throw new UnsupportedOperationException();
        //---------- END TASK 5 -----------------------------------------------//

        if (debug) {
            // When debugging, print an excerpt of the suffix array.
            String pivotValue = ".".repeat(offset) + String.valueOf(pivotChar);
            String header = String.format("start: %d, end: %d, pivot: %s", start, end, pivotValue);
            sa.print(header, new int[] {start, middleStart, middleEnd, end}, " <=> ");
        }

        // Return the new interval containing all elements selected by the pivot char.
        // Note that `middleEnd` should point to the element *after* the last in the interval.
        return new IndexPair(middleStart, middleEnd);
    }


    public static void main(String[] args) {
        SuffixSorter sorter = new MultikeyQuicksort();
        SuffixArray sa = new SuffixArray();

        // Run this for debugging.
        sorter.setDebugging(true);
        sa.setText("ABRACADABRA");
        sa.buildIndex(sorter);
        sa.checkIndex();
        sa.print("ABRACADABRA");

        /*
        // Some example performance tests.
        // Wait with these until you're pretty certain that your code works.
        sorter.setDebugging(false);
        String alpabet = "ABCD";
        for (int k = 1; k < 6; k++) {
            int size = k * 2_000_000;
            sa.generateRandomText(size, alpabet);
            sa.buildIndex(sorter);
            sa.checkIndex();
            sa.print(String.format("size: %,d, alphabet: '%s'", size, alpabet));
        }
        */

        // What happens if you try different alphabet sizes?
        // (E.g., smaller ("AB") or larger ("ABC....XYZ"))

        // What happens if you use only "A" as alphabet?
        // (Hint: try much smaller test sizes)
    }
}

