package channel;

import java.util.BitSet;

public class GilbertElliot implements ChannelModel {

	private enum State{
        GOOD,
        BAD
    }
	
	private double prob_gg = 0.995; // prob. transizione GOOD --> GOOD
	private double prob_bb = 0.996; // prob. transizione BAD --> BAD
    private double prob_gb = 1 - prob_gg; //prob. transizione GOOD --> BAD
    private double prob_bg = 1 - prob_bb; //prob. transizione BAD --> GOOD

    private double ber_good = 0.0001, ber_bad = 0.001; //bit error rate per gli stati good e bad

    private State statoCorrente;

    private double rate = 1000000; //1 Mbps
	
	
	@Override
	public BitSet send(BitSet encodedPayload) throws Exception {
		 
	        int num_bit = 0;

	        statoCorrente = State.GOOD;
	        
	        double rand1, rand2;
	       

	            for(int i=0; i<encodedPayload.length();i++) {

	                rand1 = Math.random();
	                rand2 = Math.random();

	                if (statoCorrente.equals(State.GOOD)) {

	                    if (rand1 < ber_good) {
	                        //flip bit
	                    	encodedPayload.flip(i);
	                    }

	                    if (rand2 < prob_gb) {
	                        statoCorrente = State.BAD;
	                    }

	                } else {

	                    if (rand1 < ber_bad) {
	                        //flip bit
	                    	encodedPayload.flip(i);
	                    }

	                    if (rand2 < prob_bg) {
	                        statoCorrente = State.GOOD;
	                    }


	                }
	        
	            }
	            return encodedPayload;
	}

}//GilbertElliot
