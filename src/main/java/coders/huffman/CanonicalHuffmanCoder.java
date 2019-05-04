package coders.huffman;

import java.util.BitSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.TreeSet;

import coders.Message;
import utils.GenericUtils;
import utils.HuffmanUtils;

/*
 * per comprendere di cosa si parla
 * 	https://www.geeksforgeeks.org/greedy-algorithms-set-3-huffman-coding/
 * 	https://it.wikipedia.org/wiki/Codifica_di_Huffman
 * 
 * Funziona con testi di almeno 2 caratteri non vuoti			ASSERT
 *  
 * La codifica canonica differisce da quella normale per:
 * 
 * The normal Huffman coding algorithm assigns a variable length code to every
 * symbol in the alphabet. More frequently used symbols will be assigned a
 * shorter code. For example, suppose we have the following non-canonical
 * codebook:
 * 
 * A = 11 
 * B = 0 
 * C = 101 
 * D = 100 
 * 
 * Here the letter A has been assigned 2 bits, B 
 * has 1 bit, and C and D both have 3 bits. To make the code a canonical Huffman
 * code, the codes are renumbered. The bit lengths stay the same with the code
 * book being sorted first by codeword length and secondly by alphabetical
 * value:
 * 
 * B = 0 
 * A = 11 
 * C = 101 
 * D = 100 
 * 
 * Each of the existing codes are replaced with a
 * new one of the same length, using the following algorithm:
 * 
 *  - The first symbol in the list gets assigned a codeword which is the same
 * length as the symbol's original codeword but all zeros. This will often be a
 * single zero ('0'). 
 *  - Each subsequent symbol is assigned the next binary number
 * in sequence, ensuring that following codes are always higher in value. 
 *  - When you reach a longer codeword, then after incrementing, append zeros until the
 * length of the new codeword is equal to the length of the old codeword. This
 * can be thought of as a left shift. 
 * 
 * By following these three rules, the
 * canonical version of the code book produced will be:
 * 
 * B = 0 
 * A = 10 
 * C = 110 
 * D = 111
 * 
 * Per capire di cosa si parla:
 *  - https://en.wikipedia.org/wiki/Canonical_Huffman_code
 *  - https://www.geeksforgeeks.org/canonical-huffman-coding/
 * 
 * 
 * 
 */

public class CanonicalHuffmanCoder {

	// struttura usata per tenere ordinate in maniera crescente le lunghezze delle
	// codifiche e per ognuna in ordine crescenti i simboli corrispondenti
	private TreeMap<Integer, TreeSet<Character>> lengthTable;

	// strutture utili a contare le occorrenze delle varie parole e a
	// ordinare le coppie (symbolo,occorrenze) in maniera veloce
	private HashMap<Character, Coppia> frequencies;

	// albero usato nella codifica
	private HuffmanNode root = null;
	
	//struttura usata come codebook per la codifica canonica
	private HashMap<Character, String> canonicalCodeTable;

	public CanonicalHuffmanCoder() {
		lengthTable = new TreeMap<Integer, TreeSet<Character>>();
		frequencies = new HashMap<>();
	}

	public Message encode(String input) {
		// TODO eliminare
		System.out.println("Input: " + input + "\n\n");

		if (input == null || input.length() < 2) {
			System.out.println("!!!! Input non valido (nullo o minore di 2 caratteri) !!!!");
			System.exit(0);
		}

		// calcola le frequenze
		computeOccurencies(input);
		// crea l'albero date le frequenza, ottenendo la codeTable
		createHuffmanTree();
		// calcola la codifica canonica (lunghezza codi, insieme di simoli ordinati
		// lexicograf) partendo dall'albero appena creato
		getLengthTable(root, 0);
		
		//otteniamo la codifica canonica esplcita dalla lengthTable
		canonicalCodeTable = HuffmanUtils.getCanonicalCodeTable(lengthTable);

		// TODO eliminare
		printNormalCodeTable();
		printCanonicalCodeTable();
		System.out.println(canonicalCodeTable);
		System.out.println("\nLengthTable:\n"+lengthTable);

		// ottenuta la canonicalCodeTable possiamo codificare il messaggio e aggiungerlo
		// come payload
		String encodedPayload = encodePayload(input);
		System.out.println(encodedPayload); //TODO eliminare

		// creiamo il messaggio e lo ritorniamo
		Message mex = new Message();
		mex.setHeader(lengthTable);
		mex.setPayload(encodedPayload);
		return mex;
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

		// TODO eliminare
		System.out.println("Frequenze: " + frequencies + "\n");

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
	}

	// Funzione ricorsiva che dall'albero ricava la codifica canonica (lengthTable, l'informazione da inviare come header)
	private void getLengthTable(HuffmanNode root, int code_length) {
		if (root == null)
			return;

		if (root.left == null && root.right == null) {

			lengthTable.putIfAbsent(code_length, new TreeSet<Character>());

			lengthTable.get(code_length).add(root.symbol);
			return;
		}
		getLengthTable(root.left, code_length + 1);
		getLengthTable(root.right, code_length + 1);
	}
	
	private void printCanonicalCodeTable() {
		System.out.println("\nCodifica di Huffman Canonica: "); 
		for (java.util.Map.Entry<Character, String> entry : canonicalCodeTable.entrySet()) {
			System.out.println(entry.getKey()+":"+entry.getValue());
		}
	}

	// metodo che permette di stampare la codifica di Huffman non canonica
	private void printNormalCodeTable() {

		PriorityQueue<Entry> codeTable = new PriorityQueue<>();
		// navigazione dell'albero per ottenere i codici
		getCode(root, "", codeTable);

		System.out.println("Codifica di Huffman normale:");
		while (!codeTable.isEmpty())
			System.out.println(codeTable.poll());
	}

	// metodo usato per stampare la codifica di Huffman normale
	private void getCode(HuffmanNode root, String s, PriorityQueue<Entry> codeTable) {
		if (root.left == null && root.right == null) {
			codeTable.add(new Entry(root.symbol, s));
			return;
		}
		getCode(root.left, s + "0", codeTable);
		getCode(root.right, s + "1", codeTable);
	}

	// metodo usato per codificare l'input dato l'albero di huffman già creato
	private String encodePayload(String input) {
		StringBuilder encodedPayload = new StringBuilder();
		for (int i = 0; i < input.length(); i++) {
			encodedPayload.append(canonicalCodeTable.get(input.charAt(i)));
		}
		return encodedPayload.toString();
	}

	//main di prova per codifica/decodifica di Huffman, eliminare TODO
	public static void main(String args[]) {
		CanonicalHuffmanCoder c = new CanonicalHuffmanCoder();
		Message m = c.encode("domenico non è particolarmente convinto che questa versione possa funzionare");
		System.out.println("\n");
		String mess=m.getPayload();
		BitSet bs=GenericUtils.getBitSetFromString(mess);
		System.out.println("n byte="+"domenico non è particolarmente convinto che questa versione possa funzionare".length());
		System.out.println("n byte="+bs.toByteArray().length);
		CanonicalHuffmanDecoder dec = new CanonicalHuffmanDecoder();
		dec.decode(m);
	}

	protected class Coppia {
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

}
