package codificatori_conv_rip;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class ThreadDecodificatoreViterbi implements Runnable{

	
	private int inizioRange, fineRange;
	private byte[] message;
	private CountDownLatch latch;
	private byte[] result_dec;

	public ThreadDecodificatoreViterbi(int inizio, int fine, byte[] message, CountDownLatch latch) {
		// TODO Auto-generated constructor stub
		this.inizioRange = inizio;
		this.fineRange = fine;
		this.message = message;
		this.latch = latch;
	}
	
	private class Path{
		byte [] ultimoStato;
		int pathMetric;
		byte [] input;
		
		public Path() {
			input = new byte[(fineRange - inizioRange)/2];
			ultimoStato = new byte[3];
		}

		public byte[] getUltimoStato() {
			return ultimoStato;
		}

		public void setInput(byte[] input) {
			this.input = input;
		}

		public void setUltimoStato(byte[] ultimoStato) {
			this.ultimoStato = ultimoStato;
		}

		public int getPathMetric() {
			return pathMetric;
		}

		public void setPathMetric(int pathMetric) {
			this.pathMetric = pathMetric;
		}

		public byte[] getInput() {
			return input;
		}
		
		public String toString() {
			return "{ "+Arrays.toString(ultimoStato)+" input="+Arrays.toString(input)+" "+pathMetric+" }\n";
		}

	}
	
	private List<Path> paths = new LinkedList<>();
	
	@Override
	public void run() {
		int k = 0;
		Path path_iniziale = new Path();
		path_iniziale.setPathMetric(0);
		byte [] stato = {0,0,0};
		path_iniziale.setUltimoStato(stato);
		paths.add(path_iniziale);
		
		Map<String, Integer> valoreStati = new HashMap<>();
		byte [] val_stato = {0,0,0};
		valoreStati.put(Arrays.toString(val_stato),0);
		for(int i = inizioRange; i<fineRange;i+=2) {
			byte [] bitRicevuti = {message[i],message[i+1]};
			int lenght = paths.size();
			for(int j = 0; j < lenght; j++) {
				Path path = paths.get(0);
				//System.out.println(path);
				byte [] ultimoStato = path.getUltimoStato();
				byte [] nuovoStato1 = shift_register(ultimoStato, (byte)0);
				byte [] nuovoStato2 = shift_register(ultimoStato, (byte)1);
				
				int risultato11 = nuovoStato1[0] ^ nuovoStato1[1] ^ nuovoStato1[2];
				int risultato12 = nuovoStato1[0] ^ nuovoStato1[1];

				int risultato21 = nuovoStato2[0] ^ nuovoStato2[1] ^ nuovoStato2[2];
				int risultato22 = nuovoStato2[0] ^ nuovoStato2[1];
				
				int distHamming = calcolaDistanzaHamming(risultato11, risultato12, bitRicevuti);
				int distHamming2 = calcolaDistanzaHamming(risultato21, risultato22, bitRicevuti);
				
				int pathMetric = 0;//path.getPathMetric();
				String nuovoStato1s = Arrays.toString(nuovoStato1);
				if(valoreStati.containsKey(nuovoStato1s) && k<3) {
					pathMetric = valoreStati.get(nuovoStato1s);
					
				}
				else {
					pathMetric = path.getPathMetric();
				}
				valoreStati.put(nuovoStato1s, pathMetric+distHamming);
				
				Path nuovoPath1 = new Path();
				nuovoPath1.setUltimoStato(nuovoStato1);
				
				byte [] input = path.getInput();
				
				byte [] input1 = Arrays.copyOf(input, input.length);//path.getInput();
				input1[k] = 0;
				nuovoPath1.setInput(input1);
				nuovoPath1.setPathMetric(pathMetric+distHamming);
				//System.out.println("Generato "+nuovoPath1);
				int pathMetric2 = 0;//path.getPathMetric();
				String nuovoStato2s = Arrays.toString(nuovoStato2);
				if(valoreStati.containsKey(nuovoStato2s) && k<3) {
					pathMetric2 = valoreStati.get(nuovoStato2s);
					
				}
				else {
					pathMetric2 = path.getPathMetric();
				}
				valoreStati.put(nuovoStato2s, pathMetric2+distHamming2);
				
				Path nuovoPath2 = new Path();
				nuovoPath2.setUltimoStato(nuovoStato2);
				byte [] input2 = Arrays.copyOf(input, input.length);//path.getInput();
				input2[k] = 1;
				nuovoPath2.setInput(input2);
				nuovoPath2.setPathMetric(pathMetric2+distHamming2);
				//System.out.println("Generato "+nuovoPath2);
				paths.add(nuovoPath2);				
				paths.add(nuovoPath1);
				paths.remove(path);
				
				
			}
			//System.out.println("**************");
			for(int j = 0;j<paths.size();j++) {
				Path path1 = paths.get(j);
				for(int t = j+1; t<paths.size();t++) {
					
					Path path2 = paths.get(t);
					if(Arrays.equals(path1.getUltimoStato(), path2.getUltimoStato())) {
						//System.out.println("Elminiazione tra "+Arrays.toString(path1.getUltimoStato())+" e "+Arrays.toString(path2.getUltimoStato()));
						//System.out.println(path1.getPathMetric()+" "+path2.getPathMetric());
						if(path1.getPathMetric() > path2.getPathMetric()) {
							paths.remove(path2);
							j--;
						}
						else {
							paths.remove(path1);
							j--;
						}
					}
				}
			}
			//System.out.println(paths);
			
			k++;
		}
		
		Path pathMigliore = null;
		int bestPathMetric = Integer.MAX_VALUE;
		for (Path path : paths) {
			if(path.getPathMetric() < bestPathMetric) {
				pathMigliore = path;
				bestPathMetric = path.getPathMetric();
			}
		}
		
		result_dec = pathMigliore.getInput();
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
