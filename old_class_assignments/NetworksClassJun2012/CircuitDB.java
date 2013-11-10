import java.io.*;
import java.util.*;
import java.net.*; 
import java.nio.*;

class CircuitDB
{ 
    private static final int NBR_ROUTERS = 6; // + 1 so indices are lined up
    private int nbr_link; 
    private LinkCost linkcost[]; 

    // constructor for when we have an array of LinkCost already
    CircuitDB(int nbr_link, LinkCost[] linkcost) {
        this.nbr_link = nbr_link;
        this.linkcost = linkcost;
    }

    // default constructor (for building up the CircuitDB)
    public CircuitDB() { 
        nbr_link = 0;
        linkcost = new LinkCost[NBR_ROUTERS];
    }

    // build up the CircuitDB by adding on at a time
    // (used in router.java)
    public void addLinkCost(LinkCost lc) {
        // avoid duplicates
        if (!contains(lc)){
            nbr_link += 1;
            linkcost[nbr_link] = lc;
        }
    }

    // check if it already contains this link
    public Boolean contains(LinkCost lc) {
        for (int i = 1; i < nbr_link + 1; i ++) {
            if ( (lc.getCost() == linkcost[i].getCost()) &&
                  (lc.getLink() == linkcost[i].getLink()) ) {
               return true;
            }
        }
        return false;
    }

    // accessor functions

    public LinkCost getLinkCost(int i) {
        return linkcost[i];
    }

    public int getLength() {
        return nbr_link; 
    }


    // Get Topology strings for each entry, given a prefix
    // e.g. Given prefix "R1 -> 1", and CircuitDB has 1 link:
    //      R1 -> 1 nbr link 1
    //      R1 -> 1 link 1 cost 1
    public String getTopologyString(String prefix) {
        String ret = "";
        if (nbr_link != 0) {
            ret += prefix + " nbr link " + nbr_link + "\n";
            for (int i = 1; i < nbr_link + 1; i ++) {
                ret += prefix + " link " + linkcost[i].getLink()
                              + " cost " + linkcost[i].getCost() + "\n";
            }
        }
        return ret.trim();
    }

    // get the CircuitDB string to be written to the log in the following format:
    //      nbr_link 2, linkcost[0]:link 1, cost 1, linkcost[1]:link 5, cost 5
    public String getString() {
        String ret = "nbr_link " + nbr_link;
        for (int i = 1; i < nbr_link + 1; i ++) {
            LinkCost tmp = linkcost[i];
            ret += ", linkcost[" + (i-1) + "]:link " + tmp.getLink() 
                   + ", cost " + tmp.getCost();
        }
        return ret;
    }

    // byte to struct convertor functions

    public byte[] toBytes() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4 + 8 * NBR_ROUTERS).order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putInt(nbr_link);
        for (int i = 1; i < linkcost.length; i++) {
            byteBuffer.put(linkcost[i].toBytes());
        }
        byte[] dest = new byte[byteBuffer.position()];
        byteBuffer.position(0);
        byteBuffer.get(dest, 0, dest.length);
        return dest;
    }

    static CircuitDB fromBytes(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        int nbr_link = byteBuffer.getInt();
        LinkCost[] linkcost = new LinkCost[NBR_ROUTERS];
        for (int i = 1; i < linkcost.length; i++) {
            byte[] linkCostBytes = new byte[8];
            byteBuffer.get(linkCostBytes);
            linkcost[i] = LinkCost.fromBytes(linkCostBytes);
        }
        return new CircuitDB(nbr_link, linkcost);
    }
}
