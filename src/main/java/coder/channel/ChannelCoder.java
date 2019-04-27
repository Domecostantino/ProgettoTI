package coder.channel;

import java.util.BitSet;

public interface ChannelCoder {
	public BitSet encode(String inputPath);
	public void decode(BitSet encoded_data, String outputPath);
}