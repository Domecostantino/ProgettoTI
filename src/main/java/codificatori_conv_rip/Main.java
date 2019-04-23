package codificatori_conv_rip;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

public class Main {

	public static void main(String[] args) throws Exception {
		byte[] message = {15,16,17,18,19,19,20,12,45,3,5,-5,-17};//,-89,15,16,17,18,19,20,12,45,3,5,-5,-89,15,16,17,18,19,20,12,45,3,5,-5,-89};
		int numThread = 4;

		Instant start = Instant.now();
		CountDownLatch latch = new CountDownLatch(numThread);
		
		int range = message.length / numThread;
		int resto = message.length % numThread;
		int inizioRange = 0;

		byte[] bits = new byte[message.length * 8];

		for (int i = 0; i < numThread; i++) {
			if (i == numThread - 1) {
				ThreadConversioneByteBit tb = new ThreadConversioneByteBit(inizioRange, inizioRange + range + resto,
						message, bits, latch);
				Thread t = new Thread(tb);
				t.start();
			} else {
				ThreadConversioneByteBit tb = new ThreadConversioneByteBit(inizioRange, inizioRange + range, message,
						bits, latch);
				Thread t = new Thread(tb);
				t.start();
			}
			inizioRange += range;

		}
		latch.await();
		System.out.println("**************************");

		CountDownLatch latch2 = new CountDownLatch(numThread);
		
		int blocchi = bits.length/8;
		int lavoroPerThread = blocchi/numThread;
		int restoLavoro = blocchi%numThread;
		int range2 = lavoroPerThread*8;
		
		int inizioRange2 = 0;
		
		byte[] bytes = new byte[bits.length / 8];

		for (int i = 0; i < numThread; i++) {
			if (i == numThread - 1) {
				ThreadConversioneBitByte tb = new ThreadConversioneBitByte(inizioRange2, inizioRange2 + range2 + restoLavoro*8,
						bits, bytes, latch2);
				Thread t = new Thread(tb);
				t.start();
			} else {
				ThreadConversioneBitByte tb = new ThreadConversioneBitByte(inizioRange2, inizioRange2+range2, bits,
						bytes, latch2);
				Thread t = new Thread(tb);
				t.start();
			}
			inizioRange2 += range2;
			
		}
		latch2.await();
		Instant end = Instant.now();
		System.out.println(Duration.between(start, end).toMillis() + " ms.");
		
		System.out.println(Arrays.toString(bytes));
	}

	public static int binaryToInteger(String binary) {
		char[] numbers = binary.toCharArray();
		int result = 0;
		for (int i = numbers.length - 1; i > 0; i--)
			if (numbers[i] == '1')
				result += Math.pow(2, (numbers.length - i - 1));
		if (binary.charAt(0) == '1')
			result *= -1;
		return result;
	}

	public static String complemento(String s) {// 11111110 che sarebbe -2 integer diventa 100000010 (primo bit segno e
												// il resto è il numero)
		if (s.charAt(0) == '0') // funzionante solo per 8 bit
			return s;
		if (s.equals("10000000"))// -128
			return "110000000";
		if (s.equals("11000000"))// -64
			return "11000000";
		char[] mask = { '0', '0', '0', '0', '0', '0', '0', '0' };
		boolean uno = false;
		for (int i = s.length() - 1; i > 0; i--) {
			if (!uno && s.charAt(i) == '1') {
				uno = true;
				i--;
			}
			if (uno)
				mask[i] = '1';
		}
		String maskered = new String(mask);
		return null;//xor(maskered, s);
	}
	
	
	
}
