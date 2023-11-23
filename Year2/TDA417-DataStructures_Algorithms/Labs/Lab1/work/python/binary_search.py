from abc import abstractmethod
from typing import Protocol, TypeVar, List

class ComparableProtocol(Protocol):
    """Protocol for annotating comparable types."""
    @abstractmethod
    def __lt__(self: 'Comparable', other: 'Comparable', /) -> bool: ...

Comparable = TypeVar('Comparable', bound=ComparableProtocol)


def containsIterative(array: List[Comparable], value: Comparable) -> bool:

    min = 0
    max = len(array)-1

    while min <= max:

        middle = (min + max) // 2

        if array[middle] < value:
            min = middle + 1
        elif array[middle] > value:
            max = middle - 1
        else:
            return True
    return False


def containsRecursive(array: List[Comparable], value: Comparable) -> bool:

    return containsRecursiveHelper(array, value, 0, len(array)-1)

def containsRecursiveHelper(array,value,min,max):

    if max == None:
        max = len(array)

    middle = (min+max) // 2

    if min > max:
        return False

    if array[middle] < value:
        min = middle+1
        return containsRecursiveHelper(array,value,min,max)
    elif array[middle] > value:
        max = middle-1
        return containsRecursiveHelper(array,value,min,max)
    else:
        return True


def firstIndexOf(array: List[Comparable], index: Comparable) -> int:
    min = 0 
    max = len(array)-1
    result = -1

    while min <= max:
        middle = (min+max) // 2

        if array[middle] == index:
            result = middle
            max = middle - 1
        elif array[middle] > index:
            max = middle - 1
        else:
            min = middle + 1
    return result
        

def main():
    integerTestArray = [1, 3, 5, 7, 9]

    assert containsIterative(integerTestArray, 4) == False
    assert containsIterative(integerTestArray, 7) == True
    assert containsRecursive(integerTestArray, 0) == False
    assert containsRecursive(integerTestArray, 9) == True

    stringTestArray = ["cat", "cat", "cat", "dog", "turtle", "turtle"]

    assert firstIndexOf(stringTestArray, "cat") == 0
    assert firstIndexOf(stringTestArray, "dog") == 3
    assert firstIndexOf(stringTestArray, "turtle") == 4
    assert firstIndexOf(stringTestArray, "zebra") == -1


if __name__ == '__main__':
    main()