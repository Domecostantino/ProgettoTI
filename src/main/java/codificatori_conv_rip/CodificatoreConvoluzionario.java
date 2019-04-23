package codificatori_conv_rip;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class CodificatoreConvoluzionario{

	private final int NUMERO_THREAD = 1;
	private int LENGTH_SHIFT_REGISTER;
	
	private byte[] polinomio1,polinomio2;
		
        public CodificatoreConvoluzionario(int length){
            LENGTH_SHIFT_REGISTER=length;
        }
        
	public byte[] codifica(byte[] msg) throws InterruptedException {
		System.out.println("***************** INIZIO CODIFICA **************************");
		
		byte [] bits_msg = new byte[msg.length*8];
		int indice=0;
		for (int i = 0; i < msg.length; i++) {
			String bits = String.format("%8s", Integer.toBinaryString(msg[i] & 0xFF)).replace(' ', '0');
			for(int j = 0; j<bits.length(); j++){
				bits_msg[indice++]= Byte.parseByte("" + bits.charAt(j));
			}			
		}
		//System.out.println(Arrays.toString(bits_msg));
		ThreadCodificatore[] pool = new ThreadCodificatore[NUMERO_THREAD];
		int range = bits_msg.length / NUMERO_THREAD;
		int resto = bits_msg.length % NUMERO_THREAD;
		int inizioRange = 0;
		CountDownLatch latch = new CountDownLatch(NUMERO_THREAD);
		
		for (int i = 0; i < NUMERO_THREAD; i++) {
			if (i == NUMERO_THREAD - 1 && resto > 0) {
				ThreadCodificatore tc = new ThreadCodificatore(LENGTH_SHIFT_REGISTER,bits_msg, inizioRange, inizioRange + range + resto, latch);
				Thread t1 = new Thread(tc);
				pool[i] = tc;
				t1.start();
				inizioRange += range;
			} else {
				ThreadCodificatore tc = new ThreadCodificatore(LENGTH_SHIFT_REGISTER,bits_msg, inizioRange, inizioRange + range, latch);
				Thread t1 = new Thread(tc);
				t1.start();
				inizioRange += range;
				pool[i] = tc;
			}
		}

		latch.await();
		
		byte [] result = new byte[bits_msg.length*2];
		int k = 0;
		for (int i = 0; i < pool.length; i++) {
			byte [] result_thread = pool[i].getResult();
			for (int j = 0; j < result_thread.length; j++) {
				result[k] = result_thread[j];
				k++;
			}
		}
		
		byte [] codifica = convertiInByte(result);
		
		System.out.println("***************** FINE CODIFICA **************************");
		return codifica;
	}	

	public byte[] convertiInByte(byte[] messaggio) {
		byte[] xy = new byte[messaggio.length / 8];

		int k = 0;
		for (int i = 0; i < 8 * xy.length; i += 8) {
			String bits = "";
			for (int j = 0; j < 8; j++) {
				bits += Byte.toString(messaggio[j + i]);
			}

			byte x = 0;
			for (int j = 0; j < 8; j++) {
				x += Math.pow(2, j) * (bits.charAt(7 - j) - 48);
			}
			xy[k] = x;
			k++;
		}

		return xy;
	}
	
	
	private class Node{
		
		byte [] ultimoStato;
		byte [] input;
		int pathMetric;
		
		public byte[] getInput() {
			return input;
		}

		public void setInput(byte[] input) {
			this.input = input;
		}

		public byte[] getUltimoStato() {
			return ultimoStato;
		}
		public void setUltimoStato(byte[] predecessori) {
			this.ultimoStato = predecessori;
		}
		
		public int getPathMetric() {
			return pathMetric;
		}
		public void setPathMetric(int pathMetric) {
			this.pathMetric = pathMetric;
		}
		
		public String toString() {
			String s = "Ultimo stato: "+Arrays.toString(ultimoStato)+"\n";
			s += " PM = "+pathMetric+" input:"+Arrays.toString(input)+"\n";
			return s;
		}
		
		
	}
	
	public byte [] decodificaViterbi(byte [] msg) {
		switch (LENGTH_SHIFT_REGISTER) {
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
		
		System.out.println("inizio codifica...");
		// Conversione del messaggio da byte a bit
		System.out.println("Inizio conversione in bit....");
		byte [] bits_msg = new byte[msg.length*8];
		int indice=0;
		for (int i = 0; i < msg.length; i++) {
			String bits = String.format("%8s", Integer.toBinaryString(msg[i] & 0xFF)).replace(' ', '0');
			for(int j = 0; j<bits.length(); j++){
				bits_msg[indice++]= Byte.parseByte("" + bits.charAt(j));
			}			
		}
		System.out.println("Fine conversione in bit");
		
		List<Node> paths = new LinkedList<>();
		Node inizio = new Node();
		inizio.setPathMetric(0);
		
		byte [] statoIniziale = new byte[LENGTH_SHIFT_REGISTER];
		byte [] input_stato = new byte[bits_msg.length/2];
		inizio.setUltimoStato(statoIniziale);
		inizio.setInput(input_stato);
		paths.add(inizio);
		
		int k = 0;
		int r = 0;
		byte [] output = new byte[bits_msg.length/2];
		int bitLetti = 0;
		for (int i = 0; i < bits_msg.length; i+=2) {
			byte [] bit_ricevuti = {bits_msg[i],bits_msg[i+1]};
			bitLetti++;
			int lenght = paths.size();
			//System.out.println("i = "+i+" bit ricevuti: "+Arrays.toString(bit_ricevuti));
			for(int j = 0;j<lenght;j++){
				Node nodoCorrente = paths.get(0);
				byte [] ultimoStato = nodoCorrente.getUltimoStato();
				
				byte [] prossimoStato1 = shift_register(ultimoStato, (byte)0);
				byte [] prossimoStato2 = shift_register(ultimoStato, (byte)1);
				
				int risultato11 = 0;
				int risultato12 = 0;
				for (int t = 0; t < prossimoStato1.length; t++) {

					risultato11 = risultato11 ^ (prossimoStato1[t] * polinomio1[t]);
					risultato12 = risultato12 ^ (prossimoStato1[t] * polinomio2[t]);

				}

				int risultato21 = 0;
				int risultato22 = 0;
				for (int t = 0; t < prossimoStato2.length; t++) {

					risultato21 = risultato21 ^ (prossimoStato2[t] * polinomio1[t]);
					risultato22 = risultato22 ^ (prossimoStato2[t] * polinomio2[t]);

				}
				
				int distHamming1 = calcolaDistanzaHamming(risultato11, risultato12, bit_ricevuti);
				int distHamming2 = calcolaDistanzaHamming(risultato21, risultato22, bit_ricevuti);
				
				byte [] input = nodoCorrente.getInput();
				byte [] input1 = copiaArray(input);
				input1[k] = 0;
				byte [] input2 = copiaArray(input);
				input2[k] = 1;
				
				int pathMetric = nodoCorrente.getPathMetric();
				
				Node nuovoPath = new Node();
				nuovoPath.setUltimoStato(prossimoStato1);
				nuovoPath.setInput(input1);
				nuovoPath.setPathMetric(pathMetric + distHamming1);
					
				Node nuovoPath2 = new Node();
				nuovoPath2.setInput(input2);
				nuovoPath2.setPathMetric(pathMetric + distHamming2);
				nuovoPath2.setUltimoStato(prossimoStato2);
				
				
				paths.add(nuovoPath);
				paths.add(nuovoPath2);
				paths.remove(nodoCorrente);
				
			}
			int j = 0;
			while(j<paths.size()) {
				Node path = paths.get(j);
				int t = j+1;
				while(t<paths.size()) {
					Node path2 = paths.get(t);
					byte [] ultimoStato1 = path.getUltimoStato();
					byte [] ultimoStato2 = path2.getUltimoStato();
					if (arrayUguali(ultimoStato1,ultimoStato2)) {
						int val1 = path.getPathMetric();
						int val2 = path2.getPathMetric();
						if (val1 > val2) {
							paths.remove(path2);
							j--;
						} else {
							paths.remove(path);
							j--;
						}
					}
					t++;
				}
				j++;
			}
			
			k++;
			/**
			if(bitLetti == 8) {
				Node bestPath = null;
				int pathMetric = Integer.MIN_VALUE;
				for (Node path : paths) {
					int PM = path.getPathMetric();
					if(PM > pathMetric) {
						pathMetric = PM;
						bestPath = path;
					}
				}
				
				byte [] inputParziale = bestPath.getInput();
				
				for(int s = 0; s < 8; s++) {
					output[r+s] = inputParziale[s];
				}
				r += 8;
				
				bitLetti = 0;
				paths = new LinkedList<>();
				inizio = new Node();
				inizio.setPathMetric(0);
				
				statoIniziale = new byte[LENGTH_SHIFT_REGISTER];
				input_stato = new byte[8];
				inizio.setUltimoStato(statoIniziale);
				inizio.setInput(input_stato);
				paths.add(inizio);
				
				k = 0;
			}
			*/
		}
		
		Node bestPath = null;
		int pathMetric = Integer.MIN_VALUE;
		for (Node path : paths) {
			int PM = path.getPathMetric();
			if(PM > pathMetric) {
				pathMetric = PM;
				bestPath = path;
			}
		}
		
		byte [] decodifica = convertiInByte(bestPath.getInput());
		
		return decodifica;
	}
	
	private boolean arrayUguali(byte[] ultimoStato1, byte[] ultimoStato2) {
		// TODO Auto-generated method stub
		for (int i = 0; i < ultimoStato2.length; i++) {
			if(ultimoStato1[i] != ultimoStato2[i]) {
				return false;
			}
		}
		return true;
	}

	private byte [] copiaArray(byte[] input) {
		byte [] dest = new byte[input.length];
		for (int i = 0; i < dest.length; i++) {
			dest[i] = input[i];
		}
		return dest;
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
	
	private byte [] shift_register(byte [] stato,byte input) {
		// TODO Auto-generated method stub
		byte [] nuovoStato = new byte[stato.length];
		for(int i = stato.length-1; i>0; i--) {
			nuovoStato[i] = stato[i-1];
		}
		nuovoStato[0] = input;
		return nuovoStato;
	}
	
	public byte [] decodificaSequenziale(byte [] msg) throws InterruptedException {		
		System.out.println("***************** INIZIO DECODIFICA **************************");	
		System.out.println("Inizio conversione in bit....");
		byte [] bits_msg = new byte[msg.length*8];
		int indice=0;
		for (int i = 0; i < msg.length; i++) {
			String bits = String.format("%8s", Integer.toBinaryString(msg[i] & 0xFF)).replace(' ', '0');
			for(int j = 0; j<bits.length(); j++){
				bits_msg[indice++]= Byte.parseByte("" + bits.charAt(j));
			}			
		}
		System.out.println("Fine conversione in bit");
		
		
		//ThreadDecodificatoreViterbi[] pool = new ThreadDecodificatoreViterbi[NUMERO_THREAD];
		ThreadDecodificatore[] pool = new ThreadDecodificatore[NUMERO_THREAD];
		int range = bits_msg.length / NUMERO_THREAD;
		int resto = bits_msg.length % NUMERO_THREAD;
		int inizioRange = 0;
		CountDownLatch latch = new CountDownLatch(NUMERO_THREAD);
		
		for (int i = 0; i < NUMERO_THREAD; i++) {
			if (i == NUMERO_THREAD - 1 && resto > 0) {
			    ThreadDecodificatore tc = new ThreadDecodificatore(LENGTH_SHIFT_REGISTER, inizioRange, inizioRange + range + resto,bits_msg, latch);
				//ThreadDecodificatoreViterbi tc = new ThreadDecodificatoreViterbi( inizioRange, inizioRange + range + resto,message, latch);
				Thread t1 = new Thread(tc);
				pool[i] = tc;
				t1.start();
				inizioRange += range;
			} else {
				ThreadDecodificatore tc = new ThreadDecodificatore(LENGTH_SHIFT_REGISTER,inizioRange, inizioRange + range, bits_msg, latch);
				//ThreadDecodificatoreViterbi tc = new ThreadDecodificatoreViterbi( inizioRange, inizioRange + range,message, latch);
				Thread t1 = new Thread(tc);
				t1.start();
				inizioRange += range;
				pool[i] = tc;
			}
		}

		latch.await();
		
		byte [] decodifica = new byte[bits_msg.length/2];
		int k = 0;
		for (int i = 0; i < pool.length; i++) {
			byte [] result_thread = pool[i].getResult();
			for (int j = 0; j < result_thread.length; j++) {
				decodifica[k] = result_thread[j];
				k++;
			}
		}
		byte [] result = convertiInByte(decodifica);
		System.out.println("***************** FINE DECODIFICA **************************");	
		return result;
	}
	
	public static void main(String[] args) throws InterruptedException, IOException {
		Path path = Paths.get("//home//pietro//Desktop//fischio_uomo.mp3");

		
		CodificatoreConvoluzionario cc = new CodificatoreConvoluzionario(6);
		byte [] msg = {15,16,16,18,19,20};
		//byte [] msg = new byte [5_000];
		
		byte [] codifica = cc.codifica(msg);
		for (int i = 0; i < codifica.length; i++) {
			System.out.println(codifica[i]);
		}
		System.out.println(msg);
		System.out.println("****************");
		Instant start = Instant.now();
		
		byte [] bits_msg = new byte[codifica.length*8];
		int indice=0;
		for (int i = 0; i < codifica.length; i++) {
			String bits = String.format("%8s", Integer.toBinaryString(codifica[i] & 0xFF)).replace(' ', '0');
			for(int j = 0; j<bits.length(); j++){
				bits_msg[indice++]= Byte.parseByte("" + bits.charAt(j));
			}			
		}
		
		int count = 0;
		for (int i = 0; i < bits_msg.length; i++) {
			if(Math.random() < 0.03) {
				count++;
				bits_msg[i] = (byte)(1 - bits_msg[i]);
			}
		}
		
		System.out.println("Errori = "+count);
		byte [] alterata = cc.convertiInByte(bits_msg);
		
		int errori = 0;
		for (int i = 0; i < alterata.length; i++) {
			if(alterata[i] != codifica[i]) {
				errori++;
			}
		}
		System.out.println("Byte diversi = "+errori+"/"+alterata.length);
		byte [] decodifica = cc.decodificaSequenziale(alterata);
		//byte [] decodifica = cc.decodifica(alterata);
		Instant end = Instant.now();
		System.out.println(Duration.between(start, end).toMillis()+" ms.");
		System.out.println(Arrays.equals(decodifica, msg));
		System.out.println(Arrays.toString(decodifica));
		int errori2 = 0;
		for (int i = 0; i < decodifica.length; i++) {
			if(decodifica[i] != msg[i]) {
				errori2++;
			}
		}
		System.out.println("Byte diversi = "+errori2+"/"+decodifica.length);
	}

}
