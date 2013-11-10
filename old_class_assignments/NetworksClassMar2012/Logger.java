import java.io.*;

/*
 * The Logger class is used for 
 *          seqnum.log, ack.log and arrival.log
 * 
 * It opens a log file (with the name passed in), and
 * has a function to write the seqnum to the log file
 * (one per line). 
 *
 */
class Logger {

    private PrintWriter out;

    public Logger(String filename) {
        try {
          out = new PrintWriter(filename);
        }
        catch (Exception e){
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void recordSeqNum(int seqnum){
        out.println(Integer.toString(seqnum));
        out.flush();
    }

    // close the file once we're done 
    public void close() {
        try {
            out.close();
        }
        catch (Exception e){
            System.err.println("Error: " + e.getMessage());
        }
    }
}


