import sys

''' 
String compression problem:

Compress a string, but only return its
compressed from if it is shorter than
the original string.

Examples:
input: aaaabbbbbbbcccdde
output: a4b7c3d2e

input: abcdd
output: abcdd
'''

def compress_char_string(char, count):
    if count == 1:
        return char
    else:
        return "%s%d" % (char, count)

def compress_string(word):
    if not word: return null 

    # init the first vals
    compressed = ''
    char = word[0]
    count = 1

    for c in word[1:]:
        if (c == char):
            count += 1
        else:
            # encountered the next char, so
            # accumulate the rest of the string
            compressed += compress_char_string(char, count)
            char = c
            count = 1
    compressed += compress_char_string(char, count)

    # once we're done processing, decide
    # which to return
    if (len(compressed) < len(word)):
        return compressed
    return word

def main(args):
    print compress_string(args)

if __name__ == "__main__":
    sys.exit(main(sys.argv[1]))


