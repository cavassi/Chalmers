
from functools import cmp_to_key

from suffix_array import SuffixArray
from suffix_sorter import SuffixSorter


class BuiltinSort(SuffixSorter):
    def sort(self, sa: SuffixArray):
        sa.index.sort(
            key = cmp_to_key(sa.compareSuffixes)
        )


