package coders.LZW;

import java.io.IOException;

import coders.SourceCoder;

public class LZWSourceCoder implements SourceCoder {

	@Override
	public void encode(String input, String output) {
		LZWCompression lzw = new LZWCompression();
		try {
			lzw.LZW_Compress(input, output);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void decode(String input, String output) {
		LZWCompression lzw = new LZWCompression();
		try {
			lzw.LZW_Decompress(input, output);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		LZWSourceCoder c=new LZWSourceCoder();
		String in = "userinput.tmp";
		String out = "us";
		String cod = "uscod";
		c.encode(in, cod);
		c.decode(cod, out);
	}
}
