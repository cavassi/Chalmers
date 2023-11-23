
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

// This class is designed it be run.
// It is a search prompt for searching in the specified text file.
// We assume that the suffix array has been built and stored on disk before.
// (For this, see `BuildSuffixArray`.)
public class SearchIndex {
    public static final int NUM_MATCHES = 10;
    public static final int CONTEXT = 40;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        CommandParser parser = new CommandParser("SearchIndex", "Search tool for text files.");
        parser.addArgument("--textfile", "-f", "text file (utf-8 encoded)")
            .makeRequired();
        parser.addArgument("--linear-search", "-l", "use linear search (much slower than binary search)")
            .makeTrueOption();
        parser.addArgument("--num-matches", "-n", "number of matches to show (default: "+NUM_MATCHES+" matches)")
            .makeInteger().setDefault(NUM_MATCHES);
        parser.addArgument("--context", "-c", "context to show to the left and right (default: "+CONTEXT+" characters)")
            .makeInteger().setDefault(CONTEXT);
        parser.addArgument("--trim-lines", "-t", "trim each search result to the matching line")
            .makeTrueOption();
        parser.addArgument("--search-string", "-s", "string(s) to search for")
            .makeList();

        CommandParser.Namespace options = parser.parseArgs(args);

        String textFile = options.getString("textfile");
        boolean linearSearch = options.getBoolean("linear-search");
        int numMatches = options.getInteger("num-matches");
        int context = options.getInteger("context");
        boolean trimLines = options.getBoolean("trim-lines");
        List<String> searchStrings = options.getStringList("search-string");

        // Create a stopwatch to time the execution of each phase of the program.
        Stopwatch stopwatch = new Stopwatch();

        // Read the text file.
        SuffixArray suffixArray = new SuffixArray();
        suffixArray.loadText(textFile);
        stopwatch.finished(String.format("Reading %s chars from '%s'", suffixArray.size(), textFile));

        // Load the index if we're using it.
        if (!linearSearch) {
            suffixArray.loadIndex();
            stopwatch.finished("Loading the index");
        }

        // Set up the search loop.
        System.out.println();
        Scanner input;
        String prompt;
        if (searchStrings != null && !searchStrings.isEmpty()) {
            input = new Scanner(String.join("\n", searchStrings));
            prompt = "";
        } else {
            input = new Scanner(System.in);
            prompt = "Search key (ENTER to quit): ";
        }

        // The main REPL (read-eval-print loop).
        // Read search key from input line, exit if there is no more input.
        while (true) {
            System.out.print(prompt);
            System.out.flush();
            if (!input.hasNextLine())
                break;
            String value = input.nextLine();
            if (value.isEmpty())
                break;

            // Search for the first occurrence of the search string.
            System.out.format("Searching for '%s':\n", value.replaceAll("(\\n|\\r)+", " "));
            stopwatch.reset();
            Iterable<Integer> results = (
                linearSearch
                ? suffixArray.linearSearch(value)
                : suffixArray.binarySearch(value)
            );

            // Iterate through the search results.
            int ctr = 0;
            String plus = "";
            for (int start : results) {
                int end = start + value.length();
                printKeywordInContext(suffixArray.text, start, end, context, trimLines);
                ctr++;
                if (ctr >= numMatches) {
                    plus = "+";
                    break;
                }
            }
            stopwatch.finished(String.format("Finding %d%s matches", ctr, plus));
            System.out.println();
        }
    }

    public static void printKeywordInContext(String text, int start, int end, int context, boolean trimLines) {
        // Print one match (between positions [start...end-1]),
        // together with `args.context` bytes of context before and after.

        int contextStart = Math.max(0, start - context);
        int contextEnd = Math.min(text.length(), end + context);

        String prefix = text.substring(contextStart, start);
        String found  = text.substring(start, end);
        String suffix = text.substring(end, contextEnd);

        if (trimLines) {
            int i;
            if ((i = prefix.lastIndexOf("\n")) >= 0) {
                prefix = prefix.substring(i+1);
            }
            if ((i = suffix.indexOf("\n")) >= 0) {
                suffix = suffix.substring(0, i);
            }
        }

        found = found.replaceAll("\\n", " ").replaceAll("\\r", "");
        prefix = prefix.replaceAll("\\n", " ").replaceAll("\\r", "");
        suffix = suffix.replaceAll("\\n", " ").replaceAll("\\r", "");

        System.out.format("%8d:  %" + context + "s|%s|%-" + context + "s\n", start, prefix, found, suffix);
    }

}

