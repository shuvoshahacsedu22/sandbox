import java.io.*;

/*
 * The logger class is used for routerX.log
 * 
 * It opens a log file (with the name passed in), and
 * has a function to write a message to the log file
 * (one per line). 
 *
 */
class logger {

    private PrintWriter out;

    public logger(String filename) {
        try {
          out = new PrintWriter(filename);
        }
        catch (Exception e){
            System.err.println("Error: " + e.getMessage());
        }
    }

    // write message to log file 
    public void write(String m){
        if (!m.equals("")) {
            out.println(m);
            out.flush();
        }
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


