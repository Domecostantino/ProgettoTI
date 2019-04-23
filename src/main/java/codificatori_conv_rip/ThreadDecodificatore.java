package codificatori_conv_rip;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ThreadDecodificatore implements Runnable {

	private int inizioRange, fineRange;
	private byte[] message;
	private int THRESHOLD = 1;
	private CountDownLatch latch;
	private byte[] result_dec;
	private int length_shift_register;

	private byte[] polinomio1, polinomio2;

	public ThreadDecodificatore(int lenght_shift_register, int inizio, int fine, byte[] message, CountDownLatch latch) {
		// TODO Auto-generated constructor stub
		this.inizioRange = inizio;
		this.fineRange = fine;
		this.message = message;
		this.latch = latch;
		this.length_shift_register = lenght_shift_register;

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

	@Override
	public void run() {
		System.out.println("Thread codificatore" + Thread.currentThread().getName() + " partito");
		// TODO Auto-generated method stub
		List<Biforcazione> biforcazioni = new LinkedList<>();
		int k = 0;// Indica il passo dell'algoritmo
		int countError = 0;
		byte[] ultimoStato = new byte[length_shift_register];
		byte[] result = new byte[(fineRange - inizioRange) / 2];
		int inizioByteCodifica = inizioRange;
		int i = inizioRange;
		boolean resetThreshold = false;
		while (i < fineRange) {

			if (i % 16 == 0 && !resetThreshold) {
				THRESHOLD = 1;
				countError = 0;
				biforcazioni = new LinkedList<>();
				ultimoStato = new byte[length_shift_register];
				inizioByteCodifica = i;
				resetThreshold = false;
			}

			if (resetThreshold) {
				resetThreshold = false;
			}

			byte[] bitRicevuti = { message[i], message[i + 1] };

			// System.out.println("i = "+i+" bitRicevuti = "+Arrays.toString(bitRicevuti)+"
			// THRESHOLD = "+THRESHOLD+" k = "+k+" countErr = "+countError);

			byte[] nuovoStato1 = shift_register(ultimoStato, (byte) 0);
			byte[] nuovoStato2 = shift_register(ultimoStato, (byte) 1);

			int risultato11 = 0;
			int risultato12 = 0;
			for (int j = 0; j < nuovoStato1.length; j++) {

				risultato11 = risultato11 ^ (nuovoStato1[j] * polinomio1[j]);
				risultato12 = risultato12 ^ (nuovoStato1[j] * polinomio2[j]);

			}

			int risultato21 = 0;
			int risultato22 = 0;
			for (int j = 0; j < nuovoStato2.length; j++) {

				risultato21 = risultato21 ^ (nuovoStato2[j] * polinomio1[j]);
				risultato22 = risultato22 ^ (nuovoStato2[j] * polinomio2[j]);

			}

			int distHamming = calcolaDistanzaHamming(risultato11, risultato12, bitRicevuti);
			int distHamming2 = calcolaDistanzaHamming(risultato21, risultato22, bitRicevuti);

			if (distHamming == 1 && distHamming2 == 1) {
				// Se entro qui significa che c'è un errore in tutte e due i possibli input
				countError++;
				if (countError >= THRESHOLD) {
					if (biforcazioni.size() == 0) {
						resetThreshold = true;
						countError = 0;
						biforcazioni = new LinkedList<>();
						THRESHOLD++;
						i = inizioByteCodifica;
						ultimoStato = new byte[length_shift_register];
						k = i / 2 - 1;
					} else {
						Biforcazione ultimaBiforcazione = biforcazioni.get(biforcazioni.size() - 1);
						int x = ultimaBiforcazione.getPassoBiforcazione() + 1;
						k = x - 1;

						i = 2 * k;
						ultimoStato = ultimaBiforcazione.getStatoNonPreso();
						countError = ultimaBiforcazione.getErroreBiforcazione() + 1;
						biforcazioni.remove(ultimaBiforcazione);
						result[k] = 1;

					}
				} else {
					Biforcazione biforcazione = new Biforcazione();
					biforcazione.setErroreBiforcazione(countError - 1);
					biforcazione.setPassoBiforcazione(k);
					biforcazione.setStatoNonPreso(nuovoStato2);
					biforcazioni.add(biforcazione);
					result[k] = 0;
					ultimoStato = nuovoStato1;
				}

			} else if (distHamming2 > distHamming) {
				// Prendo il percorso con il secondo stato
				ultimoStato = nuovoStato2;
				result[k] = 1;

			} else if (distHamming2 <= distHamming) {
				// path.add(nuovoStato1);
				ultimoStato = nuovoStato1;
				result[k] = 0;
			}

			k++;

			if (!resetThreshold) {
				i += 2;
			}

		}

		// System.out.println(Arrays.toString(result));
		result_dec = result;
		System.out.println("Fine decodifica thread decodificatore" + Thread.currentThread().getName());
		latch.countDown();
	}

	private byte[] shift_register(byte[] ultimoStato, byte input) {
		byte[] nuovoStato = new byte[ultimoStato.length];
		// TODO Auto-generated method stub
		for (int i = ultimoStato.length - 1; i > 0; i--) {
			nuovoStato[i] = ultimoStato[i - 1];
		}
		nuovoStato[0] = input;
		return nuovoStato;
	}

	private int calcolaDistanzaHamming(int risultato1, int risultato2, byte[] bitRicevuti) {
		// TODO Auto-generated method stub
		int count = 0;
		if (risultato1 == bitRicevuti[0])
			count++;
		if (risultato2 == bitRicevuti[1])
			count++;

		return count;
	}

	public byte[] getResult() {
		return result_dec;
	}

}
