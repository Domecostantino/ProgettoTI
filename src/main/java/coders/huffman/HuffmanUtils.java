package coders.huffman;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/*
 * Classe usata per alleggerire le classi CanonicalHuffmanCoder/Decoder, fornendo metodi di utilità
 * 
 */
public class HuffmanUtils {

	/*
	 * In accordo alla logica di costruzione del codice canonico otteniamo il
	 * prossimo codice aggiungendo 1 in binario Il metodo riceve una stringa con un
	 * numero binario, aggiunge uno e ritorna il nuovo numero in binario senza
	 * eliminare eventuali zeri iniziali
	 * 
	 * 
	 */
	static String addOne(String previousCode) {
		String result = ""; // Initialize result
		String one = "1";
		int s = 0; // Initialize digit sum

		// Travers both strings starting from last
		// characters
		int i = previousCode.length() - 1, j = 0;
		while (i >= 0 || j >= 0 || s == 1) {
			// Comput sum of last digits and carry
			s += ((i >= 0) ? previousCode.charAt(i) - '0' : 0);
			s += ((j >= 0) ? one.charAt(j) - '0' : 0);
			// If current digit sum is 1 or 3, add 1 to result
			result = (char) (s % 2 + '0') + result;
			// Compute carry
			s /= 2;
			// Move to next digits
			i--;
			j--;
		}
		return result;
	}

	/*
	 * 
	 * funzione di servizio utile a creare la codeTable relativa al corrente input
	 * partendo dalla tabella delle lunghezze. Usato in CanonicalHuffmanCoder.
	 * 
	 */
	static HashMap<Character, String> getCanonicalCodeTable(TreeMap<Integer, TreeSet<Character>> lengthTable) {
		HashMap<Character, String> canonicalCodeTable = new HashMap<>();

		Set<Entry<Integer, TreeSet<Character>>> entries = lengthTable.entrySet();
		Iterator<Entry<Integer, TreeSet<Character>>> lengthsIterator = entries.iterator();

		// codifica primo elemento
		// prendiamo la prima entry che ci sara' certamente
		Entry<Integer, TreeSet<Character>> firstEntry = lengthsIterator.next();
		// creiamo un iteratore sui caratteri
		Iterator<Character> charIterator = firstEntry.getValue().iterator();
		// leggiamo il primo valore e creiamo una codifica formata da soli zeri
		String firstCode = "";
		char firstChar = charIterator.next();
		for (int i = 0; i < firstEntry.getKey(); i++) {
			firstCode += "0";
		}
		// inseriamo la prima coppia (codificaCanonica, simbolo) nella invCanTable
		canonicalCodeTable.put(firstChar, firstCode);

		String previousCode = firstCode;
		// ora inseriamo le altre coppie di lunghezza uguale alla prima partendo dal
		// valore di quest'ultima
		while (charIterator.hasNext()) {
			char currentSymbol = charIterator.next();
			String code = HuffmanUtils.addOne(previousCode);
			canonicalCodeTable.put(currentSymbol, code);
			previousCode = code;
		}

		// dalla seconda entry in poi creiamo un ciclo che lo fa in automatico
		// basandosi sempre sulla ultima codifica ricordando di appendere lo zero finale
		// quando si passa alla lunghezza successiva
		while (lengthsIterator.hasNext()) {
			boolean addFinalZero = true;
			Entry<Integer, TreeSet<Character>> currentEntry = lengthsIterator.next();
			charIterator = currentEntry.getValue().iterator();
			while (charIterator.hasNext()) {
				char currentSymbol = charIterator.next();
				String code = HuffmanUtils.addOne(previousCode);
				if (addFinalZero) {
					code += "0";
					addFinalZero = false;
				}
				canonicalCodeTable.put(currentSymbol, code);
				previousCode = code;
			}
		}
		return canonicalCodeTable;
	}
	
	/*
	 * 
	 * funzione di servizio utile a creare la codeTable relativa al corrente input
	 * partendo dalla tabella delle lunghezze. Usato in CanonicalHuffmanDecoder.
	 * 
	 */
	static HashMap<String, Character> createInverseCanonicalCodeTable(TreeMap<Integer, TreeSet<Character>> lengthTable) {

		HashMap<String, Character> inverseCanonicalCodeTable = new HashMap<>();

		Set<Entry<Integer, TreeSet<Character>>> entries = lengthTable.entrySet();
		Iterator<Entry<Integer, TreeSet<Character>>> lengthsIterator = entries.iterator();

		// codifica primo elemento
		// prendiamo la prima entry che ci sara' certamente
		Entry<Integer, TreeSet<Character>> firstEntry = lengthsIterator.next();
		// creiamo un iteratore sui caratteri
		Iterator<Character> charIterator = firstEntry.getValue().iterator();
		// leggiamo il primo valore e creiamo una codifica formata da soli zeri
		String firstCode = "";
		char firstChar = charIterator.next();
		for (int i = 0; i < firstEntry.getKey(); i++) {
			firstCode += "0";
		}
		// inseriamo la prima coppia (codificaCanonica, simbolo) nella invCanTable
		inverseCanonicalCodeTable.put(firstCode, firstChar);

		String previousCode = firstCode;
		// ora inseriamo le altre coppie di lunghezza uguale alla prima partendo dal
		// valore di quest'ultima
		while (charIterator.hasNext()) {
			char currentSymbol = charIterator.next();
			String code = HuffmanUtils.addOne(previousCode);
			inverseCanonicalCodeTable.put(code, currentSymbol);
			previousCode = code;
		}

		// dalla seconda entry in poi creiamo un ciclo che lo fa in automatico
		// basandosi sempre sulla ultima codifica ricordando di appendere lo zero finale
		// quando si passa alla lunghezza successiva
		while (lengthsIterator.hasNext()) {
			boolean addFinalZero = true;
			Entry<Integer, TreeSet<Character>> currentEntry = lengthsIterator.next();
			charIterator = currentEntry.getValue().iterator();
			while (charIterator.hasNext()) {
				char currentSymbol = charIterator.next();
				String code = HuffmanUtils.addOne(previousCode);
				if (addFinalZero) {
					code += "0";
					addFinalZero = false;
				}
				inverseCanonicalCodeTable.put(code, currentSymbol);
				previousCode = code;
			}
		}
		return inverseCanonicalCodeTable;
	}

}
