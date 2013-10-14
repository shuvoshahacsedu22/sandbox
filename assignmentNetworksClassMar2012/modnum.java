
/*
 * Since both the sender and receiver have
 * to keep a seqnum Modulo 32, I decided to 
 * make a separate class for it so that 
 * incrementing and decrementing the seqnum
 * (modulo 32) would be cleaner
 *
 * 
 *
 *
 *
 */


class modnum {

    private int i;
	private final int Modulo = 32;

    // constructor for new modnum
    public modnum(){
        i = 0;
    }

    // constructor for new modnum with int j
    public modnum(int j){
        i = j;
        mod();
    }

    public int getVal(){
        return i;
    }

    // increment the value and then mod it 
    public int increment(){
        i = i + 1;
        return mod();
    }

    // we only want to obtain the decremented value
    // so create a new modnum (called by receiver)
    public int getDecremented(){
        modnum temp = new modnum(i-1);
        return temp.mod();
    }

    // used to set the seqnum to a new val
    // E.g. sender sets base to seqnum of ACK + 1
    public void setVal(int j){
        i = j;
        mod();
    }

    // modulo the value so that it's in the 
    // range [0, Modulo - 1] which is [0, 31]
    public int mod(){
        i = i % Modulo;
        // we can't have negative seqnum
        // ad Modulo to get it back in the range
        if (i < 0) {
            i = i + Modulo;
        }
        return i;
    }

    // check if 
    public Boolean inInterval(modnum x, modnum y){
        int a = x.getVal();
        int b = y.getVal();
        int j = getVal();

        // if a <= b, check that we are between them
        if (a <= b) {
            return ( j>=a && j<b );
        }
        // otherwise, [a, b] wraps around the modulo
        // e.g. a = 29 and b = 5. 
        // then check that we are above a or below b
        else {
            return ( j>=a || j<b );
        }
    }

}
