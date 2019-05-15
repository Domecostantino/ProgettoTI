package coders.huffman;

import java.nio.charset.StandardCharsets;

import coders.SourceCoder;
import coders.Message;
import utils.GenericUtils;

public class HuffmanSourceCoder implements SourceCoder {

	@Override
	public void encode(String inputFileName, String outputFileName) {
		CanonicalHuffmanCoder coder = new CanonicalHuffmanCoder();
		String input = GenericUtils.readFile(inputFileName, StandardCharsets.UTF_8);
		Message m = coder.encode(input);
		GenericUtils.writeMessageToFile(m, outputFileName);
	}

	@Override
	public void decode(String inputFileName, String outputFileName) {
		Message m = GenericUtils.getMessageFromFile(inputFileName);
		CanonicalHuffmanDecoder decoder = new CanonicalHuffmanDecoder();
		GenericUtils.writeString(decoder.decode(m), outputFileName);
	}

}
