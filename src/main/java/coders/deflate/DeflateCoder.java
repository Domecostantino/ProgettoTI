package coders.deflate;

import java.io.File;
import java.io.IOException;

import coders.SourceCoder;
import coders.Message;
import coders.huffman.CanonicalHuffmanCoder;
import coders.huffman.CanonicalHuffmanDecoder;
import utils.GenericUtils;

public class DeflateCoder implements SourceCoder {
	LZ77 lz77 = new LZ77(3843, 3843);

	@Override
	public void encode(String inputFileName, String outputFileName) {
		try {
			String enc = lz77.encode(inputFileName);
//			System.out.println("bytes codifica lz77: " + enc.length());
//			System.out.println(enc);
			CanonicalHuffmanCoder hcoder = new CanonicalHuffmanCoder();
			Message mess = hcoder.encode(enc);
			GenericUtils.writeMessageToFile(mess, outputFileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void decode(String inputFileName, String outputFileName) {
		Message m = GenericUtils.getMessageFromFile(inputFileName);
		CanonicalHuffmanDecoder hdec = new CanonicalHuffmanDecoder();
		String decodedString = lz77.decode(hdec.decode(m));
//		System.out.println(decodedString);
		GenericUtils.writeString(decodedString, outputFileName);
	}

	public static void main(String[] args) {
		DeflateCoder dc = new DeflateCoder();
		String in = "mail_de_rango.txt";
		String out = "maildec.txt";
		String cod = "deflatemail";
		dc.encode(in, cod);
		System.out.println("Dimensione file input:" + new File(in).length());
		System.out.println("Dimensione file codificato:" + new File(cod).length());
		dc.decode(cod, out);
	}

}
