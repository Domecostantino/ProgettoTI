package codificatori_conv_rip;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

import javax.xml.transform.Result;

public class CodificatoreARipetizione {

	private int RIPETIZIONI_1;

	private final int NUMERO_THREAD = 4;
	
	public CodificatoreARipetizione(int ripetizioni) {
		this.RIPETIZIONI_1 = ripetizioni;
	}

	public byte[] codifica(String path) throws Exception {
		System.out.println("****** INIZIO CODIFICA **********");
		Instant start = Instant.now();

		Path filePath = Paths.get(path);
		System.out.println("Lettura dal file " + path);
		byte[] msg = Files.readAllBytes(filePath);
		
		System.out.println("Fine lettura");

		System.out.println("Conversione in bit del file....");
		CountDownLatch latch = new CountDownLatch(NUMERO_THREAD);
		byte[] bits_msg = new byte[msg.length * 8];
		int range = msg.length / NUMERO_THREAD;
		int resto = msg.length % NUMERO_THREAD;
		int inizioRange = 0;
		for (int i = 0; i < NUMERO_THREAD; i++) {
			if (i < NUMERO_THREAD - 1) {
				ThreadConversioneByteBit tb = new ThreadConversioneByteBit(inizioRange, inizioRange + range + resto,
						msg, bits_msg, latch);
				Thread t = new Thread(tb);
				t.start();
			} else {
				ThreadConversioneByteBit tb = new ThreadConversioneByteBit(inizioRange, inizioRange + range, msg,
						bits_msg, latch);
				Thread t = new Thread(tb);
				t.start();
			}
			inizioRange += range;
			
		}
		latch.await();		
		System.out.println("Fine conversione");
		
		System.out.println("Inizio codifica...");
		// PRIMO STEP
		byte[] result = new byte[bits_msg.length * RIPETIZIONI_1];
		int k = 0;
		for (int i = 0; i < bits_msg.length; i++) {
			for (int j = 0; j < RIPETIZIONI_1; j++) {
				result[k++] = bits_msg[i];
			}
		}
		System.out.println("Fine codifica");
		
		System.out.println("-------------------------");

		System.out.println("Conversione in byte...");
		byte[] bytes = new byte[result.length/8];
		CountDownLatch latch2 = new CountDownLatch(NUMERO_THREAD);
		
		int blocchi = result.length/8;
		int lavoroPerThread = blocchi/NUMERO_THREAD;
		int restoLavoro = blocchi%NUMERO_THREAD;
		int range2 = lavoroPerThread*8;
		int inizioRange2 = 0;
		
		for (int i = 0; i < NUMERO_THREAD; i++) {
			if (i == NUMERO_THREAD - 1) {
				ThreadConversioneBitByte tb = new ThreadConversioneBitByte(inizioRange2, inizioRange2 + range2 + restoLavoro*8,
						result, bytes, latch2);
				Thread t = new Thread(tb);
				t.start();
			} else {
				ThreadConversioneBitByte tb = new ThreadConversioneBitByte(inizioRange2, inizioRange2 + range2, result,
						bytes, latch2);
				Thread t = new Thread(tb);
				t.start();
			}
			inizioRange2 += range2;

		}
		latch2.await();
		System.out.println("Fine conversione");
		
		System.out.println("Scrittura della codifica sul file...");
		try {
			FileOutputStream fos = new FileOutputStream(path + ".codificata");
			fos.write(bytes);
			fos.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println("Fine scrittura sul file");
		Instant end = Instant.now();
		
		System.out.println("Tempo impiegato per la codifica: " + Duration.between(start, end).toMillis() + " ms.");
		System.out.println("******************************");
		return bytes;
	}

	public byte[] decodifica(String path) throws Exception {
		System.out.println("*********** INIZIO DECODIFICA ****************");
		Instant start = Instant.now();
		Path filePath = Paths.get(path);
		System.out.println("Lettura dal file " + path);
		byte[] msg = Files.readAllBytes(filePath);
		System.out.println("Fine lettura");
		
		System.out.println("Conversione in bit del file....");
		CountDownLatch latch = new CountDownLatch(NUMERO_THREAD);
		byte[] bits_msg = new byte[msg.length * 8];
		int range = msg.length / NUMERO_THREAD;
		int resto = msg.length % NUMERO_THREAD;
		int inizioRange = 0;
		for (int i = 0; i < NUMERO_THREAD; i++) {
			if (i < NUMERO_THREAD - 1) {
				ThreadConversioneByteBit tb = new ThreadConversioneByteBit(inizioRange, inizioRange + range + resto,
						msg, bits_msg, latch);
				Thread t = new Thread(tb);
				t.start();
			} else {
				ThreadConversioneByteBit tb = new ThreadConversioneByteBit(inizioRange, inizioRange + range, msg,
						bits_msg, latch);
				Thread t = new Thread(tb);
				t.start();
			}
			inizioRange += range;
			
		}
		latch.await();		
		System.out.println("Fine conversione");

		// secondo step
		System.out.println("Inizio decodifica...");
		byte[] decodifica2 = new byte[bits_msg.length / RIPETIZIONI_1];
		int k = 0;
		for (int i = 0; i < bits_msg.length; i += RIPETIZIONI_1) {
			int sum = 0;
			for (int j = 0; j < RIPETIZIONI_1; j++) {
				sum += bits_msg[i + j];
			}
			if (sum > (RIPETIZIONI_1 / 2 + 1)) {
				decodifica2[k] = (byte) 1;
			} else {
				decodifica2[k] = (byte) 0;
			}
			k++;
		}
		System.out.println("Fine decodifica");
		
		System.out.println("Inizio conversione in byte....");
		byte[] bytes = new byte[decodifica2.length/8];
		CountDownLatch latch2 = new CountDownLatch(NUMERO_THREAD);
		
		int blocchi = decodifica2.length/8;
		int lavoroPerThread = blocchi/NUMERO_THREAD;
		int restoLavoro = blocchi%NUMERO_THREAD;
		int range2 = lavoroPerThread*8;
		int inizioRange2 = 0;
		
		for (int i = 0; i < NUMERO_THREAD; i++) {
			if (i == NUMERO_THREAD - 1) {
				ThreadConversioneBitByte tb = new ThreadConversioneBitByte(inizioRange2, inizioRange2 + range2 + restoLavoro*8,
						decodifica2, bytes, latch2);
				Thread t = new Thread(tb);
				t.start();
			} else {
				ThreadConversioneBitByte tb = new ThreadConversioneBitByte(inizioRange2, inizioRange2 + range2, decodifica2,
						bytes, latch2);
				Thread t = new Thread(tb);
				t.start();
			}
			inizioRange2 += range2;

		}
		latch2.await();
		System.out.println("Fine conversione in byte");
		
		System.out.println("Scrittura della decodifica sul file " + path.substring(0, path.indexOf(".")) + "_decodificata.wav");
		try {
			FileOutputStream fos = new FileOutputStream(path.substring(0, path.indexOf(".")) + "_decodificata.wav");
			fos.write(bytes);
			fos.close();

		} catch (Exception e) {
			// TODO: handle exception
		}
		System.out.println("Fine scrittura sul file");
		Instant end = Instant.now();
		System.out.println("Tempo impiegato per la decodifica: " + Duration.between(start, end).toMillis() + " ms.");
		return bytes;
	}

	
	public static void main(String[] args) throws Exception {
		String path = "//home//pietro//Desktop//canzone.wav";

		Path filePath = Paths.get(path);
		byte[] msg = Files.readAllBytes(filePath);

		CodificatoreARipetizione car = new CodificatoreARipetizione(13);
		car.codifica(path);

		System.out.println("**********************");
		File file = new File("//home//pietro//Desktop//canzone.wav.codificata");

		try {
			Canale3g.sendFile(file);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("**********************");

		System.out.println("************* INIZIO DECODIFICA *****************");
		byte[] decodifica = car.decodifica("//home//pietro//Desktop//canzone.wav.codificata.canale");
		System.out.println("************* FINE DECODIFICA *****************");
		int byteDiversi = 0;
		
		System.out.println(msg.length+" "+decodifica.length);
		
		for (int i = 0; i < decodifica.length; i++) {
			if (decodifica[i] != msg[i]) {
				byteDiversi++;
			}
		}
		System.out.println(decodifica.length);
		System.out.println(byteDiversi);
		System.out.println("Percentuale byte errati = " + (double)(byteDiversi * 100) / msg.length);
	}

	
}
