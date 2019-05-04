package coders.repetition;

import java.util.BitSet;

import coder.channel.ChannelCoder;
import coder.channel.ChannelMessage;
import utils.GenericUtils;

public class RepChannelCoder implements ChannelCoder {
	private RepetitionCode repCode;

	public RepChannelCoder(int r) {
		repCode = new RepetitionCode(r);
	}

	@Override
	public BitSet encode(ChannelMessage input) {
		BitSet out = null;
		// trasformo input in array di boolean
		byte[] fileContent = input.getPayload();
		BitSet bs = BitSet.valueOf(fileContent);
		boolean[] boolData = new boolean[bs.length()];
		for (int i = 0; i < bs.length(); i++) {
			boolData[i] = bs.get(i);
		}
		// codifico
		boolean[] encData = repCode.encode(boolData);
		out = new BitSet(encData.length);
		for (int i = 0; i < encData.length; i++) {
			if (encData[i])
				out.set(i);
		}
		return out;
	}

	@Override
	public void decode(BitSet encoded_data, ChannelMessage output) {
		boolean[] boolData = new boolean[encoded_data.length()];
		for (int i = 0; i < encoded_data.length(); i++) {
			boolData[i] = encoded_data.get(i);
		}
		//decodifico
		boolean[] decData = repCode.decode(boolData);
		BitSet bs = new BitSet(decData.length);
		for (int i = 0; i < decData.length; i++) {
			if (decData[i])
				bs.set(i);
		}
		//scrivo file
		byte[] out = bs.toByteArray();
		output.setPayload(out);
	}
	
	public static void main(String[] args) {
		RepChannelCoder rcc=new RepChannelCoder(3);
		ChannelMessage m=GenericUtils.getChannelMessage("ciao.txt");
		BitSet bitData=rcc.encode(m);
		bitData.flip(5);
		bitData.flip(6);
		bitData.flip(7);
		rcc.decode(bitData, m);
		GenericUtils.writeChannelMessage(m, "ciao2");
	}

}
