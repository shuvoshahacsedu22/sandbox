import sys

'''
Calculate the number of words in the supplied files, 
assuming the entire file (and therefore possibly an 
entire word may not fit) may not fit in memory.A

Use a fixed size buffer to read in portions of the
file at once, and update the word count at every
word that is processed.

'''

BUFF_SIZE = 256

def isWhitespace(char):
    return char in [' ', '\t', '\n'] 

def count_words(buff, inMidWord):
    num_words = 0
    for i in range(len(buff)):
        if (isWhitespace(buff[i])):
            if inMidWord:
                num_words += 1
            inMidWord = False
        else:
            inMidWord = True
    return (num_words, inMidWord)

def word_count(file):
    num_words = 0
    inMidWord = False

    try:
        input = open(file, 'r')
        buff = input.read(BUFF_SIZE)
        while buff:
            (add_words, inMidWord) = count_words(buff, inMidWord)
            num_words += add_words
            buff = input.read(BUFF_SIZE)
            
    except IOError as e:
        print "Error reading file: %s" % e
    finally:
        input.close()
    
    print "  %s\t%s" % (num_words, file)

def main(args):
    if len(args) > 1:
        print "word count: " 
        for f in args[1:]:
            word_count(f)

if __name__=="__main__":
    main(sys.argv)
