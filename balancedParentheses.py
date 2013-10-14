'''
Solving the balanced parentheses problem.

Given a string, determine if it contains
balanced parentheses.

There are two versions here:
1. Simple case (only one set of parentheses).
2. Complex case (three sets of parentheses).

'''

LEFT_PARENS  = ['(','[','{']
RIGHT_PARENS = [')',']','}']

def isBalancedSimple(content): 
    """Check if the content string has 
    balanced parentheses: ( , )
    This can be done simply by using a counter
    """
    counter = 0
    for c in content:
        # ) was encountered before (
        if counter < 0:
            return False
        if c == '(':
            counter += 1
        elif c == ')':
            counter -= 1
    return (counter == 0)


def isBalanced(content):
    """Check if the content string has
    balanced parentheses: [ (), {}, [] ]

    Use a stack to push the left paren
    and pop the corresponding right
    paren. Fail if it does not match
    or if the stack is not empty upon
    completing content iteration.
    """
    # use list as a stack
    stack = [] 

    for c in content:
       if c in LEFT_PARENS:
           stack.append(c)
       elif c in RIGHT_PARENS:
           left_paren = stack.pop()
           # must match the left paren
           if c != RIGHT_PARENS[(LEFT_PARENS.index(left_paren))]:
               return False

    return len(stack) == 0

