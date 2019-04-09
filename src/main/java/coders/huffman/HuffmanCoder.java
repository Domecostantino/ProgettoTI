package coders.huffman;

import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

import coders.Coder;
import coders.Message;

/*
 * per comprendere di cosa si parla
 * 	https://www.geeksforgeeks.org/greedy-algorithms-set-3-huffman-coding/
 * 	https://it.wikipedia.org/wiki/Codifica_di_Huffman
 * 
 * Funziona con testi di almeno 2 caratteri
 */

public class HuffmanCoder implements Coder {
	// strutture utili a contare le occorrenze delle varie parole e a
	// ordinare le coppie (symbolo,occorrenze) in maniera veloce
	private HashMap<Character, Coppia> frequencies = new HashMap<>();
	// in questo caso usiamo la classe Entry per salvare (symbolo, codifica)
	private PriorityQueue<Entry> codeTable;
	private HuffmanNode root = null;

	@Override
	public Message encode(String input) {
		//TODO eliminare
		System.out.println("Input: "+input+"\n\n");

		// calcola le frequenze
		computeOccurencies(input);
		// crea l'albero date le frequenza, ottenendo la codeTable
		createHuffmanTree();
		// calcola la codifica canonica partendo da quella normale usata per codificare
		// il payload
		HashMap<Character, String> canonicalCodeTable = CanonicalCode.getCanonicalCodeTable(codeTable);
		// ricavare la tabella contenente la lunghezza delle codifiche dei vari simboli
		// da inviare come header e usata nella decodifica
		HashMap<Character, Integer> lengthTable = getLengthTable(canonicalCodeTable);

		//TODO eliminare
		System.out.println();
		System.out.println("lengthtable: "+lengthTable);

		// TODO creare header (lengthTable) e payload del messaggio
		Message mex = new Message();
		mex.setHeader(lengthTable);
		return mex;
	}

	@Override
	public String decode(Message input) {

		// 1. leggere header (canonicalCodeTable)

		// 2. <creare l'albero>

		// 3. decodificare

		// 4. ritornare output

		return null;
	}

	private void computeOccurencies(String input) {
		// lettura del'input e creazione delle coppie <lettera, occorrenze>
		int i = 0;
		int inputLength = input.length();
		while (i < inputLength) {
			char current = input.charAt(i);
			if (frequencies.containsKey(current))
				frequencies.get(current).addOccurence();
			else
				frequencies.put(current, new Coppia(current));
			i++;
		}
	}

	private void createHuffmanTree() {
		// ordinamento delle coppie <parola, occorrenze> in base alle occorenze

		// creiamo la coda prioritaria che confronta il numero di occorrenze
		PriorityQueue<HuffmanNode> coppie = new PriorityQueue<>(Comparator.comparing(HuffmanNode::getOccurrences));
		for (Coppia coppia : frequencies.values()) {
			coppie.add(new HuffmanNode(coppia));
		}
		
		//TODO eliminare
		System.out.println("Frequenze: "+frequencies+"\n");
		
		// creazione dell'albero - iterativamente estraiamo i due valori pi� piccoli e
		// creaimo un
		// nodo "interno" finche' la cardinalita' della coda e' pari a 1
		while (coppie.size() > 1) {
			// estrazione due minori
			HuffmanNode x = coppie.poll();
			HuffmanNode y = coppie.poll();

			// creazione nuovo nodo
			HuffmanNode newNode = new HuffmanNode();
			newNode.occurrences = x.occurrences + y.occurrences;
			newNode.symbol = '¶'; // simbolo non-ASCII

			// creazione dei collegamenti con i nodi genitori
			newNode.left = x;
			newNode.right = y;
			root = newNode;

			coppie.add(newNode);
		}

		// istanziamo la nostra codeTable
		codeTable = new PriorityQueue<>();
		// navigazione dell'albero per ottenere i codici
		if(root!=null)
			getCode(root, "");

		//TODO eliminare
		System.out.println("Codifica di Huffman normale:");
		PriorityQueue<Entry> codeTableCopy = new PriorityQueue<HuffmanCoder.Entry>(codeTable);
		while (!codeTableCopy.isEmpty())
			System.out.println(codeTableCopy.poll());

	}

	// metodo ricorsivo che popola la codeTable
	private void getCode(HuffmanNode root, String s) {
		if (root.left == null && root.right == null) {
			codeTable.add(new Entry(root.symbol, s));
			return;
		}
		getCode(root.left, s + "0");
		getCode(root.right, s + "1");
	}

	/*
	 * Data la canonicalCodeTable crea una mappa di (simbolo, lunghezza della
	 * codifica) utile al decodificatore
	 */
	private HashMap<Character, Integer> getLengthTable(HashMap<Character, String> canonicalCodeTable) {
		if(canonicalCodeTable==null)
			return null;
		HashMap<Character, Integer> lengthTable = new HashMap<>(canonicalCodeTable.size());
		for (java.util.Map.Entry<Character, String> entry : canonicalCodeTable.entrySet()) {
			lengthTable.put(entry.getKey(), entry.getValue().length());
		}
		return lengthTable;
	}

	/*
	 * 
	 * Di test per questa codifica
	 */
	public static void main(String[] args) {
		Coder c = new HuffmanCoder();
		c.encode("ewrr");
	}

	private class Coppia {
		int count = 1;
		char symbol;

		public Coppia(char symbol) {
			this.symbol = symbol;
		}

		public void addOccurence() {
			count++;
		}

		public String toString() {
			return "[" + symbol + "," + count + "]";
		}

	}

	protected class Entry implements Comparable<Entry> {
		String code = "";
		Character symbol;

		public Entry(char symbol, String code) {
			this.symbol = symbol;
			this.code = code;
		}

		// permette di ordinare le entry prima in base alla lunghezza (crescente) della
		// codifica e in caso di lunghezza uguale lessicograficamente sui simboli
		@Override
		public int compareTo(Entry e1) {
			if (code.length() < e1.code.length())
				return -1;
			else if (code.length() > e1.code.length())
				return 1;
			else
				return symbol.compareTo(e1.symbol);
		}

		public String toString() {
			return "[" + symbol + "," + code + "]";
		}
	}

	private class HuffmanNode {
		int occurrences;
		char symbol;
		HuffmanNode left = null;
		HuffmanNode right = null;

		public HuffmanNode() {
		}

		public HuffmanNode(Coppia coppia) {
			occurrences = coppia.count;
			symbol = coppia.symbol;
		}

		public int getOccurrences() {
			return occurrences;
		}

		public String toString() {
			return "[" + symbol + "," + occurrences + "]";
		}
	}
}
