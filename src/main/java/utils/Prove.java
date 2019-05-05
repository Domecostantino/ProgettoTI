package utils;

import java.io.File;
import java.io.IOException;
import java.util.BitSet;

import coder.channel.ChannelCoder;
import coder.channel.ChannelMessage;
import coder.source.SourceCoder;
import coders.LZW.funzionante.LZWCoder;
import coders.deflate.DeflateCoder;
import coders.huffman.HuffmanCoder;
import coders.repetition.ConcatenatedChannelCoder;

public class Prove {

	public static void main(String[] args) throws IOException {
		
//		ChannelMessage mess=GenericUtils.getChannelMessage("ciao.txt");
//		int[] reps= {3};
//		ChannelCoder chcoder=new ConcatenatedChannelCoder(reps);
//		BitSet b=chcoder.encode(mess);
//		chcoder.decode(b, mess);
//		GenericUtils.writeChannelMessage(mess, "ciao4");
		SourceCoder coder=new LZWCoder();//TODO Controllare
		String in = "Lorem ipsum.txt";
		String out = "LI.txt";
		String cod = "LI";
		coder.encode(in, cod);
		
		coder.decode(cod, out);
		System.out.println("Dimensione file input:" + new File(in).length());
		System.out.println("Dimensione file codificato:" + GenericUtils.getChannelMessage(cod).getPayload().length);
		
	}

}
