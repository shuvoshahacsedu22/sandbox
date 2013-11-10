import java.io.*;
import java.net.*; 

/*
 * receiver class
 * (documentation at each function)
 *
 */

class receiver {

    // for the arrival.log
    private Logger log;

    // for the output file
    private FileOutputStream out;

    // intializes as 0 with mod 32
    private modnum expected = new modnum();

    // sockets for sending and receiving
    private senderSocket   sendSocket;
    private receiverSocket recvSocket;

    // commandline args
    private String hostname;
    private int portAck;
    private int portData;
    private String filename;

    // create the receiver. process all the CL args
    // and set up the instance variables
    public receiver(String[] args) throws Exception {
        hostname = args[0];
        portAck  = Integer.parseInt(args[1]);
        portData = Integer.parseInt(args[2]);
        filename = args[3];

        sendSocket = new senderSocket(hostname, portAck);
        recvSocket = new receiverSocket(portData);

        out = new FileOutputStream(filename);
        log = new Logger("arrival.log");
    }

    // send and ACK to the emulator with specified seqnum
    public void sendACK(int seqnum) throws Exception {
        packet p = packet.createACK(seqnum);
        sendSocket.send(p);
    }

    // send EOT to the emulator with the expected seqnum then exit. we're done!
    public void sendEOT() throws Exception {
        packet p = packet.createEOT(expected.getVal());
        sendSocket.send(p);

        // cleanup (close connections, sockets), before exiting
        cleanUp();
        System.exit(0);
    }

    // receive a packet from the emulator and process it accordingly
    public void consumePacket() throws Exception {
        packet p = recvSocket.receive();
        int seqnum = p.getSeqNum();

        switch (p.getType()) {

            // ACK case
            case 0: System.err.println("receiver received packet of type ACK");
                    break;

            // regular packet with data
            case 1: if (seqnum == expected.getVal()) {

                        // write the data to the file, record seqnum,
                        // and send ACK and update expected seqnum
                        out.write(p.getData());
                        log.recordSeqNum(seqnum);
                        sendACK(seqnum);
                        expected.increment();
                    }
                    else {
                        // send ACK with previously acknowledged seqnum
                        sendACK(expected.getDecremented());
                    }
                    break;

            // EOT case
            case 2: sendEOT();
                    break;
        }
    }

    // receive a packet via the recvSocket
    public packet receive() throws Exception {
        return recvSocket.receive();
    }

    // close all sockets and files
    public void cleanUp() throws Exception{
        recvSocket.close();
        sendSocket.close();
        out.close();
        log.close();
    }

    // main function: create receiver with CL args
    // and then continually receive and process (consume) packets
    // receiver exits when it receives and sends EOT
    public static void main(String[] args) throws Exception {
        receiver r = new receiver(args);
        while (true) {
            r.consumePacket();
        }
    }
}
