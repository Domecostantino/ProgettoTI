package channel;

import java.util.BitSet;

import org.apache.commons.math3.special.Erf;//utility per il calcolo di integrali

public class Canale3g implements ChannelModel {

	public static double inalterati = 0.0;
	public static double alterati = 0.0;
	public static double BER;

	// parametri del modello di canale
	public static double distanza = 20.0;
	public static double f = 1300.0; // frequenza
	public static double powerTx = 30; // decibel
	public static double guadagnoTx = 13; // decibel
	public static double guadagnoRx = 14;

	public static double rumoreGenerico = 0;
	public static double Ltx = 0;
	public static double Lrx = 0;// valori del canale

	public static double lossPathSignal;
	private static double linkBudgetRx;
	private static double SNR;
	private static double eb_su_no;

	@Override
	public BitSet send(BitSet encodedPayload){
		System.out.println(summary());
		// mi serve se viene modificata la distanza
		calcolaBER();
		BitSet packet = simulaErrore(encodedPayload);
		statistiche();
		return packet;
	}// send

	public static String summary() {
		String d = "Info parametri di simulazione:";
		d = d + "\n Loss path signal: " + lossPathSignal;
		d = d + "\n Link budget rx: " + linkBudgetRx;
		d = d + "\n Distanza: " + distanza;
		d = d + "\n BER: " + BER;
		return d;
	}

	public static void calcolaBER() {
		// i valori sono presi dal pdf GSM LINK BUDGET e da documento utile.pdf
		// BANDE 900 MHZW 1800 MHZ
		// double distanza = 80.0;// KM //MAX DISTANZA 35KM TERMINALE RADIO BASE
		// double f=1300.0;
		// double f = 800.0;// MHZ
		// double lossPathSignal=(4.0*Math.PI*distanza*f)/speedLi;
		// lossPathSignal=20.0*Math.log10(lossPathSignal);
		lossPathSignal = 20.0 * Math.log10(distanza) + 20.0 * Math.log10(f) - 27.55;

		System.out.println("free space path loss Friss Formula " + lossPathSignal);

		linkBudgetRx = powerTx + guadagnoTx - Ltx - lossPathSignal + guadagnoRx - rumoreGenerico - Lrx;

		// SNR =linkBudgetRx-NOISE; // QUANDO SIGNL NOISE ARE IN DB POSSO FARE LA
		// SOTTRAZIONE WIKIPEDIA SIGNA_TO_NOISE_RATIO
		SNR = linkBudgetRx;
		eb_su_no = (SNR * 2) / -10000.0;
		// SNR=0.077/SNR;
		System.out.println("EB_NO " + eb_su_no);
		System.out.println("SNR1 " + linkBudgetRx);

		BER = Erf.erf(Math.sqrt(eb_su_no)) * 0.5;
		if (Double.isNaN(BER)) {
			BER = 0;
		}

		// double bitRate=8000*300;

		// double SQRT_EB_NO = Erf.erfcInv(SNR);
		// BER = Erf.erfc(SQRT_EB_NO) * 0.5;

		System.out.println("SNR " + SNR);
		System.out.println("Rx " + linkBudgetRx);
		System.out.println("noise " + rumoreGenerico);

		System.out.println("ber:" + BER);
		System.out.println("d:" + distanza);

	}// calcolaBER

	public static void statistiche() {
		System.out.println("puliti:" + inalterati + " sporchi : " + alterati);
		System.out.println(
				"BIT ERROR RATE " + BER * 100 + "% VERIFICA " + (alterati / (inalterati + alterati)) * 100 + " % ");
	}// statistiche

	public static BitSet simulaErrore(BitSet encodedPayload) {
		BitSet frameOriginario = (BitSet) encodedPayload.clone();// deepCopy
		for (int i = 0; i < encodedPayload.length(); i++) {
			double r = Math.random();
			if (r <= BER) {
				// simula errore nel bit
				alterati++;
				encodedPayload.flip(i);
			} else {
				inalterati++;
			}
		}
		if (encodedPayload.equals(frameOriginario) && BER == 0) {
			System.out.println("errore!");
			System.out.println("il Bitset originario" + frameOriginario);
			System.out.println("il Bitset alterato" + encodedPayload);
		}
		return encodedPayload;
	}// simula_errore

	public static void main(String[] Args) {
		calcolaBER();
	}// main

}// Canale3g
