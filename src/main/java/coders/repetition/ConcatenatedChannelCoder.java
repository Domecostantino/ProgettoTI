package coders.repetition;

import java.util.BitSet;

import coders.ChannelCoder;
import coders.ChannelMessage;
import utils.MyBitSet;

public class ConcatenatedChannelCoder implements ChannelCoder {
	
	private ConcatenatedCoder coder;
	int[] rep_per_level;

	/**
	 * 
	 * @param rep_per_level è un array che indica il numero di ripetizioni per ogni livello della codifica,
	 * la dimensione dell'array è pari al numero di livelli
	 */
	public ConcatenatedChannelCoder(int[] rep_per_level) {
		this.rep_per_level = rep_per_level;
		coder=new ConcatenatedCoder(rep_per_level);
	}
	
	public int[] getRep_per_level() {
		return rep_per_level;
	}

	@Override
	public MyBitSet encode(ChannelMessage inChannelMessage) {
		BitSet out = null;
		// trasformo input in array di boolean
		byte[] fileContent = inChannelMessage.getPayload();
		BitSet bs = BitSet.valueOf(fileContent);
                int bslength=fileContent.length*8;
		boolean[] boolData = new boolean[bslength];
		for (int i = 0; i < bslength; i++) {
			boolData[i] = bs.get(i);
		}
		// codifico
		boolean[] encData = coder.encode(boolData);
		out = new BitSet(encData.length);
		for (int i = 0; i < encData.length; i++) {
			if (encData[i])
				out.set(i);
		}
		return new MyBitSet(out, encData.length);
	}

	@Override
	public void decode(MyBitSet encoded_data, ChannelMessage outChannelMessage) {
		boolean[] boolData = new boolean[encoded_data.getLength()];
		for (int i = 0; i < encoded_data.getLength(); i++) {
			boolData[i] = encoded_data.getBitset().get(i);
		}
		//decodifico
		boolean[] decData = coder.decode(boolData);
		BitSet bs = new BitSet(decData.length);
		for (int i = 0; i < decData.length; i++) {
			if (decData[i])
				bs.set(i);
		}
		//scrivo file
		byte[] out = bs.toByteArray();
		outChannelMessage.setPayload(out);
	}

}
