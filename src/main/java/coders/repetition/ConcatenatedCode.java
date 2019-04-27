package coders.repetition;

import java.util.Arrays;

public class ConcatenatedCode {
	private int levels;
	private RepetitionCode[] repCodes;

	/**
	 * 
	 * @param rep_per_level è un array che indica il numero di ripetizioni per ogni livello della codifica,
	 * la dimensione dell'array è pari al numero di livelli
	 */
	public ConcatenatedCode(int[] rep_per_level) {
		this.levels=rep_per_level.length;
		repCodes=new RepetitionCode[levels];
		for (int i = 0; i < levels; i++) {
			repCodes[i]=new RepetitionCode(rep_per_level[i]);
		}
	}
	
	public boolean[] encode(boolean[] data) {
		boolean[] out= data;
		for (int i = 0; i < levels; i++) {
			out=repCodes[i].encode(out);
		}
		return out;
	}
	
	public boolean[] decode(boolean[] encoded_data) {
		boolean[] out= encoded_data;
		for (int i = levels-1; i >= 0; i--) {
			out=repCodes[i].decode(out);
		}
		return out;
	}
	
	public static void main(String[] args) {
		int[] reps= {3,3};
		ConcatenatedCode cc=new ConcatenatedCode(reps);
		boolean[] b = { false, true, true, true };
		System.out.println(Arrays.toString(b));
		boolean[] enc = cc.encode(b);
		System.out.println(Arrays.toString(enc));
		enc[4] = !enc[4];
		enc[3] = !enc[3];
		enc[2] = !enc[2];
		enc[1] = !enc[1];
		enc[0] = !enc[0];
		System.out.println(Arrays.toString(enc));
		boolean[] dec = cc.decode(enc);
		System.out.println(Arrays.toString(dec));
	}

}
