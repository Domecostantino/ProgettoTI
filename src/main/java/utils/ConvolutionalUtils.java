package utils;

import java.math.BigInteger;

public class ConvolutionalUtils {
	
	public static int hammingDistance(String p_received, String p_node) {
		int count = 0;
		for (int i = 0; i < p_received.length(); i++) {
			if(p_received.charAt(i)!=p_node.charAt(i))
				count++;
		}
		return count;
	}
	
	public static byte[] getBits(String input) {
		byte[] bits = new byte[input.length()];
		for (int i = 0; i < bits.length; i++) {
			bits[i] = (byte) (input.charAt(i) == '0' ? 0 : 1);
		}
		return bits;
	}
	
	public static byte[][] convertGeneratorPolynomials(int r, int K, String[] generatorPolynomial ) {
		byte[][] gs = new byte[r][K + 1];
		for (int i = 0; i < gs.length; i++) {
			for (int j = 0; j < gs[i].length; j++) {
				gs[i][j] = (byte) (generatorPolynomial[i].charAt(j) == '0' ? 0 : 1);
			}
		}
		return gs;
	}

	
	public static void main(String[] args) {
		
		
		String text = "lalasdasdal";
		System.out.println("Text: "+text);

		String binary = new BigInteger(text.getBytes()).toString(2);
		System.out.println("As binary: "+binary);

		String text2 = new String(new BigInteger(binary, 2).toByteArray());
		System.out.println("As text: "+text2);
		
		byte a = 0;
		byte b = 1;
		System.out.println(a ^ b);
		

		byte z = (byte) '1';
		System.out.println(z);
		
		String aaa = "ciao";
		System.out.println(aaa.substring(0,aaa.length()-1));
	}

}//BitUtility