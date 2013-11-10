'''
Return the max of 2 numbers WITHOUT using
any if statement or comparison operators

Written: October 2013
'''

def get_max(i, j):
    diff = abs(j - i )
    return (i + j + diff) / 2

