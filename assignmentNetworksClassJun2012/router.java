import java.io.*;
import java.util.*;
import java.net.*; 
import java.nio.*;


/*
 * router class 
 *
 * The router class initializes itself with all the required data. 
 * Then it performs the following steps:
 * - send an INIT msg to the nse
 * - receive a CircuitDB from nse and update our set of neighbours
 * - send a HELLO to each of these neighbours (from above action)
 * - loop forever: 
 *      - receive a pkt on the socket, and determine it's type:
 *          - HELLO: update the Topology and RIB to show that we have a path
 *                   to our neighbour with this link and cost
 *                   also update the ACKs information so that we 
 *                   know this neighbour can receive future LSPDUs
 *          - LSPDU: update our Topology with this information, and then
 *                   run Dijkstra's alg'm (in method updateRIB) to recalculate
 *                   the minimum costs. Once we've updated all the information
 *                   correctly, send the LSPDU to all the neighbours that have
 *                   ACK'd, and not the one that sent us this LSPDU.
 *
 *
 * The last loop runs forever, as indicated in the discussion board for this course.
 * I have created cleanUp methods to clean up all the resources, but since
 * there is no termination, I cannot actually call this method. Thus sockets
 * are not freed every execution. Manual clean up is necessary.
 * (Note: Java does not allow you to put any code in an unreachable area. I 
 *  tried to have the CleanUp method in the main function, but it does not 
 *  allow that)
 *
 */

class router {

    private static final int NBR_ROUTERS = 6; // + 1 so indices are lined up

    public logger log; // for the routerX.log
    private socket socket; // for sending/receiving pkts

    // NOTE: could have used ACKED to count the number of neighbours
    // who have acked, but then i'd have to iterate through the array
    // every time, which is costly, vs. keeping a number of the current ACKS
    private int num_acks; // keep track of NUMBER OF neighbours who have acked
    private Boolean[] ACKED; // keep track of WHICH neighbours have acked
    private CircuitDB db; // keep track of neighbours

    private int[][] C; // keep track of the costs between connected routers
    private int[][] L; // keep track of links between neighbours
    private CircuitDB[] T; // Topology of links
    private LinkCost[] RIB; // cost of path from src to dest
    private static final int INFINITY = 1000; // large enough so we don't exceed it

    // commandline args
    private int id;
    private String nse_host;
    private int nse_port;
    private int router_port;

    // create the router. process all the CL args
    // and set up the instance variables
    public router(String[] args) throws Exception {
        id          = Integer.parseInt(args[0]);
        nse_host    = args[1];
        nse_port    = Integer.parseInt(args[2]);
        router_port = Integer.parseInt(args[3]);

        num_acks = 0;

        //initially fill with false because no one has ACKED yet
        ACKED = new Boolean[8];
        for (int i = 0; i < ACKED.length; i ++) {
            ACKED[i] = false;
        }

        // create log file and router 
        log = new logger("router" + id + ".log");
        socket = new socket(nse_host, nse_port, router_port);

        // initialize the cost matrix
        // everything is INFINITY, except for our own router
        C = new int[6][6];
        for (int i = 1; i < C.length; i ++) {
            for (int j = 1; j < C[i].length; j ++) {
                if (i == j ) {
                    C[i][j] = 0;
                } else {
                    C[i][j] = INFINITY;
                }
            }
        }

        // initialize all the link information to -1 (empty/ no knowledge yet)
        // L[link#]= [-1, -1]
        // when we obtain information that router 3 is connected to link 7 this becomes
        // L[3] = [3,-1]
        // and finally, once we obtained the other partner (router 7) we get:
        // L[3] = [3, 7]
        // these are updated in the method addToL (which also updates the cost matrix)
        L = new int[8][2];
        for (int i =1; i < L.length; i ++) {
            L[i][0] = -1;
            L[i][1] = -1;
        }


        // initialize Topology to array of 'empty' CircuitDB
        // we will gradually add the information as we discover it
        T = new CircuitDB[6];
        for (int i = 1; i < T.length; i ++) {
            T[i] = new CircuitDB();
        }

        // initialize RIB to infinity for all except our own id
        RIB = new LinkCost[6];
        for (int i = 0; i < RIB.length; i ++) {
            if (i == id) {
                // set the local path cost to 0
                RIB[i] = new LinkCost(id, 0);
            }
            else {
                // filler because we can't actually set to infinity
                RIB[i] = new LinkCost(-1, INFINITY); 
            }
        }
    }

    // Clean up all resources, once there are no more packets to be 
    // sent or received (close sockets and files)
    public void cleanUp() {
        socket.close();
        log.close();
    }

    public String getID() {
        return "R" + id;
    }

    // get the string for another router's id (for logging)
    public String getRID(int id) {
        return "R" + id;
    }

    /* COST MATRIX FUNCTIONS BEGIN */

    public int getC(int i, int j) {
        return C[i][j];
    }

