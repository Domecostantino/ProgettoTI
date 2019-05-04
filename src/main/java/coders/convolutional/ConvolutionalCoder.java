package coders.convolutional;

import java.math.BigInteger;
import java.util.Arrays;

import coders.Message;
import utils.ConvolutionalUtils;

/*
 * Per capire di cosa si parla:
 *  https://it.wikipedia.org/wiki/Codice_convoluzionale
 *  https://en.wikipedia.org/wiki/Convolutional_code
 *  http://www.uniroma2.it/didattica/infocod/deposito/Informazione_e_Codifica_14.pdf
 *  http://web.mit.edu/6.02/www/f2010/handouts/lectures/L8.pdf
 * 
 * 
 * parametri codifica convoluzionale:
 * 
 * K: constraint lenght (lunghezza registri di memoria a scorrimento)  (where the encoder has K-1 memory elements).
 * r: set of generator functions {G0, G1, ...}; il coding rate e' pari a 1/r (noi usiamo r=2 oppure r=3) scelto da input
 * (esistono in generale anche cod conv con lungInput/r. ma non li consideriamo, il nostro come gli altri codifica 
 * un bit alla volta, in maniera convoluzionale)
 * 
 * Il codificatore convoluzionale può essere visto come una macchina a stati finiti i cui stati totali sono 2^(K-1) 
 * perche un bit e' quello corrente e gli altri sono memoria passata utilizzata nel corrente passo di codifica
 * 
 * 
 * In alternativa vedere http://complextoreal.com/wp-content/uploads/2013/01/convo.pdf
 * 
 */

public class ConvolutionalCoder  {
	private int K, r;
	private final int NUM_LEVELS = 7;

	private String[] generatorPolynomial;

	private byte[] memoryRegister;

	/*
	 * passi codifica conv:
	 * 
	 * crea i polinomi generatori
	 * 
	 * formatta opportunamente l'input trasformandolo in bit
	 * 
	 * effettuo la codifica usando i polinomi generatori
	 * 
	 * invio l'header (K,r utile al decodificatore per creare il trellis) e il
	 * payload codificato
	 * 
	 */

	public ConvolutionalCoder(int constraintLenght, int r) {
		this.K = constraintLenght;
		this.r = r;

		GeneratorTable generatorTable = new GeneratorTable();
		generatorPolynomial = generatorTable.getGeneratorPolynomials(r, K);

		memoryRegister = new byte[K + 1]; // inizialmente tutti 0

	}
	/*
	 * Convolutionally encoding the data is accomplished using a shift register and
	 * associated combinatorial logic that performs modulo-two addition. (A shift
	 * register is merely a chain of flip-flops wherein the output of the nth
	 * flip-flop is tied to the input of the (n+1)th flip-flop. Every time the
	 * active edge of the clock occurs, the input to the flip-flop is clocked
	 * through to the output, and thus the data are shifted over one stage.)
	 * 
	 * The octal numbers (25) 8, (33) 8, (37)8 represent the code generator
	 * polynomials, which when read in binary (10101)2 , (11011)2 and (11111)2
	 * correspond to the Shift register connections to the upper and lower
	 * modulo-two adders, respectively as shown in the figure above. Following steps
	 * are followed while designing convolutional encoder.
	 * 
	 * 
	 */

