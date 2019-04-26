package coders.LZW.funzionante;

import java.io.IOException;

import coder_interfaces.Encoder;

public class LZWCoder implements Encoder {

	@Override
	public void encode(String input, String output) {
		LZWCompression lzw = new LZWCompression();
		try {
			lzw.LZW_Compress(input, output);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
