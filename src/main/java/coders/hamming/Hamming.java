package coders.hamming;

import java.util.Arrays;

public class Hamming {
	private boolean[][] parityCheck;
	private boolean[][] generator;
	private boolean[][] errorCodes;
	private int r;
	private int length;

	public Hamming(int r) {
		this.r = r;
		this.length = (int)Math.pow(2, r) - 1;

		parityCheck = new boolean[length][r];
		generator = new boolean[length - r][length];
		errorCodes = new boolean[length + 1][length];

		// Popola la matrice di parita'
		int currentRow = 0;
		int currentIdentityRow = length - r;

		for (int i = length; i > 0; i--) {
			int row;

			// Se e' potenza di 2
			if ((i & (i - 1)) == 0) {
				row = currentIdentityRow++;
			} else {
				row = currentRow++;
			}

			// inserimento valore binario
			for (int j = 0; j < r; j++) {
				parityCheck[row][j] = (i & (1 << (r - 1 - j))) != 0;
			}
		}

		// Matrica generatrice
		int dataLength = length - r;

		for (int i = 0; i < dataLength; i++) {
			for (int j = 0; j < dataLength; j++) {
				generator[i][j] = i == j;
			}

			for (int j = 0; j < r; j++) {
				generator[i][dataLength + j] = parityCheck[i][j];
			}
		}

		for (int i = 0; i < length; i++) {
			boolean[] code = new boolean[length];
			code[i] = true;

			int syndrome = booleanArrayToInt(getSyndrome(code));
			errorCodes[syndrome][i] = true;
		}
	}

	/**
	 * Metodo di codifica
	 * @param data
	 * @return
	 */
	public boolean[] encode(boolean[] data) {
		if (data.length != length - r) {
			return new boolean[0];
		}

		boolean[] output = new boolean[length];

		for (int i = 0; i < data.length; i++) {
			if (!data[i]) {
				continue;
			}

			boolean[] row = generator[i];

			for (int j = 0; j < length; j++) {
				output[j] ^= row[j];
			}
		}

		return output;
	}

	/**
	 * Metodo di decodifica
	 * @param codeword
	 * @return
	 */
	public boolean[] decode(boolean[] codeword) {
		if (codeword.length != length) {
			return new boolean[0];
		}

		boolean[] syndrome = getSyndrome(codeword);
		int syndromeInt = booleanArrayToInt(syndrome);
		boolean[] errorCode = errorCodes[syndromeInt];

		// Correzione errore
		boolean[] corrected = new boolean[length];

		for (int i = 0; i < length; i++) {
			corrected[i] = codeword[i] ^ errorCode[i];
		}

		return Arrays.copyOf(corrected, length - r);
	}

	public boolean[] getSyndrome(boolean[] codeword) {
		if (codeword.length != length) {
			return new boolean[0];
		}

		boolean[] error = new boolean[r];

		for (int i = 0; i < r; i++) {
			for (int j = 0; j < length; j++) {
				error[i] ^= codeword[j] & parityCheck[j][i];
			}
		}

		return error;
	}
	
	public static int booleanArrayToInt(boolean[] booleans) {
		int n = 0;

		for (int i = 0; i < booleans.length; i++) {
			n = (n << 1) | (booleans[i] ? 1 : 0);
		}

		return n;
	}
	
	
	public static void main(String [] args) {
		Hamming hc=new Hamming(3);
		for (int i = 0; i < hc.parityCheck.length; i++) {
			for (int j = 0; j < hc.parityCheck[i].length; j++) {
				System.out.print(hc.parityCheck[i][j]+"|");
			}
			System.out.println();
		}
		for (int i = 0; i < hc.generator.length; i++) {
			for (int j = 0; j < hc.generator[i].length; j++) {
				System.out.print(hc.generator[i][j]+"|");
			}
			System.out.println();
		}
		boolean[] b= {false,false,false,true};
		boolean[] code=hc.encode(b);
		for (int i = 0; i < code.length; i++) {
			System.out.print(code[i]);
		}
//		boolean[] b={false,true,true,true};
//		System.out.println(Arrays.toString(b));
//		boolean[] enc=hc.encode(b);
//		System.out.println(Arrays.toString(enc));
//		enc[2]=!enc[2];
//		System.out.println(Arrays.toString(enc));
//		boolean[] dec=hc.decode(enc);
//		System.out.println(Arrays.toString(dec));
//                System.out.println((int)Math.pow(2, 4) - 1);
	}
	
	
}