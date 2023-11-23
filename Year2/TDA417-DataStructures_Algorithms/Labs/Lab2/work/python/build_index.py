#!/usr/bin/env python3

from pathlib import Path
from typing import Dict, Callable

from suffix_array import SuffixArray
from command_parser import CommandParser
from stopwatch import Stopwatch

from suffix_sorter import SuffixSorter
from insertion_sort import InsertionSort
from quicksort import Quicksort
from multikey_quicksort import MultikeyQuicksort
from builtin_sort import BuiltinSort

from pivot_selector import PivotSelector, TakeFirstPivot, TakeMiddlePivot, RandomPivot, MedianOfThreePivot, AdaptivePivot


suffixSorters: Dict[str, Callable[[], SuffixSorter]] = {
    "insertion": InsertionSort,
    "quicksort": Quicksort,
    "multikey": MultikeyQuicksort,
    "builtin": BuiltinSort,
}

pivotSelectors: Dict[str, PivotSelector] = {
    "first": TakeFirstPivot,
    "middle": TakeMiddlePivot,
    "random": RandomPivot,
    "median": MedianOfThreePivot,
    "adaptive": AdaptivePivot,
}


parser = CommandParser(description="Build an inverted search index.")
parser.add_argument("--textfile", "-f", required=True, type=Path, 
                    help="text file (utf-8 encoded)")
parser.add_argument("--algorithm", "-a", required=True, choices=list(suffixSorters), 
                    help="sorting algorithm")
parser.add_argument("--pivot", "-p", choices=list(pivotSelectors), 
                    help="pivot selectors (only for quicksort algorithms)")

def main():
    options = parser.parse_args()

    # Create stopwatches to time the execution of each phase of the program.
    stopwatchTotal = Stopwatch()
    stopwatch = Stopwatch()

    # Read the text file.
    textfile = options.textfile
    suffixArray = SuffixArray()
    suffixArray.loadText(textfile)
    stopwatch.finished(f"Reading {suffixArray.size()} chars '{textfile}'")

    # Select sorting algorithm.
    sortingAlgorithm = suffixSorters[options.algorithm]
    sorter = sortingAlgorithm()
    if options.pivot:
        setattr(sorter, "pivotSelector", pivotSelectors[options.pivot])

    # Build the index using the selected sorting algorithm.
    suffixArray.buildIndex(sorter)
    stopwatch.finished("Building index")

    # Check that it's sorted.
    suffixArray.checkIndex()
    stopwatch.finished("Checking index")

    # Save it to an index file.
    suffixArray.saveIndex()
    stopwatch.finished(f"Saving index to '{suffixArray.indexFile}'")

    stopwatchTotal.finished("In total the program")


if __name__ == "__main__":
    main()

