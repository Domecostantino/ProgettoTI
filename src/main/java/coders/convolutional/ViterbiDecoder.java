package coders.convolutional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeSet;

import coders.Decoder;
import coders.Message;

/*
 * Esistono diversi metodi per la decodifica di un codice convoluzionale.
 * La più famosa è l'algoritmo di Viterbi che fa uso del diagramma a traliccio
 * ricavato dai parametri usati nella codifica.
 * 
 * per capire di cosa si sta parlando:
 * 	http://web.mit.edu/6.02/www/f2010/handouts/lectures/L8.pdf
 * 	http://web.mit.edu/6.02/www/f2010/handouts/lectures/L9.pdf
 * 	http://web.mit.edu/6.02/www/s2009/handouts/labs/lab5.shtml
 * 	http://www.uniroma2.it/didattica/infocod/deposito/Informazione_e_Codifica_14.pdf
 * 	https://en.wikipedia.org/wiki/Convolutional_code#Decoding_convolutional_codes
 * 
 * In modo particolare seguo la notazione e le nozioni di
 * 	http://complextoreal.com/wp-content/uploads/2013/01/convo.pdf
 * 
 */

public class ViterbiDecoder implements Decoder {

	// il traliccio viene rappresentato come una array di livelli con una lista di
	// nodi per ogni livello
	private HashMap<Integer, TreeSet<TrellisNode>> trellis;

	private int K, r;
	private final int NUM_LEVELS = 3;

	private String[] generatorPolynomial;

	@Override
	public String decode(Message input) {

		// riceviamo il messaggio ed estrapoliamo header e payload
		ConvolutionalHeader header = (ConvolutionalHeader) input.getHeader();
		this.K = header.getK();
		this.r = header.getR();

		GeneratorTable generatorTable = new GeneratorTable();
		generatorPolynomial = generatorTable.getGeneratorPolynomials(r, K);

		String payload = input.getPayload();
		int range = NUM_LEVELS * r;

		StringBuilder decodedPayload = new StringBuilder();

		// cicliamo su tutto il payload in input
		for (int i = 0; i < payload.length(); i += range) {

			// scorporiamo blocchi da da 14 o 21 bits dal payload in relazione a r
			String block;
			int nextBound = i + range;
			if (nextBound < payload.length()) {
				block = payload.substring(i, nextBound);
			} else {
				block = payload.substring(i);
			}

			// per ogni blocco creiamo un traliccio di 7 livelli e otteniamo la decodifica
			// convoluzionale
			String decodedBlock = createTrellisAndDecode(block);

			decodedPayload.append(decodedBlock);

			System.out.println("\n\n ************************************** \n\n");
		}

		return decodedPayload.toString();
	}

	/*
	 * Questo metodo permette di creare un diagramma a traliccio di 7 livelli (o
	 * meno) per decodificare il blocco in input vedere:
	 * http://web.mit.edu/6.02/www/s2009/handouts/labs/lab5.shtml
	 * http://complextoreal.com/wp-content/uploads/2013/01/convo.pdf
	 * 
	 */
	@SuppressWarnings("unlikely-arg-type")
	private String createTrellisAndDecode(String block) { // TODO inizialmente per r=2 poi estendere

		createTrellis();
		System.out.println(trellis);
		
		byte[][] gs = convertGeneratorPolynomials();

		// Initialize PM[…,0] as PM[s,0] = 0 if s == starting_state else ∞
		TrellisPath initialPath = new TrellisPath();
		TrellisNode radix = trellis.get(0).first();
		initialPath.addNode(radix); // la radice

		TreeSet<TrellisNode> previousLevel = trellis.get(0);

		// il blocco è multiplo di r, per ogni parity bits block di lunghezza r (si
		// ciclerà NUM_LEVELS volte)
		for (int i = 0; i < block.length(); i += r) {

			String p_received = block.substring(i, i + r);

			TreeSet<TrellisNode> levelNodes = trellis.get((i+r) / r);
			System.out.println(levelNodes);
			// per ogni nodo del livello
			for (TrellisNode currentNode : levelNodes) {
				// recupera i nodi predecessori
				LinkedList<TrellisNode> predecessors = getPredecessors(previousLevel, currentNode);
				// per ognuno calcola i bit che avrebbe dovuto trasmettere il codificatore
				for (TrellisNode predecessor : predecessors) {
					String p_node = expectedParity(currentNode.getState(), gs);
					System.out.println("current Node: "+currentNode+"   predec :" + predecessor);
					System.out.println("paritybits "+p_node);
				}
				/*
				 * 
				 * x = ((to_state >> (k-2)) << (k-1)) + from_state return [xorbits(g & x) for g
				 * in glist]
				 */

			}

			previousLevel = levelNodes;

		}
		return null;

	}

	private String expectedParity(String state, byte[][] gs) {
		StringBuilder expectedParity = new StringBuilder();
		byte[] stateBits = getBits(state);
		byte[] ep = new byte[r];
		//considerando tutti i polinomi generatori (r)
		for (int l = 0; l < ep.length; l++) {
			//e per ognuno tutti i registri di memoria (K+1)
			for (int j = 0; j < stateBits.length; j++) {
				ep[l] =  (byte) (ep[l] ^ (stateBits[j]*gs[l][j]));
			}
			expectedParity.append(ep[l]);
		}

		return expectedParity.toString();
	}
	
	public static byte[] getBits(String input) {
		byte[] bits = new byte[input.length()];
		for (int i = 0; i < bits.length; i++) {
			bits[i] = (byte) (input.charAt(i)=='0'?0:1);
		}
		return bits;
	}
	
	private byte[][] convertGeneratorPolynomials() {
		byte[][] gs = new byte[r][K+1];
		for(int i=0; i<gs.length; i++) {
			for (int j = 0; j < gs[i].length; j++) {
				gs[i][j] = (byte) (generatorPolynomial[i].charAt(j)=='0'?0:1);
			}
		}
		return gs;
	}

	private LinkedList<TrellisNode> getPredecessors(TreeSet<TrellisNode> previousLevel, TrellisNode currentNode) {
		LinkedList<TrellisNode> predecessors = new LinkedList<>();
		for (TrellisNode node : previousLevel) {
			if (node.getSuccessors().contains(currentNode))
				predecessors.add(node);
		}
		return predecessors;
	}

	private void createTrellis() {
		TrellisNode radix = null;
		String firstState = "";
		for (int i = 0; i < K; i++)
			firstState += "0";

		radix = new TrellisNode(firstState, null);

		TreeSet<TrellisNode> firstLevel = new TreeSet<>();
		firstLevel.add(radix);
		trellis = new HashMap<>();
		trellis.put(0, firstLevel);

		radix.generateSuccessors();
		LinkedList<TrellisNode> successors = radix.getSuccessors();
		addTrellisNodes(1, successors);
		for (TrellisNode succ : successors) {
			createTrellisRecursive(1, succ);
		}

	}

	private void createTrellisRecursive(int i, TrellisNode node) {
		if (i < NUM_LEVELS) {
			node.generateSuccessors();
			LinkedList<TrellisNode> successors = node.getSuccessors();
			addTrellisNodes(i + 1, successors);
			for (TrellisNode succ : successors) {
				createTrellisRecursive(i + 1, succ);
			}
		}
	}

	private void addTrellisNodes(int i, LinkedList<TrellisNode> successors) {
		if (trellis.containsKey(i)) {
			TreeSet<TrellisNode> nodes = trellis.get(i);
			nodes.addAll(successors);
			trellis.put(i, nodes);
		} else {
			trellis.put(i, new TreeSet<>(successors));
		}
	}

}
