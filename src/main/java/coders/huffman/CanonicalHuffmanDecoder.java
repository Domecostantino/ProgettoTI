package coders.huffman;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

import coders.Decoder;
import coders.Message;
import utils.HuffmanUtils;

/*
 * Questa classe ha lo scopo di concretizzare il metodo decode dell'interfaccia Coder
 * e quindi decodificare un messaggio in input
 */
public class CanonicalHuffmanDecoder implements Decoder {
	// struttura usata per tenere ordinate in maniera crescente le lunghezze delle
	// codifiche e per ognuna in ordine crescenti i simboli corrispondenti
	private TreeMap<Integer, TreeSet<Character>> lengthTable;

	// struttura usata come codebook per la codifica canonica
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
		System.out.println("LengthTable ricevuta:" + lengthTable); // TODO eliminare

		// ricostruiamo la codeTable dalla lengthTable
		inverseCanonicalCodeTable = HuffmanUtils.createInverseCanonicalCodeTable(lengthTable);
		System.out.println("inverseCanonicalCodeTable: " + inverseCanonicalCodeTable); // TODO eliminare

		// recuperiamo il messaggio codificato
		String payload = input.getPayload();

		// decodifichiamo usando la invCanCodeTable
		String result = getDecodedMessage(payload);
		System.out.println(result); // TODO eliminare
		return result;
	}
	

	// leggiamo un carattere alla volta del payload e vediamo se matcha una
	// codifica, la proprieta' di essere una codifica prefissa permette questo tipo
	// di decodifica senza delimitatori
	private String getDecodedMessage(String payload) {
		String partialCode = "";
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < payload.length(); i++) {
			partialCode += payload.charAt(i);
			if(inverseCanonicalCodeTable.containsKey(partialCode)) {
				result.append(inverseCanonicalCodeTable.get(partialCode));
				partialCode = "";
			}
		}
		return result.toString();

	}

}
