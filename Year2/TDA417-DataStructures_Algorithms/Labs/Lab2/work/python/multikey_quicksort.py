
from typing import Tuple, Any

from suffix_array import SuffixArray
from suffix_sorter import SuffixSorter
from pivot_selector import PivotSelector, TakeFirstPivot
from progress_bar import ProgressBar


class MultikeyQuicksort(SuffixSorter):
    sa : SuffixArray
    progressBar: ProgressBar[Any]
    progressBarSpanSize: int

    def __init__(self):
        self.pivotSelector = TakeFirstPivot

    def setPivotSelector(self, pivotSelector: PivotSelector):
        self.pivotSelector = pivotSelector

    def sort(self, sa: SuffixArray):
        self.sa = sa
        with ProgressBar(total=sa.size(), description="Multikey quicksorting") as self.progressBar:
            self.progressBarSpanSize = sa.size() // 10_000
            # Don't change this call, the second argument should be `sa.size()`:
            self.multikeyQuicksort(0, sa.size(), offset=0)
            self.progressBar.setValue(sa.size())

    def multikeyQuicksort(self, start: int, end: int, offset: int):
        # Hint when completing the code: 
        # The variable `end` points to the element *after* the last one in the interval!

        size = end - start

        # Base case: the list to sort has at most one element.
        if size <= 1: 
            return

        # Don't update the progress bar unnecessarily often.
        if size >= self.progressBarSpanSize:
            self.progressBar.setValue(start)

        #---------- TASK 5: Multikey quicksort -------------------------------#
        # TODO: Replace these lines with your solution!
        raise NotImplementedError()
        #---------- END TASK 5 -----------------------------------------------#

    def getCharAtOffset(self, i: int, offset: int) -> str:
        try:
            return self.sa.text[self.sa.index[i] + offset]
        except IndexError:
            return ""

    def partition(self, start: int, end: int, offset: int) -> Tuple[int, int]:
        # Hints when completing the code: 
        # - The variable `end` points to the element *after* the last one in the interval!
        # - You can use the following local functions for convenience:
        getCharAtOffset = self.getCharAtOffset
        swap = self.sa.swap
    
        # Select the pivot, and find the pivot character.
        pivotIndex = self.pivotSelector.pivotIndex(self.sa, start, end)
        pivotChar = getCharAtOffset(pivotIndex, offset)

        # Swap the pivot so that it is the first element.
        if start != pivotIndex:
            swap(start, pivotIndex)

        # Initialise the middle pointers.
        middleStart = start
        middleEnd = end

        #---------- TASK 5: Multikey quicksort -------------------------------#
        # TODO: Replace these lines with your solution!
        raise NotImplementedError()
        #---------- END TASK 5 -----------------------------------------------#

        if self.debug:
            # When debugging, print an excerpt of the suffix array.
            pivotValue = "." * offset + pivotChar
            header = f"start: {start}, end: {end}, pivot: {pivotValue}"
            self.sa.print(header, [start, middleStart, middleEnd, end], " <=> ")

        # Return the new interval containing all elements selected by the pivot char.
        # Note that `middleEnd` should point to the element *after* the last in the interval.
        return (middleStart, middleEnd)


def main():
    sorter: SuffixSorter = MultikeyQuicksort()
    sa: SuffixArray = SuffixArray()

    # Run this for debugging.
    sorter.setDebugging(True)
    sa.setText("ABRACADABRA")
    sa.buildIndex(sorter)
    sa.checkIndex()
    sa.print("ABRACADABRA")

    """
    # Some example performance tests.
    # Wait with these until you're pretty certain that your code works.
    sorter.setDebugging(False);
    alphabet = "ABCD"
    for k in range(1, 6):
        size = k * 200_000
        sa.generateRandomText(size, alphabet)
        sa.buildIndex(sorter)
        sa.checkIndex()
        sa.print(f"size: {size:,d}, alphabet: '{alphabet}'")
    """

    # What happens if you try different alphabet sizes?
    # (E.g., smaller ("AB") or larger ("ABC....XYZ")

    # What happens if you use only "A" as alphabet?
    # (Hint: try much smaller test sizes)


if __name__ == "__main__":
    main()

