import java.util.*;
import java.net.*; 
import java.nio.*;


// class for LS PSU packets (send and receive)
class LSPDU {
      private int sender;    // sender of the LS PDU 
      private int router_id; // router id 
      private int link_id;   // link id 
      private int cost;      // cost of the link 
      private int via;       // id of the link through which the LS PDU is sent 

    public LSPDU (int sid, int rid, int lid, int c, int v) {
        sender = sid;
        router_id = rid;
        link_id = lid;
        cost = c;
        via = v;
    }

    // change the sender (used when forwarding on LSPDU pkts)
    public void changeSender(int sid) {
        sender = sid;
    }

    // change the sender (used when forwarding on LSPDU pkts)
    public void changeVia(int v) {
        via = v;
    }

    // accessor functions

    public int getSID() {
        return sender;
    }

    public int getRID() {
        return router_id;
    }

    public int getLID() {
        return link_id;
    }

    public int getCost() {
        return cost;
    }

    public int getVia() {
        return via;
    }

    // byte to struct convertor functions

    public byte[] toBytes() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4*5).order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putInt(sender);
        byteBuffer.putInt(router_id);
        byteBuffer.putInt(link_id);
        byteBuffer.putInt(cost);
        byteBuffer.putInt(via);
        byte[] dest = new byte[byteBuffer.position()];
        byteBuffer.position(0);
        byteBuffer.get(dest, 0, dest.length);
        return dest;
    }

    static LSPDU fromBytes(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        int s = byteBuffer.getInt();
        int rid = byteBuffer.getInt();
        int lid = byteBuffer.getInt();
        int cost = byteBuffer.getInt();
        int via = byteBuffer.getInt();
        return new LSPDU(s, rid, lid, cost, via);
    }

}
