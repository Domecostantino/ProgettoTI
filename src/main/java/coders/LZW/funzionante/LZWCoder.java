package coders.LZW.funzionante;

import java.io.IOException;

import coder.source.SourceCoder;

public class LZWCoder implements SourceCoder {

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
		LZWCoder c=new LZWCoder();
		String in = "userinput.tmp";
		String out = "us";
		String cod = "uscod";
		c.encode(in, cod);
		c.decode(cod, out);
	}
}
