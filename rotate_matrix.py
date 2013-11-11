
###### ROTATE MATRIX (Creating a new matrix) #######

def rotate_matrix(matrix):
    """
    Rotate an nxn matrix 90 degrees
    E.g.
        1 2     becomes     3 1
        3 4                 4 2

    Any cell i,j becomes j,n-i-1

    This version creates a copy of the array.
    """
    size = len(matrix)
    rotated = create_empty(size)
    for i in range(size):
        for j in range(size):
            rotated[j][size-i-1] = matrix[i][j]
    return rotated

def create_empty(size):
    matrix = []
    for i in range(size):
        matrix.append([0]*size)
    return matrix

###### ROTATE MATRIX (Swapping is done in place) #######

def rotate_matrix_in_place(matrix):
    """    
    Same as rotate_matrix, but do the swapping
    in place.
    """    
    width = len(matrix)
    count = 0
    while (width > 1):
        rotate_border(matrix, count, width)
        count += 1
        width = width - 1
    return matrix

def rotate_border(matrix, i, width):
    """
    Rotate the border beginning at
    position (i,i) with size width*width.
    """
    for j in range(i, width-1):
        rotate_at_pos(matrix, i, j)
            
def rotate_at_pos(matrix, i, j):
    """
    Execute a 4-way swap of the cells
    beginning at position (i,j).
    """
    size = len(matrix)
    move = matrix[i][j] 
    for x in range(4):
        next_i = j
        next_j = size - i - 1
        tmp = matrix[next_i][next_j]
        matrix[next_i][next_j] = move 
        move = tmp
        (i,j) = (next_i,next_j)
    return matrix


################ TESTS #########################


def run_test(matrix):
    print (rotate_matrix(matrix) == rotate_matrix_in_place(matrix))

def run_tests():
    # all should print True
    run_test([[1,2],[3,4]])
    run_test([[1,2,3],[4,5,6],[7,8,9]])
    run_test([[1,2,3,4],[5,6,7,8],[9,10,11,12],[13,14,15,16]])
    run_test([[1,2,3,4,5],[6,7,8,9,10],[11,12,13,14,15],[16,17,18,19,20],[21,22,23,24,25]])
    run_test([[1,2,3,4,5,6],[7,8,9,10,11,12],[13,14,15,16,17,18],[19,20,21,22,23,24],[25,26,27,28,29,30],[31,32,33,34,35,36]])

run_tests()

