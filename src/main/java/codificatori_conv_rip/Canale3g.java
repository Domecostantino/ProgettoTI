package codificatori_conv_rip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.math3.special.Erf;

/**
 *
 * @author luigi
 */
public class Canale3g {

	/**
	 * @param args
	 *            the command line arguments
	 */

	public static int payload = 300 * 1000;
	private static double P = 0.1;
	private static double BER;
	private static FileOutputStream out;

	public static void main(String[] args) throws Exception {
	
		
		byte[]b= {1,0,1,1,1,0,0,0};
		
		sendFile(new File("/home/luigi/Scrivania/lzw/audio wav/cat.wav"));
		//sendArrayBit(b);
		stat();
	}

	public static void calcolaBER_1() {

		double SQRT_EB_NO = Erf.erfcInv(P * 2);
		BER = Erf.erfc(SQRT_EB_NO) * 0.5;
		double EB_NO = Math.pow(2, SQRT_EB_NO);
		System.out.println("SQRT_EB_NO " + SQRT_EB_NO);
		System.out.println("EB_NO " + EB_NO);
		System.out.println("BER " + BER);
		System.out.println("BER DESIDERATA " + P);
		

	}
	public static void calcolaBER_2() {
		// i valori sono presi dal pdf GSM LINK BUDGET
		// BANDE 900 MHZW 1800 MHZ
		double distanza = 4;// KM //MAX DISTANZA 35KM TERMINALE RADIO BASE
		double f = 1880.0;// MHZ
		// double speedLi=299792458;
		// double lossPathSignal=(4.0*Math.PI*distanza*f)/speedLi;
		// lossPathSignal=20.0*Math.log10(lossPathSignal);
		double lossPathSignal = 32.45 + 20 * Math.log10(distanza) + 20 * Math.log10(f);

		System.out.println("PATH LOSS " + lossPathSignal);

		double powerTx = 40; // decibel
		double guadagnoTx = 13; // decibel
		double guadagnoRx = 14;

		double rumoreGenerico = 0;
		double Ltx = 0;
		double Lrx = 0;// valori del canale
		double powerRx = powerTx + guadagnoTx + guadagnoRx - Ltx - lossPathSignal - rumoreGenerico - Lrx;

		// double EB=signal/bitRate;

		// OPPURE
		double PNOISE = Ltx + lossPathSignal + rumoreGenerico + Lrx;
		double SNR = powerRx / PNOISE;
		double w = Math.pow(10, (SNR / 10));

		// double bitRate=8000*300;

		System.out.println("SNR " + SNR);
		System.out.println("Rx " + powerRx);

		System.out.println("be_no " + w);

		System.out.println("SQRT_be_no " + Math.sqrt(w));

		BER = Erf.erfc(Math.sqrt(w)) * 0.5;
		System.out.println("ber:" + BER);
	
	}
	
	public static void stat() {
		System.out.println("INVIATI: " + bytes_inviati);

		System.out.println("puliti:" + puliti + " sporchi : " + alterati);
		System.out.println("BIT ERROR RATE "+BER*100 +"% VERIFICA " + (alterati / (puliti + alterati)) * 100 + " % ");

		System.out.println("Comunication time " + (elapsedTime) + " secondi");

	}

	public static void sendFile(File file_input) throws Exception {
		calcolaBER_1();

		File file_output = new File(file_input.getAbsolutePath() + ".canale");
		out = new FileOutputStream(file_output);
		FileInputStream is = new FileInputStream(file_input);

		System.out.println("Output: " + file_output.getPath());
		System.out.println("INput: " + file_input.getPath());

		byte[] frame = new byte[1];

		elapsedTime = 0;

		while (true) {
			int n_letti = is.read(frame);
			if (n_letti == -1)
				break;

			frame[0] = simula_errore(frame[0]);

			sendframe(frame, n_letti);

		}
		sendBucket(Arrays.copyOfRange(buffer, 0, puntatore));
		
		
		is.close();
		out.close();
		stat();

	}

