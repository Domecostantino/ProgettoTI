package codificatori_tmp;


import java.util.HashMap;
import java.util.LinkedList;

public class Nodo {

	private HashMap<IndiceSuccessore, boolean []> successori;
	//private boolean[][] successori;
	private boolean[] stato;
	//private boolean[][] output;
	private LinkedList<BranchMetric> metriche;
	//private boolean[] input;
	private boolean[][] g;
	//private int numeroArchi;
	int n, N, k;
	int lunghezzaCammino;
	private BranchMetric bm;
	
	public Nodo(int N, int n, boolean[] stato, boolean[][] g, int k) {
		this.N = N;
		this.n= n;
		this.k = k;
		this.stato = stato;
		this.g = g;
		//numeroArchi = 2;

		successori = new HashMap<>();
		generatoreCombinazioniNodiSuccessivi();
		successori();
			
		metriche = new LinkedList<>();
		lunghezzaCammino = 0;
		bm = new BranchMetric();
	}//costruttore
	
	
	//determina per ciascun successore la corrispondente memoria
	public HashMap successori() {
		for(HashMap.Entry<IndiceSuccessore, boolean[]> entry: successori.entrySet()){
			boolean[] nuovoStato = new boolean[(N-1)*k];
			
			//effettuo lo shift di k posizioni della memoria
			int x = k;
			for (int i = 0; i <= stato.length-1-k; i++) {
				nuovoStato[x] = stato[i];
				x++;
			}
						
			//aggiungo i k input in testa alla memoria
			int len = entry.getKey().getIndice().length;
			for(int j = 0; j< len; j++)
				nuovoStato[j] = entry.getKey().getIndice()[j];
			
			//aggiorno i bit del successore
			successori.replace(entry.getKey(), nuovoStato);
			
		}//for
		return successori;
	}//creaSuccessori 
	
	/*public boolean[] calcolaOutput(boolean input) {
		boolean[] risultato = new boolean[n];
		for(int i=0; i<g.length; i++) {
			risultato[i] = g[i][0] && input ? true : false;
			for(int j=0; j<g[i].length-1; j++)
				if(g[i][1+j]) 
					risultato[i] = risultato[i] ^ stato[j];					
		}//for
		return risultato;
	}//calcolaOutput*/
	
	//calcola la codifica dei bit in input
	public boolean[] calcolaOutput(boolean [] input) {
		boolean[] risultato = new boolean[n];
		for(int i = 0; i<g.length; i++){
			risultato[i] = g[i][0] && input[0]? true: false;
			/**
			 * Spiegazione for successivo: 
			 * Il vettore <input> è associato a ciascun sommatore, di conseguenza
			 * non è necessario verificare i corrisponderti flag se sono true o false
			 */
			for(int j = 1; j < input.length; j++)
				risultato[i] = risultato[i] ^ input[j];
			/**
			 *Itero sui flag dei generatori 
			 */
			
			for(int j=0; j<stato.length; j++){
				int indice = k+j;
				if(g[i][k+j]) 
					risultato[i] = risultato[i] ^ stato[j];
			}
		}
		return risultato;
	}
	
	public boolean[] getOutput(boolean [] input) { return calcolaOutput(input); }
	
	public boolean[] getSuccessore(boolean [] input) {
		IndiceSuccessore indice = new IndiceSuccessore(input);
		return successori.get(indice);
	}
	
	public HashMap<IndiceSuccessore,boolean[]> getSuccessori() { return successori; }
	
	public boolean[] getStato() { return stato; }
	
	public BranchMetric getBranchMetric() { return bm; }
	
	public void addBranchMetric(BranchMetric bm) {
		metriche.add(bm);
	}
	
	public void setLunghezzaCammino(int lunghezza) { lunghezzaCammino = lunghezza; }
	
	public int getLunghezzaCammino() { return lunghezzaCammino; }
	
	public boolean equals(Object o) {
		Nodo n = (Nodo) o;
		for(int  i=0; i<stato.length; i++)
			if(stato[i] != n.stato[i])
				return false;
		return true;
	}
	
	public void calcolaMetriche(Nodo precedente, boolean[] input, boolean[] output, boolean [] decodifica, int distanzaIniziale) {
		bm.addMetrica(precedente, input, output, decodifica, distanzaIniziale);
	}
	
	class BranchMetric {
		
		private LinkedList<Integer> distanza;
		private LinkedList<Nodo> precedenti;
		private LinkedList<boolean []> decodifica;
		
		public BranchMetric() {
			precedenti = new LinkedList<>();
			distanza = new LinkedList<>();
			decodifica = new LinkedList<>();
			
		}
		
		public void addMetrica(Nodo precedente, boolean[] input, boolean[] output, boolean[] decodifica, int distanzaIniziale) {
			precedenti.add(precedente);
			this.decodifica.add(decodifica);
			this.distanza.add(calcolaDistanza(input, output, distanzaIniziale));
		}
		
