package coders.huffman;

import coders.huffman.CanonicalHuffmanCoder.Coppia;

class HuffmanNode {
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