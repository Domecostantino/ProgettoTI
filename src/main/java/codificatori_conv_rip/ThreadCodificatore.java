package codificatori_conv_rip;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

public class ThreadCodificatore implements Runnable {

	private byte[] bits_msg;
	private int inizioRange, fineRange;
	private byte[] result;
	private CountDownLatch latch;
	private byte[] shift_register;
	
	private int lenght_shift_Register;
	
	private byte[] polinomio1,polinomio2;

	public ThreadCodificatore(int lenght_shift_register, byte[] msg, int inizio, int fine, CountDownLatch latch) {
		this.inizioRange = inizio;
		this.fineRange = fine;
		this.latch = latch;
		this.bits_msg = msg;
		this.lenght_shift_Register = lenght_shift_register;
		shift_register = new byte[lenght_shift_register];
		
		switch (lenght_shift_register) {
			case 3:
				polinomio1 = new byte[3];
				polinomio2 = new byte[3];
				polinomio1[0] = 1;
				polinomio1[1] = 1;
				polinomio1[2] = 0;
				
				polinomio2[0] = 1;
				polinomio2[1] = 1;
				polinomio2[2] = 1;

			case 4:
				polinomio1 = new byte[4];
				polinomio2 = new byte[4];
				
				polinomio1[0] = 1;
				polinomio1[1] = 1;
				polinomio1[2] = 0;
				polinomio1[3] = 1;
				
				polinomio2[0] = 1;
				polinomio2[1] = 1;
				polinomio2[2] = 1;
				polinomio2[3] = 0;
				
			case 5:
				polinomio1 = new byte[5];
				polinomio2 = new byte[5];
				
				polinomio1[0] = 1;
				polinomio1[1] = 1;
				polinomio1[2] = 0;
				polinomio1[3] = 1;
				polinomio1[4] = 0;
				
				polinomio2[0] = 1;
				polinomio2[1] = 1;
				polinomio2[2] = 1;
				polinomio2[3] = 0;
				polinomio2[4] = 1;
			case 6:
				polinomio1 = new byte[6];
				polinomio2 = new byte[6];
				
				polinomio1[0] = 1;
				polinomio1[1] = 1;
				polinomio1[2] = 0;
				polinomio1[3] = 1;
				polinomio1[4] = 0;
				polinomio1[5] = 1;
				
				
				polinomio2[0] = 1;
				polinomio2[1] = 1;
				polinomio2[2] = 1;
				polinomio2[3] = 0;
				polinomio2[4] = 1;
				polinomio2[5] = 1;
		}
	}

	public void run() {
		System.out.println("Thread codificatore " + Thread.currentThread().getName() + " partito");
		result = new byte[(fineRange - inizioRange) * 2];
		int indice = 0;
		for (int i = inizioRange; i < fineRange; i++) {

			if (i % 8 == 0) {
				// ogni 8 faccio partire una nuova decodifica
				//shift_register = new byte[lenght_shift_Register];
				// System.out.println("*******");
			}

			byte input = bits_msg[i];
			shift_register(input);
			int output1 = 0;
			int output2 = 0;
			for (int j = 0; j < shift_register.length; j++) {
				
				output1 = output1 ^ (shift_register[j]*polinomio1[j]);
				output2 = output2 ^ (shift_register[j]*polinomio2[j]);
				
			}

			result[indice] = (byte) output1;
			indice++;
			result[indice] = (byte) output2;
			indice++;
		}
		System.out.println("Fine codifica thread codificatore " + Thread.currentThread().getName());
		latch.countDown();
	}

	private void shift_register(byte input) {
		// TODO Auto-generated method stub
		for (int i = shift_register.length - 1; i > 0; i--) {
			shift_register[i] = shift_register[i - 1];
		}
		shift_register[0] = input;
	}

	public byte[] getResult() {
		return result;
	}

}
