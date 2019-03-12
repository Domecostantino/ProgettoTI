package coders;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.PriorityQueue;

/*
 * per comprendere di cosa si parla
 * 	https://github.com/gyaikhom/huffman/blob/master/huffman.pdf
 * 	https://www.geeksforgeeks.org/greedy-algorithms-set-3-huffman-coding/
 * 	https://it.wikipedia.org/wiki/Codifica_di_Huffman
 * 
 * 
 */

public class HuffmanCoder implements Coder {
	// struttura utile a contare le occorrenze delle varie parole e a
	// ordinare le coppie in maniera veloce
	private HashMap<String, Coppia> frequecies = new HashMap<>();
	private HashMap<String, String> codeTable = null;
	private HuffmanNode root = null;
	private int numWords = 0;

	@Override
	public Message encode(String input) {

		computeOccurencies(input);

		// FUNZIONA l'ordinamento

		// convertire i conteggi in frequenze
		// !!! non c'� bisogno di fare questa conversione in quanto si possono sommare i
		// numeri di occorrenze
		// prendendo di volta in volta il minore e usare come condizione di uscita il
		// numero di parole totali.

		createHuffmanTree();

		// 4. inviare header contente le coppie <char, frequenza> (oppure direttamente
		// la tabella contente le coppie <char, codifica>
		// e poi inviare il payload codificato

		return null;
	}

	@Override
	public String decode(Message input) {

		// 1. leggere header (tabella di corrispondenza)

		// 2. <creare l'albero> (no se si ha gia la tabella)

		// 3. decodificare in base alla tabella

		// 4. ritornare output

		return null;
	}

	private void computeOccurencies(String input) {
		// lettura del'input e creazione delle coppie <lettera, occorrenze>
		int i = 0;
		while (i < input.length()) {
			String current = "" + input.charAt(i);
			// decidere quali sono i delimitatori per ora solo spazio
			if (current.equals(" ")) { // aggiungiamo lo spazio (singolo)
				if (frequecies.containsKey(current))
					frequecies.get(current).addOccurence();
				else
					frequecies.put(current, new Coppia(current));
				numWords++;
				i++;
			} else {
				String word = null;
				int j = input.indexOf(" ", i);
				if (j > 0) { // se ci sono ancora parole
					word = input.substring(i, j);
					i = j;
				} else { // se e' l'ultima parola
					word = input.substring(i, input.length());
					i = input.length();
				}
				if (frequecies.containsKey(word))
					frequecies.get(word).addOccurence();
				else
					frequecies.put(word, new Coppia(word));
				numWords++;

			}

		}
	}

	private void createHuffmanTree() {
		// ordinamento delle coppie <parola, occorrenze> in base alle occorenze
		PriorityQueue<HuffmanNode> coppie = new PriorityQueue<>(new Comparator<HuffmanNode>() {
			@Override
			public int compare(HuffmanNode o1, HuffmanNode o2) {
				if (o1.occorences > o2.occorences)
					return 1;
				else if (o1.occorences < o2.occorences)
					return -1;
				return 0;
			}
		});
		for (Entry<String, Coppia> entry : frequecies.entrySet()) {
			coppie.add(new HuffmanNode(entry.getValue()));
		}

		// creazione dell'albero - iterativamente estraiamo i due valori pi� piccoli e
		// creaimo un
		// nodo "interno" finch� la cardinalita' della coda e' pari a 1
		while (coppie.size() > 1) {
			// estrazione due minori
			HuffmanNode x = coppie.poll();
			HuffmanNode y = coppie.poll();

			// creazione nuovo nodo
			HuffmanNode newNode = new HuffmanNode();
			newNode.occorences = x.occorences + y.occorences;
			newNode.word = "***ServiceNode***";
			
			//creazione dei collegamenti con i nodi genitori
			newNode.left = x;
			newNode.right = y;
			root = newNode;

			coppie.add(newNode);
		}

		//navigazione dell'albero per ottenere i codici
		
		codeTable = new HashMap<>(numWords);
		getCode(root,"");
		System.out.println(codeTable);

	}

	private void getCode(HuffmanNode root, String s ) {
		if (root.left == null && root.right == null) {
			codeTable.put(root.word, s);
			return;
		}
		getCode(root.left, s + "0");
		getCode(root.right, s + "1");
	}

	public static void main(String[] args) {
		Coder c = new HuffmanCoder();
		c.encode("riccardo deve scrivere la tesi altrimenti la tesi non si scriverà da sola");
	}

	private class Coppia {
		int count = 1;
		String word;

		public Coppia(String s) {
			this.word = s;
		}

		public void addOccurence() {
			count++;
		}

		public String toString() {
			return "[" + word + "," + count + "]";
		}

	}

	private class HuffmanNode {
		int occorences;
		String word;
		HuffmanNode left = null;
		HuffmanNode right = null;

		public HuffmanNode() {
		}

		public HuffmanNode(Coppia coppia) {
			occorences = coppia.count;
			word = coppia.word;
		}

		public String toString() {
			return "[" + word + "," + occorences + "]";
		}
	}
}
