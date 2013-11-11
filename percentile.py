def find_x_percentile_sort(nums):
    """
    Find the 90th percentile of a list of numbers,
    by sorting them and then returning the
    appropriate value.
    """
    nums = sorted(nums)
    percentile = int(round(len(nums)*0.9) )
    return nums[percentile-1]

def find_x_percentile(nums):
    """
    Same as above, but instead of the slow sort, 
    buckets are created for each measure and
    count the number occurrences of each. 
    A final pass is done, to find the 90th
    percentile: return the index of the
    bucket that contains the 90th percentile
    count.
    """
    array = [0] * 1000
    for num in nums:
        array[num] += 1

    percentile = round(len(nums)*(0.9))
    count = 0
    for (index, val) in enumerate(array):
        count += val
        if (count >= percentile):
            return index

def test_percentile(nums):
    assert(find_x_percentile(nums) == find_x_percentile_sort(nums))

def run_tests():
    test_percentile([1,2,3,4,5,6,7,8,9,10])
    test_percentile([1,2,3,4,5,6,7,8,9,10,11])
    test_percentile([10,10,32,41,65,26,17,8,9,10,11])


