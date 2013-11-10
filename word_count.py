import sys

'''
Calculate the number of words in the supplied files, 
assuming the entire file (and therefore possibly an 
entire word) may not fit in memory.

Use a fixed size buffer to read in portions of the
file at once, and update the word count at every
word that is processed.

Example:

Kelsey-Stemmlers-MacBook-Pro:sandbox kelsey$ python wordCount.py ~/tmp.{s,l,h,e}*

word count: 
  33    /Users/kelsey/tmp.small
  198   /Users/kelsey/tmp.large
  594   /Users/kelsey/tmp.huge
  1782  /Users/kelsey/tmp.enorm
'''

BUFF_SIZE = 256

def is_whitespace(char):
    return char in [' ', '\t', '\n'] 

def count_words(buff, in_mid_word):
    num_words = 0
    for i in range(len(buff)):
        if (is_whitespace(buff[i])):
            if in_mid_word:
                num_words += 1
            in_mid_word = False
        else:
            in_mid_word = True
    return (num_words, in_mid_word)

def word_count(file):
    num_words = 0
    in_mid_word = False

    try:
        input = open(file, 'r')
        buff = input.read(BUFF_SIZE)
        while buff:
            (add_words, in_mid_word) = count_words(buff, in_mid_word)
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
