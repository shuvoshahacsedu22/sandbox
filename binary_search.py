import math

def findX(array, x):
    """
    Given a sorted array, find the index
    of x if it exists, -1 otherwise. This
    can be done via binary search.
    """
    left = 0
    right = len(array)

    while (left < right):
        mid = int(math.floor((right + left)/2))
        if (x == array[mid]):
            return mid
        if (x < array[mid]): 
            right = mid
        else:
            left = mid + 1
    return -1

def run_tests():
    # some simple tests 
    assert(findX([],1) == -1)
    assert(findX([1],1) == 0)
    assert(findX([2],1) == -1)
    assert(findX([1,2],1) == 0)
    assert(findX([1,2],2) == 1)
    assert(findX([1,2],4) == -1) 
    assert(findX([1,2,3,4,6,7,8,10],9) == -1)
    assert(findX([1,13,2,3,4,6,7,8,10,13],13) == 9)

