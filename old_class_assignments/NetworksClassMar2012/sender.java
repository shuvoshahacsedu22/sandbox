import java.io.*;
import java.util.*;
import java.net.*; 

/*
 * sender class 
 *
 * The sender class has a main function
 * that continually sends packets with the
 * data read in from the input file
 * The seqnum is updated, and packets
 * are only sent if there is room in the window
 * 
 * There is an ACKListener thread that runs,
 * continually receiving and processing packets
 *
 * There is a Timer that calls the timeout()
 * function every DELAY (see below) ms
 * to re-trasnmit the packages that haven't
 * yet been acknowledged
 *
 * Since there are 3 threads running at once, 
 * I've made the functions send, receivePacket
 * and timeout, all synchronized.
 *
 */


class sender {

    private Logger seqlog; // for the seqnum.log
    private Logger acklog; // for the ack.log
    private fileReader in; // for the input file

    // listener for the ACKs
    private ACKListener listener;

    // timer for the timeouts
    private Timer timer;

    // see README for explanation of this value
    private int DELAY = 150; 

	private final int SeqNumModulo = 32;
    private final int window = 10;

    private modnum base       = new modnum();
    private modnum nextseqnum = new modnum();

    // used to keep track of when I have 
    // finished reading all the input from the file
    volatile Boolean done = false;

    // array that stores the data packets as 
    // they are sent. Once acknowledged, they 
    // are overwritten when a new packet has
    // the same seqnum. (e.g. packet 33 is 
    //  1 Mod 32. so sendPacket[1] will be
    // replace by the data from packet 33,
    // and so on
    private packet[] sendPacket;

    private senderSocket   sendSocket;
    private receiverSocket recvSocket;

    // commandline args
    private String hostname;
    private int portData;
    private int portAck;
    private String filename;

    // create the sender. process all the CL args
    // and set up the instance variables
    public sender(String[] args) throws Exception {
        sendPacket = new packet[SeqNumModulo];
        hostname = args[0];
        portData = Integer.parseInt(args[1]);
        portAck  = Integer.parseInt(args[2]);
        filename = args[3];

        seqlog = new Logger("seqnum.log");
        acklog = new Logger("ack.log");
        in     = new fileReader(filename);

        sendSocket = new senderSocket(hostname, portData);
        recvSocket = new receiverSocket(portAck);
        listener = new ACKListener();

        listener.start();
    }

    // Clean up all resources, once there are no more packets to be 
    // sent or received (close sockets and files)
    public void cleanUp() {
        recvSocket.close();
        sendSocket.close();
        seqlog.close();
        acklog.close();
    }

    /* TIMER FUNCTIONS BEGIN */

    public void startTimer() {
        timer = new Timer();
        timer.schedule(new timeoutTask(), DELAY);
    }

    public void restartTimer() {
        stopTimer();
        startTimer();
    }

    public void stopTimer() {
        timer.cancel();
    }

    /* TIMER FUNCTIONS END */

    // send EOT with nextseqnum as the seqnum
    public void sendEOT() throws Exception {
        try {
            packet p = packet.createEOT(nextseqnum.getVal());
            sendSocket.send(p);
        } 
        catch (Exception e) {
            System.err.println("Error: couldn't send EOT" + e.getMessage());
        }

    }

    // this function returns true if the data could be sent 
    // false, otherwise
    // that way the caller of this function knows when to recall it
    public synchronized Boolean send(String data) {
        try {

            // check if there's room in the window
            // and send the packet if so (update nextseqnum,
            // save data in sendPacket array, record seqnum)
            if (nextseqnum.inInterval(base,
                                      new modnum(base.getVal() + window))) {
                int nextVal = nextseqnum.getVal();
                seqlog.recordSeqNum(nextVal);
                sendPacket[nextVal] = packet.createPacket(nextVal, data);
                sendSocket.send(sendPacket[nextVal]);
                if (base.getVal() == nextVal) {
                    startTimer();
                }
                nextseqnum.increment();
                return true;
            } 
            // no room in the window. return false to be 
            // tried again later
            else 
            {
                return false;
            }
        }
        catch (Exception e) {
            System.err.println("Error: couldn't send packet" + e.getMessage());
            return false;
        }
    }

    // timeout called from Timer thread. 
    // re-transmit all unacknowledged packages
    public synchronized void timeout () throws Exception {

        modnum i = new modnum(base.getVal());
        while ( i.inInterval(base,
                             nextseqnum)) {
            sendSocket.send(sendPacket[i.getVal()]);
            seqlog.recordSeqNum(i.getVal());
            i.increment();
        }
        restartTimer();
    }

    // receive a packet by appropriately processing it
    public synchronized void receivePacket(packet p) throws Exception{
        int seqnum = p.getSeqNum();
        switch (p.getType()) {

            // receive ACK 
            case 0: acklog.recordSeqNum(seqnum);
                    base.setVal(seqnum + 1);
                    if (base.getVal() == nextseqnum.getVal()) {

                        // if we get here, we're done sending data! 
                        if (done) {
                            stopTimer();
                            sendEOT(); 
                        } 
                        else  {
                            stopTimer();
                        }
                    } else {
                        startTimer();
                    }
                    break;

            // receive regular packet
            case 1: System.err.println("Error: sender received regular packet");
                    break;

            // receive EOT
            case 2: cleanUp();  // close sockets, files before exiting
                    System.exit(0);
                    break;
        }
    }

    // main function for sender 
    // while input file has more data, send it. If send returns
    // false => no room in window. Sleep (wait) and try again
    public void transmitFile() throws Exception {
        String data = "";

        // check for the case where these is no data in the file
        // (see README for more details)
        if (!in.hasMoreData()){
            while(!send("")){
                // allow some time before we try to send again
                Thread.sleep(DELAY); 
            }
        }

        while (in.hasMoreData()) {
            data = in.getNext();
            while (!send(data)) {
                // allow some time before we try to send again
                Thread.sleep(DELAY); 
            }
        }
        // set bool so that we can send EOT upon ACK of last 
        // sent package... and close input file
        done = true;
        in.close(); 
    }

    // main sender method. 
    // create sender, transmit file.
    public static void main(String[] args) throws Exception {
        sender s = new sender(args);
        s.transmitFile();
        //s.startTimer();
    }

    // ACKListener class to listen for, receive and process packages
    class ACKListener extends Thread {

        // continuously receive and process packets
        // once we have finished sending the input file (bool done = true)
        // and we have received the EOT, we will exit from this loop
        // (and the program)
        public void run () {
            try {
                while (true) {
                    packet p = recvSocket.receive();
                    receivePacket(p);
                }
            }
            catch (Exception e) {
                System.err.println("ListenerError: " + e.getMessage());
            }
        }
    }

    // timeoutTask class to call timeout() function on Timer timeout
    class timeoutTask extends TimerTask {

        public void run () {
            try {
                timeout();
            }
            catch (Exception e) {
                System.err.println("TimerError: " + e.getMessage());
            }
        }

    }



}
