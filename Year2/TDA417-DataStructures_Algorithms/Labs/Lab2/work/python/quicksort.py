from typing import Any

from suffix_array import SuffixArray
from suffix_sorter import SuffixSorter
from pivot_selector import TakeFirstPivot
from pivot_selector import MedianOfThreePivot
from progress_bar import ProgressBar


class Quicksort(SuffixSorter):
    sa: SuffixArray
    progressBar: ProgressBar[Any]
    progressBarSpanSize: int

    def __init__(self):
        self.pivotSelector = MedianOfThreePivot

    def sort(self, sa: SuffixArray):
        self.sa = sa
        with ProgressBar(total=sa.size(), description="Quicksorting") as self.progressBar:
            self.progressBarSpanSize = sa.size() // 10_000
            # Don't change this call, the second argument should be `sa.size()`:
            self.quicksort(0, sa.size())
            self.progressBar.setValue(sa.size())

    def quicksort(self, start: int, end: int):
        # Hint when completing the code: 
        # The variable `end` points to the element *after* the last one in the interval!

        size = end - start

        # Base case: the list to sort has at most one element.
        if size <= 1: 
            return

        # Don't update the progress bar unnecessarily often.
        if size >= self.progressBarSpanSize:
            self.progressBar.setValue(start)

        if start >= end:
            return
        
        pivot = self.pivotSelector.pivotIndex(self.sa, start, end)
        print(pivot)


    def partition(self, start: int, end: int) -> int:
        # Hints when completing the code: 
        # - The variable `end` points to the element *after* the last one in the interval!
        # - Use the following local functions for convenience:
        index = self.sa.index
        swap = self.sa.swap
        compareSuffixes = self.sa.compareSuffixes

        # Select the pivot, and find the pivot suffix.
        pivotIndex = self.pivotSelector.pivotIndex(self.sa, start, end)
        pivotSuffix = index[pivotIndex]

        # Swap the pivot so that it is the first element.
        if start != pivotIndex:
            swap(start, pivotIndex)

        # Hoare partition scheme - the pointers move in opposite direction.
        lo = start + 1
        hi = end - 1
        
        # This is the value that will be returned in the end - the final position of the pivot.
        newPivotIndex: int

        #---------- TASK 3b: Quicksort ---------------------------------------#
        # TODO: Replace these lines with your solution!
        raise NotImplementedError()
        #---------- END TASK 3b ----------------------------------------------#

        if self.debug:
            # When debugging, print an excerpt of the suffix array.
            pivotValue = (
                self.sa.text[pivotSuffix : pivotSuffix + 20] + "..."
                if pivotSuffix + 20 <= self.sa.size() else
                self.sa.text[pivotSuffix :]
            )
            header = f"start: {start}, end: {end}, pivot: {pivotValue}"
            self.sa.print(header, [start, newPivotIndex, newPivotIndex+1, end], " <=> ")

        return newPivotIndex


def main():
    sorter: SuffixSorter = Quicksort()
    sa: SuffixArray = SuffixArray()

    # Run this for debugging.
    sorter.setDebugging(True)
    sa.setText("ABRACBDABRC")
    sa.buildIndex(sorter)
    sa.checkIndex()
    sa.print("ABRACBDABRC")

    """
    # Some example performance tests.
    # Wait with these until you're pretty certain that your code works.
    sorter.setDebugging(False);
    alphabet = "ABCD"
    for k in range(1, 6):
        size = k * 100_000
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

