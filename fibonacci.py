'''
Fibonacci functions with recursion and 
dynamic programming.
'''

def fibonacci(i):
    '''
    Calculates the ith fibonacci number.
    Runs in O(2^n)
    '''
    if (i == 0): return 0
    if (i == 1): return 1
    return fibonacci(i-1) + fibonacci(i-2)


fibs = []
def fibonacciDP(i):
    '''
    Calculates the ith fibonacci number,
    using dynamic programming.
    Runs in O(N)
    '''
    if (i == 0): 
        fibs.insert(i, 0)
        return 0
    if (i == 1):
        fibs.insert(i, 1)
        return 1
    # return cached result if it exists
    if (len(fibs) >= i): return fibs[i]

    # set ith fibonacci number
    fibs.insert(i, fibonacciDP(i-1) + fibonacciDP(i-2))
    return fibs[i]
