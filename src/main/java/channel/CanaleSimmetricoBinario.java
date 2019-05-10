package channel;

import java.util.BitSet;
import java.util.Random;

import utils.MyBitSet;

public class CanaleSimmetricoBinario implements ChannelModel {
	private double ber = 0.02; //Bit Error Rate
    private double rate = 1000000; //1 Mbps
    private Random random = new Random();
    
    
	@Override
	public MyBitSet send(MyBitSet encodedPayload) {
		
		for (int i = 0; i < encodedPayload.getLength(); i++) {
             if (random.nextDouble() < ber)
                 encodedPayload.getBitset().flip(i);
         }//for
		 
		return encodedPayload;
	}

}//CanaleSimmetricoBinario
