package coder.source;


public interface SourceCoder {
	/**
	 * Metodo di codifica
	 * @param inputFileName è il path del file sorgente
	 * @param outputFileName è il path in cui verrà creato il file codificato
	 */
	public void encode(String inputFileName, String outputFileName);
	
	/**
	 * Metodo di decodifica
	 * @param inputFileName è il path del file codificato
	 * @param outputFileName è il path in cui verrà creato il file decodificato
	 */
	public void decode(String inputFileName, String outputFileName);
}
