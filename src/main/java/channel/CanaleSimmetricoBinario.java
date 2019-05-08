package channel;

import java.util.BitSet;
import utils.MyBitSet;

public class CanaleSimmetricoBinario implements ChannelModel {
	private double ber = 0.01; //Bit Error Rate
    private double rate = 1000000; //1 Mbps
    
    
	@Override
	public MyBitSet send(MyBitSet encodedPayload) {
		
		for (int i = 0; i < encodedPayload.getLength(); i++) {
             if (Math.random() < ber)
                 encodedPayload.getBitset().flip(i);
         }//for
		 
		return encodedPayload;
	}

}//CanaleSimmetricoBinario
