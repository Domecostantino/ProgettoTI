package coders.LZSS;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import coder.source.SourceCoder;

public class LZSSCoder implements SourceCoder {

	@Override
	public void encode(String inputFileName, String outputFileName) {
		try {
			InputStream inp = new FileInputStream(new File(inputFileName));
			LZSS lzss = new LZSS(inp);
			ByteArrayOutputStream baos = lzss.compress();
			byte[] out = baos.toByteArray();
			try (FileOutputStream fos = new FileOutputStream(outputFileName)) {
				fos.write(out);
				// fos.close(); There is no more need for this line since you had created the
				// instance of "fos" inside the try. And this will automatically close the
				// OutputStream
			}
		} catch (IOException e) {
			// TODO: handle exception
		}

	}
	
	@Override
	public void decode(String inputFileName, String outputFileName) {
		try {
			InputStream inp = new FileInputStream(new File(inputFileName));
			LZSS lzss = new LZSS(inp);
			ByteArrayOutputStream baos = lzss.uncompress();
			byte[] out = baos.toByteArray();
			try (FileOutputStream fos = new FileOutputStream(outputFileName)) {
				fos.write(out);
				// fos.close(); There is no more need for this line since you had created the
				// instance of "fos" inside the try. And this will automatically close the
				// OutputStream
			}
		} catch (IOException e) {
			// TODO: handle exception
		}

	}

}
