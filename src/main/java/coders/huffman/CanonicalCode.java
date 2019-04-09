package coders.huffman;

import java.util.HashMap;
import java.util.PriorityQueue;

import coders.huffman.HuffmanCoder.Entry;

/*
 * Questa classe espone il metodo getCanonicalCode che ricevendo una codeTable 
 * normale (ottenuta dalla codifica classica di Huffman permette di ricavare la 
 * sua trasposizione in forma canonica, utile ad inviare la codetable in maniera 
 * più compatta e a velocizzare la decodifica
 * 
 * Per capire di cosa si parla:
 *  - https://en.wikipedia.org/wiki/Canonical_Huffman_code
 *  - https://www.geeksforgeeks.org/canonical-huffman-coding/
 * 
 */
public class CanonicalCode {

	/*
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
	 * 
	 */
	public static HashMap<Character, String> getCanonicalCodeTable(PriorityQueue<Entry> normalCodeTable) {
		if(normalCodeTable.isEmpty())
			return null;
		
		HashMap<Character, String> canonicalCodeTable = new HashMap<>(normalCodeTable.size());
		// la normalCodeTable e' gia' ordinata per lunghezze crescenti, passiamo ad
		// implementare i tre passi
		
		
		// Passo 1 - associamo al primo elemento un codice formato da tanti zero quanto
		// e' la sua lunghezza
		
		Entry firstEntry = normalCodeTable.poll();
		String codeFirstEntry = "";
		int i = 0;
		while(i<firstEntry.code.length()) {
			codeFirstEntry+="0";
			i++;
		}
		canonicalCodeTable.put(firstEntry.symbol, codeFirstEntry);
		
		//TODO eliminare
		System.out.println();
		System.out.println("Codifica di Huffman canonica:");
		System.out.println(codeFirstEntry);
	
		// Passo 2/3 - Se la codifica del simbolo corrente ha la stessa lunghezza della
		// precedente la codifica e' incrementata di 1 (in binario). Se la lunghezza e'
		// superiore la codifica e' incrementata di 1 e si appende uno zero alla fine
		int currentEntryLength;
		int previousEntryLength = codeFirstEntry.length();
		String lastValue = codeFirstEntry;
		while (!normalCodeTable.isEmpty()) {
			Entry currentEntry = normalCodeTable.poll();
			currentEntryLength = currentEntry.code.length();
			String newCode = "";

			int binaryValue = Integer.parseInt(lastValue, 2);
			binaryValue++;

			if (currentEntryLength > previousEntryLength) {
				newCode = Integer.toBinaryString(binaryValue) + "0";
				previousEntryLength = currentEntryLength;
			} else {
				newCode = Integer.toBinaryString(binaryValue);
			}
			lastValue = newCode;
			//TODO eliminare
			System.out.println(newCode);
			canonicalCodeTable.put(currentEntry.symbol, newCode);
		}

		//TODO eliminare
		System.out.println();
		System.out.println("canonicalCodeTable: "+canonicalCodeTable);
		
		return canonicalCodeTable;
	}
	
}
