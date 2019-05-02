package coders.LZW.funzionante;

import java.io.IOException;

import coder.source.Coder;

public class LZWCoder implements Coder {

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
}
