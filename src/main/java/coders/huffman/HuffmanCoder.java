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
 * 
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

		//calcola le frequenze
		computeOccurencies(input);
		//crea l'albero date le frequenza, ottenendo la codeTable
		createHuffmanTree();

		// 4. inviare header contente la codeTable
		// e poi inviare il payload codificato

		return null;
	}

	@Override
	public String decode(Message input) {

		// 1. leggere header (codeTable)

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
		
		System.out.println(frequencies);
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
		getCode(root, "");
		while (!codeTable.isEmpty()) {
			System.out.println(codeTable.poll());
		}

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

	public static void main(String[] args) {
		Coder c = new HuffmanCoder();
		c.encode("proviamo questo schifoooooooooooooaaaa e vediamo come va ");
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

	private class Entry implements Comparable<Entry> {
		String code = "";
		char symbol;

		public Entry(char symbol, String code) {
			this.symbol = symbol;
			this.code = code;
		}

		@Override
		public int compareTo(Entry e1) {
			if (code.length() < e1.code.length())
				return -1;
			else if (code.length() > e1.code.length())
				return 1;
			else
				return this.code.compareTo(e1.code);
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
