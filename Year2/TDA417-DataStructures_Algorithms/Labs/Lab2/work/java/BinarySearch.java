
import java.io.IOException;

public class BinarySearch {

    public static int binarySearchFirst(SuffixArray sa, String value) {
        int[] index = sa.index;
        String text = sa.text;

        int min = 0;
        int max = index.length - 1;
        int result = -1;

        while (min <= max){
            int middle = (min+max)/2;
            int start = index[middle];
            int end = Math.min(start + value.length(), text.length());            
            
            for(int i = start; i < end; i++){
                
            }

            int compare = text.substring(start, end).compareTo(value);
            
            if(compare == 0){
                result = middle;
                max = middle -1;
            }
            else if(compare > 0){
                max = middle - 1;
            }
            else{
                min = middle + 1;
            }
        }
        return result;
    }
    

    public static void main(String[] args) throws IOException {
        SuffixSorter sorter = new Quicksort();
        SuffixArray sa = new SuffixArray();

        //sa.loadText("Labs/Lab2/work/texts/bnc-tiny.txt");
        sa.setText("ABRACADABRA");
        sa.buildIndex(sorter);
        sa.checkIndex();
        sa.print("Suffix array", new int[]{0, sa.size()}, "   ");

        // Search for some strings, e.g.: "ABRA", "RAC", "RAD", "AA"
        String value = "";
        System.out.format("Searching for: '%s'%n", value);
        int i = binarySearchFirst(sa, value);
        if (i < 0) {
            System.out.format("--> String not found%n");
        } else {
            int pos = sa.index[i];
            System.out.format("--> String found at index: %d --> text position: %d%n", i, pos);
        }

        
        // Next step is to search in a slightly larger text file, such as:

        
        // Try, e.g., to search for the following strings:
        // "and", "ands", "\n\n", "zz", "zzzzz"
        
    }
}

