package utils;

import java.util.BitSet;

public class Statistics {

	// per le statistiche sui tempi di codifica/decodifica
	private long initialTime;
	private long channelCodingTime;
	private long sourceCodingTime;
	private long sourceDecodingTime;
	private long channelDecodingTime;

	// per le statistiche sui sulla compressione di sorgente
	private double compressionRate;
	private long initialSize;
	private long sourceCodeSize;

	// per le statistiche sulla capacità correttiva della codifica di canale
	private byte[] sourceCoding;
	private byte[] channelDecoding;
	private BitSet corruptedSourceCodingWithoutChannelEncoding;
	private double channelDecodingErrorRate;
	private double onlySourceCodeChannelErrorRate;

	/*
	 * Il numero di bit flippati erroneamente che il decodificatore di canale non è
	 * riuscito a correggere sulla lunghezza del messaggio inviato
	 */
	public double getChannelDecodingErrorRate() {
		// ricaviamo lo xor tra la codifica di sorgente in input al codificatore di
		// canale e la decodifica di canale, poi confrontiamo questo valore con la
		// lunghezza dell'input (del codificatore di canale)
		int flippedBits = 0;
		BitSet set1 = BitSet.valueOf(sourceCoding);
		BitSet set2 = BitSet.valueOf(channelDecoding);
		set1.xor(set2);
		flippedBits = set1.cardinality();
		System.out.println("Bits flippati decodifica canale: "+flippedBits); //TODO eliminare

		channelDecodingErrorRate = (channelDecoding.length!=0)? ((double) flippedBits / ((double) channelDecoding.length*8)):0.0;
		return channelDecodingErrorRate*100;
	}

	/*
	 * Il numero di bit flippati erroneamente dal canale sulla lunghezza del
	 * messaggio (solo codifica di sorgente)
	 */
	public double getOnlySourceCodeChannelErrorRate() {
		int flippedBits = 0;
		BitSet set1 = BitSet.valueOf(sourceCoding);
		set1.xor(corruptedSourceCodingWithoutChannelEncoding);
		flippedBits = set1.cardinality();
		System.out.println("Bits flippati solo cod sorgente: "+flippedBits); //TODO eliminare
		
		onlySourceCodeChannelErrorRate = (corruptedSourceCodingWithoutChannelEncoding.length()!=0)? ((double) flippedBits / (double) corruptedSourceCodingWithoutChannelEncoding.length()):0.0;
		return onlySourceCodeChannelErrorRate*100;
	}
	
	public double getErrorRecoveryRate() {
		return (onlySourceCodeChannelErrorRate!=0.0)?(1-(channelDecodingErrorRate/onlySourceCodeChannelErrorRate))*100:0;
	}

	public void setCorruptedSourceCodingWithoutChannelEncoding(BitSet corruptedSourceCodingWithoutChannelEncoding) {
		this.corruptedSourceCodingWithoutChannelEncoding = corruptedSourceCodingWithoutChannelEncoding;
	}

	public void setSourceCoding(byte[] sourceCoding) {
		this.sourceCoding = sourceCoding;
	}

	public void setChannelDecoding(byte[] channelDecoding) {
		this.channelDecoding = channelDecoding;
	}

	public long getSourceCodeSize() {
		return sourceCodeSize;
	}

	public void setSourceCodeSize(long l) {
		this.sourceCodeSize = l;
	}

	public double getCompressionRate() {
		return 1 - ((double) sourceCodeSize / (double) initialSize);
	}

	public long getInitialSize() {
		return initialSize;
	}

	public void setInitialSize(long l) {
		this.initialSize = l;
	}

	public long getSourceCodingTime() {
		return sourceCodingTime - initialTime;
	}

	public long getChannelCodingTime() {
		return channelCodingTime - sourceCodingTime;
	}

	public long getChannelDecodingTime() {
		return channelDecodingTime - channelCodingTime;
	}

	public long getSourceDecodingTime() {
		return sourceDecodingTime - channelDecodingTime;
	}

	public void setInitialTime(long initialTime) {
		this.initialTime = initialTime;
	}

	public void setSourceCodingTime(long sourceCodingTime) {
		this.sourceCodingTime = sourceCodingTime;
	}

	public void setChannelCodingTime(long channelCodingTime) {
		this.channelCodingTime = channelCodingTime;
	}

	public void setSourceDecodingTime(long sourceDecodingTime) {
		this.sourceDecodingTime = sourceDecodingTime;
	}

	public void setChannelDecodingTime(long channelDecodingTime) {
		this.channelDecodingTime = channelDecodingTime;
	}

}// Statistics
