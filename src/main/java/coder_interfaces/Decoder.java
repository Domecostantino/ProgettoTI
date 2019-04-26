package coder_interfaces;


public interface Decoder {
	/**
	 * Metodo di decodifica
	 * @param inputFileName è il path del file codificato
	 * @param outputFileName è il path in cui verrà creato il file decodificato
	 */
	public void decode(String inputFileName, String outputFileName);
}
