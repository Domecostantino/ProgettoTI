package utils;

import java.math.BigInteger;

public class ConvolutionalUtils {
	

	
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
	}

}//BitUtility