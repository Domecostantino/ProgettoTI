package coders.huffman;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

import coders.Decoder;
import coders.Message;

/*
 * Questa classe ha lo scopo di concretizzare il metodo decode dell'interfaccia Coder
 * e quindi decodificare un messaggio in input
 */
public class CanonicalHuffmanDecoder implements Decoder {
	// struttura usata per tenere ordinate in maniera crescente le lunghezze delle
	// codifiche e per ognuna in ordine crescenti i simboli corrispondenti
	private TreeMap<Integer, TreeSet<Character>> lengthTable;
	
	//struttura usata come codebook per la codifica canonica
	private HashMap<String, Character> inverseCanonicalCodeTable;

	public CanonicalHuffmanDecoder() {

	}

	@SuppressWarnings("unchecked")
	@Override
	public String decode(Message input) {
		// recuperiamo l'header dal messaggio (lengthTable) siamo sicuri che è la
		// struttura adeguata se stiamo usando Huffman come codifica di sorgente
		try {
			lengthTable = (TreeMap<Integer, TreeSet<Character>>) input.getHeader();
		} catch (ClassCastException e) {
			System.out.println("Non è stato mandato l'header giusto (Codifica di Huffman)");
		}
		System.out.println("LengthTable ricevuta:"+lengthTable); //TODO eliminare
		
		//ricostruiamo la codeTable dalla lengthTable
		inverseCanonicalCodeTable = createInverseCanonicalCodeTable();
		
		//recuperiamo il messaggio codificato
		String payload = input.getPayload();
		
		//decodifichiamo
		String result = getDecodedMessage();
		System.out.println(result); //TODO eliminare
		return result;
	}

	

	private HashMap<String, Character> createInverseCanonicalCodeTable() {
		
		inverseCanonicalCodeTable = new HashMap<>();
		
		
		//codifica 1 elemento
		int lengthFistElem = lengthTable.firstEntry().getKey();
		char firstSymbol = lengthTable.get(lengthFistElem).first();
		//TODO codificare primo simbolo e poi tutti gli altri della prima entry basandosi sul primo
		//TODO dalla seconda entry in poi creare un ciclo che lo fa in automatico basandosi sempre sulla
		//ultima codifica
		return null;
	}
		
//		String previousValue
//		for (Entry<Integer, TreeSet<Character>> entry : lengthTable.entrySet()) {
//			String[] codings = getElements(fistElement,entry.getKey(), entry.getValue().size());
//			fistElement = false;
//			int i=0;
//			for (Character symbol : entry.getValue()) {
//				inverseCanonicalCodeTable.put(codings[i], symbol);
//				i++;
//			}
//			fistElement = true;
//		}
//		System.out.println(inverseCanonicalCodeTable);
//		
//		return inverseCanonicalCodeTable;
//	}
//	
//	private String[] getElements(boolean firstElement, Integer length, int numElements) {
//		String[] codings = new String[numElements];
//		int i=0;
//		if(firstElement) {
//			codings[i] = "";
//			for (int j = 0; j < length; j++) {
//				codings[i] += "0";
//			}
//			i++;
//		}
//		for (;  i< codings.length; i++) {
//			
//		}
//		return codings;
//	}

	private String getDecodedMessage() {
		
		return null;
	}

}