	// FUNZIONE PER PIETRO
	public static void sendArrayBit(byte[] input) throws Exception {

		out = new FileOutputStream("outputCanale.umts");

		elapsedTime = 0;

		double eb_su_no = Erf.erfInv(Math.pow(P / 0.5, 2));

		BER = 0.5 * Erf.erf(Math.sqrt(eb_su_no));

		int count = 1;
		String s = "";
		byte[]f=new byte[1];
		for (int i = 0; i < input.length; i++) {
			s = s + input[i];
			if (count == 8) {
				byte frame = (byte) Integer.parseInt(String.valueOf(s), 2);
				f[0] = simula_errore(frame);
				sendframe(f, 1);
				count = 1;
				s = "";
			}
			count++;
		}
		if (count > 1) {

		}

		// System.out.println("File size: " + input.length());

		sendBucket(Arrays.copyOfRange(buffer, 0, puntatore));
		out.close();
	}

	public static byte[] buffer;
	public static int puntatore = 0;

	public static void sendframe(byte[] frame, int bytes_letti) {
		/// frame=simula_errore_frame(frame);

		if (buffer == null) {
			// genera un dimensione casuale dal 250 al 400 kb
			buffer = new byte[payload];
		}
		int i;
		for (i = 0; i < bytes_letti; i++) {

			if (puntatore + i >= buffer.length) {
				sendBucket(buffer);
				break;
			}
			buffer[puntatore + i] = frame[i];

		}
		// byte del frame che sono sfodari
		if (i < bytes_letti) {
			// ho ancora roba dal fram che non entra nel buffer
			puntatore = 0;
			int k = 0;
			int padding = bytes_letti - i;
			for (; i < bytes_letti; i++) {
				buffer[puntatore + k] = frame[i];
				k++;
			}
			puntatore = puntatore + padding;
		} else {

			puntatore = puntatore + bytes_letti;

			if (puntatore >= buffer.length) {
				sendBucket(buffer);
				puntatore = 0;
			}
		}

	}

	// SIMULO LA VELOCITA DI TRANSFERMITO MEDIA DI UN CANALE UMTS
	public static int bytes_inviati = 0;
	public static double puliti = 0.0;
	public static double alterati = 0.0;
	public static long elapsedTime;

	public static void sendBucket(byte[] buffer) {

		bytes_inviati = bytes_inviati + buffer.length;
		simula_invio(buffer);
		System.out.println("VELOCITA CANALE: " + (payload / 1000) + " Kbyte al secondo");
		// output.addLast(Arrays.copyOf(buffer,buffer.length));
	}

	public static byte simula_errore(byte frame) {

		// Eb/N0 == Es/p*No -> Es=Eb*p
		// UMTS p=0.0017 bit/s

		// double EB = 0.0;// ENERGIA PER BIT
		// double NO = 0.0;// NOISE SPECTRAL DENSITY

		// EB = 10.0 * 0.0017;

		char[] block = String.format("%8s", Integer.toBinaryString(frame & 0xFF)).replace(' ', '0').toCharArray();

		for (int i = 0; i < block.length; i++) {

			double r = Math.random();
			if (r <= BER) {
				// SIMULARE ERRORE NEL BIT;
				alterati++;
				block[i] = block[i] == '1' ? '0' : '1';

			} else {
				puliti++;
			}

		}
		byte frame2 = (byte) Integer.parseInt(String.valueOf(block), 2);
		
		if(frame!=frame2 &&BER==0) {
			System.out.println("ERRORREEEEE");
			System.out.println(frame);
			System.out.println(frame2);
			
			
		}
		return frame2;
	}

	public static void simula_invio(byte[] buffer) {
		try {

			// long s=System.currentTimeMillis();
			out.write(buffer);
			// System.out.println((System.currentTimeMillis()-s)/1000.0);

			elapsedTime++;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static String b2bit(byte b) {
		String output = "";
		for (int i = 7; i >= 0; i--) {
			if (b - ((int) Math.pow(2.0, i)) < 0) {
				output += "0";
			} else {
				b -= ((int) Math.pow(2.0, i));
				output += "1";
			}

		}
		return output;

	}
	
	
	public static void sendFile2(File file_input) throws Exception {
		
		File file_output = new File(file_input.getAbsolutePath() + ".canale");
		out = new FileOutputStream(file_output);
		FileInputStream is = new FileInputStream(file_input);

		System.out.println("Output: " + file_output.getPath());
		System.out.println("INput: " + file_input.getPath());

		byte[] frame = new byte[1];

		elapsedTime = 0;

		while (true) {
			int n_letti = is.read(frame);
			if (n_letti == -1)
				break;
			frame[0]=simula_errore(frame[0]);
			out.write(frame);
		}
		
		
		is.close();
		out.close();
		stat();
		
		
		
	}

	
}