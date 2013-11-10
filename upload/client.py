from globals import HOST, PORT, BUFF_SIZE

import socket
import sys

class Client():

    def upload_file(self, filename):
        """
        This function allows the client to upload
        a file to the server. The file must be
        in the current directory, and the steps are
        as follows:
        1. Get current seek position from the server 
           (incase we are in 'resume' mode, we can't
           simply start at position 0).
        2. Stream the remaining data, while allowing
           the user to interrupt the download at
           anytime time with CTRL-C.
        """

        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        seek_pos = 0 # default
        try:
            sock.connect((HOST, PORT))
            f = open(filename, 'r')

            seek_pos = self.get_seek_position(sock, filename)
            sys.stdout.write("FILE: %s\n" % filename)
            sys.stdout.write("   begin upload at %s\n" % seek_pos)
            f.seek(seek_pos)
            data = f.read(BUFF_SIZE)

            while (data):
                sock.sendall(data)
                seek_pos = int(sock.recv(BUFF_SIZE).strip())
                f.seek(seek_pos)
                data = f.read(BUFF_SIZE)

            f.close()
            sys.stdout.write("   upload completed [%s total]\n" % seek_pos)

        except IOError, e:
            sys.stdout.write("Error reading file '%s': %s\n" % (filename, e))

        except KeyboardInterrupt:
            sys.stdout.write("   upload interrupted at %s\n" % seek_pos)

        finally:
            sock.close()

    def get_seek_position(self, sock, filename):
        """
        Send the filename to the server and get back
        the seek position from which to start sending the
        data.
        """
        sock.sendall(filename)
        seek_pos = sock.recv(BUFF_SIZE).strip()
        return int(seek_pos)

def main():
    client = Client()
    while True:
        filename = raw_input("Enter filename or 'q' to quit: ")
        if ('q' == filename):
            break
        client.upload_file(filename)

if __name__ == "__main__":
    sys.exit(main())
