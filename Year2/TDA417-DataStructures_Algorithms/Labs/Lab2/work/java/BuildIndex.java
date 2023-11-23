
import java.io.IOException;
import java.util.Map;

public class BuildIndex {

    static Map<String, SuffixSorter> suffixSorters = Map.of(
        "insertion", new InsertionSort(),
        "quicksort", new Quicksort(),
        "multikey", new MultikeyQuicksort(),
        "builtin", new BuiltinSort()
    );

    static Map<String, PivotSelector> pivotSelectors = Map.of(
        "first", PivotSelector.TakeFirstPivot,
        "middle", PivotSelector.TakeMiddlePivot,
        "random", PivotSelector.RandomPivot,
        "median", PivotSelector.MedianOfThreePivot,
        "adaptive", PivotSelector.AdaptivePivot
    );

    public static void main(String[] args) throws IOException {
        CommandParser parser = new CommandParser("BuildIndex", "Build an inverted search index.");
        parser.addArgument("--textfile", "-f", "text file (utf-8 encoded)")
            .makeRequired();
        parser.addArgument("--algorithm", "-a", "sorting algorithm")
            .makeRequired().setChoices(suffixSorters.keySet());
        parser.addArgument("--pivot", "-p", "pivot selectors (only for quicksort algorithms)")
            .setChoices(pivotSelectors.keySet());

        CommandParser.Namespace options = parser.parseArgs(args);

        // Create stopwatches to time the execution of each phase of the program.
        Stopwatch stopwatchTotal = new Stopwatch();
        Stopwatch stopwatch = new Stopwatch();

        // Read the text file.
        String textFile = options.getString("textfile");
        SuffixArray suffixArray = new SuffixArray();
        suffixArray.loadText(textFile);
        stopwatch.finished(String.format("Reading %s chars from '%s'", suffixArray.size(), textFile));

        // Select sorting algorithm.
        SuffixSorter sortingAlgorithm = suffixSorters.get(options.getString("algorithm"));
        if (options.getString("pivot") != null) {
            sortingAlgorithm.setPivotSelector(pivotSelectors.get(options.getString("pivot")));
        }

        // Build the index using the selected sorting algorithm.
        suffixArray.buildIndex(sortingAlgorithm);
        stopwatch.finished("Building index");
 
        // Check that it's sorted.
        suffixArray.checkIndex();
        stopwatch.finished("Checking index");

        // Save it to an index file.
        suffixArray.saveIndex();
        stopwatch.finished(String.format("Saving index to '%s'", suffixArray.indexFile));

        stopwatchTotal.finished("In total the program");
    }

}

