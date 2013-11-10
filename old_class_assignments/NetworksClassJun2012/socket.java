import java.net.*; 
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/*
 * socket class
 *
 * This class is used to send and receive
 * packets. The router has a socket member variable
 *
 */

class socket {

    private static DatagramSocket socket;
    private static DatagramPacket sendPacket;
    private static DatagramPacket recvPacket;

    private static InetAddress IPAddress;
    private static int port;

    // create a socket, initialize all instance variables
    public socket(String hostname, int port, int local_port) throws Exception {
        IPAddress = InetAddress.getByName(hostname);
        this.port = port;
        socket = new DatagramSocket(local_port);
    }

    // send an INIT packet to Dest
    public static void sendInit(int i) throws Exception {
		ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.putInt(i);
        sendPacket = new DatagramPacket(buffer.array(), buffer.array().length, 
                                        IPAddress, port);
        socket.send(sendPacket);
    }

    // receive circuitDB packet packet
    public static CircuitDB receiveCircuitDB() throws Exception {
        // set to 200 to take care of larger cases 
        // (could make smaller, but still effective)
        byte[] recvData = new byte[200];
        recvPacket = new DatagramPacket(recvData, recvData.length);
        socket.receive(recvPacket);  
        return CircuitDB.fromBytes(recvPacket.getData());
    }

    // send a HELLO pkt to a neighbour through the specified link
    public static void sendHello(int rid, int lid) throws Exception {
        byte[] bytes = (new HelloPkt(rid, lid)).toBytes();
        sendPacket = new DatagramPacket(bytes, bytes.length, IPAddress, port);
        socket.send(sendPacket);
    }

    // send a LSPDU pkt over the socket
    public static void sendLSPDU(LSPDU lspdu) throws Exception {
        byte[] bytes = lspdu.toBytes();
        sendPacket = new DatagramPacket(bytes, bytes.length, IPAddress, port);
        socket.send(sendPacket);
    }

    // general receive pkt method for receiving both HELLO and LSPDU
    // router method receivePacket determines which it is
    public static byte[] receivePacket() throws Exception {
        byte[] recvData = new byte[4*5];
        recvPacket = new DatagramPacket(recvData, recvData.length);
        socket.receive(recvPacket);  
        return recvPacket.getData();
    }

    // close the socket
    public static void close() {
        socket.close();
    }
}


