import java.io.*;
import java.util.*;
import java.net.*; 
import java.nio.*;

class LinkCost
{ 
    private int link;   /* link id */
    private int cost; /* associated cost */

    public LinkCost(int link, int cost) {
        this.link = link;    
        this.cost = cost;    
    }

    // accessor functions

    public int getLink(){
        return link;
    }

    public int getCost(){
        return cost;
    }

    // byte to struct convertor functions

    public byte[] toBytes() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4 * 2).order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putInt(link);
        byteBuffer.putInt(cost);
        byte[] dest = new byte[byteBuffer.position()];
        byteBuffer.position(0);
        byteBuffer.get(dest, 0, dest.length);
        return dest;
    }

    static LinkCost fromBytes(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        int link = byteBuffer.getInt();
        int cost = byteBuffer.getInt();
        return new LinkCost(link, cost);
    }
}
