
from abc import abstractmethod

from progress_bar import ProgressBar

# We have to do like this to avoid circular module imports
from typing import TYPE_CHECKING
if TYPE_CHECKING:
    from suffix_array import SuffixArray
    from pivot_selector import PivotSelector


class SuffixSorter:
    """Abstract class for Suffix sorting algorithms."""

    @abstractmethod
    def sort(self, sa: 'SuffixArray'): ...

    pivotSelector: 'PivotSelector'
    def setPivotSelector(self, pivotSelector: 'PivotSelector'):
        self.pivotSelector = pivotSelector

    debug: bool = False
    def setDebugging(self, debug: bool):
        self.debug = debug
        ProgressBar.visible = not debug

