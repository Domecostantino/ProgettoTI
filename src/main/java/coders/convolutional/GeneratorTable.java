package coders.convolutional;

import java.util.HashMap;

public class GeneratorTable {

	/*
	 * Struttura che contiene le due generatorTable (mappe) per r=1/2 e r=1/3 ogni
	 * mappa ha chiavi pari a K (intero da 2 a 7, che rappresenta la lunghezza dei
	 * buffer usati nel processo di codifica) e i valori corrispondenti sono una
	 * sequenza di bit rappresentata come stringa, ognuno corrispondente al
	 * polinomio generatore associato
	 */
	private HashMap<Integer, HashMap<Integer, String[]>> generatorTables;

	public GeneratorTable() {
		generatorTables = new HashMap<>();

		insertGeneratorPolynomials();
	}

	/*
	 * Creazione dei polinomi generatori per valori diversi di r e K, prendendo
	 * quelli specificati nel libro di clark
	 * 
	 */
	private void insertGeneratorPolynomials() {
		HashMap<Integer, String[]> table1_2 = new HashMap<>();
		HashMap<Integer, String[]> table1_3 = new HashMap<>();

		table1_2.put(2, new String[] { "111", "101" });
		table1_2.put(2, new String[] { "111", "101" });
		table1_2.put(3, new String[] { "1111", "1101" });
		table1_2.put(4, new String[] { "11101", "10011" });
		table1_2.put(5, new String[] { "111101", "101011" });
		table1_2.put(6, new String[] { "1111001", "1011011" });
		table1_2.put(7, new String[] { "11111001", "10100111" });

		table1_3.put(2, new String[] { "111", "111", "101" });
		table1_3.put(3, new String[] { "1111", "1101", "1011" });
		table1_3.put(4, new String[] { "11111", "11011", "10101" });
		table1_3.put(5, new String[] { "111101", "101011", "100111" });
		table1_3.put(6, new String[] { "1111001", "1110101", "1011011" });
		table1_3.put(7, new String[] { "11110111", "11011001", "10010101" });

		generatorTables.put(2, table1_2);
		generatorTables.put(3, table1_3);
	}

	public String[] getGeneratorPolynomials(int r, int K) {
		return generatorTables.get(r).get(K);
	}
}
