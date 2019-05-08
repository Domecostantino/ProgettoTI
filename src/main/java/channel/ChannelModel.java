package channel;

import utils.MyBitSet;

public interface ChannelModel {
	/*
	 * Metodo che simula l'invio del messaggio flippando i bit in accordo al modello
	 * di errore che implementa l'interfaccia
	 */
	public MyBitSet send(MyBitSet encodedPayload);
}
