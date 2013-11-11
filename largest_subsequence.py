import sys

def largest_subsequence(nums):
    max = -sys.maxint - 1
    sum = 0
    for num in nums:
        sum += num
        if (sum > max):
            max = sum
        if (sum < 0):
            sum = 0
    return max

