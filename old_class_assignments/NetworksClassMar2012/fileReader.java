import java.io.*;

/*
 *  This class is used to read the input file
 *  500 bytes at a time, and to check if there
 *  is any remaining data to be read. 
 *
 *  Note: When reading data, I want to get 500
 *  bytes each time. However, if there isn't 
 *  500 bytes left to be read, I set the byte
 *  array size to the numbers of bytes remaining.
 *
 */

class fileReader {

    private FileInputStream fin;
    private byte fileContent[];

    public fileReader(String filename) throws Exception {
        fin = new FileInputStream(new File(filename));
    }

    public boolean hasMoreData() throws Exception {
        return (fin.available() != 0);
    }

    public String getNext() throws Exception {
        int remaining = fin.available();
        if (remaining >= 500) {
            fileContent = new byte[500];
        } else {
            fileContent = new byte[remaining];
        }
        fin.read(fileContent);
        //create string from byte array
        return (new String(fileContent));
    }

    // close the file once we're done reading it
    public void close() throws Exception {
        fin.close();
    }
}
