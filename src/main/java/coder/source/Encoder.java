package coder.source;


public interface Encoder {
	/**
	 * Metodo di codifica
	 * @param inputFileName è il path del file sorgente
	 * @param outputFileName è il path in cui verrà creato il file codificato
	 */
	public void encode(String inputFileName, String outputFileName);
}
