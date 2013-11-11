    
def is_apple(grid):
    """
    Determine if pixels in the grid represent
    an apple (alternative is a banana).
    1. Calculate the percentage filled of the
       section representing the image.
    2. Calculate the ratio of the height:width
       of the section representing the image.
    Returns:
      * True if ratio ~= 1 +/- 0.2 and
                percentage filled > 80%
      * False otherwise

    NOTE: the ratio and fill percent were
    chosen arbitrarily.

    Preconditions: there must be at least 1
    cell that represents the image (i.e. cannot
    be a grid full of 0s).
    """
    (fill_percent, ratio) = get_metrics(grid)
    if (abs(ratio - 1)  < 0.2) and (fill_percent > .80):
        return True
    return False

def get_metrics(grid):
    """
    Process the entire grid and find the left-, right-,
    top- and bottom-most indices that represent the
    image. With those values, calculate the ratio 
    (heigh:width), and the percent of the image filled.
    """
    # TODO: lots of variables and if statements
    #       in this method. Clean it up.
    fill_count = 0
    size = len(grid)
    left = size
    right = 0
    top = size
    bottom = 0

    for i in range(size):
        for j in range(size):
            cell = grid[i][j]
            if cell:
                fill_count += 1
                if i < left:
                    left = i
                if i + 1 > right:
                    right = i + 1
                if j + 1 > bottom:
                    bottom = j + 1
                if j < top:
                    top = j
    ratio = (bottom - top) / (right - left)
    fill_percent = float(fill_count / ((bottom-top)*(right-left)))
    return (fill_percent, ratio)

def run_tests():
    # TODO: add some larger test cases
    assert(is_apple([[1]]) == True)
    assert(is_apple([[1,0],[0,0]]) == True)
    assert(is_apple([[1,1],[1,1]]) == True)
    assert(is_apple([[1,0],[0,1]]) == False)
    assert(is_apple([[1,1],[0,0]]) == False)

