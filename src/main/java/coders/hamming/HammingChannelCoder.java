package coders.hamming;

import java.util.BitSet;

import coder.channel.ChannelCoder;
import coder.channel.ChannelMessage;

public class HammingChannelCoder implements ChannelCoder {
	private HammingCode coder;

	public HammingChannelCoder() {
		coder = new HammingCode(3);// Hamming 7/4
	}

	@Override
	public BitSet encode(ChannelMessage inChannelMessage) {
		BitSet out = null;
		// trasformo input in array di boolean
		byte[] fileContent = inChannelMessage.getPayload();
		out = new BitSet(fileContent.length*8*7/4);
		int setIndex=0;
		for (int i = 0; i < fileContent.length; i++) {
			byte[] b = new byte[1];
			b[0] = fileContent[i];
			BitSet bs = BitSet.valueOf(b);
//			System.out.println(bs.length());
			boolean[] boolData0 = new boolean[4];
			boolean[] boolData1 = new boolean[4];
			int bits=4;
			for (int j = 0; j < bs.length(); j++) {
				if (j < bits)
					boolData0[j] = bs.get(j);
				else
					boolData1[j-bits] = bs.get(j);
			}
			// codifico
			boolean[] encData0 = coder.encode(boolData0);
			boolean[] encData1 = coder.encode(boolData1);
			for (int j = 0; j < encData0.length; j++) {
				if (encData0[j])
					out.set(setIndex);
				setIndex++;
			}
			for (int j = 0; j < encData1.length; j++) {
				if (encData1[j])
					out.set(setIndex);
				setIndex++;
			}
		}

		
		
		return out;
	}

	@Override
	public void decode(BitSet encoded_data, ChannelMessage outChannelMessage) {
		int numBlocks=encoded_data.length()/7;
		BitSet bs = new BitSet(numBlocks*4);
		for (int j = 0; j < numBlocks; j++) {
			boolean[] boolData = new boolean[7];
			for (int i = 0; i < 7; i++) {
				boolData[i] = encoded_data.get(j*7+i);
			}
			// decodifico
			boolean[] decData = coder.decode(boolData);
			
			for (int i = 0; i < decData.length; i++) {
				if (decData[i])
					bs.set(j*4+i);
			}
		}
		
		// scrivo file
		byte[] out = bs.toByteArray();
		outChannelMessage.setPayload(out);
	}

}
