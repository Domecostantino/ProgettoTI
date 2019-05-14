package coders.repetition;

import java.util.BitSet;

import coder.channel.ChannelCoder;
import coder.channel.ChannelMessage;
import utils.GenericUtils;
import utils.MyBitSet;

public class RepChannelCoder implements ChannelCoder {
	private RepetitionCode repCode;
	private int r;

	public RepChannelCoder(int r) {
            repCode = new RepetitionCode(r);
            this.r = r;
	}

	public int getR() {
		return r;
	}

	@Override
	public MyBitSet encode(ChannelMessage input) {
		BitSet out = null;
		// trasformo input in array di boolean
		byte[] fileContent = input.getPayload();
		BitSet bs = BitSet.valueOf(fileContent);
                int bslength=fileContent.length*8;
		boolean[] boolData = new boolean[bslength];
		for (int i = 0; i < bslength; i++) {
			boolData[i] = bs.get(i);
		}
		// codifico
		boolean[] encData = repCode.encode(boolData);
		out = new BitSet(encData.length);
		for (int i = 0; i < encData.length; i++) {
			if (encData[i])
				out.set(i);
		}
		return new MyBitSet(out, encData.length);
	}

	@Override
	public void decode(MyBitSet encoded_data, ChannelMessage output) {
		boolean[] boolData = new boolean[encoded_data.getLength()];
		for (int i = 0; i < encoded_data.getLength(); i++) {
			boolData[i] = encoded_data.getBitset().get(i);
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
		MyBitSet bitData=rcc.encode(m);
		bitData.getBitset().flip(5);
		bitData.getBitset().flip(6);
		bitData.getBitset().flip(7);
		rcc.decode(bitData, m);
		GenericUtils.writeChannelMessage(m, "ciao2");
	}

}
