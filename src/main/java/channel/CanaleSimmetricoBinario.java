package channel;

import java.util.BitSet;

public class CanaleSimmetricoBinario implements ChannelModel {
	private double ber = 0.01; //Bit Error Rate
    private double rate = 1000000; //1 Mbps
    
    
	@Override
	public BitSet send(BitSet encodedPayload) {
		
		for (int i = 0; i < encodedPayload.length(); i++) {
             if (Math.random() < ber)
                 encodedPayload.flip(i);
         }//for
		 
		return encodedPayload;
	}

}//CanaleSimmetricoBinario
