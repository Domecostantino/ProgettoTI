package coders.convolutional;

import java.util.BitSet;

import coder.channel.ChannelCoder;
import coder.channel.ChannelMessage;
import utils.GenericUtils;

public class ConvolutionalChannelCoder implements ChannelCoder {
	int k, r;
	int codeLength;

	public ConvolutionalChannelCoder(int k, int r) {
		this.k = k;
		this.r = r;
	}

	@Override
	public BitSet encode(ChannelMessage inChannelMessage) {
		ConvolutionalCoder coder=new ConvolutionalCoder(k, r);
		BitSet bs=BitSet.valueOf(inChannelMessage.getPayload());
		String enc=coder.encode(GenericUtils.toBinaryString(bs,bs.length()));
		codeLength = enc.length();
		return GenericUtils.getBitSetFromString(enc);
	}

	@Override
	public void decode(BitSet encoded_data, ChannelMessage outChannelMessage) {
		ViterbiDecoder decoder=new ViterbiDecoder(k, r);
		String dec=decoder.decode(GenericUtils.toBinaryString(encoded_data,codeLength));
		outChannelMessage.setPayload(GenericUtils.getBitSetFromString(dec).toByteArray());
	}

}
