package channel;

import java.util.Random;

import utils.MyBitSet;

public class CanaleSimmetricoBinario implements ChannelModel {

	private double ber = 0.02;
	private double rate = 700000; // 700 Kbps
	private Random random = new Random();

	public CanaleSimmetricoBinario(double ber) {
		if (ber <= 1d && ber >= 0d) {
			this.ber = ber;
		} else {
			System.out.println("ber out of range");
		}
	}

	public String getBer() {
		return "" + ber;
	}

	@Override
	public MyBitSet send(MyBitSet encodedPayload) {
		for (int i = 0; i < encodedPayload.getLength(); i++) {
			if (random.nextDouble() < ber) {
				encodedPayload.getBitset().flip(i);
			}
		}
        //simulazione tempo invio
		long time = (long) Math.ceil(encodedPayload.getLength() / rate);
		try {
			Thread.sleep(time);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
		return encodedPayload;
	}

}// CanaleSimmetricoBinario
