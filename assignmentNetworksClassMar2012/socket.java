import java.net.*; 

/*
 * senderSocket and receiverSocket classes
 *
 * NOTE: I could have used inheritance for these
 * classes, but they work well as is. One is 
 * used for sending only, and the other is for 
 * receiving only, so I think they're better as 
 * separate classes.
 */

class senderSocket {

    private static DatagramSocket sendSocket;
    private static DatagramPacket sendPacket;
    private static byte[] sendData = new byte[512];  
    private static InetAddress IPAddress;
    private static int port;

    // create a senderSocket, initialize all instance variables
    public senderSocket(String hostname, int port) throws Exception {
        IPAddress = InetAddress.getByName(hostname);
        this.port = port;
        sendSocket = new DatagramSocket();
    }

    // send packet to Dest
    public static void send(packet p) throws Exception {
        sendData = p.getUDPdata();
        sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
        sendSocket.send(sendPacket);
    }

    // close the socket
    public static void close() {
        sendSocket.close();
    }
}

class receiverSocket {

    private static DatagramSocket recvSocket;
    private static DatagramPacket recvPacket;
    private static byte[] recvData = new byte[512];
    private static int port;

    // create recevierSocket with all instance variables initialized
    public receiverSocket(int port) throws Exception {
        this.port = port;
        recvSocket = new DatagramSocket(port);
    }

    // receive packet
    public static packet receive() throws Exception {
        recvPacket = new DatagramPacket(recvData, recvData.length);
        recvSocket.receive(recvPacket);  
        packet p = packet.parseUDPdata(recvPacket.getData());
        return p;
    }

    // close the socket
    public static void close() {
        recvSocket.close();
    }

}
