package coder.channel;

import java.util.BitSet;

public interface ChannelCoder {
	public BitSet encode(ChannelMessage inChannelMessage);
	public void decode(BitSet encoded_data, ChannelMessage outChannelMessage);
}