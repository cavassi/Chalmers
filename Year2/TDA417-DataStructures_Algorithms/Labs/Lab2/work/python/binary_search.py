
from typing import List

from suffix_array import SuffixArray
from suffix_sorter import SuffixSorter
from quicksort import Quicksort


def binarySearchFirst(sa: SuffixArray, value: str) -> int:
    index: List[int] = sa.index
    text: str = sa.text

    result = -1

    min = 0
    max = len(sa.index)    


    # #---------- TASK 4: Binary search returning the first index ----------#
    # # TODO: Replace these lines with your solution!
    # # It's up to you if you want to use an iterative or recursive version.
    # # It's ok to add additional helper methods.
    # raise NotImplementedError()
    # #---------- END TASK 4 -----------------------------------------------#

    return result


def main():
    sorter: SuffixSorter = Quicksort()
    sa: SuffixArray = SuffixArray()

    sa.setText("ABRACADABRA")
    sa.buildIndex(sorter)
    #sa.checkIndex()
    sa.print("Suffix array", [0, sa.size()], "   ")

    # Search for some strings, e.g.: "ABRA", "RAC", "RAD", "AA"
    value: str = "ABRA"
    print(f"Searching for: '{value}'")
    i = binarySearchFirst(sa, value)
    if i < 0:
        print("--> String not found")
    else:
        pos = sa.index[i]
        print(f"--> String found at index: {i} --> text position: {pos}")

    """
    # Next step is to search in a slightly larger text file, such as:
    sa.loadText("texts/bnc-tiny.txt");
    sa.buildIndex(sorter);
    ...
    # Try, e.g., to search for the following strings:
    # "and", "ands", "\n\n", "zz", "zzzzz"
    """


if __name__ == "__main__":
    main()

