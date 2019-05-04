package codificatori_conv_rip;

import java.util.concurrent.CountDownLatch;

public class ThreadConversioneBitByte implements Runnable {

	private int inizioRange, fineRange;
	private byte[] arrayBit;
	private CountDownLatch latch;
	private byte[] arrayByte;
	
	public ThreadConversioneBitByte(int inizioRange, int fineRange, byte[] arrayBit,byte[] arrayByte, CountDownLatch latch) {
		// TODO Auto-generated constructor stub
		this.inizioRange = inizioRange;
		this.fineRange = fineRange;
		this.arrayBit = arrayBit;
		this.arrayByte = arrayByte;
		this.latch = latch;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("Inizio conversione da bit a byte Thread "+Thread.currentThread().getName()+" range: <"+inizioRange+","+fineRange+">");
		int k = inizioRange/8;
		int c = 0;
		int i = inizioRange;
		for (i = inizioRange; i < fineRange; i += 8) {
			String bits = "";
			for (int j = 0; j < 8; j++) {
				bits += Byte.toString(arrayBit[j + i]);
			}
			byte x = 0;
			for (int j = 0; j < 8; j++) {
				x += Math.pow(2, j) * (bits.charAt(7 - j) - 48);
			}
			arrayByte[k] = x;
			k++;
			c++;
		}
		latch.countDown();
		System.out.println("Fine conversione da bit a byte Thread "+Thread.currentThread().getName());
	}
	
	
}
