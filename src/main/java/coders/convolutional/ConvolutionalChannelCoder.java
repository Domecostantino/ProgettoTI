package coders.convolutional;

import java.util.BitSet;

import coders.ChannelCoder;
import coders.ChannelMessage;
import utils.GenericUtils;
import utils.MyBitSet;

public class ConvolutionalChannelCoder implements ChannelCoder {
	int k, r;
	int codeLength;

	public ConvolutionalChannelCoder(int k, int r) {
		this.k = k;
		this.r = r;
	}

	@Override
	public MyBitSet encode(ChannelMessage inChannelMessage) {
		ConvolutionalCoder coder=new ConvolutionalCoder(k, r);
		BitSet bs=BitSet.valueOf(inChannelMessage.getPayload());
		String enc=coder.encode(GenericUtils.toBinaryString(bs,inChannelMessage.getPayloadLength()));
		codeLength = enc.length();
		return new MyBitSet(GenericUtils.getBitSetFromString(enc), codeLength);
	}

	@Override
	public void decode(MyBitSet encoded_data, ChannelMessage outChannelMessage) {
		ViterbiDecoder decoder=new ViterbiDecoder(k, r);
		String dec=decoder.decode(GenericUtils.toBinaryString(encoded_data.getBitset(),codeLength));
		outChannelMessage.setPayload(GenericUtils.getBitSetFromString(dec).toByteArray());
	}
	
	public int getK() {
		return k;
	}

	public int getR() {
		return r;
	}

}
