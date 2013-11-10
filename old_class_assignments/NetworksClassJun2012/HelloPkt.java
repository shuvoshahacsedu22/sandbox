import java.io.*;
import java.util.*;
import java.net.*; 
import java.nio.*;


// class for HELLO packets (send and receive)
class HelloPkt {

    private int router_id; // sending router id
    private int link_id;   // link id by which it is sent

    public HelloPkt(int rid, int lid) {
        router_id = rid;
        link_id = lid;
    }

    // accessor functions 

    public int getRID() {
        return router_id;
    }

    public int getLID() {
        return link_id;
    }


    // byte to struct convertor functions

    public byte[] toBytes() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4 * 2).order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putInt(router_id);
        byteBuffer.putInt(link_id);
        byte[] dest = new byte[byteBuffer.position()];
        byteBuffer.position(0);
        byteBuffer.get(dest, 0, dest.length);
        return dest;
    }

    static HelloPkt fromBytes(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        int rid = byteBuffer.getInt();
        int lid = byteBuffer.getInt();
        return new HelloPkt(rid, lid);
    }
}
