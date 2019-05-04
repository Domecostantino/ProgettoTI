package coders.repetition;

import java.util.BitSet;

import coder.channel.ChannelCoder;
import coder.channel.ChannelMessage;

public class ConcatenatedChannelCoder implements ChannelCoder {
	
	private ConcatenatedCoder coder;

	/**
	 * 
	 * @param rep_per_level è un array che indica il numero di ripetizioni per ogni livello della codifica,
	 * la dimensione dell'array è pari al numero di livelli
	 */
	public ConcatenatedChannelCoder(int[] rep_per_level) {
		coder=new ConcatenatedCoder(rep_per_level);
	}
	
	@Override
	public BitSet encode(ChannelMessage inChannelMessage) {
		BitSet out = null;
		// trasformo input in array di boolean
		byte[] fileContent = inChannelMessage.getPayload();
		BitSet bs = BitSet.valueOf(fileContent);
		boolean[] boolData = new boolean[bs.length()];
		for (int i = 0; i < bs.length(); i++) {
			boolData[i] = bs.get(i);
		}
		// codifico
		boolean[] encData = coder.encode(boolData);
		out = new BitSet(encData.length);
		for (int i = 0; i < encData.length; i++) {
			if (encData[i])
				out.set(i);
		}
		return out;
	}

	@Override
	public void decode(BitSet encoded_data, ChannelMessage outChannelMessage) {
		boolean[] boolData = new boolean[encoded_data.length()];
		for (int i = 0; i < encoded_data.length(); i++) {
			boolData[i] = encoded_data.get(i);
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
