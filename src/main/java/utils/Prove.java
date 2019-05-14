package utils;

import java.io.File;
import java.io.IOException;
import java.util.BitSet;

import coder.channel.ChannelCoder;
import coder.channel.ChannelMessage;
import coder.source.SourceCoder;
import coders.LZW.funzionante.LZWCoder;
import coders.Message;
import coders.deflate.DeflateCoder;
import coders.huffman.HuffmanCoder;
import coders.repetition.ConcatenatedChannelCoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import static utils.GenericUtils.getBitSetFromString;

public class Prove {

	public static void main(String[] args) throws IOException {
		
//		ChannelMessage mess=GenericUtils.getChannelMessage("ciao.txt");
//		int[] reps= {3};
//		ChannelCoder chcoder=new ConcatenatedChannelCoder(reps);
//		BitSet b=chcoder.encode(mess);
//		chcoder.decode(b, mess);
//		GenericUtils.writeChannelMessage(mess, "ciao4");
//		SourceCoder coder=new HuffmanCoder();//TODO Controllare
//		String in = "ciao3.txt";
//		String out = "c.txt";
//		String cod = "c";
//		coder.encode(in, cod);
//		
//		coder.decode(cod, out);
//		System.out.println("Dimensione file input:" + new File(in).length());
//		System.out.println("Dimensione file codificato:" + GenericUtils.getChannelMessage(cod).getPayload().length);
//        System.out.println(GenericUtils.readFile(out, StandardCharsets.UTF_8));
                String s="000";
                Message m=new Message();
                m.setPayload(s);
                GenericUtils.writeMessageToFile(m, "prova");
                ChannelMessage cm=GenericUtils.getChannelMessage("prova");
                System.out.println(cm.getPayloadLength());
		
	}

}
