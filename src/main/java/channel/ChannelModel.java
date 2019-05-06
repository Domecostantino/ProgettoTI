package channel;

import java.util.BitSet;

public interface ChannelModel {
	/*
	 * Metodo che simula l'invio del messaggio flippando i bit in accordo al modello
	 * di errore che implementa l'interfaccia
	 */
	public BitSet send(BitSet encodedPayload);
}
