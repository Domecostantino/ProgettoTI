package channel;

import java.util.Random;

import utils.MyBitSet;

public class GilbertElliot implements ChannelModel {

    private enum State {
        GOOD,
        BAD
    }
    public final static int SOFT = 0, HARD = 1;
    private final int type;

	private final double[] prob_gg = {0.995, 0.9}; // prob. transizione GOOD --> GOOD
    private final double[] prob_bb = {0.996, 0.9}; // prob. transizione BAD --> BAD
    private final double prob_gb ; //prob. transizione GOOD --> BAD
    private final double prob_bg ; //prob. transizione BAD --> GOOD

    private final double[] ber_good = {0.0001, 0.0001}, ber_bad = {0.001, 0.1}; //bit error rate per gli stati good e bad

    private State statoCorrente;
	private double rate = 700000; // 700 Kbps
	private Random random = new Random();

    public GilbertElliot(int type){
        if(type<=HARD&&type>=SOFT){
            this.type=type;
        }else{
            System.out.println("G_E type out of range");
            this.type=SOFT;
        }
        prob_gb = 1 - prob_gg[type];
        prob_bg = 1 - prob_bb[type];
    }
    
    public int getType() {
		return type;
	}
    
    @Override
    public MyBitSet send(MyBitSet encodedPayload) {

        statoCorrente = State.GOOD;

        double rand1, rand2;

        for (int i = 0; i < encodedPayload.getLength(); i++) {
            rand1 = Math.random();
            rand2 = Math.random();

            if (statoCorrente.equals(State.GOOD)) {

                if (rand1 < ber_good[type]) {
                    //flip bit
                    encodedPayload.getBitset().flip(i);
                }

                if (rand2 < prob_gb) {
                    statoCorrente = State.BAD;
                }

            } else {

                if (rand1 < ber_bad[type]) {
                    //flip bit
                    encodedPayload.getBitset().flip(i);
                }

                if (rand2 < prob_bg) {
                    statoCorrente = State.GOOD;
                }

            }

        }
        //simulazione tempo invio
        long time = (long) Math.ceil(encodedPayload.getLength() / rate);
		try {
			Thread.sleep(time);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
        return encodedPayload;
    }

}//GilbertElliot