    // update the cost matrix
    public void updateC(int i, int j, int cost) {
        C[i][j] = cost;
        C[j][i] = cost;
    }

    /* COST MATRIX FUNCTIONS END */

    /* LINK MATRIX FUNCTIONS BEGIN */

    // update the link matrix of known neighbours.
    // returns false if the rid already exists for this 
    // link. otherwise, updates L and possibly M and 
    // returns true.
    public Boolean addToL(int lid, int rid, int cost) {
        int[] L2 = L[lid];
        // if the rid already exists for this link, return false
        // because we've already processed it
        if ((L2[0] == rid) || (L2[1] == rid)) { return false;}

        // if it's not in either, add it to the first if it's
        // empty, and return true.
        // or add it to the second, update the cost matrix 
        // (since we've completed the link), and return true.
        if (L2[0] == -1)  { 
            L2[0] = rid;
            return true;
        }
        L2[1] = rid;
        updateC(L2[0], rid, cost);
        return true;
    }

    /* LINK MATRIX FUNCTIONS END */


    /* PACKET METHODS BEGIN */

    // method that sends the init pkt to the nse 
    private void sendInit() throws Exception {
        socket.sendInit(id);
        log.write(this.getID() + " sends a INIT: router_id " + id);
    }

    // receive the circuitDB pkt from the nse and update our circuitDB 
    public void receiveCircuitDB() throws Exception{
        db = socket.receiveCircuitDB();
        String m = getID() + " receives a circuit_DB: " + db.getString();
        log.write(m);
        for (int i = 1; i < db.getLength() + 1; i ++){
            T[id].addLinkCost(db.getLinkCost(i));
            writeTopology();
            writeRIB();
        }
    }

    // create string for logging HELLO send/receive 
    public String createHelloMsg(String action, int rid, int lid) {
        return getID() + " " + action + " a HELLO: router_id " + rid 
                                              + ", link_id " + lid;
    }

    // send the hello pkt to each of the router's neighbours
    public void sendHellos() throws Exception {
        for (int i = 1; i < db.getLength() + 1; i ++){
            int lid = db.getLinkCost(i).getLink();
            log.write(createHelloMsg("sends", id, lid));
            socket.sendHello(id,lid);
        }
    }

    // create string for logging LDPSU send/receive 
    public String createLSPDUMsg(String action, LSPDU l) {
        return getID() + " " + action + " a LSPDU: sender " + l.getSID()
                                              + ", router_id " + l.getRID()
                                              + ", link_id " + l.getLID()
                                              + ", cost " + l.getCost()
                                              + ", via " + l.getVia();
    }

    // helper function to send LSPDU packet to all neighbours
    // called when we send our first LSPDU, and every
    // time we receive a LSPDU. don't send to the via
    public void sendLSPDUToNeighbours(LSPDU lspdu) throws Exception {
        // save the value we don't want to send to 
        int nosend = lspdu.getVia();

        lspdu.changeSender(id);
        for (int i = 1; i < db.getLength() + 1; i ++){
            int via = db.getLinkCost(i).getLink();
            if ((via != nosend) && ACKED[via]) {
                lspdu.changeVia(via);
                log.write(createLSPDUMsg("sends", lspdu));
                socket.sendLSPDU(lspdu);
            }
        }
    }

    // send our knowledge thus far now that we've 
    // received all ACKS from our neighbours
    public void sendLSPDU() throws Exception {
        Set<Integer> sent = new TreeSet<Integer>();
        for (int j = 1; j < T.length; j ++) {
            CircuitDB db_temp = T[j];
            for (int i = 1; i < db_temp.getLength() + 1; i ++) {
                int lid = db_temp.getLinkCost(i).getLink();
                int cost = db_temp.getLinkCost(i).getCost();
                // note we have -1 as the default here because the method we 
                // call changes this value (via) to each of the links we
                // share with a neighbour
                if (!sent.contains(new Integer(lid))){
                    sendLSPDUToNeighbours(new LSPDU(id, id, lid, cost, -1));
                }
                sent.add(new Integer(lid));
            }
        }
    }

    // receive a packet
    public void receivePacket() throws Exception {
        byte[] bytes = socket.receivePacket();
        // assume it's a LSPDU and convert it
        // then check if there are links of value 0  
        // if so, convert to HelloPkt instead
        // this is somewhat of a hack because we need to 
        // listen to grab all data for both Hello and LSPDU
        // so we attempt to convert it to one. if there wasn't
        // enough data given, we know it was really a HELLO
        LSPDU lspdu = LSPDU.fromBytes(bytes);
        if (lspdu.getLID() == 0) {
            receiveHello(HelloPkt.fromBytes(bytes));
        } else {
            receiveLSPDU(lspdu);
        }
    }

