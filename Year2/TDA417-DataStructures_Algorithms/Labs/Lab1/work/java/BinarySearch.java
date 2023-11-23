
/**
 * Different implementations of binary search.
 * 
 * Common description of the below functions:
 * - Precondition: `array` is sorted according to the natural order.
 * - Precondition: all arguments are non-null (no need to check).
 * - Required complexity: O(log(n)) comparisons where n is the length of `array`.
*/
public class BinarySearch {

    /**
     * Check if the array contains the given value.
     * Iterative solution.
     * 
     * @param array an array sorted according to the natural order of its elements.
     * @param value the value to search for.
     * @return true if `value` is in `array`.
     */
    public static<V extends Comparable<? super V>> boolean containsIterative(V[] array, V value) {
        //---------- TASK 1: Iterative version of binary search -------------------//
        // Hint: you probably need some auxiliary variables telling which part 
        // of the array you're looking at.
        throw new UnsupportedOperationException("TODO");
        //---------- END TASK 1 ---------------------------------------------------//
    }

    /**
     * Check if the array contains the given value.
     * Recursive solution.
     * 
     * @param array an array sorted according to the natural order of its elements.
     * @param value the value to search for.
     * @return true if `value` is in `array`.
     */
    public static<V extends Comparable<? super V>> boolean containsRecursive(V[] array, V value) {
        //---------- TASK 2: Recursive version of binary search -------------------//
        // Hint: you need a recursive helper function with some extra
        // arguments telling which part of the array you're looking at.
        throw new UnsupportedOperationException("TODO");
        //---------- END TASK 2 ---------------------------------------------------//
    }

    /**
     * Return the *first* position in the array whose element matches the given value.
     * 
     * @param array an array sorted according to the natural order of its elements.
     * @param value the value to search for.
     * @return the first position where `value` occurs in `array`, or -1 if it doesn't occur.
     */
    public static<V extends Comparable<? super V>> int firstIndexOf(V[] array, V value) {
        //---------- TASK 3: Binary search returning the first index --------------//
        // It's up to you if you want to use an iterative or recursive version.
        throw new UnsupportedOperationException("TODO");
        //---------- END TASK 3 ---------------------------------------------------//
    }

    // Put your own tests here.

    public static void main(String[] args) {
        Integer[] integer_test_array = {1, 3, 5, 7, 9};

        assert containsIterative(integer_test_array, 4) == false;
        assert containsIterative(integer_test_array, 7) == true;

        assert containsRecursive(integer_test_array, 0) == false;
        assert containsRecursive(integer_test_array, 9) == true;

        String[] string_test_array = {"cat", "cat", "cat", "dog", "turtle", "turtle"};

        assert firstIndexOf(string_test_array, "cat") == 0;
        assert firstIndexOf(string_test_array, "dog") == 3;
        assert firstIndexOf(string_test_array, "turtle") == 4;
        assert firstIndexOf(string_test_array, "zebra") == -1;
    }

}

