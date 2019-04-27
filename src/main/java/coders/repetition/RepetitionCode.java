package coders.repetition;
import java.util.Arrays;

import coders.hamming.HammingCode;

public class RepetitionCode {
	// parametro ripetizioni
	private int r;

	/**
	 * 
	 * @param r numero di ripetizioni
	 */
	public RepetitionCode(int r) {
		this.r = r;
	}

	public boolean[] encode(boolean[] data) {
		boolean[] out = new boolean[data.length * r];
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < r; j++) {
				out[i * r + j] = data[i];
			}
		}
		return out;
	}

	public boolean[] decode(boolean[] coded_data) {
		boolean[] out = new boolean[coded_data.length / r];
		for (int i = 0; i < out.length; i++) {
			int[] count = { 0, 0 };
			for (int j = 0; j < r; j++) {
				count[coded_data[i * r + j] ? 1 : 0]++;
			}
			out[i] = count[0] < count[1] ? true : false;
		}
		return out;
	}

	public static void main(String[] args) {
		RepetitionCode rc = new RepetitionCode(3);
		boolean[] b = { false, true, true, true };
		System.out.println(Arrays.toString(b));
		boolean[] enc = rc.encode(b);
		System.out.println(Arrays.toString(enc));
		enc[2] = !enc[2];
		System.out.println(Arrays.toString(enc));
		boolean[] dec = rc.decode(enc);
		System.out.println(Arrays.toString(dec));
	}

}
