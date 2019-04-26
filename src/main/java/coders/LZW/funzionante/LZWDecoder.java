package coders.LZW.funzionante;

import java.io.IOException;

import coder_interfaces.Decoder;

public class LZWDecoder implements Decoder {

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