		public void impostaMetricaMinima() {
			int i = 0;
			Nodo precedente = precedenti.get(i); 
			int distanzaMinima = distanza.get(i);
			boolean [] decodificaRisultante = decodifica.get(i++);
			
			/*System.out.println("Nodo :");
			System.out.println("Distanza minima"+distanzaMinima);
			System.out.println("decodifica : ");
			for (int x = 0; x < decodificaRisultante.length; x++) {
				System.out.print(decodificaRisultante[x]+" ");
			}
			System.out.println();
			*/
			
			for(; i<precedenti.size(); i++) {
				if(distanza.get(i)<distanzaMinima) {
					precedente = precedenti.get(i); 
					distanzaMinima = distanza.get(i);
					decodificaRisultante = decodifica.get(i);
				}//if
			}
			precedenti.clear();
			distanza.clear();
			decodifica.clear();
			precedenti.add(precedente);
			distanza.add(distanzaMinima);
			decodifica.add(decodificaRisultante);	
			
			/*System.out.println("precedenti : "+ precedenti.size());
			System.out.println("distanza : ");
			for (Iterator iterator = distanza.iterator(); iterator.hasNext();) {
				int dist = (int) iterator.next();
				System.out.print(dist+" ");
			}
			System.out.println();
			System.out.println("decodifica : ");
			for (Iterator iterator = decodifica.iterator(); iterator.hasNext();) {
				boolean[] dec = (boolean[]) iterator.next();
				for (int x = 0; x < dec.length; x++) {
					System.out.print(dec[x]+" ");
				}
				System.out.println();
			}
			
			System.out.println("-----------------------");*/
		}//impostaMetricaMinima
		
		private int calcolaDistanza(boolean[] input, boolean[] output, int distanza) {
			int d = distanza;
			for(int i=0; i<input.length; i++)
				if (input[i] != output[i])
					d++;
			return d;
		}
		
		//lascia i valori da ritornare in testa alle varie liste, per poi svuotarle
		public int getDistanza() { return distanza.getFirst(); }
		
		public Nodo getPrecedente() { return precedenti.getFirst(); }   //metti remove
		
		public boolean [] getDecodifica() { return decodifica.getFirst(); }
		
		public void clear() {
			precedenti.clear();
			distanza.clear();
			decodifica.clear();
		}
		
	}//BranchMetric
	
	private HashMap generatoreCombinazioniNodiSuccessivi(){
		//Ogni nodo ha 2^k successori, ciascuno composto da k bit
		//il seguente ciclo opera fino al raggiungimento delle 2^k possibili combinazioni realizzabili
		boolean[] tmp = new boolean[k];
		
		//inserisco la configurazione iniziale, ovvero tutto false
		IndiceSuccessore indice = new IndiceSuccessore(tmp);
		boolean [] tmp1 = new boolean[(N-1)*k];
		successori.put(indice, tmp1);
		
		boolean operation = true;
		
		HashMap<IndiceSuccessore, boolean[]> mapTMP ;
		
		while(operation){
			operation = false;
			mapTMP = new HashMap<>();
			//itero sulle combinazioni calcolate fino al passo corrente
			for(HashMap.Entry<IndiceSuccessore, boolean[]> entry: successori.entrySet()){
				
				boolean [] key = entry.getKey().getIndice();
				
				boolean [] shiftTrue = new boolean[key.length]; //shift di una posizione e aggiunta di True in prima posizione
				boolean [] shiftFalse = new boolean[key.length];//shift di una posizione e aggiunta di False in prima posizione
				System.arraycopy(key, 0, shiftFalse, 1, key.length-1);
				System.arraycopy(key, 0, shiftTrue, 1, key.length-1);
				
				//aggiungo true e false in prima posizione ai relativi registri
				shiftTrue[0] = true;
				shiftFalse[0] = false;
				
				//creo i corrispettivi indici per l'inserimento della mappa
				IndiceSuccessore i1 = new IndiceSuccessore(shiftFalse);
				IndiceSuccessore i2 = new IndiceSuccessore(shiftTrue);

				//i due vettori vengono inseriti all'interno della mappa se non presenti in essa
				if(!successori.containsKey(i1)){
					operation = true;
					mapTMP.put(i1, null);
				}
				
				if(!successori.containsKey(i2)){
					operation = true;
					mapTMP.put(i2, null);
				}
				
			}
			
			//copio gli elementi trovati
			if(operation){
				for(HashMap.Entry<IndiceSuccessore, boolean[]> entry: mapTMP.entrySet())
						successori.put(entry.getKey(), entry.getValue());
			}
		}
		return successori;
	}
	
	class IndiceSuccessore { 
		
		private boolean [] indice;
		
		public IndiceSuccessore(boolean [] indice){
			this.indice = indice;
		}
		
		public boolean [] getIndice(){
			return this.indice;
		}
		
		@Override
		public boolean equals(Object o) {
			
			IndiceSuccessore indice = (IndiceSuccessore) o;
			if(this.indice.length != indice.getIndice().length)
				return false;
			boolean [] indiceCorrente = indice.getIndice();
			for (int i = 0; i < indiceCorrente.length; i++) {
				if((this.indice[i] && !indiceCorrente[i]) || (!this.indice[i] && indiceCorrente[i]))
					return false;
			}
			return true;
		}
		
		@Override
		public int hashCode() {
			int somma = 0;
			int potenza = 0;
			for (int i = 0; i < indice.length; i++) {
				if(indice[i])
					somma+=Math.pow(2, potenza);
				potenza++;
			}
			return somma;
		}
		
		public String toString(){
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < indice.length; i++) {
				sb.append(indice[i]+"-");
			}
			sb.append("\n");
			return sb.toString();
		}
		
	}
	

	public static void main(String [] args){
		boolean [][] g = {{true,false,false},{true,true,false},{true,true,true}};
		boolean [] mem = {true,true,false,false};
		Nodo nodo = new Nodo(3, 3, mem, g, 2);
		boolean[] input = {false,false};
		
		boolean[] b = nodo.getSuccessore(input);
		System.out.println();
		for (int i = 0; i < b.length; i++) {
			System.out.print(b[i]);
		}
		System.out.println();
	}
	
}//Nodo
