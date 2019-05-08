package coder.channel;

import utils.MyBitSet;

public interface ChannelCoder {
	public MyBitSet encode(ChannelMessage inChannelMessage);
	public void decode(MyBitSet encoded_data, ChannelMessage outChannelMessage);
}