package codificatori_conv_rip;

import java.util.concurrent.CountDownLatch;

public class ThreadConversioneByteBit implements Runnable{
	
	private int inizioRange, fineRange;
	private byte[] arrayByte;
	private CountDownLatch latch;

	private byte[] arrayBit;
	
	public ThreadConversioneByteBit(int inizioRange, int fineRange, byte[] arrayByte,byte[] arrayBit, CountDownLatch latch) {
		// TODO Auto-generated constructor stub
		this.inizioRange = inizioRange;
		this.fineRange = fineRange;
		this.arrayByte = arrayByte;
		this.arrayBit = arrayBit;
		this.latch = latch;
	}
	
	public void run() {
		System.out.println("Inizio conversione Thread "+Thread.currentThread().getName()+" range: <"+inizioRange+","+fineRange+">");
		int k = inizioRange*8;
		for(int i = inizioRange;i<fineRange;i++) {
			String bits = String.format("%8s", Integer.toBinaryString(arrayByte[i] & 0xFF)).replace(' ', '0');
			for(int j = 0; j < bits.length(); j++){
				arrayBit[k]= Byte.parseByte("" + bits.charAt(j));
				k++;
			}		
		}
		latch.countDown();
		System.out.println("Fine conversione Thread "+Thread.currentThread().getName());
	}
	

}
