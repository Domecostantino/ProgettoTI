package coders.convolutional;

import java.util.ArrayList;
import java.util.LinkedList;

import coders.Decoder;
import coders.Message;

/*
 * Esistono diversi metodi per la decodifica di un codice convoluzionale.
 * La più famosa è l'algoritmo di Viterbi che fa uso del diagramma a traliccio
 * ricavato dai parametri usati nella codifica.
 * 
 * per capire di cosa si sta parlando:
 * 	http://web.mit.edu/6.02/www/f2010/handouts/lectures/L8.pdf
 * 	http://web.mit.edu/6.02/www/f2010/handouts/lectures/L9.pdf
 * 	http://web.mit.edu/6.02/www/s2009/handouts/labs/lab5.shtml
 * 	http://www.uniroma2.it/didattica/infocod/deposito/Informazione_e_Codifica_14.pdf
 * 	https://en.wikipedia.org/wiki/Convolutional_code#Decoding_convolutional_codes
 * 
 * In modo particolare seguo la notazione e le nozioni di
 * 	http://complextoreal.com/wp-content/uploads/2013/01/convo.pdf
 * 
 */

public class ViterbiDecoder implements Decoder {

	// il traliccio viene rappresentato come una array di livelli con una lista di
	// nodi per ogni livello
	private ArrayList<LinkedList<TrellisNode>> trellis;
	private int K, r;
	private final int NUM_LEVELS = 7;

	@Override
	public String decode(Message input) {
		// riceviamo il messaggio ed estrapoliamo header e payload
		ConvolutionalHeader header = (ConvolutionalHeader) input.getHeader();
		this.K = header.getK();
		this.r = header.getR();

		String payload = input.getPayload();

		// creaiamo il diagramma a traliccio basandoci su K e r
		createTrellis(payload);

		return null;
	}

	private void createTrellis(String payload) {
		trellis = new ArrayList<>();

		for (int i = 0; i < payload.length(); i++) {
			TrellisNode radix;

			if (r == 2)
				radix = new TrellisNode("00");
			else if (r == 3)
				radix = new TrellisNode("000");
			

			for (int j = 1; j < NUM_LEVELS; j++) {

			}

		}

	
	}

}
