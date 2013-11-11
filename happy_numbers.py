import math

"""
Definition:
A happy number is defined by the following process: 
Starting with any positive integer, replace the 
number by the sum of the squares of its digits, and 
repeat the process until the number equals 1 (where 
it will stay), or it loops endlessly in a cycle 
which does not include 1. Those numbers for which 
this process ends in 1 are happy numbers, while 
those that do not end in 1 are unhappy numbers.

Source: http://en.wikipedia.org/wiki/Happy_number
"""

def sum_squares(num):
    """
    Calculate the sum of the squares of the digits
    of a number.
    """
    sum = 0
    while (num != 0):
        sum += math.pow((num % 10), 2)
        num = num/10
    return int(sum)

def is_happy(num, visited):
    """
    Calculate the current sum of the squares.
    Return True if the sum of the squares is 1.
    Return False if the sum of the squares has
    been seen before (i.e. a cycle), by looking 
    it up in the visited dict.
    Otherwise, recurse on the current value.
    """
    
    sum = sum_squares(num)
    if (sum == 1):
        return True

    if sum in visited:
        return False
    else:
        visited[sum] = True
        return is_happy(sum, visited)

def is_happy_number(num):
    return is_happy(num, {})

