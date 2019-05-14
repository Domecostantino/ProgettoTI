package channel;

import java.util.BitSet;
import java.util.Random;

import utils.MyBitSet;

public class CanaleSimmetricoBinario implements ChannelModel {

    private double ber = 0.02; //Bit Error Rate
	private double rate = 1000000; //1 Mbps
    private Random random = new Random();

    public CanaleSimmetricoBinario(double ber) {
        if (ber <= 1d && ber >= 0d) {
            this.ber = ber;
        } else {
            System.out.println("ber out of range");
        }
    }
    
    public String getBer() {
		return ""+ber;
	}

    @Override
    public MyBitSet send(MyBitSet encodedPayload) {
        for (int i = 0; i < encodedPayload.getLength(); i++) {
            if (random.nextDouble() < ber) {
                encodedPayload.getBitset().flip(i);
            }
        }//for
        return encodedPayload;
    }

}//CanaleSimmetricoBinario
