from globals import HOST, PORT, BUFF_SIZE

from collections import defaultdict
import SocketServer
import sys

files = defaultdict(int)

class Server(SocketServer.BaseRequestHandler):

    def handle(self):
        """
        Handles uploading (or resuming uploading) 
        one file from a client connection.
        1. Obtain the filename from the client and 
           return the position to which the server
           has already successfully uploaded.
        2. Continue to receive data from the client
           while updating the current position, and 
           returning it to the client each time so
           the client knows where to begin the next
           chunk of data. 
        NOTES: the server writes the file to the 
        current directory, while appending '.server'
        to the filename passed in by the client.
        """

        try:
            filename = "%s.server" % self.request.recv(BUFF_SIZE).strip()
            f = open(filename, 'a+')

            pos = self.get_seek_position(filename)
            f.seek(pos)
            self.request.sendall(str(pos))
            data = self.request.recv(BUFF_SIZE)

            while (data):
                f.write(data)
                f.flush()

                pos = f.tell()
                files[filename] = pos
                self.request.sendall(str(pos))

                data = self.request.recv(BUFF_SIZE)

            f.close()
            sys.stdout.write("Done uploading file '%s' [%s total]\n" % (filename, pos))

        except IOError, e:
            sys.stdout.write("Error writing to file '%s': %s\n" % (filename, e))

    def get_seek_position(self, filename):
        """ 
        Given a filename from a client, look up where 
        the server should start/resume uploading from.
        This information is kept in a dictionary which
        maps filesnames to the position where the
        server has successfully uploaded.
        """
        if filename not in files:
            pos = 0
            files[filename] = pos
        else:
            pos = files[filename]
        return pos


def main():
    server = SocketServer.TCPServer((HOST, PORT), Server)
    server.serve_forever()

if __name__ == "__main__":
    sys.exit(main())

