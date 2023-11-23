
import random
from abc import abstractmethod
from typing import Protocol

from suffix_array import SuffixArray


class PivotSelector(Protocol):
    @staticmethod
    @abstractmethod
    def pivotIndex(sa: SuffixArray, start: int, end: int) -> int:
        ...

class TakeFirstPivot(PivotSelector):
    @staticmethod
    def pivotIndex(sa: SuffixArray, start: int, end: int) -> int:
        return start


class TakeMiddlePivot(PivotSelector):
    @staticmethod
    def pivotIndex(sa: SuffixArray, start: int, end: int) -> int:
        return (start + end) // 2


class RandomPivot(PivotSelector):
    @staticmethod
    def pivotIndex(sa: SuffixArray, start: int, end: int) -> int:
        return random.randrange(start, end)


def medianOfThreeIndex(sa: SuffixArray, i: int, j: int, k: int) -> int:
    pos_i, pos_j, pos_k = sa.index[i], sa.index[j], sa.index[k]

    # Out of the below three bools, two must be equal. (Why?)
    less_or_equal_ij = sa.compareSuffixes(pos_i, pos_j) <= 0
    less_or_equal_jk = sa.compareSuffixes(pos_j, pos_k) <= 0
    less_or_equal_ki = sa.compareSuffixes(pos_k, pos_i) <= 0

    if less_or_equal_ij == less_or_equal_jk:
        # The sequence [i], [j], [k] is ascending or descending.
        return j
    elif less_or_equal_jk == less_or_equal_ki:
        # The sequence [j], [k], [i] is ascending or descending.
        return k
    else: # less_or_equal_ki == less_or_equal_ij
        # The sequence [k], [i], [j] is ascending or descending.
        return i


class MedianOfThreePivot(PivotSelector):
    @staticmethod
    def pivotIndex(sa: SuffixArray, start: int, end: int) -> int:
        mid = (start + end - 1) // 2
        return medianOfThreeIndex(sa, start, mid, end - 1)


class AdaptivePivot(PivotSelector):
    @staticmethod
    def pivotIndex(sa: SuffixArray, start: int, end: int) -> int:
        size = end - start

        # For small arrays, just pick first element.
        if size < 10:
            return start
        
        # For medium arrays, pick median-of-three.
        if size < 100:
            return MedianOfThreePivot.pivotIndex(sa, start, end)
        
        # For large arrays, pick median-of-three of median-of-three.
        lo = start
        hi = end - 1
        mid = (lo + hi) // 2

        d = size // 8
        i = medianOfThreeIndex(sa, lo, lo + d, lo + 2 * d)
        j = medianOfThreeIndex(sa, hi, hi - d, hi - 2 * d)
        k = medianOfThreeIndex(sa, mid - d, mid, mid + d)
        return medianOfThreeIndex(sa, i, j, k)