    // receive a LSPDU packet and update information accordingly
    // send to neighbours when done
    public void receiveLSPDU(LSPDU lspdu) throws Exception {
        log.write(createLSPDUMsg("receives", lspdu));
        int rid = lspdu.getRID();
        int link = lspdu.getLID();
        int cost = lspdu.getCost();
        if (addToL(link, rid, cost)){
            T[rid].addLinkCost(new LinkCost(link, cost));
            writeTopology();
            updateRIB();
            writeRIB();
            sendLSPDUToNeighbours(lspdu);
        }
    }

    // receive a Hello msg from a neighbour and update our information
    public void receiveHello(HelloPkt p) throws Exception {
        int rid = p.getRID();
        int lid = p.getLID();

        // record the ack and set that link to true to know that we have
        // confirmed it and can send future msgs to it
        num_acks += 1;
        ACKED[lid] = true;

        log.write(createHelloMsg("receives", rid, lid));
        T[rid].addLinkCost(new LinkCost(lid,lid));
        updateC(id,rid,lid);
        writeTopology();
        updateNeighboursRIB();
        writeRIB();

        // if we've received all our HELLOs back, send LSPDU to neighbours
        if (num_acks == db.getLength()) {
            sendLSPDU();
        }

    }

    /* PACKET FUNCTIONS END */


    // update the costs in the RIB for our direct neighbours
    public void updateNeighboursRIB() {
        int[] C2 = C[id];
        for (int i = 1; i < C2.length; i ++) {
            int cost = C2[i];
            if (cost != INFINITY) {
                RIB[i] = new LinkCost(i, cost);
            }
        }
    }


    /* DIJKSTRA ALG'M BEGIN */

    // helper function for getting neighbours
    public Vector getNeighboursOf(int w) {
        Vector v = new Vector(); 
        for (int i = 1; i < 6; i ++) {
            if (C[w][i] < INFINITY && C[w][i] > 0) {
                v.addElement(new Integer(i));
            }
        }
        return v;
    }

    // find min DW in set N
    public int findMinDW(Set<Integer> N) {
        int min = 0; // initialize to something that will lose a min test
        for (int i = 1; i < RIB.length; i ++) {
            if (!N.contains(new Integer(i)) && RIB[i].getCost() < RIB[min].getCost()) {
                min = i;
            }
        }
        return min;
    }

    // This is where I use Dijstra's alg'm to update the RIB
    // Note that I don't perform the intialization steps because
    // I continue the algorithm with the information I have 
    // gained from receiving another LSPDU
    public LinkCost[] updateRIB() {

        Set<Integer> N = new TreeSet<Integer>();
        N.add(new Integer(id));

        // Stopping condition is that there exists some min.
        // not yet in N, because there won't always be a cost
        // associated to each link. we don't have all of this
        // information until we've acquired all LSPDUs from 
        // all neighbours. therefore, we have a different
        // stopping condition than the alg'm
        while (findMinDW(N) != 0){
            int w = findMinDW(N);
            N.add(new Integer(w));
            //get an Iterator object for Vector using iterator() method.
            Iterator itr = getNeighboursOf(w).iterator();
            while(itr.hasNext()) {
                int v = (int) (Integer) itr.next();
                if (!N.contains(new Integer(v))) {
                    int tmp = RIB[w].getCost() + C[w][v];
                    if (tmp < RIB[v].getCost()) {
                        // the getLink call before guarantees we 
                        // get one of our neighbours (output requires
                        // the router we went through to get the 
                        // shortest path)
                        RIB[v] = new LinkCost(RIB[w].getLink(),tmp);
                    }
                }
            }
        }
        return RIB;
    }

    /* DIJKSTRA ALG'M END */


    // write the Topology to the log file
    // calls it for each CircuitDB entry using
    // the getTopologyString help method
    public void writeTopology() {
        log.write("\n#Topology database");
        for (int i = 1; i < T.length; i ++) {
            String prefix = getID() + " -> " + i;
            log.write(T[i].getTopologyString(prefix));
        }
        log.write("\n");
    }

    // create the RIB line for this router and dest router
    // provide values for those paths not yet set
    // e.g., local path (-1, 0) -> local, 0
    //       local path (-1, INFINITY) -> None, INFINITY
    public String createRIBMsg(int i) {
        String path = "R" + Integer.toString(RIB[i].getLink());
        String cost = Integer.toString(RIB[i].getCost());

        // local path
        if (path.equals(getID())) {
            path = "Local";
        }
        // no path created yet
        else if (path.equals("R-1")) {
                path = "none";
                cost = "INFINITY";
        }
        return getID() + " -> " + getRID(i) + " -> " + path + ", " + cost;
    }

    // write the RIB to the lof 
    public void writeRIB() {
        log.write("\n#RIB");
        for (int i = 1; i < RIB.length; i ++) {
            log.write(createRIBMsg(i));
        }
        log.write("\n");
    }


    public static void main(String[] args) throws Exception {
        router r = new router(args);

        // send INIT to nse   
        r.sendInit();
        r.receiveCircuitDB();
        Thread.sleep(2);
        r.sendHellos();

        while (true) {
            r.receivePacket();
        }
    }
}