	public String encode(String input) { // TODO pulire stampe
		// conversione dell'input testuale in una stringa di bit
//		String input = new BigInteger(input.getBytes()).toString(2);
		System.out.println(input);
		System.out.println(input.length());
		System.out.println(input);

		StringBuilder encodedPayload = new StringBuilder();
		int range = NUM_LEVELS;
		// cicliamo su tutto il payload in input
		for (int i = 0; i < input.length(); i += range) {

			// scorporiamo blocchi da da 14 o 21 bits dal payload in relazione a r
			String block;
			int nextBound = i + range;
			if (nextBound < input.length()) {
				block = input.substring(i, nextBound);
			} else {
				block = input.substring(i);
			}

			System.out.println(block);
			// codifica convoluzionale mediante i polinomi generatori
			String encodingBlock = computeEncoding(block);
			System.out.println();
			System.out.println(encodingBlock);

			encodedPayload.append(encodingBlock);

			System.out.println("\n\n ************************************** \n\n");
		}

		System.out.println("EncodedPayload: "+encodedPayload.toString());
		

		Message message = new Message();

//		// header
//		ConvolutionalHeader header = new ConvolutionalHeader(K, r);
//		message.setHeader(header);
//
//		// payload
//		message.setPayload(encodedPayload.toString());

		return encodedPayload.toString();
	}

	/*
	 * metodo privato che calcola la codifica convoluzionale e costruiste il trellis
	 * da usare come header
	 */
	private String computeEncoding(String bitInput) {

		StringBuilder result = new StringBuilder();

		memoryRegister = new byte[K + 1];
		// recuperiamo i polinomi generatori
		System.out.println(Arrays.toString(generatorPolynomial));
		// li convertiamo in una matrice di byte per semplicita
		byte[][] gs = ConvolutionalUtils.convertGeneratorPolynomials(r,K,generatorPolynomial);

		// ora iteriamo un bit in input per volta
		for (int i = 0; i < bitInput.length(); i++) {

			byte currentBit = (byte) (bitInput.charAt(i) == '0' ? 0 : 1);
			byte[] currentBitEncoding = new byte[r];

			shiftRegister((byte) currentBit);

			// considerando tutti i polinomi generatori (r)
			for (int l = 0; l < currentBitEncoding.length; l++) {
				// e per ognuno tutti i registri di memoria (K+1)
				for (int j = 0; j < memoryRegister.length; j++) {
					currentBitEncoding[l] = (byte) (currentBitEncoding[l] ^ (memoryRegister[j] * gs[l][j]));
				}
				result.append(currentBitEncoding[l]);
//				System.out.println(currentBitEncoding[l]); //TODO eliminare
			}

		} // TODO controllare a mano se è giusto
		return result.toString();
	}

	private void shiftRegister(byte input) {
		for (int i = memoryRegister.length - 1; i > 0; i--) {
			memoryRegister[i] = memoryRegister[i - 1];
		}
		memoryRegister[0] = input;

//		System.out.println(Arrays.toString(memoryRegister)); // TODO eliminare
	}

	public static void main(String[] args) {
		ConvolutionalCoder cc = new ConvolutionalCoder(7, 3);
		String q = "ciao Vincenzo come stai èllamado";
		String s = cc.encode(q);
		
		String changedMessage = s.replace("0000", "0100");
		System.out.println("corrupted "+changedMessage);
		
		System.out.println("\n\n--------------------- DECODE ----------------------\n\n");

//		String p = new BigInteger(q.getBytes()).toString(2);
//		p = p.replace("1011", "1010");
//		System.out.println(p);
//		
//		String text2 = new String(new BigInteger(p, 2).toByteArray());
//		System.out.println(text2);
		
//		Message corrupted = new Message();
//		corrupted.setHeader(m.getHeader());
//		corrupted.setPayload(changedMessage);
		

		ViterbiDecoder dec = new ViterbiDecoder(7,3);
		String d = dec.decode(changedMessage);
		System.out.println("decodedPayload: "+d);
		
		String text2 = new String(new BigInteger(d, 2).toByteArray());
		System.out.println(text2);
		
		System.out.println("con replace(\"0001\", \"0000\") e r=2 si ha: ");
		System.out.println(q+ " -> "+text2);
		
		String p = new BigInteger(q.getBytes()).toString(2);
		p = p.replace("0000", "0101");
		String text3 = new String(new BigInteger(p, 2).toByteArray());
		System.out.println("Messaggio senza codifica di sorgente sempre con replace(\"0001\", \"0000\"); : "+text3);
	}

}
