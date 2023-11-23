import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

class InsertionSort extends SuffixSorter {

    public void sort(SuffixArray sa) {
        // Use the following for convenience:
        // * sa.compareSuffixes
        // * sa.index
        // * sa.swap

        for (int i : ProgressBar.range(0, sa.index.length, "Insertion sorting")) {
            int j = i;
            while (j > 0 && sa.compareSuffixes(sa.index[j], sa.index[j-1]) == -1){
                sa.swap(j, j-1);
                j--;
            }

            if (debug) {
                // When debugging, print an excerpt of the suffix array.
                sa.print("i = " + i, new int[] {0, i+1}, " * ");
            }
        }
    }


    public static void main(String[] args) {
        SuffixSorter sorter = new InsertionSort();
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
            int size = k * 10_000;
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

