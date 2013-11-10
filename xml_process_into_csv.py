import os
import sys
import xml.etree.cElementTree as et

'''
Read and process each XML file in specified directory,
while accumulating the necessary information to aggregate
into one large file. 
'''

def main(args):
    out = open(args[0], 'w')
    path = args[1]

    for file in os.listdir(path):
        # ignore hidden files
        if file.startswith('.'):
            continue

        fin = open(os.path.join(path,file), 'r')
        content = fin.read()
        tree=et.fromstring(content)

        (game, plays, players, oot_games) = tree.getchildren()
        game_id = game.items()[0][1]

        for play in plays.getchildren():
            play_id = play.items()[0][1]
            line = play.text.split('~')
            line[8] = "'%s'" % line[8]
            out.write("%s,%s\n" % (game_id,','.join(line)))

        fin.close()
    out.close()

if __name__ == "__main__":
    sys.exit(main(sys.args[1:]))
