package utils;

import java.io.IOException;
import java.util.BitSet;

import coder.channel.ChannelCoder;
import coder.channel.ChannelMessage;
import coders.convolutional.ConvolutionalChannelCoder;
import coders.hamming.HammingChannelCoder;
import coders.repetition.RepChannelCoder;

public class Prove {

	public static void main(String[] args) throws IOException {
		
		ChannelMessage mess=GenericUtils.getChannelMessage("ciao.txt");
		ChannelCoder chcoder=new ConvolutionalChannelCoder(7,3);
		BitSet b=chcoder.encode(mess);
		chcoder.decode(b, mess);
		GenericUtils.writeChannelMessage(mess, "ciao4");
		
	}

}
