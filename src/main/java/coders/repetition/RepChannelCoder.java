package coders.repetition;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.BitSet;

import coder.channel.ChannelCoder;

public class RepChannelCoder implements ChannelCoder {
	private RepetitionCode repCode;

	public RepChannelCoder(int r) {
		repCode = new RepetitionCode(r);
	}

	@Override
	public BitSet encode(String inputPath) {
		File file = new File(inputPath);
		BitSet out = null;
		try {
			// trasformo input in array di boolean
			byte[] fileContent = Files.readAllBytes(file.toPath());
			BitSet bs = BitSet.valueOf(fileContent);
			boolean[] boolData = new boolean[bs.length()];
			for (int i = 0; i < bs.length(); i++) {
				boolData[i] = bs.get(i);
			}
			// codifico
			boolean[] encData = repCode.encode(boolData);
			out = new BitSet(encData.length);
			for (int i = 0; i < encData.length; i++) {
				if (encData[i])
					out.set(i);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return out;
	}

	@Override
	public void decode(BitSet encoded_data, String outputPath) {
		boolean[] boolData = new boolean[encoded_data.length()];
		for (int i = 0; i < encoded_data.length(); i++) {
			boolData[i] = encoded_data.get(i);
		}
		//decodifico
		boolean[] decData = repCode.decode(boolData);
		BitSet bs = new BitSet(decData.length);
		for (int i = 0; i < decData.length; i++) {
			if (decData[i])
				bs.set(i);
		}
		//scrivo file
		byte[] out = bs.toByteArray();
		try (FileOutputStream fos = new FileOutputStream(outputPath)) {
			fos.write(out);
			fos.close(); // There is no more need for this line since you had created the instance of
							// "fos" inside the try. And this will automatically close the OutputStream
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		RepChannelCoder rcc=new RepChannelCoder(3);
		BitSet bitData=rcc.encode("ciao.txt");
		bitData.flip(5);
		bitData.flip(6);
		bitData.flip(7);
		rcc.decode(bitData, "ciao2.txt");
	}

}
